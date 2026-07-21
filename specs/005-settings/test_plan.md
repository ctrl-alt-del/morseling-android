# Settings — Test Plan

## Unit Tests

### MorseConverterViewModelTest
- [x] **Settings WPM applied**: FakeSettingsProvider with wpm=30 → ViewModel init applies WPM 30
- [x] **Settings WPM clamped**: FakeSettingsProvider with wpm=50 → ViewModel clamps to 40
- [x] **Settings mode applied**: FakeSettingsProvider with mode="flash" → ViewModel applies flash
- [x] **Flash unavailable fallback**: FakeSettingsProvider with mode="flash", flash not available → Audio mode
- [x] **Settings language read**: FakeSettingsProvider emits language change → ViewModel reacts

## Instrumented Tests
- [x] **Settings screen navigation**: Tap gear → settings screen loads
- [x] **Mode chips displayed**: Audio and Flash FilterChips visible
- [x] **WPM slider displayed**: Slider with label "XX WPM" visible
- [x] **Language list item**: "Language" item visible with current selection

## Edge Cases
- [x] **Language change persists**: Set Chinese → restart app → UI is in Chinese
- [x] **Mode persists**: Set Flash → restart app → Flash is default
- [x] **WPM persists**: Set 30 → restart app → WPM is 30
- [x] **WPM clamping on init**: Stored WPM = 0 → init clamps to 5
- [x] **WPM clamping on init**: Stored WPM = 50 → init clamps to 40
- [x] **runBlocking doesn't ANR**: First DataStore read < 50ms (much less in practice)
