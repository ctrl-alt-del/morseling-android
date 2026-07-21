# Components
**Source files**: `app/src/main/java/com/morseling/audio/`, `app/src/main/java/com/morseling/data/`, `app/src/main/java/com/morseling/di/`, `app/src/main/java/com/morseling/model/`, `app/src/main/java/com/morseling/navigation/`, `app/src/main/java/com/morseling/ui/`, `app/src/main/java/com/morseling/util/`, `app/src/main/java/com/morseling/viewmodel/`
**Depends on**: [[knowledge/architecture/overview]]
**Depended on by**: [[knowledge/data/entities]], [[knowledge/apis/interfaces]]
**See also**: [[knowledge/conventions/file-organization]]
**Tags**: #architecture #modules

## Package: `audio/`

**Role**: Morse code audio + flashlight playback implementations.

| File | Purpose |
|------|---------|
| `MorsePlayer.kt` | Interface: `play(input: String, wpm: Int): Flow<Int>` — emits character index per symbol played |
| `MorseCodeConverter.kt` | Audio player: uses `ToneGenerator` for beeps. Singleton. Dots/dashes/spaces at computed durations. |
| `FlashlightMorsePlayer.kt` | Flashlight player: uses Camera2 API (`CameraManager.setTorchMode()`). Checks flash availability at construction. |

## Package: `data/`

**Role**: Room database (history) + DataStore preferences (settings) + repository layer.

| File | Purpose |
|------|---------|
| `ConversionHistoryEntity.kt` | Room entity: `id`, `textInput`, `morseOutput`, `timestamp`, `isFavorite` |
| `ConversionHistoryDao.kt` | Room DAO: getAll, insert(REPLACE), deleteById, deleteAll, updateFavorite, findByTextInput, updateTimestamp |
| `ConversionDatabase.kt` | Room DB singleton: `morseling.db`, version 1. Provides DAO. |
| `ConversionRepository.kt` | Repository: save with dedup (findByTextInput → updateTimestamp or insert), delete wrappers |
| `SettingsProvider.kt` | Interface: `val settings: Flow<AppSettings>`, suspend setLanguage/setDefaultMode/setDefaultWpm |
| `SettingsRepository.kt` | DataStore impl: `settings.preferences_pb`, three keys (language, default_mode, default_wpm) |
| `AppSettings.kt` | Data class: language, defaultMode, defaultWpm |

## Package: `di/`

**Role**: Hilt dependency injection modules.

| File | Purpose |
|------|---------|
| `PlayerModule.kt` | Provides `@Named("audio")` MorseCodeConverter, `@Named("flash")` FlashlightMorsePlayer. Both `@Singleton`. |
| `DatabaseModule.kt` | Provides Room `ConversionDatabase` + `ConversionHistoryDao`. Both `@Singleton`. |
| `SettingsModule.kt` | Provides DataStore instance + binds SettingsProvider to SettingsRepository. |

## Package: `model/`

**Role**: Domain models shared across packages.

| File | Purpose |
|------|---------|
| `PlaybackUiState.kt` | UI state data class: textInput, morseOutput, playbackMode, isFlashAvailable, isPlaying, playingCharIndex, wpm, error. Companion `DEFAULT_TEXT = "Hello World"`. |
| `PlaybackMode.kt` | Enum: `AUDIO`, `FLASH` |

## Package: `navigation/`

**Role**: Compose Navigation graph.

| File | Purpose |
|------|---------|
| `MorselingNavGraph.kt` | `NavHost` with 4 routes: `main` (start), `history`, `settings`, `licenses`. `mainViewModel` scoped to NavHost. History/Settings ViewModels per-route. History→main callback via `loadFromHistory()`. |

## Package: `ui/`

**Role**: Composable screens + Material 3 theme.

| File | Purpose |
|------|---------|
| `MainScreen.kt` | Text input, Convert button, Morse output with `HighlightedMorseText`, 16 preset chips, play/stop, Snackbar host |
| `HistoryScreen.kt` | `LazyColumn` of `ElevatedCard` entries: text, morse, timestamp, favorite star, delete button. Clear-all with `AlertDialog`. |
| `SettingsScreen.kt` | Default mode FilterChips, WPM Slider, language RadioButton dialog, version display, licenses nav |
| `LicensesScreen.kt` | Scrollable list of 11 library attributions (name, license, URL) |
| `theme/Color.kt` | 65 color constants for light + dark custom schemes |
| `theme/Shape.kt` | Corner radii: extraSmall=4, small=8, medium=12, large=16, extraLarge=24 |
| `theme/Theme.kt` | Dynamic color (Android 12+) with fallback to custom schemes. Dark mode via `isSystemInDarkTheme()`. |
| `theme/Type.kt` | Typography: bodyLarge=Monospace (16sp, 2sp letter spacing for Morse), titleMedium=14sp SemiBold, headlineLarge=28sp Bold |

## Package: `util/`

**Role**: Text-to-Morse translation, including Pinyin for Chinese.

| File | Purpose |
|------|---------|
| `MorseTranslator.kt` | Singleton object. Maps A-Z/0-9 to Morse. `textToMorse()`: uppercase → per-char lookup. `toPinyinIfCjk()`: detects CJK via `Character.UnicodeScript.HAN` → calls TinyPinyin. `hasConvertibleChars()`/`unconvertibleChars()`. Uses `text.codePoints()` for surrogate pair handling. |

## Package: `viewmodel/`

**Role**: MVVM ViewModels with StateFlow exposed.

| File | Purpose |
|------|---------|
| `MorseConverterViewModel.kt` | Core ViewModel: convertText, play/stop, setWpm, setPlaybackMode, loadFromHistory, clearError. Collects settings reactively in `init`. `onCleared()` cancels job + releases players. |
| `HistoryViewModel.kt` | Collects Room Flow of history entries. toggleFavorite, deleteById, deleteAll. |
| `SettingsViewModel.kt` | Wraps SettingsProvider: setLanguage, setDefaultMode, setDefaultWpm. `setLanguageAndAwait()` suspends until DataStore write completes. |
