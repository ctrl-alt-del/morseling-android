# Morse Playback — Takeaways

## What Went Well

- **Flow-based playback is elegant** — `Flow<Int>` for character indices separates timing logic (player) from rendering logic (UI). Each layer has a clear responsibility.
- **Interface + two implementations** — the Strategy pattern with Hilt `@Named` qualifiers lets DI handle player selection. No conditional logic in ViewModel — just `activePlayer` reference.
- **Job guard pattern** — `if (playJob === coroutineContext[Job])` in finally prevents the single most confusing coroutine bug (stale coroutine resetting state). Discovered during testing, not after release.
- **Paris timing standard** — `1200/wpm` with dot=1, dash=3, space=3, slash=7 is mathematically clean and gives natural-sounding playback.

## What We Learned

- **CancellationException must come BEFORE the generic Exception catch** — Kotlin's `CancellationException` extends `IllegalStateException`, not `Exception`, but catching it separately is the safest practice. Catching it as `Exception` would show "Playback failed" on every stop.
- **ToneGenerator volume is not user-configurable** — uses STREAM_NOTIFICATION at max volume. Users expect volume control. Could consider adding a volume slider in future.
- **Flashlight on many devices has startup lag** — first `setTorchMode(true)` on some devices has 50-200ms delay. This affects timing of very fast dots. The timing loop accounts for this by starting the torch before the delay period.
- **FakeMorsePlayer in tests must respect the Flow contract** — emitting `input.indices` sequentially lets ViewModel tests verify that `playingCharIndex` updates correctly without mocking timing.

## API / Tech Surprises

- **CameraManager needs Context** — `FlashlightMorsePlayer` takes a `Context` parameter for `CameraManager.getCameraIdList()`. Hilt provides this automatically via `@ApplicationContext`.
- **ToneGenerator has limited volume levels** — the constructor's `durationMs` parameter is actually volume (0-100), not duration. Naming mismatch in Android SDK docs.
- **setTorchMode is asynchronous** — the method returns immediately but the torch may not activate for 50-200ms. This means the first dot in a sequence may be slightly truncated on slow devices.

## Patterns Worth Reusing

- **Interface + @Named qualifiers for strategy selection** — cleaner than factory pattern or manual switch/when. Hilt wires the right implementation based on configuration.
- **Flow<Int> for indexed streaming** — any operation that produces a sequence of index positions can use this pattern. UI just collects and reacts.
- **finally block with job guard** — `if (playJob === coroutineContext[Job]) { cleanup }` is worth using in any scenario where multiple coroutine instances might race.
- **Static sanitize + durations as companion functions** — pure functions with no state are independently testable. `MorseCodeConverterTest` doesn't need a ToneGenerator.
