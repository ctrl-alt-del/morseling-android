---
feature_id: "003"
name: "Text Input & Presets"
status: "✅ Done"
depends_on: ["001"]
touches:
  - "app/src/main/java/com/morseling/ui/MainScreen.kt"
  - "app/src/main/java/com/morseling/viewmodel/MorseConverterViewModel.kt"
  - "app/src/main/java/com/morseling/model/PlaybackUiState.kt"
  - "app/src/main/res/values/strings.xml"
  - "app/src/main/res/values-zh-rCN/strings.xml"
created: "2026-07-21"
---

# Text Input & Presets — Implementation Plan

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Jetpack Compose, Material 3 (OutlinedTextField, AssistChip)
**Storage**: N/A (text input is transient in ViewModel state)
**Testing**: Compose UI Test (instrumented)
**Target Platform**: Android 26+
**Performance Goals**: Text input lag < 16ms (60fps), preset chip tap response < 100ms
**Constraints**: Single Activity, Compose-only UI
**Scale/Scope**: Single-user, text input up to 4 visible lines

## Constitution Check

- [x] **Article I (Spec-First)**: spec.md written. Retroactive.
- [x] **Article II (Test-First)**: Instrumented test covers UI elements display. Retroactive.
- [x] **Article III (Modularity)**: Text input logic is in MainScreen + MorseConverterViewModel — clear boundary.
- [x] **Article IV (Simplicity)**: 0 new files — modifications to existing files only.
- [x] **Article V (Anti-Abstraction)**: Using Compose OutlinedTextField and AssistChip directly.
- [x] **Article VI (Integration Reality)**: UI tested with Compose test rule + real composables.
- [x] **Article VII (Observability)**: textInput in StateFlow, errors surfaced.

## Approach

Text input uses a standard Material 3 `OutlinedTextField` bound to `uiState.textInput` via two-way binding. Changes flow through `viewModel.updateTextInput()` which also clears any prior error. During playback, the text field, Convert button, and preset chips are `enabled = !uiState.isPlaying`.

Presets are a static list of `data class Preset(label, text)` pairs. Each renders as an `AssistChip` in a `LazyRow`. Tapping applies the preset text to the input field.

The Convert button triggers `viewModel.convertText()` which validates input and calls `MorseTranslator.textToMorse()`.

## Data Model

| Entity | Key Attributes | Relationships |
|--------|---------------|---------------|
| Preset | label: String, text: String | Static list, no persistence |

**16 presets**:
| Label | Text |
|-------|------|
| S.O.S. | SOS |
| CQ | CQ CQ CQ |
| Hello | Hello |
| 73 | 73 |
| Mayday | Mayday |
| TU | TU |
| HW | HW |
| Happy BD | Happy BD |
| Good luck | Good luck |
| GN | GN |
| GA | GA |
| GE | GE |
| CUL | CUL |
| AR | AR |
| R | R |
| Test | Test |

## API / CLI Contract

N/A — internal UI component.

## Alternatives Considered

| Alternative | Pros | Cons | Why Rejected |
|-------------|------|------|--------------|
| SuggestiveTextField (autocomplete) | Smart suggestions | More complex, needs dictionary, overkill for simple input | Simple OutlinedTextField is sufficient |
| User-customizable presets | Personalization | Adds settings complexity, data persistence | Static presets cover 80% of use cases; customization can be a future feature |
| Dropdown/ExposedDropdownMenu for presets | Compact, traditional | Less visually distinctive than chips | Chips are more discoverable and engaging |

**Decision**: OutlinedTextField + static AssistChips in LazyRow. Simple, familiar, covers all use cases.

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Presets contain text that won't convert | Low | Low | All presets contain only A-Z, 0-9, and spaces — always convertible |
| IME covers content on small screens | Medium | Low | Scrollable content; Android's window soft input mode handles IME |
| Text input changes during playback | Low | Medium | Input disabled during playback |

## Dependencies

| Depends On | Status | Blocking? |
|-----------|--------|-----------|
| 001-text-to-morse-conversion | ✅ Done | No |
| Material 3 Compose | Ready | No |

## Complexity Tracking

None — all constitution gates pass without violations.

## Files to Create / Change

| Action | File | Rationale |
|--------|------|-----------|
| Modify | `ui/MainScreen.kt` | Add OutlinedTextField, Convert button, preset LazyRow, disabled states |
| Modify | `viewmodel/MorseConverterViewModel.kt` | Add updateTextInput() method |
| Modify | `model/PlaybackUiState.kt` | Add textInput field with DEFAULT_TEXT |
| Modify | `values/strings.xml` | Add placeholder, button, and preset strings |
| Modify | `values-zh-rCN/strings.xml` | Add Chinese translations for all strings |

## Quickstart Validation

1. App opens → "Hello World" in text field → Convert button visible → preset chips visible
2. Tap "S.O.S." chip → text field shows "SOS" → tap Convert → ". . . --- . . ." displayed
3. Type "test" in text field → text updates → Convert → Morse output updates
4. Play Morse → verify text field and Convert button are disabled
5. Stop playback → verify text field and Convert button are enabled again
6. Delete all text → tap Convert → error "Enter text to convert"
