# Settings — Specification

## User Stories

### User Story 1 — Default Playback Mode (Priority: P2)

As a user, I want to set my preferred playback mode (Audio/Flash) so that I don't need to switch modes every time.

**Why this priority**: Convenience. Users can always switch mode on the main screen.

**Independent Test**: Settings → tap "Flash" chip → navigate back → main screen shows Flash mode active.

**Acceptance Scenarios**:

1. **Given** the default mode is "audio", **When** the app first launches, **Then** playback mode is set to Audio
2. **Given** the user selects "Flash" in Settings, **When** they navigate back to main, **Then** playback uses Flash mode
3. **Given** Flash mode is selected in Settings but the device has no flash, **When** the app starts, **Then** playback falls back to Audio (mode is unchanged in Settings)
4. **Given** the user selects a mode, **When** the app restarts, **Then** the saved mode is applied (DataStore persistence)

---

### User Story 2 — Default WPM Speed (Priority: P2)

As a user, I want to set my preferred playback speed so that I don't need to adjust speed every session.

**Why this priority**: Learning tool. Morse students need consistent speed for practice. Casual users benefit from defaults.

**Independent Test**: Settings → drag WPM slider to 30 → back to main → play → playback is at 30 WPM.

**Acceptance Scenarios**:

1. **Given** the app is first installed, **When** the user checks WPM, **Then** default is 20 WPM
2. **Given** the user drags the slider to 30, **When** they play Morse code, **Then** playback uses 30 WPM (dot = 40ms)
3. **Given** WPM is set to 5, **When** the app restarts, **Then** WPM remains at 5 (DataStore persistence)
4. **Given** the slider displays, **When** the user sees the UI, **Then** current value is shown as "XX WPM"

---

### User Story 3 — Language Selection (Priority: P2)

As a bilingual user, I want to switch between English and Chinese so that I can use the app in my preferred language.

**Why this priority**: Essential for Chinese-speaking user base. Without it, the app is English-only.

**Independent Test**: Settings → tap Language → select 简体中文 → app restarts in Chinese.

**Acceptance Scenarios**:

1. **Given** the system language is the default, **When** the app launches, **Then** "System default" language is active
2. **Given** the user selects "English", **When** the app recreates, **Then** all UI text is in English
3. **Given** the user selects "简体中文", **When** the app recreates, **Then** all UI text is in Chinese
4. **Given** the user selects "System default", **When** the app recreates, **Then** UI follows the device's system language

---

### User Story 4 — Version Display (Priority: P3)

As a user, I want to see the app version in settings so that I know which version I'm running.

**Why this priority**: Diagnostic. Rarely needed but essential for support.

**Independent Test**: Settings → About section → "Version" row shows "2.0.0".

**Acceptance Scenarios**:

1. **Given** the settings screen is open, **When** the user scrolls to About, **Then** the version number from BuildConfig is displayed

---

### User Story 5 — Licenses Navigation (Priority: P3)

As a user, I want to view open source licenses so that I know what libraries the app uses.

**Why this priority**: Legal requirement for open source compliance. Not a user-facing feature per se.

**Independent Test**: Settings → tap "Open Source Licenses" → licenses screen opens with library list.

**Acceptance Scenarios**:

1. **Given** the settings screen is open, **When** the user taps "Open Source Licenses", **Then** the Licenses screen opens
2. **Given** the licenses screen is open, **When** the user taps back, **Then** they return to Settings

---

## Edge Cases

- What happens if DataStore read fails? `runBlocking` in `applySavedLocale()` would throw — app crashes on startup. No error handling for DataStore failure.
- What happens if language change triggers recreate() during a sensitive operation? Language change only possible from Settings screen (not main), so no playback/history ops are in progress.
- What happens if version info is unavailable? `BuildConfig.VERSION_NAME` is always available in debug/release builds.
- What happens if slider goes below 5 or above 40? `coerceIn(5, 40)` in ViewModel clamps the value.

## Functional Requirements

- **FR-045**: System MUST persist default playback mode ("audio"/"flash") across app restarts via DataStore
- **FR-046**: System MUST persist default WPM (int, 5-40) across app restarts via DataStore
- **FR-047**: System MUST persist language preference ("system"/"en"/"zh-rCN") across app restarts via DataStore
- **FR-048**: System MUST apply language on startup via `AppCompatDelegate.setApplicationLocales()` before `setContent`
- **FR-049**: System MUST recreate activity on language change for immediate effect
- **FR-050**: System MUST show current settings values on the settings screen (mode chips, WPM slider value, language label)
- **FR-051**: System MUST display app version from BuildConfig in About section
- **FR-052**: System MUST provide navigation from Settings to Licenses screen
- **FR-053**: System MUST provide a WPM slider with range 5-40 and 6 discrete steps
- **FR-054**: System MUST provide a language selection dialog with RadioButtons (System/English/简体中文)

## Non-Functional Requirements

- **Performance**: DataStore reads complete < 10ms. Settings screen renders < 16ms. Language change triggers activity recreate (< 200ms).
- **Security**: Settings are local-only (DataStore). No settings leave the device.
- **Accessibility**: Slider has content description. RadioButtons are individually selectable. Chips have text labels.
- **Observability**: Settings state flows reactively through `SettingsProvider.settings: Flow<AppSettings>`. ViewModel collects and applies.

## Success Criteria

- **SC-016**: Users can change settings and see them reflected on the next screen visit within 1 second
- **SC-017**: Language changes persist across app restarts (verified via app relaunch)
- **SC-018**: All settings have reasonable defaults (mode=audio, wpm=20, language=system)
- **SC-019**: Settings UI is discoverable via gear icon in main screen top bar

## Assumptions

- DataStore is always available and doesn't fail on read/write
- `activity.recreate()` is safe to call from any state (no ongoing playback during settings interaction)
- `runBlocking` in `onCreate` is acceptable for a single DataStore read (~5ms)
- Users understand WPM as a Morse code concept
