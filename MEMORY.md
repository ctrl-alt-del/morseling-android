# MEMORY — Accumulated Project Knowledge

## 🧠 Tech Gotchas
<!-- Tagged: #api #ui #build — AI searches by tag -->
<!-- ⚡ = broke in production, non-negotiable guardrail -->

- ⚡ **runBlocking for locale**: `MainActivity.applySavedLocale()` uses `runBlocking` on the main thread to read DataStore synchronously before `setContent`. Acceptable ONLY in `onCreate` before UI setup. **Never use `runBlocking` anywhere else.** `app/src/main/java/com/morseling/MainActivity.kt:33-43` #locales #datastore
- ⚡ **CancellationException must be caught separately**: In `MorseConverterViewModel.play()`, `CancellationException` is caught BEFORE `Exception`. If caught as `Exception`, pressing Stop would show a spurious "Playback failed" error. `app/src/main/java/com/morseling/viewmodel/MorseConverterViewModel.kt:play()` #coroutines #error
- ⚡ **Job guard in finally block**: `if (playJob === coroutineContext[Job])` before resetting state in `finally`. Without this, a stale coroutine's finally could reset state after a new playback started. `app/src/main/java/com/morseling/viewmodel/MorseConverterViewModel.kt:finally` #coroutines #state
- **Flash availability checked at construction, not at play**: `FlashlightMorsePlayer.isAvailable()` probes CameraManager once. If camera disconnects later, playback fails silently. `app/src/main/java/com/morseling/audio/FlashlightMorsePlayer.kt:18` #camera #flash
- **WPM clamped in two places**: `coerceIn(5, 40)` in both `setWpm()` and settings `collect {}`. Both must be updated if range changes. `app/src/main/java/com/morseling/viewmodel/MorseConverterViewModel.kt` #wpm #settings
- **ViewModel error strings hardcoded**: Error messages are English strings in ViewModel code (no Context for `stringResource()`). Cannot be localized. `res/values/strings.xml` has matching strings but ViewModel doesn't use them. #i18n #error
- **License URLs not clickable**: `LicensesScreen.kt` displays URLs as primary-colored text but uses plain `Text` without `ClickableText` or `UriHandler`. Users can read but not open URLs. `app/src/main/java/com/morseling/ui/LicensesScreen.kt` #ui #accessibility
- **Settings default_mode stored as raw string**: `"audio"`/`"flash"` in DataStore. No enum validation — a typo would silently break. DataStore only supports primitives so enum isn't directly usable. `app/src/main/java/com/morseling/data/SettingsRepository.kt` #datastore #type-safety
- **Room exportSchema = false**: No migration history available. Future schema changes will need manual migration handling. `app/src/main/java/com/morseling/data/ConversionDatabase.kt` #room #database
- **Single-delete no confirmation**: Tapping trash on a history entry immediately deletes it. Only "clear all" has an AlertDialog. `app/src/main/java/com/morseling/ui/HistoryScreen.kt` #ui #ux
- **Presets are static**: 16 preset chips are hardcoded in `MainScreen.kt`. No user customization possible. `app/src/main/java/com/morseling/ui/MainScreen.kt` #ui #presets

## 🔧 Patterns That Worked
<!-- Reusable patterns discovered across features -->

