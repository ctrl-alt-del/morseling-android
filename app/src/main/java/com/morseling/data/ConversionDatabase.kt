package com.morseling.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ConversionHistoryEntity::class], version = 1, exportSchema = false)
abstract class ConversionDatabase : RoomDatabase() {
    abstract fun conversionHistoryDao(): ConversionHistoryDao
}
