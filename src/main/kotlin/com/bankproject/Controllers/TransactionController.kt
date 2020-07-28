package com.bankproject.Controllers

import com.bankproject.Repositories.Transaction
import com.bankproject.Services.TransactionService
import com.bankproject.Services.Type
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/transactions")
class TransactionController(@Autowired val transactionService: TransactionService) {

    @GetMapping("/getAll")
    fun getAll(@RequestParam iban: String,
               @RequestParam password: String,
               @RequestParam depositsOnly: Boolean?,
               @RequestParam withdrawalsOnly: Boolean?): Iterable<Transaction> {

        val type = getType(depositsOnly, withdrawalsOnly)
        return transactionService.getAll(iban, password, type)
    }

    @GetMapping("/getAllByDateAndType")
    fun getAllBetween(@RequestParam iban: String,
                      @RequestParam timestampStart: Long?,
                      @RequestParam timestampEnd: Long?,
                      @RequestParam password: String,
                      @RequestParam depositsOnly: Boolean?,
                      @RequestParam withdrawalsOnly: Boolean?): Iterable<Transaction> {
        if (timestampStart == null && timestampEnd == null) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide at least one date")
        }

        val type = getType(depositsOnly, withdrawalsOnly)

        return when {
            timestampStart == null -> transactionService.getAllBefore(iban, timestampEnd!!, password, type)
            timestampEnd == null -> transactionService.getAllAfter(iban, timestampStart, password, type)
            else -> transactionService.getAllBetween(iban, timestampStart, timestampEnd, password, type)
        }
    }

    private fun getType(depositsOnly: Boolean?, withdrawalsOnly: Boolean?): Type? {
        if (depositsOnly == true && withdrawalsOnly == true) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Either only deposits or only withdraways. Remove parameters if you want both")
        }

        var type: Type? = null
        if (depositsOnly == true) {
            type = Type.DEPOSIT
        } else if (withdrawalsOnly == true) {
            type = Type.WITHDRAWAL
        }
        return type
    }

}