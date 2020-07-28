package com.bankproject.Services

import com.bankproject.Repositories.Transaction
import com.bankproject.Repositories.TransactionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TransactionService(@Autowired val transactionRepository: TransactionRepository,
                         @Autowired val accountService: AccountService) {


    fun getAll(iban: String, password: String, type: Type?): Iterable<Transaction> {
        checkData(iban, password)
        type?.let {
            return if (it == Type.DEPOSIT) transactionRepository.findAllDepositsForIBAN(iban)
            else transactionRepository.findAllWithdrawalsForIBAN(iban)
        }
        return transactionRepository.findAllTransactionsForIBAN(iban)
    }

    fun getAllAfter(iban: String, timestamp: Long, password: String, type: Type?): Iterable<Transaction> {
        checkData(iban, password)
        type?.let {
            return if (it == Type.DEPOSIT) transactionRepository.findDepositsAfterDate(iban, timestamp)
            else transactionRepository.findWithdrawalsAfterDate(iban, timestamp)
        }
        return transactionRepository.findTransactionsAfterDate(iban, timestamp)
    }

    fun getAllBefore(iban: String, timestamp: Long, password: String, type: Type?): Iterable<Transaction> {
        checkData(iban, password)
        type?.let {
            return if (it == Type.DEPOSIT) transactionRepository.findDepositsBeforeDate(iban, timestamp)
            else transactionRepository.findWithdrawalsBeforeDate(iban, timestamp)
        }
        return transactionRepository.findTransactionsBeforeDate(iban, timestamp)
    }

    fun getAllBetween(iban: String, timestampStart: Long, timestampEnd: Long, password: String, type: Type?): Iterable<Transaction> {
        checkData(iban, password)
        type?.let {
            return if (it == Type.DEPOSIT) transactionRepository.findDepositsBetweenDates(iban, timestampStart, timestampEnd)
            else transactionRepository.findWithdrawalsBetweenDates(iban, timestampStart, timestampEnd)
        }
        return transactionRepository.findTransactionsBetweenDates(iban, timestampStart, timestampEnd)
    }

    private fun checkData(iban: String, password: String) {
        val account = accountService.checkIBAN(iban)
        accountService.checkPermission(account, password)
    }


}

enum class Type{DEPOSIT, WITHDRAWAL}