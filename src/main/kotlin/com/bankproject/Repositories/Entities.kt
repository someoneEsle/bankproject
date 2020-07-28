package com.bankproject.Repositories

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class User(
		var firstname: String,
		var lastname: String,
		var address: String,
		var zipCode: Int,
		var countryCode: String,
		var password: String,
		@Id @GeneratedValue val userId: Long = 0)

// the balance is always in eur
@Entity
class Account(
		val userId: Long,
		var balance: Double,
		@Id val iban: String)

/*
sourceIBAN: the account where the money is being withdrawn
targetIBAN: the account where the money is being deposited
if sourceIBAN is null, the transaction is a simple deposit (e.g. the user
goes to the bank and deposits money in person)
if the targetIBAN is null, the transaction is a withdrawal (e.g. ATM)
 */
@Entity
class Transaction(
		val sourceIBAN: String?,
		val targetIBAN: String?,
		val amount: Double,
		val timestamp: Long,
		@Id @GeneratedValue val transactionId: Long = 0)
