# Internal Interfaces
**Source files**: `app/src/main/java/com/morseling/audio/MorsePlayer.kt`, `app/src/main/java/com/morseling/data/SettingsProvider.kt`, `app/src/main/java/com/morseling/data/ConversionHistoryDao.kt`
**Depends on**: [[knowledge/data/entities]]
**Depended on by**: [[knowledge/architecture/data-flow]], [[knowledge/patterns/common]]
**See also**: [[knowledge/conventions/testing]]
**Tags**: #api #interface #contract

## MorsePlayer

```kotlin
interface MorsePlayer {
    fun play(input: String, wpm: Int): Flow<Int>
    fun isAvailable(): Boolean
    fun release()
}
```

**Implementations**:
- `MorseCodeConverter` (`@Named("audio")`) — uses `ToneGenerator`
- `FlashlightMorsePlayer` (`@Named("flash")`) — uses Camera2 API

**Contract**:
- `play()` emits character index on each symbol played. Emits `input.indices` range.
- `isAvailable()` returns `true` for audio (always), device-dependent for flash.
- `release()` cleans up resources (`ToneGenerator.release()` for audio, no-op for flash).
- Input string must be sanitized ('.'/'−'/' '/') before calling play.

## SettingsProvider

```kotlin
interface SettingsProvider {
    val settings: Flow<AppSettings>
    suspend fun setLanguage(language: String)
    suspend fun setDefaultMode(mode: String)
    suspend fun setDefaultWpm(wpm: Int)
}
```

**Implementations**:
- `SettingsRepository` — Jetpack DataStore-backed (production)
- `FakeSettingsProvider` — in-memory for tests

**Contract**:
- `settings` emits current `AppSettings` on every change.
- Setters write to DataStore and return after persist completes.
- Values not clamped here — consumer (ViewModel) handles clamping.

## ConversionHistoryDao

```kotlin
@Dao
interface ConversionHistoryDao {
    @Query("SELECT * FROM conversion_history ORDER BY timestamp DESC")
    fun getAll(): Flow<List<ConversionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ConversionHistoryEntity)

    @Query("DELETE FROM conversion_history WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM conversion_history")
    suspend fun deleteAll()

    @Query("UPDATE conversion_history SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)

    @Query("SELECT * FROM conversion_history WHERE textInput = :textInput LIMIT 1")
    suspend fun findByTextInput(textInput: String): ConversionHistoryEntity?

    @Query("UPDATE conversion_history SET timestamp = :timestamp WHERE id = :id")
    suspend fun updateTimestamp(id: Int, timestamp: Long)
}
```

**Note**: Room DAO is generated at compile time via KSP. This is the annotated interface, not the implementation.
