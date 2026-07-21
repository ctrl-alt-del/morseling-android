# Text Input & Presets — Specification

## User Stories

### User Story 1 — Text Input Field (Priority: P1 🎯 MVP)

As a user, I want to type or paste text into an input field so that I can prepare text for Morse conversion.

**Why this priority**: Required for any conversion to happen. Core UX element.

**Independent Test**: App opens → text field visible with "Hello World" placeholder → user can type or paste text.

**Acceptance Scenarios**:

1. **Given** the app is opened, **When** the main screen displays, **Then** a text input field shows "Hello World" as default text
2. **Given** the text field is focused, **When** the user types text, **Then** the text appears in the field
3. **Given** the text field contains text, **When** the user pastes new text, **Then** the pasted text replaces existing content
4. **Given** playback is active, **When** the user attempts to edit text, **Then** the text field is disabled

---

### User Story 2 — Convert Button (Priority: P1 🎯 MVP)

As a user, I want a prominently placed Convert button so that I can trigger Morse conversion with a single tap.

**Why this priority**: Directly tied to User Story 1 — input is useless without conversion trigger.

**Independent Test**: App opens → Convert button visible with Translate icon → tap → conversion triggers.

**Acceptance Scenarios**:

1. **Given** the app is on the main screen, **When** the user views the interface, **Then** a Convert button with Translate icon is visible below the text field
2. **Given** text is entered, **When** the user taps Convert, **Then** the text is converted to Morse code
3. **Given** playback is active, **When** the user attempts to tap Convert, **Then** the button is disabled
4. **Given** the text field is empty, **When** the user taps Convert, **Then** an error "Enter text to convert" is displayed

---

### User Story 3 — Quick Preset Chips (Priority: P3)

As a user, I want quick-access preset phrases so that I can instantly load common Morse code messages without typing.

**Why this priority**: Quality-of-life feature. Users can always type manually. Saves time for common phrases.

**Independent Test**: App opens → scrollable row of chip buttons visible → tap "S.O.S." → text field fills with "SOS".

**Acceptance Scenarios**:

1. **Given** the main screen is displayed, **When** the user views the interface, **Then** a horizontally scrollable row of preset chips is visible below the Convert button
2. **Given** the user taps the "S.O.S." chip, **When** the chip is activated, **Then** the text field fills with "SOS"
3. **Given** the user taps the "CQ" chip, **When** the chip is activated, **Then** the text field fills with "CQ CQ CQ"
4. **Given** playback is active, **When** the user attempts to tap a preset chip, **Then** all preset chips are disabled
5. **Given** the user taps a preset and then taps another, **When** the second chip is tapped, **Then** the text field updates to the second preset's text

---

## Edge Cases

- What happens with extremely long pasted text? TextField has max 4 lines — user must scroll. Morse output handles any length (no truncation).
- What happens with special characters in presets? Presets are static strings defined in code — they contain only convertible characters.
- What happens when keyboard is open and user taps a preset? Text field updates, keyboard remains open. IME action is Done.
- What happens on screen rotation? Text is ViewModel-scoped → survives configuration change.

## Functional Requirements

- **FR-024**: System MUST provide a multiline text input (min 2, max 4 lines) with placeholder text
- **FR-025**: System MUST default text input to "Hello World" on first launch
- **FR-026**: System MUST disable text input during playback
- **FR-027**: System MUST provide a Convert button with Translate icon
- **FR-028**: System MUST disable Convert button during playback
- **FR-029**: System MUST clear error state when user types new text
- **FR-030**: System MUST provide 16 preset chips as AssistChips in a horizontally scrollable row
- **FR-031**: System MUST fill text input with preset text on chip tap
- **FR-032**: System MUST disable preset chips during playback
- **FR-033**: System MUST provide multilingual placeholder text ("Type any text" / "输入任意文本")

## Non-Functional Requirements

- **Performance**: Text field updates synchronously via `onValueChange`. Preset chip taps respond within < 100ms.
- **Security**: N/A — text input is local only.
- **Accessibility**: Text field has content description. Preset chips have text labels. Input field supports IME actions. Placeholder text available in English and Chinese.
- **Observability**: `uiState.textInput` reflects current text. `uiState.error` reflects current error state.

## Success Criteria

- **SC-009**: Users can type or paste text and convert it within 2 taps (type → Convert)
- **SC-010**: All 16 preset chips load the correct text into the input field
- **SC-011**: Text input is correctly disabled during playback (verified visually and functionally)

## Assumptions

- Standard Android keyboard behavior (IME, text selection, clipboard) is sufficient
- Material 3 AssistChip component is available in the Compose version used
- 16 presets cover the most common Morse code phrases for amateur radio and emergency use
- Users will primarily use the text field; presets are a convenience, not a primary interaction
