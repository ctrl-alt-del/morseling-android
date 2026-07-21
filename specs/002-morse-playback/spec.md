# Morse Playback — Specification

## User Stories

### User Story 1 — Audio Playback (Priority: P1 🎯 MVP)

As a user, I want to play Morse code as audio beeps so that I can hear the Morse representation.

**Why this priority**: Audio playback is the primary consumption method for Morse code. Without it, users can only read Morse visually.

**Independent Test**: Convert "SOS" → tap Play → hear `... --- ...` as short and long beeps. Delivers standalone value as a Morse code trainer/reference.

**Acceptance Scenarios**:

1. **Given** valid Morse code is displayed, **When** the user taps Play, **Then** audio beeps play through the device speaker matching the dot/dash pattern
2. **Given** playback is in progress, **When** the user taps Stop, **Then** playback stops immediately
3. **Given** playback completes naturally, **When** the last symbol finishes, **Then** playback state resets to idle (isPlaying=false, no highlight)
4. **Given** the Morse output is empty, **When** the user taps Play, **Then** error "Enter Morse code to play" is displayed

---

### User Story 2 — Flashlight Playback (Priority: P2)

As a user, I want to play Morse code via camera flashlight blinking so that I can signal Morse code visually.

**Why this priority**: Visual Morse signaling is a common use case (emergency, maritime, amateur radio). Secondary to audio but valuable.

**Independent Test**: Set mode to Flash → convert "SOS" → tap Play → camera flashlight blinks `... --- ...`.

**Acceptance Scenarios**:

1. **Given** the device has a camera flash, **When** the user selects Flash mode, **Then** the flash option is selectable and playback uses the flashlight
2. **Given** the device has no camera flash, **When** the app is opened, **Then** flash availability is false and mode falls back to Audio
3. **Given** Flash mode is selected, **When** the user taps Play, **Then** the flashlight blinks dots (short) and dashes (long)
4. **Given** playback is stopped or completes, **When** the flashlight was on, **Then** the flashlight is turned off (cleanup in finally block)

---

### User Story 3 — WPM Speed Control (Priority: P2)

As a user, I want to adjust the playback speed in Words Per Minute so that I can practice at different speeds.

**Why this priority**: Speed control is essential for Morse code learning (start slow, increase speed) and for experienced users who want fast playback.

**Independent Test**: Set WPM to 5 (slowest) → play → dots/dashes are clearly distinguishable. Set WPM to 40 (fastest) → playback is significantly faster.

**Acceptance Scenarios**:

1. **Given** the app is in its default state, **When** the user checks playback speed, **Then** WPM is 20 (default)
2. **Given** the user adjusts WPM to 5, **When** they play Morse code, **Then** dot duration is 240ms (`1200/5`), dash is 720ms
3. **Given** the user adjusts WPM to 40, **When** they play Morse code, **Then** dot duration is 30ms (`1200/40`), dash is 90ms
4. **Given** WPM is set to a value below 5, **When** applied, **Then** value is clamped to 5
5. **Given** WPM is set to a value above 40, **When** applied, **Then** value is clamped to 40

---

### User Story 4 — Real-Time Character Highlighting (Priority: P3)

As a user, I want to see each character highlight as it is played so that I can follow along visually.

**Why this priority**: Enhances learning and engagement but playback works without it.

**Independent Test**: Play Morse code → observe that characters in the output field light up one by one in sync with the audio/flash.

**Acceptance Scenarios**:

1. **Given** playback is in progress, **When** a dot/dash/space is played, **Then** the corresponding character in the Morse output is highlighted (primaryContainer background, onPrimaryContainer color, bold)
2. **Given** playback is stopped, **When** the UI returns to idle, **Then** the Morse output returns to static display (no highlighting)
3. **Given** the highlighted character is a space, **When** rendered, **Then** it displays as `·` (middle dot) to indicate the gap visually

---

### User Story 5 — Mode Selection (Priority: P3)

As a user, I want to select between Audio and Flash playback modes so that I can choose my preferred output method.

