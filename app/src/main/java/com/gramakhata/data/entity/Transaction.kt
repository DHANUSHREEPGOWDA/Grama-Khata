package com.gramakhata.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [ForeignKey(
        entity = Customer::class,
        parentColumns = ["id"],
        childColumns = ["customerId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val customerId: Int,
    val amount: Double,
    val type: TransactionType, // CREDIT = customer gave money, DEBIT = customer owes
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

enum class TransactionType {
    CREDIT,  // Customer paid (tegedukolluvudu)
    DEBIT    // Customer took goods (koduvudu)
}
