package com.gramakhata.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val phone: String,
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