**Why this priority**: Convenience feature. Users could manually toggle if there were a physical switch.

**Independent Test**: Settings → tap "Audio" chip → play → hear beeps. Settings → tap "Flash" chip → play → see flashlight blink.

**Acceptance Scenarios**:

1. **Given** the default mode is "audio", **When** the app starts, **Then** playback uses audio
2. **Given** the default mode is "flash" but the device has no flash, **When** the app starts, **Then** playback falls back to audio
3. **Given** the user changes mode in Settings, **When** they navigate back to main screen, **Then** the new mode is active for playback

---

## Edge Cases

- What happens if ToneGenerator fails? Caught as `Exception` in play(), error "Playback failed" displayed.
- What happens if CameraManager throws? `CameraAccessException` caught during availability probe. Torch mode errors silently caught during playback.
- What happens if play() is called while already playing? New playJob replaces old one; old coroutine is cancelled (CancellationException caught silently).
- What happens on rapid Play→Stop→Play? Each call creates a new Job; the job guard (`playJob === coroutineContext[Job]`) in finally prevents stale cleanup.
- What happens with very long Morse sequences? Playback will take proportionally longer but is cancellable via Stop.
- What happens if WPM is so high that dot duration < 1ms? Coerced to minimum 1ms.

## Functional Requirements

- **FR-010**: System MUST play Morse code as audio beeps using Android ToneGenerator on STREAM_NOTIFICATION
- **FR-011**: System MUST play Morse code as flashlight blinks using Camera2 API (`setTorchMode`)
- **FR-012**: System MUST compute timing: dot=1 unit, dash=3 units, letter gap=3 units, word gap=7 units
- **FR-013**: System MUST compute unit duration as `1200 / wpm` ms, with minimum 1ms
- **FR-014**: System MUST clamp WPM to range 5–40
- **FR-015**: System MUST accept WPM range 5–40 with step size
- **FR-016**: System MUST emit character index via `Flow<Int>` during playback for highlighting
- **FR-017**: System MUST highlight the current character during playback with distinct visual style
- **FR-018**: System MUST validate Morse output before playback (reject empty, reject invalid chars)
- **FR-019**: System MUST allow stop during playback and reset state
- **FR-020**: System MUST fall back to Audio mode if Flash is selected but unavailable
- **FR-021**: System MUST turn off flashlight in finally block regardless of how playback ends
- **FR-022**: System MUST catch CancellationException silently (normal stop) and show error for other exceptions
- **FR-023**: System MUST use job guard (`playJob === coroutineContext[Job]`) in finally to prevent stale state reset

## Non-Functional Requirements

- **Performance**: Playback timing accurate to ±5ms. UI highlight updates at dot/dash/space boundaries (no dropped frames).
- **Security**: Camera permission declared (`CAMERA`). Flash usage requires user-triggered action (tap Play). Permission not used for anything else.
- **Accessibility**: Play/Stop buttons have content descriptions. Highlighted character is visually distinct (bold + colored background).
- **Observability**: Playback state exposed via `isPlaying: Boolean`. Current character index exposed via `playingCharIndex: Int?`. Errors surfaced as snackbar.
- **Offline**: Fully offline. Audio uses local ToneGenerator. Flash uses local CameraManager.

## Success Criteria

- **SC-005**: Users can play Morse code within 2 seconds of converting text
- **SC-006**: Playback timing matches the Paris standard (dot/dash ratio 1:3, word space ratio 1:7)
- **SC-007**: Flashlight playback functions on devices with flash; app does not crash on devices without flash
- **SC-008**: Character highlighting visibly sweeps across the output in sync with audio

## Assumptions

- ToneGenerator is available on all Android 8.0+ devices
- CameraManager.setTorchMode() works on most devices with flash
- Users will primarily use Audio mode; Flash mode is secondary
- The `1200 / wpm` timing formula is the standard for Morse code (Paris timing)
- Devices without flash are rare but must be handled gracefully
