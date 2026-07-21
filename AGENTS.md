# AGENTS.md

## Project

Morseling — a Kotlin/Jetpack Compose Android app that converts text to Morse code and plays it via audio beeps or camera flashlight. Supports Chinese characters through Pinyin transliteration.

## Build & Test

```bash
# Debug build
gradle assembleDebug

# Unit tests (JVM, no emulator needed)
gradle test

# Full build + unit tests + instrumented test APK
gradle assembleDebug assembleAndroidTest test

# Instrumented tests (requires emulator/device)
gradle connectedAndroidTest
```

**System Gradle** 9.5.1 is used.

## Architecture

**Pattern:** MVVM with Unidirectional Data Flow (UDF)
- ViewModels expose `StateFlow<PlaybackUiState>` / `StateFlow<List<ConversionHistoryEntity>>`
- Compose screens observe via `collectAsStateWithLifecycle()`
- User actions → ViewModel methods → StateFlow updates → Compose recomposition

**DI:** Hilt (`@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`, `@Inject constructor`)

**Navigation:** Compose Navigation (`MorselingNavGraph.kt`)
- `main` → `history` → back
- `main` → `settings` → `licenses` → back

## Package Map

```
com.morseling/
├── audio/              MorsePlayer interface, MorseCodeConverter (tone), FlashlightMorsePlayer
├── data/               Room entities/DAO/database, ConversionRepository, DataStore SettingsRepository
├── di/                 Hilt modules: PlayerModule, DatabaseModule, SettingsModule
├── model/              PlaybackUiState, PlaybackMode, AppSettings
├── navigation/         MorselingNavGraph
├── ui/                 MainScreen, HistoryScreen, SettingsScreen, LicensesScreen
│   └── theme/          Color, Shape, Theme, Typography
├── util/               MorseTranslator (text→Morse, Pinyin)
├── viewmodel/          MorseConverterViewModel, HistoryViewModel, SettingsViewModel
├── MainActivity.kt     Entry point, locale application
└── MorselingApplication.kt  @HiltAndroidApp
```

## Key Patterns

**Play state**
```kotlin
private val _uiState = MutableStateFlow(PlaybackUiState())
val uiState: StateFlow<PlaybackUiState> = _uiState.asStateFlow()
fun play() { viewModelScope.launch { ... } }
```

**Error handling**
```kotlin
try { ... }
catch (e: CancellationException) { /* normal stop */ }
catch (e: Exception) { _uiState.update { it.copy(error = "...") } }
finally { if (playJob === coroutineContext[Job]) { _uiState.update { ... } } }
```

**Flow-based playback** — `MorsePlayer.play(input, wpm): Flow<Int>` emits character index on each symbol. ViewModel collects it to drive character highlighting.

**String resources** — All UI text via `stringResource()`. English: `res/values/strings.xml`, Chinese: `res/values-zh-rCN/strings.xml`. ViewModel error messages are hardcoded English (no Context access).

**Settings persistence** — `SettingsProvider` interface backed by Jetpack DataStore. `MorseConverterViewModel.init {}` collects settings reactively. Language switch uses `AppCompatDelegate.setApplicationLocales()` + `activity.recreate()`.

**History persistence** — Room: `ConversionHistoryEntity` → `ConversionHistoryDao` → `ConversionRepository`. Same text triggers timestamp update (reorder to top).

## Conventions

- Kotlin, 4-space indent, `kotlin.code.style=official`
- No wildcard imports
- Material 3 color roles only — no hardcoded colors
- Icons from Material Icons Extended
- Private composables for reusable sub-components (e.g., `HighlightedMorseText`)
- ViewModel constructor injection via `@Inject` + `@Named` qualifiers where needed
- DataStore keys defined as `val KEY_X = stringPreferencesKey("x")` in companion object

## Triggering Feature Development

When the user describes a new feature (creates, builds, adds, wants a new screen,
etc.), follow the spec-driven development workflow in `specs/SDD.md`. Read
`MEMORY.md` before writing any spec to avoid repeating known bugs. The workflow:
1. Generate mockups if needed (`canvas-design` + `theme-factory`)
2. Co-author spec + plan (`doc-coauthoring`)
3. Write test plan and tasks
4. Implement one commit per task
5. Write takeaways → promote to `MEMORY.md`
