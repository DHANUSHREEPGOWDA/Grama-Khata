package com.gramakhata.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gramakhata.data.dao.CustomerDao
import com.gramakhata.data.dao.TransactionDao
import com.gramakhata.data.entity.Customer
import com.gramakhata.data.entity.Transaction
import com.gramakhata.utils.Converters

@Database(
    entities = [Customer::class, Transaction::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GramaKhataDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: GramaKhataDatabase? = null

        fun getDatabase(context: Context): GramaKhataDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GramaKhataDatabase::class.java,
                    "grama_khata_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
