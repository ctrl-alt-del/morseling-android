# Morse Playback — Test Plan

## Unit Tests

### MorseCodeConverterTest
- [x] **sanitize replacements**: `.` → `0`, `-` → `2`, `/` → `3`, space → `1`
- [x] **preserves length**: Sanitized string has same length as input
- [x] **slash handling**: `/` correctly mapped to word separator code

### MorseConverterViewModelTest (playback)
- [x] **play with valid Morse**: Click Play → isPlaying becomes true, playingCharIndex updates
- [x] **play with empty Morse**: "Enter Morse code to play" error
- [x] **play with invalid characters**: "Invalid character in Morse code" error
- [x] **stop playback**: Play → Stop → isPlaying becomes false, playingCharIndex null
- [x] **stop then play again**: Stop → Play → second playback starts from beginning
- [x] **setWpm clamps to minimum**: setWpm(0) → wpm becomes 5
- [x] **setWpm clamps to maximum**: setWpm(100) → wpm becomes 40
- [x] **setWpm normal value**: setWpm(30) → wpm becomes 30
- [x] **setPlaybackMode to audio**: Mode changes to AUDIO, activePlayer is audio
- [x] **setPlaybackMode to flash when available**: Mode changes to FLASH
- [x] **setPlaybackMode to flash when unavailable**: Defaults to AUDIO
- [x] **flash availability check**: isFlashAvailable reflects hardware state
- [x] **play delegates to correct player**: When AUDIO mode, audioPlayer.play() called
- [x] **play delegates to flash player**: When FLASH mode, flashPlayer.play() called
- [x] **CancellationException not treated as error**: Stop doesn't show "Playback failed"
- [x] **Exception during playback shows error**: Simulated exception → "Playback failed"
- [x] **loadFromHistory resets playback state**: After loading, isPlaying is false

## Instrumented Tests

### MainScreen
- [x] **Play button displayed**: `onNodeWithText("Play").assertIsDisplayed()`
- [x] **Play triggers playback**: Click Play → UI enters playing state
- [x] **Stop button visible during playback**: While playing, Stop button shown

## Edge Cases
- [x] **Rapid play/stop**: No state corruption — job guard prevents stale finally cleanup
- [x] **WPM at boundaries**: 5 (slowest) and 40 (fastest) produce correct timings
- [x] **No flash device**: Flash mode unavailable, Audio fallback works
- [x] **Flash cleanup**: Torch turned off in finally block (verified by code review)
- [x] **Long Morse sequences**: Playback takes time but is cancellable
- [x] **Sanitized output invariant**: sanitize(input).length == input.length
