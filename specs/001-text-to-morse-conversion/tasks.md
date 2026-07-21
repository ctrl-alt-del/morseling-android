# Text-to-Morse Conversion — Tasks

## Block 0: Spec & Design (before code)
- [x] `doc-coauthoring`: spec.md + plan.md approved
- [x] `checklist.md`: requirements quality checklist verified
- [x] `test_plan.md`: test scenarios documented

## Block 1: Core Conversion Engine

- [x] **Task 1.1**: Create `MorseTranslator` object with `CHAR_TO_MORSE` lookup map — `util/MorseTranslator.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.2**: Implement `textToMorse()` — uppercase → split words → map chars → join with separators — `util/MorseTranslator.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.3**: Add `hasConvertibleChars()` and `unconvertibleChars()` validation methods — `util/MorseTranslator.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 2: Chinese Pinyin Support

- [x] **Task 2.1**: Add TinyPinyin dependency to `libs.versions.toml` and `build.gradle.kts`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 2.2**: Implement `toPinyinIfCjk()` — detect CJK via `Character.UnicodeScript.HAN`, call `Pinyin.toPinyin()` — `util/MorseTranslator.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 2.3**: Integrate Pinyin into `textToMorse()` — CJK chars → Pinyin → Morse; non-CJK → direct Morse — `util/MorseTranslator.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 3: User Story 1 — Basic Conversion (P1 🎯 MVP)

- [x] **Task 3.1**: Add `textInput` and `morseOutput` fields to `PlaybackUiState` — `model/PlaybackUiState.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 3.2**: Implement `convertText()` in ViewModel — validate, call MorseTranslator, update state, save to history — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 3.3**: Add Convert button and Morse output display to MainScreen — `ui/MainScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

## Block 4: User Story 3 — Character Detection (P2)

- [x] **Task 4.1**: Integrate `hasConvertibleChars()` check into `convertText()` — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 4.2**: Add unconvertible-char warning with partial Morse output — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 5: Polish

- [x] **Task 5.1**: Write unit tests for MorseTranslator (9 tests) — `util/MorseTranslatorTest.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 5.2**: Write ViewModel conversion tests — `viewmodel/MorseConverterViewModelTest.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 5.3**: Add error string resources to `strings.xml` and `strings-zh-rCN/strings.xml`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`
