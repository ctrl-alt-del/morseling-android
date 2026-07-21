# Gotchas & Sharp Edges
**Source files**: `app/src/main/java/com/morseling/`
**Depends on**: [[knowledge/architecture/overview]]
**Depended on by**: [[knowledge/conventions/error-handling]]
**See also**: [[knowledge/patterns/common]]
**Tags**: #gotchas #sharp-edges

## ⚡ Critical Guardrails

### 1. ⚡ runBlocking in MainActivity.onCreate for locale
**File**: `MainActivity.kt`

`applySavedLocale()` uses `runBlocking { settingsRepository.settings.first() }` to read language before `setContent`. This blocks the main thread briefly (~5-10ms for a single DataStore read). Acceptable in `onCreate` before UI is set up, but DO NOT use `runBlocking` elsewhere in the app.

### 2. ⚡ CancellationException must be caught silently in play()
**File**: `MorseConverterViewModel.kt`

The play coroutine catches `CancellationException` separately from `Exception`. If `CancellationException` was caught as a general `Exception`, pressing Stop would show a spurious "Playback failed" error.

### 3. ⚡ Job guard in finally block
**File**: `MorseConverterViewModel.kt`

```kotlin
finally {
    if (playJob === coroutineContext[Job]) {  // ← CRITICAL
        _uiState.update { ... }
    }
}
```

Without this guard, a stale coroutine's `finally` block could reset state after a new playback has already started. Always compare `playJob` reference equality with the current coroutine's Job.

### 4. ⚡ Flash availability check on construction, not on play
**File**: `FlashlightMorsePlayer.kt`

`isAvailable()` checks camera flash existence at construction time. If a camera is later disconnected (rare), playback will fail silently. `CameraAccessException` is caught during the probe.

### 5. ⚡ WPM clamping in two places
**File**: `MorseConverterViewModel.kt`

WPM is clamped via `coerceIn(5, 40)` in both `setWpm()` and the settings `collect {}` block. If a new clamp location is added, both must be updated or extracted to a single source.

## Sharp Edges

### 6. Error strings are hardcoded English in ViewModel
ViewModel errors use hardcoded strings (e.g., `"Enter text to convert"`) because ViewModels don't have `Context` access. These cannot be localized. The string resources (`strings.xml`) contain the same strings for UI labels but ViewModel errors bypass them.

### 7. ToneGenerator volume is hardcoded to 100
`ToneGenerator(ToneGenerator.TONE_DTMF_0, 100)` — volume is fixed at maximum. No user-facing volume control exists. This uses the notification stream (`STREAM_NOTIFICATION`).

### 8. License URLs are displayed but not clickable
`LicensesScreen.kt` shows URLs as primary-colored text but doesn't make them tappable (no `ClickableText` or `UriHandler`). Users can read but not open URLs.

### 9. Settings defaultMode is a raw string, not an enum
`default_mode` in DataStore is stored as `"audio"`/`"flash"` string. Enum would be safer but DataStore only supports primitive types.

### 10. Room exportSchema = false
`ConversionDatabase` sets `exportSchema = false`. Room schema migration history is unavailable, which could complicate future schema changes.

### 11. Single-delete has no confirmation dialog
Delete on history entries is immediate (tap trash → gone). Only the "clear all" action has a confirmation dialog.

### 12. Presets list is static (not user-configurable)
The 16 presets are hardcoded in `MainScreen.kt`. No way for users to add, remove, or reorder presets.
