# Conversion History — Tasks

## Block 0: Spec & Design
- [x] `doc-coauthoring`: spec.md + plan.md approved
- [x] `checklist.md`: requirements quality checklist verified
- [x] `test_plan.md`: test scenarios documented

## Block 1: Room Database

- [x] **Task 1.1**: Create `ConversionHistoryEntity` with Room annotations — `data/ConversionHistoryEntity.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.2**: Create `ConversionHistoryDao` interface with CRUD queries — `data/ConversionHistoryDao.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.3**: Create `ConversionDatabase` singleton (version 1, morseling.db) — `data/ConversionDatabase.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.4**: Create `ConversionRepository` with save dedup logic — `data/ConversionRepository.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.5**: Create Hilt `DatabaseModule` providing DB and DAO — `di/DatabaseModule.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.6**: Add Room and KSP dependencies to `build.gradle.kts` — `app/build.gradle.kts`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 2: Auto-Save on Conversion

- [x] **Task 2.1**: Add `repository.saveConversion()` call in `convertText()` — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 2.2**: Add re-save on `loadFromHistory()` (update timestamp) — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 3: History Screen UI

- [x] **Task 3.1**: Create `HistoryViewModel` — collect Room Flow, CRUD methods — `viewmodel/HistoryViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 3.2**: Create `HistoryScreen` composable — LazyColumn with ElevatedCards — `ui/HistoryScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 3.3**: Add history route to navigation graph with onItemClick — `navigation/MorselingNavGraph.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

## Block 4: User Story 3 — Favorites (P3)

- [x] **Task 4.1**: Add star IconButton with animated color to history cards — `ui/HistoryScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 4.2**: Implement `toggleFavorite()` in HistoryViewModel — `viewmodel/HistoryViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 5: User Story 4 — Delete & Clear All (P3)

- [x] **Task 5.1**: Add delete IconButton to each history card — `ui/HistoryScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 5.2**: Implement `deleteById()` in HistoryViewModel — `viewmodel/HistoryViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 5.3**: Add "Clear all" top bar action with AlertDialog — `ui/HistoryScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 5.4**: Implement `deleteAll()` in HistoryViewModel — `viewmodel/HistoryViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 6: Polish

- [x] **Task 6.1**: Add history-related string resources — `values/strings.xml`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 6.2**: Add Chinese translations — `values-zh-rCN/strings.xml`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 6.3**: Add empty state UI ("No history yet") — `ui/HistoryScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`
