# Error Handling Conventions
**Source files**: `app/src/main/java/com/morseling/viewmodel/MorseConverterViewModel.kt`, `app/src/main/java/com/morseling/ui/MainScreen.kt`
**Depends on**: [[knowledge/architecture/data-flow]]
**Depended on by**: [[knowledge/patterns/common]]
**See also**: [[knowledge/edges/gotchas]]
**Tags**: #error-handling #cancellation #snackbar

## ViewModel error pattern

Errors are represented in `PlaybackUiState` as `error: String?`. Non-null indicates an error to display:

```kotlin
// Setting an error
_uiState.update { it.copy(error = "Enter text to convert") }

// Clearing an error
_uiState.update { it.copy(error = null) }
```

## Error display: Snackbar

```kotlin
LaunchedEffect(uiState.error) {
    uiState.error?.let { error ->
        snackbarHostState.showSnackbar(error)
        viewModel.clearError()
    }
}
```

Key points:
- `LaunchedEffect(uiState.error)` launches on every error change
- Snackbar shown, then error immediately cleared to prevent re-show
- Error strings are hardcoded English in ViewModel (no Context access for `stringResource()`)

## Playback error handling

```kotlin
fun play() {
    playJob = viewModelScope.launch {
        try {
            // ... validate and play
        }
        catch (e: CancellationException) {
            // Normal stop — user pressed Stop or new play started
            // SILENTLY caught, no error shown
        }
        catch (e: Exception) {
            // Real failure — show error
            _uiState.update { it.copy(error = "Playback failed") }
        }
        finally {
            // CRITICAL: guard against stale job
            if (playJob === coroutineContext[Job]) {
                _uiState.update { it.copy(isPlaying = false, playingCharIndex = null) }
            }
        }
    }
}
```

## Six distinct error states

| Error | Triggered by | Type |
|-------|-------------|------|
| `"Enter text to convert"` | Empty textInput on Convert | Blocking |
| `"No text can be converted to Morse code"` | No convertible chars in input | Blocking |
| `"Enter Morse code to play"` | Empty morseOutput on Play | Blocking |
| `"Invalid character in Morse code. Use: . - space /"` | Non-Morse chars in morseOutput | Blocking |
| `"Playback failed"` | Non-cancellation exception during playback | Blocking |
| `"Some characters could not be converted"` | Partial unconvertible chars | Warning (non-blocking, morse output still produced) |

## updateTextInput clears errors

```kotlin
fun updateTextInput(text: String) {
    _uiState.update { it.copy(textInput = text, error = null) }
}
```

Typing new text clears any prior error state.
