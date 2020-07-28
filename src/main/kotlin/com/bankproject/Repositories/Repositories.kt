package com.bankproject.Repositories

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long>

interface AccountRepository : CrudRepository<Account, Long> {
    fun findByIban(iban: String): Account?
}

interface TransactionRepository : CrudRepository<Transaction, Long> {

    /*
        Search regardless of date
     */

    @Query("SELECT t FROM Transaction t WHERE t.sourceIBAN=:iban or t.targetIBAN=:iban")
    fun findAllTransactionsForIBAN(iban: String): Iterable<Transaction>

    @Query("SELECT t FROM Transaction t WHERE t.targetIBAN=:iban")
    fun findAllDepositsForIBAN(iban: String): Iterable<Transaction>

    @Query("SELECT t FROM Transaction t WHERE t.sourceIBAN=:iban")
    fun findAllWithdrawalsForIBAN(iban: String): Iterable<Transaction>

    /*
        Search after a specific date
     */

    @Query("SELECT t FROM Transaction t WHERE (t.sourceIBAN=:iban or t.targetIBAN=:iban) and t.timestamp>=:date")
    fun findTransactionsAfterDate(iban: String, date: Long): List<Transaction>

    @Query("SELECT t FROM Transaction t WHERE t.targetIBAN=:iban and t.timestamp>=:date")
    fun findDepositsAfterDate(iban: String, date: Long): Iterable<Transaction>

    @Query("SELECT t FROM Transaction t WHERE t.sourceIBAN=:iban and t.timestamp>=:date")
    fun findWithdrawalsAfterDate(iban: String, date: Long): Iterable<Transaction>

    /*
        Search before a specific date
     */

    @Query("SELECT t FROM Transaction t WHERE (t.sourceIBAN=:iban or t.targetIBAN=:iban) and t.timestamp<=:date")
    fun findTransactionsBeforeDate(iban: String, date: Long): List<Transaction>

    @Query("SELECT t FROM Transaction t WHERE t.targetIBAN=:iban and t.timestamp<=:date")
    fun findDepositsBeforeDate(iban: String, date: Long): List<Transaction>

    @Query("SELECT t FROM Transaction t WHERE t.sourceIBAN=:iban and t.timestamp<=:date")
    fun findWithdrawalsBeforeDate(iban: String, date: Long): List<Transaction>

    /*
        Search between two specific dates
     */

    @Query("SELECT t FROM Transaction t WHERE (t.sourceIBAN=:iban or t.targetIBAN=:iban) and t.timestamp>=:dateStart and t.timestamp<=:dateEnd")
    fun findTransactionsBetweenDates(iban: String, dateStart: Long, dateEnd: Long): List<Transaction>

    @Query("SELECT t FROM Transaction t WHERE t.targetIBAN=:iban and t.timestamp>=:dateStart and t.timestamp<=:dateEnd")
    fun findDepositsBetweenDates(iban: String, dateStart: Long, dateEnd: Long): List<Transaction>

    @Query("SELECT t FROM Transaction t WHERE t.sourceIBAN=:iban and t.timestamp>=:dateStart and t.timestamp<=:dateEnd")
    fun findWithdrawalsBetweenDates(iban: String, dateStart: Long, dateEnd: Long): List<Transaction>

}

