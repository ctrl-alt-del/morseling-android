# Theme & Navigation — Test Plan

## Unit Tests

N/A — Theme and navigation are primarily visual/integration. Unit testing color/typography constants is low value.

## Instrumented Tests

### Navigation
- [x] **Main screen is start destination**: App opens on main screen with "Convert" and "Play" buttons
- [x] **History navigation**: Tap clock → History screen visible
- [x] **Settings navigation**: Tap gear → Settings screen visible
- [x] **Licenses navigation**: Settings → tap "Open Source Licenses" → Licenses screen visible
- [x] **Back navigation**: History → back → Main screen; Settings → back → Main screen
- [x] **Deep back**: Licenses → back → Settings → back → Main

### Theme
- [x] **Theme applied**: All screens use Material 3 color roles (not hardcoded colors)
- [x] **Morse font**: Morse output uses monospace font visible

### Licenses
- [x] **Licenses screen content**: 11 library entries displayed

## Edge Cases
- [x] **Dark mode toggle**: System dark mode switch → app theme updates
- [x] **Pre-Android 12**: Custom color schemes used (verified on emulator without dynamic color)
- [x] **Edge-to-edge**: Content draws behind status bar but not obscured
- [x] **Rapid navigation**: No navigation stack corruption
- [x] **Configuration change**: Navigation state preserved on rotation
