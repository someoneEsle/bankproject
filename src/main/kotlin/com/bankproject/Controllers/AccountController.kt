package com.bankproject.Controllers

import com.bankproject.Services.AccountService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/account")
class AccountController(@Autowired private val accountService: AccountService) {


    @PostMapping("/transfer")
    fun transfer(@RequestParam sourceIBAN: String, @RequestParam targetIBAN: String, @RequestParam amount: Double, @RequestParam password: String, @RequestParam currency: String?) {
        return accountService.transfer(sourceIBAN, targetIBAN, amount, password, currency)
    }

    @PostMapping("/deposit")
    fun deposit(@RequestParam targetIBAN: String, @RequestParam amount: Double, @RequestParam currency: String?) {
        accountService.deposit(targetIBAN, amount, currency)
    }

    @PostMapping("/withdraw")
    fun withdraw(@RequestParam sourceIBAN: String, @RequestParam amount: Double, @RequestParam password: String, @RequestParam currency: String?) {
        accountService.withdraw(sourceIBAN, amount, password, currency)
    }

    @GetMapping("/getBalance")
    fun getBalance(@RequestParam iban: String, @RequestParam password: String): Double {
        return accountService.getBalance(iban, password)
    }
}

