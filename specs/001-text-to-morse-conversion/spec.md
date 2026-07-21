# Text-to-Morse Conversion — Specification

## User Stories

### User Story 1 — Basic Text-to-Morse Translation (Priority: P1 🎯 MVP)

As a user, I want to type English text and convert it to Morse code so that I can see the Morse representation of my message.

**Why this priority**: Core value proposition of the app. Without conversion, no other features matter.

**Independent Test**: Type "HELLO" → tap Convert → output is `.... . .-.. .-.. ---`. Delivers standalone value as a text-to-Morse reference tool.

**Acceptance Scenarios**:

1. **Given** the text input contains "HELLO", **When** the user taps Convert, **Then** the Morse output displays `.... . .-.. .-.. ---`
2. **Given** the text input contains "hello world", **When** the user taps Convert, **Then** the Morse output displays `.... . .-.. .-.. --- / .-- --- .-. .-.. -..` (lowercase handled, words separated by ` / `)
3. **Given** the text input contains "123", **When** the user taps Convert, **Then** the Morse output displays `.---- ..--- ...--` (numbers converted)
4. **Given** the text input is empty, **When** the user taps Convert, **Then** an error "Enter text to convert" is displayed

---

### User Story 2 — Chinese Character Pinyin Transliteration (Priority: P1 🎯 MVP)

As a Chinese-speaking user, I want to type Chinese characters and have them translated to Pinyin then Morse code so that I can convert Chinese text.

**Why this priority**: Chinese support is a key differentiator. Without it, the app is limited to Latin-script languages only.

**Independent Test**: Type "你好" → tap Convert → output is valid Morse code representing "n i h a o". Can be verified by playing the output and recognizing the sounds.

**Acceptance Scenarios**:

1. **Given** the text input contains "你好", **When** the user taps Convert, **Then** the Morse output contains valid Morse code derived from Pinyin "nihao"
2. **Given** the text input contains mixed "Hello 世界", **When** the user taps Convert, **Then** "Hello" converts to English Morse and "世界" converts to Pinyin Morse, separated by a word slash
3. **Given** the text input contains only Chinese characters, **When** the user taps Convert, **Then** all characters are Pinyinized and converted to Morse

---

### User Story 3 — Convertible Character Detection (Priority: P2)

As a user, I want clear feedback when my input contains characters that cannot be converted so that I understand why conversion appears incomplete.

**Why this priority**: Improves UX by preventing confusion. Core conversion works without it (users would just see no output or partial output).

**Independent Test**: Type "!!!" → tap Convert → error shown. Type "Hello!!!World" → warning shown but Morse output still produced for "Hello" and "World".

**Acceptance Scenarios**:

1. **Given** the text input contains only punctuation "!!!", **When** the user taps Convert, **Then** error "No text can be converted to Morse code" is displayed
2. **Given** the text input contains "Hello!!!" with some convertible chars, **When** the user taps Convert, **Then** Morse output is produced for "Hello" and warning "Some characters could not be converted" is displayed
3. **Given** the text input contains only whitespace, **When** the user taps Convert, **Then** error "Enter text to convert" is displayed

---

## Edge Cases

- What happens when input is extremely long (10,000+ characters)? Conversion runs synchronously on the main thread — could cause UI jank.
- What happens with emoji-only input? `hasConvertibleChars()` returns false → error displayed.
- What happens with mixed RTL+LTR text? Only A-Z/0-9/CJK are converted; others pass through. RTL characters are reversed in the Latin Morse output which uses LTR rendering.
- What happens with surrogate pair characters (emoji, rare CJK)? `text.codePoints()` iteration handles surrogate pairs correctly.

## Functional Requirements

- **FR-001**: System MUST convert uppercase A-Z and digits 0-9 to their Morse code equivalents using standard International Morse Code
- **FR-002**: System MUST handle lowercase input by uppercasing before lookup
- **FR-003**: System MUST separate words with `" / "` (space-slash-space) in Morse output
- **FR-004**: System MUST separate characters within a word with a single space in Morse output
- **FR-005**: System MUST detect CJK (Han script) characters and transliterate them to Pinyin using TinyPinyin before Morse conversion
- **FR-006**: System MUST check for convertible characters before conversion and reject input with no convertible characters
- **FR-007**: System MUST warn when some characters cannot be converted while still producing Morse output for the rest
- **FR-008**: System MUST skip whitespace when counting convertible characters
- **FR-009**: System MUST use `text.codePoints()` for proper surrogate pair handling

## Non-Functional Requirements

- **Performance**: Conversion completes synchronously in < 10ms for typical input (< 500 chars). Pinyin lookup may add ~5ms per CJK character.
- **Security**: No network access — all conversion is local. No user data leaves the device.
- **Accessibility**: Convert button has content description. Morse output is displayed in monospace font for readability. Error messages are visible as snackbars.
- **Observability**: Conversion errors set `uiState.error` string. Warning messages displayed as snackbar.
- **Offline**: Fully offline — no network requests needed for conversion.

## Success Criteria

- **SC-001**: Users can convert any valid English or Chinese text to Morse code within 2 seconds of typing
- **SC-002**: 100% of characters in the A-Z/0-9 range map to correct Morse sequences
- **SC-003**: Chinese text produces recognizably correct Pinyin-based Morse (verified against established Pinyin→Morse mapping)
- **SC-004**: All 6 error/warning states trigger correctly for their respective scenarios

## Assumptions

- Users understand basic Morse code notation (. for dot, - for dash, / for word separator)
- The International Morse Code character set (A-Z, 0-9) is the canonical reference
- TinyPinyin accurately handles all CJK characters in the Unicode Han script range
- Punctuation and special characters are explicitly out of scope — they are passed through as-is
- No user-configurable Morse mappings — the character → Morse table is fixed
