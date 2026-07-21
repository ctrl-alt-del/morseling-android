# Theme & Navigation — Specification

## User Stories

### User Story 1 — Material 3 Dynamic Color Theme (Priority: P2)

As a user, I want the app to match my device's color scheme so that it feels integrated with my system.

**Why this priority**: Visual polish. App functions without dynamic color.

**Independent Test**: On Android 12+ device with custom wallpaper → app uses wallpaper-derived colors. On older device → app uses blue/teal palette.

**Acceptance Scenarios**:

1. **Given** the device supports dynamic color (Android 12+), **When** the app opens, **Then** the color scheme matches the device's Material You wallpaper colors
2. **Given** the device does not support dynamic color, **When** the app opens, **Then** the app uses a custom blue/teal color palette
3. **Given** the device is in dark mode, **When** the app opens, **Then** the dark color scheme is applied

---

### User Story 2 — Custom Typography & Shapes (Priority: P3)

As a user, I want Morse code displayed in a monospace font so that dots and dashes align visually.

**Why this priority**: Readability enhancement. Morse is still readable in any font but alignment helps.

**Independent Test**: Convert text → Morse output uses monospace font with 2sp letter spacing → dots and dashes form neat columns.

**Acceptance Scenarios**:

1. **Given** Morse code is displayed, **When** the user views the output, **Then** the font is monospace with 2sp letter spacing
2. **Given** the app title is displayed, **When** the user views the main screen, **Then** "Morseling" uses displaySmall typography
3. **Given** a history card title is displayed, **When** the user views history, **Then** the input text uses titleMedium typography

---

### User Story 3 — Screen Navigation (Priority: P1 🎯 MVP)

As a user, I want to navigate between screens so that I can access all app features.

**Why this priority**: Navigation is essential for multi-screen apps. Without it, only one screen would be visible.

**Independent Test**: App opens on main screen → tap clock icon → history screen opens → tap back → main screen.

**Acceptance Scenarios**:

1. **Given** the app opens, **When** the main screen displays, **Then** top bar has clock (history) and gear (settings) icons
2. **Given** the user taps the clock icon, **When** navigating, **Then** the history screen opens
3. **Given** the user taps the gear icon, **When** navigating, **Then** the settings screen opens
4. **Given** the user is on history/settings, **When** they tap the back arrow, **Then** they return to the main screen
5. **Given** the user is on settings, **When** they tap "Open Source Licenses", **Then** the licenses screen opens

---

### User Story 4 — Edge-to-Edge Display (Priority: P3)

As a user, I want the app to draw behind system bars so that it uses the full screen.

**Why this priority**: Visual enhancement. Content works within insets regardless.

**Independent Test**: App opens → content extends behind status bar and navigation bar → no content is obscured.

**Acceptance Scenarios**:

1. **Given** the app is on a device with system bars, **When** the main screen displays, **Then** content draws behind the status bar
2. **Given** content draws behind system bars, **When** the Scaffold renders, **Then** `paddingValues` ensures content is not obscured

---

## Edge Cases

- What happens if both dynamic and dark mode are available? Both applied simultaneously.
- What happens on pre-Android 12 devices with dark mode? Custom dark color scheme used.
- What happens on deep navigation (settings → licenses → back)? Each back press pops one screen. Max depth is settings→licenses (2 levels from main).
- What happens if navigation is triggered during animation? Compose Navigation handles this — second navigation queued.
- What happens on configuration change (rotation)? Navigation state preserved via ViewModel.

## Functional Requirements

- **FR-055**: System MUST use Material 3 dynamic color on Android 12+ with fallback to custom blue/teal palette
- **FR-056**: System MUST support light and dark color schemes following `isSystemInDarkTheme()`
- **FR-057**: System MUST use monospace font for Morse code display with 2sp letter spacing
- **FR-058**: System MUST provide Compose Navigation with 4 routes: main, history, settings, licenses
- **FR-059**: System MUST use `main` route as the start destination
- **FR-060**: System MUST provide clock and gear icons in the main screen top bar for navigation
- **FR-061**: System MUST provide back navigation on history, settings, and licenses screens
- **FR-062**: System MUST enable edge-to-edge display via `enableEdgeToEdge()`
- **FR-063**: System MUST handle system bar insets via Scaffold `paddingValues`
- **FR-064**: System MUST display 11 open source library attributions on the licenses screen (name, license, URL)

## Non-Functional Requirements

- **Performance**: Theme computation < 5ms. Navigation transitions < 100ms. LazyColumn for licenses renders efficiently.
- **Security**: N/A — visual-only feature.
- **Accessibility**: Navigation icons have content descriptions. Top bar titles are visible. Back button is accessible.
- **Observability**: Current route visible in top bar title. Navigation state managed by Compose Navigation.

## Success Criteria

- **SC-020**: App matches device color scheme on Android 12+ within 1 second of launch
- **SC-021**: Users can navigate to any screen within 2 taps from main screen
- **SC-022**: Morse code is displayed in monospace font on all screens where it appears
- **SC-023**: No content is obscured by system bars on any screen

## Assumptions

- Android 12+ devices represent the majority of the target user base
- Users understand the clock icon = history and gear icon = settings
- 11 library attributions cover all major dependencies
- License URLs are displayed (not hyperlinked) for compliance
