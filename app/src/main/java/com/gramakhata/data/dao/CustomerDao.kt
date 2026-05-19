package com.gramakhata.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gramakhata.data.entity.Customer

@Dao
interface CustomerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): LiveData<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :customerId")
    suspend fun getCustomerById(customerId: Int): Customer?

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): LiveData<List<Customer>>
}
