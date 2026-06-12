package com.morseling.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversion_history")
data class ConversionHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val textInput: String,
    val morseOutput: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
)
