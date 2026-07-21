# Settings — Tasks

## Block 0: Spec & Design
- [x] `doc-coauthoring`: spec.md + plan.md approved
- [x] `checklist.md`: requirements quality checklist verified
- [x] `test_plan.md`: test scenarios documented

## Block 1: DataStore Foundation

- [x] **Task 1.1**: Create `AppSettings` data class — `data/AppSettings.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.2**: Create `SettingsProvider` interface — `data/SettingsProvider.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.3**: Implement `SettingsRepository` with DataStore — `data/SettingsRepository.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.4**: Create Hilt `SettingsModule` — provide DataStore, bind SettingsProvider — `di/SettingsModule.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.5**: Add DataStore dependency to `build.gradle.kts`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 2: Reactive Settings in ViewModel

- [x] **Task 2.1**: Collect settings flow in MorseConverterViewModel.init — apply WPM (clamped) and mode (flash fallback) — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 3: Settings UI

- [x] **Task 3.1**: Create `SettingsViewModel` — setLanguage, setDefaultMode, setDefaultWpm, setLanguageAndAwait — `viewmodel/SettingsViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 3.2**: Create `SettingsScreen` — mode FilterChips, WPM Slider, language ListItem, About section — `ui/SettingsScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 3.3**: Add settings route to navigation graph — `navigation/MorselingNavGraph.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

## Block 4: Language Application

- [x] **Task 4.1**: Add language RadioButton dialog in SettingsScreen — `ui/SettingsScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 4.2**: Implement `applySavedLocale()` in MainActivity — runBlocking read + AppCompatDelegate — `MainActivity.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 4.3**: Implement language change with activity.recreate() — `ui/SettingsScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

## Block 5: Polish

- [x] **Task 5.1**: Add settings string resources — `values/strings.xml`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 5.2**: Add Chinese translations — `values-zh-rCN/strings.xml`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 5.3**: Add version display from BuildConfig — `ui/SettingsScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`
