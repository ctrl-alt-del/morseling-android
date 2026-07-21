# App Infrastructure — Tasks

## Block 0: Spec & Design
- [x] `doc-coauthoring`: spec.md + plan.md approved
- [x] `checklist.md`: requirements quality checklist verified
- [x] `test_plan.md`: test scenarios documented

## Block 1: Build Configuration

- [x] **Task 1.1**: Add version catalog entries (Hilt, Room, DataStore, etc.) — `gradle/libs.versions.toml`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.2**: Configure root build.gradle.kts with plugins (AGP, Kotlin, Compose, KSP, Hilt) — `build.gradle.kts`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.3**: Configure app build.gradle.kts (compileSdk 35, minSdk 26, JVM 17, deps, KSP, ProGuard) — `app/build.gradle.kts`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 2: Hilt DI Setup

- [x] **Task 2.1**: Create `MorselingApplication.kt` with `@HiltAndroidApp` — `MorselingApplication.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 2.2**: Annotate `MainActivity.kt` with `@AndroidEntryPoint` — `MainActivity.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 2.3**: Create `PlayerModule` — provide @Named audio/flash players — `di/PlayerModule.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 2.4**: Create `DatabaseModule` — provide Room DB + DAO — `di/DatabaseModule.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 2.5**: Create `SettingsModule` — provide DataStore + SettingsProvider binding — `di/SettingsModule.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 3: Manifest Configuration

- [x] **Task 3.1**: Declare CAMERA permission — `AndroidManifest.xml`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 3.2**: Declare flash feature (required=false), allowBackup, supportsRtl — `AndroidManifest.xml`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 4: Startup & Cleanup

- [x] **Task 4.1**: Implement `applySavedLocale()` in MainActivity — `MainActivity.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 4.2**: Implement `onCleared()` in MorseConverterViewModel — cancel job, release players — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 5: Verify

- [x] **Task 5.1**: Full build: `gradle assembleDebug`
- [x] **Task 5.2**: Unit tests: `gradle test`
- [x] **Task 5.3**: Instrumented tests: `gradle connectedAndroidTest`
