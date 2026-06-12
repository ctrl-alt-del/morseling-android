package com.morseling.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConversionRepository @Inject constructor(
    private val dao: ConversionHistoryDao
) {
    val allEntries: Flow<List<ConversionHistoryEntity>> = dao.getAll()

    suspend fun saveConversion(textInput: String, morseOutput: String) {
        val existing = dao.findByTextInput(textInput)
        if (existing != null) {
            dao.updateTimestamp(existing.id, System.currentTimeMillis())
        } else {
            dao.insert(
                ConversionHistoryEntity(
                    textInput = textInput,
                    morseOutput = morseOutput,
                )
            )
        }
    }

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun deleteAll() = dao.deleteAll()

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) =
        dao.updateFavorite(id, isFavorite)
}
