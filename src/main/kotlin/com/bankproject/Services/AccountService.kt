package com.bankproject.Services

import com.bankproject.Repositories.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.lang.Exception

@Service
class AccountService(@Autowired val accountRepsitory: AccountRepository,
                     @Autowired val userRepository: UserRepository,
                     @Autowired val transactionRepository: TransactionRepository) {

    // ideally, this value should be written in the DB and loaded from there
    private val MAX_DEPOSIT_AMOUNT = 5000
    private val MIN_DEPOSIT_AMOUNT = 50
    enum class CURRENCIES(val exchange: Double) {EUR(1.0), GBP(1.11), USD(0.85)}

    // transfer money from one account to another. If currency is null, it is assumed to be EUR
    fun transfer(sourceIBAN: String, targetIBAN: String, amount: Double, password: String, currency: String?) {
        val sourceAccount = checkIBAN(sourceIBAN)
        val targetAccount = checkIBAN(targetIBAN)
        checkPermission(sourceAccount, password)

        var euroAmount = getEurAmount(amount, currency)

        if (hasSufficientFunds(sourceAccount, euroAmount)) {
            val transaction = Transaction(sourceIBAN, targetIBAN, euroAmount, System.currentTimeMillis())
            sourceAccount.balance -= euroAmount
            targetAccount.balance += euroAmount
            accountRepsitory.save(sourceAccount)
            accountRepsitory.save(targetAccount)
            transactionRepository.save(transaction)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Funds insufficient")
        }
    }

    // deposit money in one account
    fun deposit(targetIBAN: String, amount: Double, currency: String?) {
        var euroAmount = getEurAmount(amount, currency)

        if (euroAmount > MAX_DEPOSIT_AMOUNT || euroAmount < MIN_DEPOSIT_AMOUNT) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Amount not allowed")
        }
        val targetAccount = checkIBAN(targetIBAN)
        targetAccount.balance += euroAmount
        accountRepsitory.save(targetAccount)
        val transaction = Transaction(null, targetIBAN, euroAmount, System.currentTimeMillis())
        transactionRepository.save(transaction)
    }

    // withdraw money from one account
    fun withdraw(sourceIBAN: String, amount: Double, password: String, currency: String?) {
        val sourceAccount = checkIBAN(sourceIBAN)
        checkPermission(sourceAccount, password)

        var euroAmount = getEurAmount(amount, currency)

        if (hasSufficientFunds(sourceAccount, euroAmount)) {
            sourceAccount.balance -= euroAmount
            accountRepsitory.save(sourceAccount)
            val transaction = Transaction(sourceIBAN, null, euroAmount, System.currentTimeMillis())
            transactionRepository.save(transaction)
        } else {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Funds insufficient")
        }
    }

    // returns balance (in EUR) for an account
    fun getBalance(iban: String, password: String): Double {
        val account = accountRepsitory.findByIban(iban)
        account?.let {
            val user = userRepository.findById(it.userId).get()
            if (user.password != password) {
                throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
                // we should keep track of how many attempts the user makes, so that we can lock the account
            }
            return it.balance
        }
    }

    // checks that there is an account with the given IBAN
    fun checkIBAN(iban: String): Account {
        return accountRepsitory.findByIban(iban) ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "IBAN not found")
    }

    // checks that the user has permission to read/withdraw/transfer from an account
    fun checkPermission(account: Account, password: String) {
        val sourceUser = userRepository.findById(account.userId).get()
        if (sourceUser.password != password) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not authorized")
        }
    }

    // checks that the user has enough money to perform a withdrawal/transfer
    private fun hasSufficientFunds(account: Account, amount: Double): Boolean {
        return account.balance >= amount
    }

    // converts USD/GBP to EUR
    private fun getEurAmount(amount: Double, currency: String?): Double {
        var euroAmount = amount
        currency?.let {
            try {
                euroAmount = amount * CURRENCIES.valueOf(currency.toUpperCase()).exchange
            } catch (e: Exception) {
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Currency not supported")
            }
        }
        return euroAmount
    }


}