- **StateFlow-based UDF**: `private val _uiState = MutableStateFlow(...)` + `val uiState: StateFlow<...> = _uiState.asStateFlow()`. Updates via `.update { it.copy(...) }`. Screens observe via `collectAsStateWithLifecycle()`. Used in all 3 ViewModels. #stateflow #udf #mvvm
- **Fake test doubles (not mocks)**: Tests create inline fake classes (`FakeMorsePlayer`, `FakeDao`, `FakeSettingsProvider`) implementing interfaces. No Mockito/MockK dependency. See `MorseConverterViewModelTest.kt`. #testing #fakes
- **Flow-based playback**: `MorsePlayer.play(input, wpm): Flow<Int>` emits character index on each symbol. ViewModel collects to drive real-time character highlighting in UI. Enables clean separation: player doesn't know about UI; UI doesn't know about timing. #playback #flow
- **Reactive settings**: `MorseConverterViewModel.init {}` collects `SettingsProvider.settings: Flow<AppSettings>`. Any DataStore change propagates to UI immediately — no manual refresh. #datastore #reactive
- **Deduplication on save**: `ConversionRepository.saveConversion()` checks `findByTextInput()` → if found, `updateTimestamp()` reorders to top; if not, `insert()`. Same text never creates duplicates. #room #dedup
- **Snackbar via LaunchedEffect**: `LaunchedEffect(uiState.error)` triggers snackbar on error change, immediately clears error to prevent re-show on recomposition. #compose #snackbar
- **Hilt @Named qualifiers**: `@Named("audio")` and `@Named("flash")` distinguish two `MorsePlayer` implementations without requiring separate interfaces. #hilt #di
- **Composable private sub-components**: `HighlightedMorseText` is a private composable called within `MainScreen`. Keeps screen composables readable by extracting specialized rendering. #compose #ui

## 📐 Architecture Decisions
<!-- ADRs made during spec-driven development -->

- **ADR-001**: Specs in `specs/` separate from permanent docs — spec files are per-feature artifacts; permanent docs stay in root.
- **ADR-002** [INFERRED]: **MVVM + UDF over MVI** — Single `PlaybackUiState` data class with `StateFlow` rather than sealed class events + reducer. Simpler for this app's scope (single screen with clear states). Would reconsider if states become complex or unpredictable.
- **ADR-003** [INFERRED]: **Fake doubles over Mockito/MockK** — Hand-written fakes give full control over test behavior, avoid reflection overhead, and make test setup explicit. Cost: more boilerplate per test. Acceptable at this scale.
- **ADR-004** [INFERRED]: **Single Activity, Compose-only navigation** — No fragments. All screens are `@Composable` functions managed by `NavHost`. Simpler lifecycle, no fragment back-stack complexity.
- **ADR-005** [INFERRED]: **TinyPinyin over pinyin4j** — TinyPinyin has a smaller footprint (~200KB), is actively maintained (promeg/tinypinyin), and supports multi-character segmentation. Trade-off: fewer features than pinyin4j.
- **ADR-006** [INFERRED]: **Room over SQLDelight** — Room is the official Android persistence library with first-class Compose/Hilt/Flow integration. SQLDelight would offer multiplatform but that's not needed.
- **ADR-007** [INFERRED]: **DataStore over SharedPreferences** — DataStore is the modern replacement: async, coroutine-based, type-safe, and supports reactive Flow collection. SharedPreferences would block the main thread.

## 📂 Code Ownership Map

| Directory | Owner | Coverage |
|-----------|-------|----------|
| `app/` | Jiyang Liu | 100% |
| `gradle/` | Jiyang Liu | 100% |
| `.github/workflows/` | Jiyang Liu | 100% |
| `.` (root config) | Jiyang Liu | 100% |

## 🐛 Common Bugs Fixed

- _(None recorded — clean git history with 0 reverted commits. Add entries here as bugs are discovered and fixed.)_

## 🧠 AI Workflow Rule

Before writing any spec, read in order:
1. `AGENTS.md` or `CLAUDE.md` — project conventions
2. `specs/SDD.md` — SDD workflow
3. `knowledge/index.md` — if `knowledge/` directory exists, read the index for
   architecture, data models, APIs, patterns, and gotchas. Traverse any domain
   files relevant to the feature.
4. `MEMORY.md` — search for relevant #tags
5. `specs/index.md` — check for feature file conflicts

If the project has no `knowledge/` directory but has existing source code, run
`codebase-to-sdd-knowledge` first to generate it.

After shipping a feature:
1. Write `takeaways.md` in the feature folder
2. Curate findings into `MEMORY.md` (tagged, ⚡ for critical)
3. Update code ownership map
