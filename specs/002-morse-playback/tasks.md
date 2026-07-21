# Morse Playback — Tasks

## Block 0: Spec & Design
- [x] `doc-coauthoring`: spec.md + plan.md approved
- [x] `checklist.md`: requirements quality checklist verified
- [x] `test_plan.md`: test scenarios documented

## Block 1: Interface & Infrastructure

- [x] **Task 1.1**: Create `MorsePlayer` interface with `play()`, `isAvailable()`, `release()` — `audio/MorsePlayer.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.2**: Create `PlaybackMode` enum (AUDIO, FLASH) — `model/PlaybackMode.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.3**: Add playback state fields to `PlaybackUiState` (isPlaying, playingCharIndex, wpm, playbackMode, isFlashAvailable) — `model/PlaybackUiState.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.4**: Declare CAMERA permission and flash feature in AndroidManifest — `AndroidManifest.xml`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 2: Audio Player

- [x] **Task 2.1**: Implement `MorseCodeConverter` — ToneGenerator-based audio playback with dot/dash/space timing — `audio/MorseCodeConverter.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 2.2**: Implement `sanitize()` for Morse string normalization (maps space→internal codes) — `audio/MorseCodeConverter.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 2.3**: Implement `durations()` — compute dot/dash/space durations from WPM — `audio/MorseCodeConverter.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 3: Flashlight Player

- [x] **Task 3.1**: Implement `FlashlightMorsePlayer` — Camera2-based flash blinking — `audio/FlashlightMorsePlayer.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 3.2**: Implement `isAvailable()` — probe for rear camera with flash capability — `audio/FlashlightMorsePlayer.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 3.3**: Ensure `finally` block turns off torch regardless of exit path — `audio/FlashlightMorsePlayer.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 4: DI Module

- [x] **Task 4.1**: Create Hilt `PlayerModule` — provide `@Named("audio")` and `@Named("flash")` MorsePlayer instances — `di/PlayerModule.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 5: ViewModel Playback Logic

- [x] **Task 5.1**: Implement `activePlayer` selection logic with flash-fallback — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 5.2**: Implement `play()` — validate, launch coroutine, collect Flow, update playingCharIndex — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 5.3**: Implement `stop()` — cancel playJob, reset state — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 5.4**: Implement `setWpm()` — clamp 5-40, update state — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 5.5**: Implement `setPlaybackMode()` — change active player, check flash availability — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 5.6**: Add `onCleared()` — cancel job, release both players — `viewmodel/MorseConverterViewModel.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 6: UI — MainScreen

- [x] **Task 6.1**: Add Play/Stop buttons — visible based on isPlaying state — `ui/MainScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 6.2**: Implement `HighlightedMorseText` — private composable for character highlighting — `ui/MainScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 6.3**: Add `AnimatedContent` transition between playing/static Morse display — `ui/MainScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 6.4**: Disable input during playback (text field, Convert, presets) — `ui/MainScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

## Block 7: Tests & Polish

- [x] **Task 7.1**: Write `MorseCodeConverterTest` — sanitize replacements, slash handling, length preservation — `audio/MorseCodeConverterTest.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 7.2**: Write playback tests in `MorseConverterViewModelTest` — play, stop, replay, error states, player delegation, WPM clamping
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 7.3**: Add playback error string resources — `values/strings.xml`, `values-zh-rCN/strings.xml`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`
