package com.gramakhata.data.repository

import androidx.lifecycle.LiveData
import com.gramakhata.data.dao.CustomerBalance
import com.gramakhata.data.dao.CustomerDao
import com.gramakhata.data.dao.TransactionDao
import com.gramakhata.data.entity.Customer
import com.gramakhata.data.entity.Transaction

class GramaKhataRepository(
    private val customerDao: CustomerDao,
    private val transactionDao: TransactionDao
) {
    // ── Customers ──────────────────────────────────────────────
    val allCustomers: LiveData<List<Customer>> = customerDao.getAllCustomers()

    suspend fun insertCustomer(customer: Customer): Long =
        customerDao.insertCustomer(customer)

    suspend fun updateCustomer(customer: Customer) =
        customerDao.updateCustomer(customer)

    suspend fun deleteCustomer(customer: Customer) =
        customerDao.deleteCustomer(customer)

    suspend fun getCustomerById(id: Int): Customer? =
        customerDao.getCustomerById(id)

    fun searchCustomers(query: String): LiveData<List<Customer>> =
        customerDao.searchCustomers(query)

    // ── Transactions ───────────────────────────────────────────
    suspend fun insertTransaction(transaction: Transaction): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun deleteTransaction(transaction: Transaction) =
        transactionDao.deleteTransaction(transaction)

    fun getTransactionsForCustomer(customerId: Int): LiveData<List<Transaction>> =
        transactionDao.getTransactionsForCustomer(customerId)

    fun getNetBalanceForCustomer(customerId: Int): LiveData<Double> =
        transactionDao.getNetBalanceForCustomer(customerId)

    fun getTotalOutstanding(): LiveData<Double> =
        transactionDao.getTotalOutstanding()

    fun getCustomersDueSorted(): LiveData<List<CustomerBalance>> =
        transactionDao.getCustomersDueSorted()

    suspend fun getTransactionsForCustomerOnce(customerId: Int): List<Transaction> =
        transactionDao.getTransactionsForCustomerOnce(customerId)
}
