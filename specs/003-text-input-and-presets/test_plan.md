# Text Input & Presets — Test Plan

## Unit Tests

### MorseConverterViewModelTest
- [x] **Initial state**: textInput = "Hello World", error = null
- [x] **updateTextInput**: Sets textInput to new value, clears error
- [x] **updateTextInput clears prior error**: Set error, call updateTextInput, verify error is null

## Instrumented Tests

### MainScreen
- [x] **Text field displayed**: Visible and shows "Hello World" by default
- [x] **Convert button displayed**: `onNodeWithText("Convert").assertIsDisplayed()`
- [x] **Text input flow**: Type text → tap Convert → Morse output appears
- [x] **Preset chips visible**: Horizontally scrollable row with chip labels
- [x] **Preset tap updates text**: Tap "S.O.S." chip → text field shows "SOS"

## Edge Cases
- [x] **Empty text + Convert**: Error "Enter text to convert" displayed
- [x] **Text field disabled during playback**: isPlaying = true → text field not editable
- [x] **Convert button disabled during playback**: isPlaying = true → Convert not clickable
- [x] **Preset chips disabled during playback**: isPlaying = true → all chips not clickable
- [x] **Typing clears error**: After error shown, typing new text removes error
- [x] **All 16 presets produce convertible text**: Verify each preset's text has hasConvertibleChars = true
