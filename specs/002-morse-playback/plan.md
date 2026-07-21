---
feature_id: "002"
name: "Morse Playback"
status: "✅ Done"
depends_on: ["001"]
touches:
  - "app/src/main/java/com/morseling/audio/MorsePlayer.kt"
  - "app/src/main/java/com/morseling/audio/MorseCodeConverter.kt"
  - "app/src/main/java/com/morseling/audio/FlashlightMorsePlayer.kt"
  - "app/src/main/java/com/morseling/viewmodel/MorseConverterViewModel.kt"
  - "app/src/main/java/com/morseling/ui/MainScreen.kt"
  - "app/src/main/java/com/morseling/di/PlayerModule.kt"
  - "app/src/main/java/com/morseling/model/PlaybackMode.kt"
  - "app/src/main/java/com/morseling/model/PlaybackUiState.kt"
  - "app/src/main/java/com/morseling/AndroidManifest.xml"
  - "app/src/test/java/com/morseling/audio/MorseCodeConverterTest.kt"
  - "app/src/test/java/com/morseling/viewmodel/MorseConverterViewModelTest.kt"
created: "2026-07-21"
---

# Morse Playback — Implementation Plan

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Android ToneGenerator (SDK), Camera2 API (SDK), Kotlin Coroutines
**Storage**: N/A (playback is transient)
**Testing**: JUnit 4 + kotlinx-coroutines-test (unit), Compose UI Test (instrumented)
**Target Platform**: Android 26+ (Android 8.0+)
**Performance Goals**: Playback timing ±5ms, UI highlight no dropped frames
**Constraints**: CAMERA permission optional, flash feature `required=false`, coroutine-based with CancellationException handling
**Scale/Scope**: Single-user, local device hardware only

## Constitution Check

- [x] **Article I (Spec-First)**: spec.md written. Retroactive.
- [x] **Article II (Test-First)**: Tests exist: MorseCodeConverterTest (3), MorseConverterViewModelTest (playback tests). Retroactive.
- [x] **Article III (Modularity)**: Clear boundaries: `audio/` package for playback, `MorsePlayer` interface for contract.
- [x] **Article IV (Simplicity)**: 6 new files (MorsePlayer interface + 2 impls + PlaybackMode + DI module + ViewModel changes). Slightly over 5, but justified by interface pattern.
- [x] **Article V (Anti-Abstraction)**: MorsePlayer interface has 2 implementations (audio + flash) — justified.
- [x] **Article VI (Integration Reality)**: FakeMorsePlayer used in tests (external Android APIs like ToneGenerator are the boundary).
- [x] **Article VII (Observability)**: Playback state in StateFlow, character index emitted, errors surfaced.

## Approach

Playback uses the **Strategy pattern**: a `MorsePlayer` interface with two implementations (`MorseCodeConverter` for audio, `FlashlightMorsePlayer` for flash). The active player is selected by `PlaybackMode` enum. Both implementations produce the same `Flow<Int>` output (character index per symbol).

Timing follows the **Paris standard**: dot = 1 unit, dash = 3 units, letter gap = 3 units, word gap = 7 units. Unit duration = `1200 / wpm` ms.

The ViewModel orchestrates playback:
1. Validates Morse output
2. Selects active player
3. Launches a coroutine that collects the player's Flow
4. Updates `playingCharIndex` on each emission
5. Handles CancellationException (stop) vs Exception (error)
6. Uses job guard in finally to prevent stale cleanup

## Data Model

| Entity | Key Attributes | Relationships |
|--------|---------------|---------------|
| MorsePlayer | `play(input, wpm): Flow<Int>`, `isAvailable(): Boolean`, `release()` | Interface, 2 implementations |
| PlaybackMode | `AUDIO`, `FLASH` | Enum, drives player selection |
| PlaybackUiState | `isPlaying: Boolean`, `playingCharIndex: Int?`, `wpm: Int`, `playbackMode: PlaybackMode`, `isFlashAvailable: Boolean` | UI state for playback |

## API / CLI Contract

