---
feature_id: "001"
name: "Text-to-Morse Conversion"
status: "✅ Done"
depends_on: []
touches:
  - "app/src/main/java/com/morseling/util/MorseTranslator.kt"
  - "app/src/main/java/com/morseling/viewmodel/MorseConverterViewModel.kt"
  - "app/src/main/java/com/morseling/ui/MainScreen.kt"
  - "app/src/test/java/com/morseling/util/MorseTranslatorTest.kt"
  - "app/src/test/java/com/morseling/viewmodel/MorseConverterViewModelTest.kt"
  - "app/src/main/java/com/morseling/model/PlaybackUiState.kt"
created: "2026-07-21"
---

# Text-to-Morse Conversion — Implementation Plan

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Jetpack Compose, TinyPinyin 2.0.3
**Storage**: N/A (conversion is pure computation, no persistence needed)
**Testing**: JUnit 4 + kotlinx-coroutines-test
**Target Platform**: Android 26+ (Android 8.0+)
**Performance Goals**: Conversion synchronous (< 10ms for < 500 chars)
**Constraints**: Fully offline, no network access, main thread execution
**Scale/Scope**: Single-user, local-only conversion

## Constitution Check

*GATE: Must pass before implementation. Re-check after design changes.*

- [x] **Article I (Spec-First)**: spec.md written. Retroactive — feature already exists.
- [x] **Article II (Test-First)**: Tests exist: MorseTranslatorTest (9 tests), MorseConverterViewModelTest (conversion tests). Retroactive — tests written after code.
- [x] **Article III (Modularity)**: Clear boundaries: `util/MorseTranslator.kt` is a pure function object with no dependencies.
- [x] **Article IV (Simplicity)**: 2 new source files (MorseTranslator, PlaybackUiState). Within 5-file limit.
- [x] **Article V (Anti-Abstraction)**: No unnecessary interfaces. MorseTranslator is a Kotlin `object` (singleton). TinyPinyin used directly.
- [x] **Article VI (Integration Reality)**: Unit tests use real MorseTranslator, not a mock.
- [x] **Article VII (Observability)**: Conversion errors surfaced as `uiState.error` and displayed as Snackbar.

## Approach

Conversion is a pure function: `String → String`. A singleton `MorseTranslator` object contains:
1. A `CHAR_TO_MORSE` map (A-Z, 0-9 → Morse sequences)
2. `textToMorse()` — uppercases input, splits into words, maps each character
3. `toPinyinIfCjk()` — detects CJK via `Character.UnicodeScript.HAN`, calls TinyPinyin
4. `hasConvertibleChars()` / `unconvertibleChars()` — validation helpers

The ViewModel calls `MorseTranslator.textToMorse()` in response to the Convert button. No coroutines needed — conversion is synchronous (CPU-bound, no I/O). The result is stored in `PlaybackUiState.morseOutput`.

## Data Model

| Entity | Key Attributes | Relationships |
|--------|---------------|---------------|
| CHAR_TO_MORSE | Map<Char, String> | Static lookup table, 36 entries |

## API / CLI Contract

N/A — this is an internal computation, no external API.

## Alternatives Considered

| Alternative | Pros | Cons | Why Rejected |
|-------------|------|------|--------------|
| pinyin4j | More features, tone marks | Larger footprint (~2MB), less Android-friendly | TinyPinyin is smaller (~200KB) and sufficient for Pinyin→Morse |
| Coroutine-based conversion | Non-blocking, cancellable | Overhead for a sub-10ms operation | Synchronous is simpler and fast enough |
| Server-side conversion | Could support more languages | Requires network, adds latency, privacy concern | Local conversion is faster, offline-ready, and private |

**Decision**: Synchronous local conversion with TinyPinyin. Fast, offline, private, simple.

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| TinyPinyin discontinued | Low | Medium | Vendored dependency, can be replaced with alternative |
| Very long input causing ANR | Low | Medium | Max input length could be enforced if issue arises; currently no limit |
| CJK character not in TinyPinyin dictionary | Low | Low | TinyPinyin falls back to character-by-character mode; edge cases produce approximate Pinyin |

## Dependencies

| Depends On | Status | Blocking? |
|-----------|--------|-----------|
| TinyPinyin 2.0.3 | Ready | No |
| Kotlin stdlib (Character.UnicodeScript) | Ready | No |

## Complexity Tracking

None — all constitution gates pass without violations.

## Files to Create / Change

| Action | File | Rationale |
|--------|------|-----------|
| Create | `util/MorseTranslator.kt` | Core conversion logic — singleton object with CHAR_TO_MORSE map |
| Modify | `viewmodel/MorseConverterViewModel.kt` | Add convertText() method |
| Modify | `ui/MainScreen.kt` | Add Convert button and Morse output display |
| Modify | `model/PlaybackUiState.kt` | Add morseOutput, textInput fields |
| Create | `util/MorseTranslatorTest.kt` | Unit tests for conversion, Pinyin, detection |
| Modify | `viewmodel/MorseConverterViewModelTest.kt` | Add conversion test cases |

## Quickstart Validation

1. Launch app → "Hello World" displayed in text input → tap Convert → Morse output shows `.... . .-.. .-.. --- / .-- --- .-. .-.. -..`
2. Delete text → type "SOS" → tap Convert → Morse output shows `... --- ...`
3. Type "你好" → tap Convert → Morse output contains valid Morse code (Pinyin)
4. Type "!!!" → tap Convert → error "No text can be converted to Morse code" displayed
