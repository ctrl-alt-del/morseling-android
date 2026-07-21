---
feature_id: "007"
name: "App Infrastructure"
status: "✅ Done"
depends_on: ["001", "002", "004", "005", "006"]
touches:
  - "app/src/main/java/com/morseling/MorselingApplication.kt"
  - "app/src/main/java/com/morseling/MainActivity.kt"
  - "app/src/main/java/com/morseling/di/PlayerModule.kt"
  - "app/src/main/java/com/morseling/di/DatabaseModule.kt"
  - "app/src/main/java/com/morseling/di/SettingsModule.kt"
  - "app/src/main/java/com/morseling/data/ConversionDatabase.kt"
  - "app/src/main/java/com/morseling/data/SettingsRepository.kt"
  - "app/src/main/java/com/morseling/viewmodel/MorseConverterViewModel.kt"
  - "app/src/main/AndroidManifest.xml"
  - "app/build.gradle.kts"
  - "build.gradle.kts"
  - "gradle/libs.versions.toml"
created: "2026-07-21"
---

# App Infrastructure — Implementation Plan

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Hilt 2.51.1 + KSP, Room 2.6.1, DataStore 1.1.1, Compose BOM 2024.12.01
**Storage**: Room (morseling.db), DataStore (settings.preferences_pb)
**Testing**: Hilt testing for instrumented tests, Fake implementations for unit tests
**Target Platform**: Android 26+ (minSdk 26, targetSdk 35, compileSdk 35)
**Performance Goals**: Cold start < 2s, DI initialization lazy
**Constraints**: Single Activity, no fragments, ProGuard enabled for release
**Scale/Scope**: Single module (:app), JVM 17 target

## Constitution Check

- [x] **Article I (Spec-First)**: spec.md written. Retroactive.
- [x] **Article II (Test-First)**: Hilt test rule used in instrumented tests. Retroactive.
- [x] **Article III (Modularity)**: DI modules in `di/` package; data layer in `data/`; entry points at root.
- [x] **Article IV (Simplicity)**: 3 DI modules + 3 root files + manifest = 7 files. Over 5 but DI modules are unavoidable with Hilt.
- [x] **Article V (Anti-Abstraction)**: Using Hilt, Room, DataStore directly. SettingsProvider is the only interface with 2 implementations — justified.
- [x] **Article VI (Integration Reality)**: Real Room DB and DataStore in production. Fake DAO/SettingsProvider in unit tests.
- [x] **Article VII (Observability)**: StateFlow for UI state, Room Flow for data. Errors logged and surfaced.

## Approach

**Hilt DI**: Three modules provide all dependencies:
- `PlayerModule`: `@Named("audio")` MorseCodeConverter, `@Named("flash")` FlashlightMorsePlayer — both @Singleton
- `DatabaseModule`: Room database + ConversionHistoryDao — both @Singleton
- `SettingsModule`: DataStore instance + SettingsProvider binding — both @Singleton

**Entry points**:
- `MorselingApplication.kt`: `@HiltAndroidApp` — triggers Hilt code generation
- `MainActivity.kt`: `@AndroidEntryPoint` — enables field injection, calls `enableEdgeToEdge()`, applies saved locale, sets Compose content

**Manifest**: Single launcher activity. CAMERA permission (for flashlight). Flash feature `required="false"`. `allowBackup="true"`, `supportsRtl="true"`.

**Build**: Single module `:app`, version catalog in `libs.versions.toml`, JVM 17 target, ProGuard enabled for release.

## Data Model

N/A — infrastructure layer. All data models are in feature-specific plans.

## API / CLI Contract

No external API. Internal contracts:
- Hilt component hierarchy: SingletonComponent → ViewModelComponent
- Room database: `morseling.db` with `conversion_history` table
- DataStore: `settings.preferences_pb` with 3 keys

## Alternatives Considered

| Alternative | Pros | Cons | Why Rejected |
|-------------|------|------|--------------|
| Manual DI (no Hilt) | No annotation processing, simpler build | Boilerplate factory classes, harder to test | Hilt eliminates boilerplate and integrates with ViewModel |
| Koin for DI | Kotlin-native, simpler setup | Less compile-time safety, less official support | Hilt is the official Android DI solution |
| SharedPreferences for settings | Simpler API | Blocking main thread, no Flow support | DataStore is async and Flow-native |
| Multi-module project | Clearer separation, faster builds | Complexity overhead for 20 files | Single module is sufficient at this scale |
| Fragment-based navigation | Familiar to Android devs | Fragment lifecycle complexity, more boilerplate | Single Activity + Compose Navigation is simpler |

**Decision**: Hilt + Room + DataStore + Single Activity + Single Module. Official Android stack, minimal complexity for the app's scale.

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| KSP/Hilt version conflicts | Medium | High | Version catalog centralizes dependency versions |
| ProGuard strips required classes | Low | Medium | `proguard-rules.pro` keeps annotations + line numbers |
| Room schema migration needed | High | Medium | Version 1 now; future: add Migration objects |
| Gradle 9.5.1 incompatibility | Low | High | Minor AGP updates may be needed; major versions need testing |

## Dependencies

| Depends On | Status | Blocking? |
|-----------|--------|-----------|
| 001-text-to-morse-conversion | ✅ Done | No |
| 002-morse-playback | ✅ Done | No |
| 004-conversion-history | ✅ Done | No |
| 005-settings | ✅ Done | No |
| 006-theme-and-navigation | ✅ Done | No |
| AGP 8.7.3 + Kotlin 2.0.21 | Ready | No |

## Complexity Tracking

| Article | Violation | Why Needed | Simpler Alternative Rejected Because |
|---------|-----------|------------|--------------------------------------|
| Art. IV | 7 files (>5) | Hilt requires annotation processing with @Module classes (3 DI files). Application + Activity + Manifest are standard Android boilerplate. Room DB class is generated but needs definition. | Could merge DatabaseModule + SettingsModule into one AppModule, but separate modules per domain (database vs preferences) are cleaner. |

## Files to Create / Change

| Action | File | Rationale |
|--------|------|-----------|
| Create | `MorselingApplication.kt` | @HiltAndroidApp entry point |
| Modify | `MainActivity.kt` | @AndroidEntryPoint, edge-to-edge, locale, setContent |
| Create | `di/PlayerModule.kt` | Provide @Named audio/flash players |
| Create | `di/DatabaseModule.kt` | Provide Room DB + DAO |
| Create | `di/SettingsModule.kt` | Provide DataStore + SettingsProvider binding |
| Create | `data/ConversionDatabase.kt` | Room database definition |
| Modify | `data/SettingsRepository.kt` | DataStore implementation |
| Modify | `viewmodel/MorseConverterViewModel.kt` | onCleared() cleanup |
| Modify | `app/build.gradle.kts` | Hilt, Room, DataStore deps + KSP config |
| Modify | `build.gradle.kts` | Hilt + KSP plugins |
| Modify | `gradle/libs.versions.toml` | Version catalog entries |
| Modify | `AndroidManifest.xml` | Permission, feature, Activity declaration |

## Quickstart Validation

1. App compiles successfully with `gradle assembleDebug`
2. All unit tests pass with `gradle test`
3. Hilt error? Check `@AndroidEntryPoint`, `@HiltAndroidApp`, `@HiltViewModel` annotations
4. Room error? Check `@Database` annotation with entities + version
5. DataStore not available? Check Hilt module provides `DataStore<Preferences>`
6. ProGuard issue? Check `proguard-rules.pro` keeps annotations
7. Build failure? Check `libs.versions.toml` version consistency
