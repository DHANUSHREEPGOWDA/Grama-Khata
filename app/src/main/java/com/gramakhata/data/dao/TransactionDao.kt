package com.gramakhata.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gramakhata.data.entity.Transaction

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    fun getTransactionsForCustomer(customerId: Int): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): LiveData<List<Transaction>>

    // Net balance: positive = customer owes us, negative = we owe customer
    @Query("""
        SELECT COALESCE(SUM(CASE WHEN type = 'DEBIT' THEN amount ELSE -amount END), 0.0)
        FROM transactions WHERE customerId = :customerId
    """)
    fun getNetBalanceForCustomer(customerId: Int): LiveData<Double>

    // Total outstanding across all customers
    @Query("""
        SELECT COALESCE(SUM(CASE WHEN type = 'DEBIT' THEN amount ELSE -amount END), 0.0)
        FROM transactions
    """)
    fun getTotalOutstanding(): LiveData<Double>

    // Due dashboard - customers sorted by highest amount owed
    @Query("""
        SELECT customerId, 
               COALESCE(SUM(CASE WHEN type = 'DEBIT' THEN amount ELSE -amount END), 0.0) as netBalance
        FROM transactions
        GROUP BY customerId
        HAVING netBalance > 0
        ORDER BY netBalance DESC
    """)
    fun getCustomersDueSorted(): LiveData<List<CustomerBalance>>

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY timestamp DESC")
    suspend fun getTransactionsForCustomerOnce(customerId: Int): List<Transaction>
}

data class CustomerBalance(
    val customerId: Int,
    val netBalance: Double
)
