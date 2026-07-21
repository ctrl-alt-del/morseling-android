---
feature_id: "005"
name: "Settings"
status: "✅ Done"
depends_on: ["001", "002"]
touches:
  - "app/src/main/java/com/morseling/data/SettingsProvider.kt"
  - "app/src/main/java/com/morseling/data/SettingsRepository.kt"
  - "app/src/main/java/com/morseling/data/AppSettings.kt"
  - "app/src/main/java/com/morseling/ui/SettingsScreen.kt"
  - "app/src/main/java/com/morseling/viewmodel/SettingsViewModel.kt"
  - "app/src/main/java/com/morseling/viewmodel/MorseConverterViewModel.kt"
  - "app/src/main/java/com/morseling/MainActivity.kt"
  - "app/src/main/java/com/morseling/di/SettingsModule.kt"
  - "app/src/main/res/values/strings.xml"
  - "app/src/main/res/values-zh-rCN/strings.xml"
created: "2026-07-21"
---

# Settings — Implementation Plan

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Jetpack DataStore 1.1.1, AppCompat 1.7.0, Compose Navigation 2.8.5
**Storage**: DataStore Preferences (`settings.preferences_pb`)
**Testing**: JUnit 4 + kotlinx-coroutines-test (unit, with FakeSettingsProvider)
**Target Platform**: Android 26+
**Performance Goals**: DataStore reads < 10ms, settings screen renders < 16ms
**Constraints**: `runBlocking` for startup locale read (acceptable for onCreate), activity recreate for language change
**Scale/Scope**: 3 settings keys, single user

## Constitution Check

- [x] **Article I (Spec-First)**: spec.md written. Retroactive.
- [x] **Article II (Test-First)**: FakeSettingsProvider used in ViewModel tests. Retroactive.
- [x] **Article III (Modularity)**: SettingsProvider interface + DataStore impl in data/; SettingsViewModel + SettingsScreen in their packages.
- [x] **Article IV (Simplicity)**: 5 files (SettingsProvider, SettingsRepository, AppSettings, SettingsScreen, SettingsViewModel) + 2 modified. Boundary case but justified.
- [x] **Article V (Anti-Abstraction)**: SettingsProvider interface has 2 impls (real + FakeSettingsProvider) — interface is justified. DataStore used directly.
- [x] **Article VI (Integration Reality)**: Real DataStore in production, FakeSettingsProvider in tests.
- [x] **Article VII (Observability)**: Settings flow collected reactively. Changes propagate immediately.

## Approach

Settings are persisted via Jetpack DataStore with three keys. `SettingsProvider` interface defines the contract for testability. `SettingsRepository` is the production DataStore implementation. The `MorseConverterViewModel.init {}` block collects settings reactively via Flow, applying mode (with flash fallback) and WPM (with clamping).

Language change is special: it requires `AppCompatDelegate.setApplicationLocales()` + `activity.recreate()`. The ViewModel provides `setLanguageAndAwait()` which suspends until DataStore write completes, then the UI calls recreate. On startup, `MainActivity.applySavedLocale()` reads language from DataStore synchronously via `runBlocking` before `setContent`.

## Data Model

| Entity | Key Attributes | Relationships |
|--------|---------------|---------------|
| AppSettings | language: String, defaultMode: String, defaultWpm: Int | Consumer: MorseConverterViewModel, MainActivity |

**DataStore keys**:
| Key | Type | Default |
|-----|------|---------|
| `language` | stringPreferencesKey | "system" |
| `default_mode` | stringPreferencesKey | "audio" |
| `default_wpm` | intPreferencesKey | 20 |

## API / CLI Contract

```kotlin
interface SettingsProvider {
    val settings: Flow<AppSettings>
    suspend fun setLanguage(language: String)
    suspend fun setDefaultMode(mode: String)
    suspend fun setDefaultWpm(wpm: Int)
}
```

## Alternatives Considered

| Alternative | Pros | Cons | Why Rejected |
|-------------|------|------|--------------|
| SharedPreferences | Familiar, simple | Blocking reads on main thread; no Flow support | DataStore is the modern replacement |
| Room for settings | Unified storage with history | Overkill for 3 key-value pairs | DataStore is purpose-built for preferences |
| LiveData for settings | Lifecycle-aware | Not Flow-native; harder to test without Android | StateFlow + Flow is the project standard |
| Single language string in SharedPreferences | Simpler | Separate storage from other settings | DataStore unifies all preferences |

**Decision**: DataStore (not SharedPreferences) for all settings. SettingsProvider interface for testability. Reactive Flow collection in ViewModel for immediate propagation.

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| `runBlocking` causes ANR on startup | Low | High | Single DataStore read (~5ms). Acceptable ONLY in onCreate before setContent. |
| `activity.recreate()` during playback | Low | Medium | Language change only from Settings screen; main screen is not in foreground during settings interaction. |
| DataStore corruption | Low | High | DataStore uses protobuf — corruption is rare. App would crash on next launch. Future: add default fallback. |
| Flash mode selected but flash unavailable | Medium | Low | ViewModel init checks `flashPlayer.isAvailable()` and falls back to AUDIO. Settings value is not changed — user must manually switch. |

## Dependencies

| Depends On | Status | Blocking? |
|-----------|--------|-----------|
| 001-text-to-morse-conversion | ✅ Done | No |
| 002-morse-playback (flash availability) | ✅ Done | No |
| DataStore 1.1.1 | Ready | No |
| AppCompat 1.7.0 | Ready | No |

## Complexity Tracking

| Article | Violation | Why Needed | Simpler Alternative Rejected Because |
|---------|-----------|------------|--------------------------------------|
| Art. IV | 5 files (at boundary) | SettingsProvider interface + DataStore impl + model + screen + ViewModel = 5. Each has distinct responsibility. | Could merge AppSettings into SettingsProvider, but separation of model from provider is cleaner. |

## Files to Create / Change

| Action | File | Rationale |
|--------|------|-----------|
| Create | `data/AppSettings.kt` | Settings data class |
| Create | `data/SettingsProvider.kt` | Interface for testability |
| Create | `data/SettingsRepository.kt` | DataStore implementation |
| Create | `viewmodel/SettingsViewModel.kt` | Settings operations with awaitForLanguage |
| Create | `ui/SettingsScreen.kt` | Settings UI (mode chips, WPM slider, language dialog, version, licenses nav) |
| Create | `di/SettingsModule.kt` | Hilt module: provide DataStore, bind SettingsProvider |
| Modify | `viewmodel/MorseConverterViewModel.kt` | Add reactive settings collection in init |
| Modify | `MainActivity.kt` | Add applySavedLocale() with runBlocking |
| Modify | `navigation/MorselingNavGraph.kt` | Add settings and licenses routes |
| Modify | `values/strings.xml` | Add settings strings |
| Modify | `values-zh-rCN/strings.xml` | Add Chinese translations |

## Quickstart Validation

1. App opens → tap gear icon → Settings screen shows with Audio mode selected, WPM at 20, language "System default"
2. Tap "Flash" chip → navigate back → main screen shows flash mode indicator (if flash available)
3. Drag WPM to 30 → navigate back → play → playback is at 30 WPM
4. Tap Language → select 简体中文 → app recreates → all UI in Chinese
5. Restart app → language is still Chinese, mode is still Flash, WPM is 30
6. Scroll to About → "Version" shows "2.0.0"
7. Tap "Open Source Licenses" → licenses screen opens
