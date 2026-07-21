# Data Entities
**Source files**: `app/src/main/java/com/morseling/data/ConversionHistoryEntity.kt`, `app/src/main/java/com/morseling/data/AppSettings.kt`, `app/src/main/java/com/morseling/model/PlaybackUiState.kt`, `app/src/main/java/com/morseling/model/PlaybackMode.kt`
**Depends on**: none
**Depended on by**: [[knowledge/architecture/data-flow]], [[knowledge/conventions/testing]]
**See also**: [[knowledge/apis/interfaces]]
**Tags**: #data #room #datastore #entities

## Room Entity: ConversionHistoryEntity

**Table**: `conversion_history`
**Database**: `morseling.db` (version 1)

| Field | Type | Purpose |
|-------|------|---------|
| `id` | `Int` (PK, auto-generate) | Primary key |
| `textInput` | `String` | User-entered text before conversion |
| `morseOutput` | `String` | Resulting Morse code string |
| `timestamp` | `Long` | `System.currentTimeMillis()` at save time |
| `isFavorite` | `Boolean` | User-toggled favorite state |

**DAO operations**:
- `getAll(): Flow<List<ConversionHistoryEntity>>` — ordered by timestamp DESC
- `insert(entity)` — `OnConflictStrategy.REPLACE`
- `deleteById(id)`
- `deleteAll()`
- `updateFavorite(id, isFavorite)`
- `findByTextInput(textInput): ConversionHistoryEntity?` — for dedup
- `updateTimestamp(id, timestamp)` — for dedup reorder

**Dedup logic**: When saving, `findByTextInput()` checks if same text exists. If yes, `updateTimestamp()` moves it to top. If no, `insert()` creates new row.

## DataStore Model: AppSettings

**Store**: `settings.preferences_pb` (Jetpack DataStore)

| Key | Type | Default | Purpose |
|-----|------|---------|---------|
| `language` | `String` | `"system"` | UI language: "system", "en", "zh-rCN" |
| `default_mode` | `String` | `"audio"` | Default playback mode: "audio", "flash" |
| `default_wpm` | `Int` | `20` | Default playback speed (5-40) |

## UI State Model: PlaybackUiState

| Field | Type | Default | Purpose |
|-------|------|---------|---------|
| `textInput` | `String` | `"Hello World"` | User text input |
| `morseOutput` | `String` | `""` | Converted Morse code |
| `playbackMode` | `PlaybackMode` | `AUDIO` | Active player mode |
| `isFlashAvailable` | `Boolean` | `false` | Camera flash hardware status |
| `isPlaying` | `Boolean` | `false` | Whether playback is active |
| `playingCharIndex` | `Int?` | `null` | Current highlighted char index during playback |
| `wpm` | `Int` | `20` | Current playback speed |
| `error` | `String?` | `null` | Current error message (null = no error) |

## Enum: PlaybackMode

| Value | Purpose |
|-------|---------|
| `AUDIO` | ToneGenerator-based audio beeps |
| `FLASH` | Camera2-based flashlight blinking |
