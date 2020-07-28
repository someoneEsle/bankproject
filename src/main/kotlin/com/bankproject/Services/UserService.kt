package com.bankproject.Services

import com.bankproject.Repositories.Account
import com.bankproject.Repositories.AccountRepository
import com.bankproject.Repositories.User
import com.bankproject.Repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import kotlin.random.Random

@Service
class UserService(@Autowired val userRepository: UserRepository, @Autowired val accountRepository: AccountRepository) {

    private enum class SupportedCountries { DE, AT, CH}

    fun registerUser(user: User): String {
        // make a few checks for password, countryCode, etc.
        if (user.address.isNullOrEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Address cannot be empty")
        }
        if (user.countryCode.isNullOrEmpty() || !SupportedCountries.values().any { it.name == user.countryCode.toUpperCase() }) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Country not supported")
        }
        if (user.firstname.isNullOrEmpty() || user.lastname.isNullOrEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "First name and last name cannot be empty")
        }
        if (user.password.isNullOrEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password cannot be empty")
        }

        val savedUser = userRepository.save(user)
        val iban = generateIban(user.countryCode)
        val account = Account(savedUser.userId, 0.0, iban)
        accountRepository.save(account)
        return iban

    }

    private fun generateIban(countryCode: String): String {
        // I know that technically IBANs are not completely random, but I don't have time to look into the IBAN standard
        // I should also check that the IBAN being generate is unique. For the sake of simplicity (and because the
        // probability of collision for this example is incredibly low) I will skip it
        val firstPart = 1000000000 + Random.nextInt(1000000000)
        val secondPart = Random.nextInt(1000000000)
        return countryCode.toUpperCase() + firstPart.toString() + secondPart.toString()
    }
}