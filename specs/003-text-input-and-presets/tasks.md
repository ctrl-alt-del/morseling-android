# Text Input & Presets — Tasks

## Block 0: Spec & Design
- [x] `doc-coauthoring`: spec.md + plan.md approved
- [x] `checklist.md`: requirements quality checklist verified
- [x] `test_plan.md`: test scenarios documented

## Block 1: Text Input Field

- [x] **Task 1.1**: Add `textInput` field with `DEFAULT_TEXT = "Hello World"` to `PlaybackUiState` — `model/PlaybackUiState.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.2**: Implement `updateTextInput()` in ViewModel — clear error, update state — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.3**: Add OutlinedTextField to MainScreen — multiline, min 2 max 4 lines, bound to uiState.textInput — `ui/MainScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 1.4**: Add placeholder string resources ("Type any text" / "输入任意文本") — `strings.xml`, `strings-zh-rCN/strings.xml`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 2: Convert Button

- [x] **Task 2.1**: Add Convert button to MainScreen — Translate icon, triggers convertText() — `ui/MainScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 2.2**: Add "Convert" string resource — `strings.xml`, `strings-zh-rCN/strings.xml`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 3: Disabled During Playback

- [x] **Task 3.1**: Disable text field when `uiState.isPlaying` — `ui/MainScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 3.2**: Disable Convert button when `uiState.isPlaying` — `ui/MainScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

## Block 4: User Story 3 — Preset Chips (P3)

- [x] **Task 4.1**: Define 16 presets as a static list with label + display text — `ui/MainScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 4.2**: Add LazyRow of AssistChips for presets — tap fills text input — `ui/MainScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 4.3**: Disable preset chips during playback — `ui/MainScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

## Block 5: Polish

- [x] **Task 5.1**: Update initial state test for DEFAULT_TEXT — `viewmodel/MorseConverterViewModelTest.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 5.2**: Instrumented test for Convert button + text input flow — `ui/MainScreenTest.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`
