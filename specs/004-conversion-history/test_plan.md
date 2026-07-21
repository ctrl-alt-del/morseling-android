# Conversion History — Test Plan

## Unit Tests

### MorseConverterViewModelTest
- [x] **Save on convertText**: After valid conversion, FakeDao.items contains the saved entry
- [x] **Dedup on same text**: Convert "SOS" twice → FakeDao has 1 entry with updated timestamp
- [x] **loadFromHistory sets text fields**: textInput and morseOutput match loaded entry
- [x] **loadFromHistory re-saves (updates timestamp)**: Entry timestamp updated after load

## Instrumented Tests
- [x] **History entries displayed**: Navigate to History → cards show input text and Morse
- [x] **Empty state**: No conversions → "No history yet" message visible
- [x] **Clear all flow**: Tap clear all → dialog appears → confirm → entries removed

## Edge Cases
- [x] **Empty history**: Empty state message shown, "Clear all" button hidden
- [x] **Single entry**: One card shown, "Clear all" visible, delete removes it to empty state
- [x] **Favorite toggle persistence**: Favorite star state survives app restart (Room persistence)
- [x] **Load from history updates main screen**: TextField and Morse output populated correctly
- [x] **Navigation back after load from history**: User lands on main screen, not history
- [x] **Rapid delete of same entry**: Second delete is no-op (entry already gone)
