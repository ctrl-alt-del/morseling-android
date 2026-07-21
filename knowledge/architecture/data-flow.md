# Data Flow
**Source files**: `app/src/main/java/com/morseling/viewmodel/MorseConverterViewModel.kt`, `app/src/main/java/com/morseling/ui/MainScreen.kt`, `app/src/main/java/com/morseling/audio/MorsePlayer.kt`
**Depends on**: [[knowledge/architecture/overview]], [[knowledge/architecture/components]]
**Depended on by**: [[knowledge/patterns/common]]
**See also**: [[knowledge/apis/interfaces]]
**Tags**: #architecture #data-flow #stateflow #udf

## Text conversion flow

```
User types text
  │
  ▼
MainScreen OutlinedTextField → updateTextInput(text)
  │
  ▼
MorseConverterViewModel.updateTextInput()
  ├── Clears error state
  └── Updates _uiState.value = current.copy(textInput = text)
  │
  ▼
StateFlow emission → MainScreen recomposes (TextField value synced)
  │
  ▼
User taps Convert button
  │
  ▼
MorseConverterViewModel.convertText()
  ├── Validates: empty? → error; no convertible chars? → error
  ├── Calls MorseTranslator.textToMorse(input)
  │   ├── Converts each char: uppercase → CHAR_TO_MORSE lookup
  │   ├── CJK chars → TinyPinyin.toPinyin() → Morse mapping
  │   └── Returns Morse string with " / " word separators
  ├── Updates _uiState: morseOutput = result
  ├── If unconvertible chars found → also sets warning error
  └── Saves to history: repository.saveConversion(text, morseOutput)
  │
  ▼
StateFlow emission → MainScreen recomposes (Morse output displayed)
```

## Playback flow

```
User taps Play
  │
  ▼
MorseConverterViewModel.play()
  ├── Validates morseOutput: empty? → error; invalid chars? → error
  ├── Selects active player (audio or flash based on playbackMode)
  ├── Launches play coroutine:
  │   └── player.play(sanitized morse, wpm).collect { index →
  │         _uiState.update { copy(playingCharIndex = index) }
  │       }
  ├── Updates _uiState: isPlaying = true
  │
  ▼
StateFlow emission → MainScreen recomposes
  ├── HighlightedMorseText shows at playingCharIndex
  ├── Stop button appears, input disabled
  │
  ▼
User taps Stop (or playback completes naturally)
  │
  ▼
playJob.cancel()
  ├── CancellationException caught silently in play coroutine
  └── Finally block: if (playJob === coroutineContext[Job])
        _uiState.update { copy(isPlaying = false, playingCharIndex = null) }
  │
  ▼
StateFlow emission → MainScreen returns to static Morse display
```

## Settings flow

```
App startup
  │
  ▼
MainActivity.applySavedLocale()
  ├── runBlocking { settingsRepository.settings.first() }
  ├── If language != "system": AppCompatDelegate.setApplicationLocales()
  └── Returns before setContent
  │
  ▼
MorseConverterViewModel.init {}
  ├── Collects settingsRepository.settings reactively:
  │   settings.collect { appSettings →
  │     setWpm(appSettings.defaultWpm)
  │     setPlaybackMode(appSettings.defaultMode)
  │     // fallback: if flash selected but flash not available → AUDIO
  │   }
```

## History flow

```
Convert completes → repository.saveConversion(text, morse)
  ├── findByTextInput(text) → found? → updateTimestamp(now)
  │                                not found? → insert new entity
  │
  ▼
HistoryViewModel → collects repository.getAll() as Flow
  ├── Room emits on any table change
  └── HistoryScreen observes via collectAsStateWithLifecycle()
  │
  ▼
User taps history entry → onItemClick(textInput, morseOutput)
  ├── mainViewModel.loadFromHistory(textInput, morseOutput)
  ├── Re-saves entry (updates timestamp, moves to top)
  ├── Navigates back to main screen
  └── Main screen populated with selected text/morse
```