```kotlin
interface MorsePlayer {
    fun play(input: String, wpm: Int): Flow<Int>  // Sanitized Morse string → character index emissions
    fun isAvailable(): Boolean                       // Hardware check
    fun release()                                    // Resource cleanup
}
```

## Alternatives Considered

| Alternative | Pros | Cons | Why Rejected |
|-------------|------|------|--------------|
| Single player class with mode parameter | Simpler, fewer files | Mode-specific logic mixed; flash availability check complex | Separated into interface+impls for clean separation |
| Android MediaPlayer for audio | More audio formats | Overhead for simple beeps; no precise timing control | ToneGenerator is simpler for single-frequency tones |
| CountDownTimer for timing | Familiar API | Callback-based; doesn't compose with coroutines | Coroutine `delay()` is cleaner and cancellable |
| LiveData for character highlighting | Lifecycle-aware | More boilerplate; doesn't compose with Flow | Flow is more natural for sequential emissions |

**Decision**: `MorsePlayer` interface with coroutine-based `Flow<Int>` playback. Clean separation, cancellable, composes with StateFlow.

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| ToneGenerator not available | Low | High | ToneGenerator is in Android SDK since API 1; extremely unlikely on API 26+ |
| Camera flash permissions denied | Medium | Medium | Flash marked `required=false`; app works without it; fallback to audio |
| Flash left on after crash | Low | High | `finally` block ensures torch off regardless of how playback ends |
| Rapid play/stop causing state corruption | Medium | Medium | Job guard (`playJob === coroutineContext[Job]`) prevents stale finally from resetting state |

## Dependencies

| Depends On | Status | Blocking? |
|-----------|--------|-----------|
| 001-text-to-morse-conversion | ✅ Done | No |
| Android Camera2 API (SDK 21+) | Ready | No |
| Android ToneGenerator (SDK 1+) | Ready | No |
| Hilt DI | Ready | No |

## Complexity Tracking

| Article | Violation | Why Needed | Simpler Alternative Rejected Because |
|---------|-----------|------------|--------------------------------------|
| Art. IV | 6 new files (>5 limit) | Interface + 2 implementations + DI module + model enum = 6 files. Each file has distinct responsibility. | Could merge MorsePlayer.kt + MorseCodeConverter.kt into one file, but interface separate is better for testability and clarity |

## Files to Create / Change

| Action | File | Rationale |
|--------|------|-----------|
| Create | `audio/MorsePlayer.kt` | Interface contract for playback implementations |
| Create | `audio/MorseCodeConverter.kt` | Audio playback via ToneGenerator |
| Create | `audio/FlashlightMorsePlayer.kt` | Flashlight playback via Camera2 |
| Create | `model/PlaybackMode.kt` | Enum for audio/flash mode selection |
| Modify | `model/PlaybackUiState.kt` | Add playback state fields (isPlaying, playingCharIndex, wpm, playbackMode, isFlashAvailable) |
| Modify | `viewmodel/MorseConverterViewModel.kt` | Add play(), stop(), setWpm(), setPlaybackMode(), activePlayer logic |
| Create | `di/PlayerModule.kt` | Hilt module providing @Named audio/flash players |
| Modify | `ui/MainScreen.kt` | Add Play/Stop buttons, HighlightedMorseText, AnimatedContent |
| Modify | `AndroidManifest.xml` | Add CAMERA permission, flash feature (required=false) |
| Create | `audio/MorseCodeConverterTest.kt` | Unit tests for sanitize/durations |
| Modify | `viewmodel/MorseConverterViewModelTest.kt` | Add playback test cases |

## Quickstart Validation

1. Convert "SOS" → tap Play → hear `... --- ...` beeps → output highlights character by character
2. Tap Stop during playback → playback stops immediately, output returns to static
3. Settings → switch to Flash mode → main screen → play → flashlight blinks pattern
4. Settings → set WPM to 5 → play → dots are clearly slow (240ms each)
5. Settings → set WPM to 40 → play → dots are fast (30ms each)
6. Tap Play with empty Morse → error "Enter Morse code to play"
7. Verify flash unavailable on emulator → Flash mode not selectable or falls back to Audio
