# Common Patterns
**Source files**: `app/src/main/java/com/morseling/viewmodel/`, `app/src/main/java/com/morseling/ui/`, `app/src/main/java/com/morseling/data/`
**Depends on**: [[knowledge/architecture/overview]], [[knowledge/architecture/data-flow]]
**Depended on by**: none
**See also**: [[knowledge/conventions/error-handling]], [[knowledge/conventions/testing]]
**Tags**: #patterns #stateflow #udf #hilt #coroutines

## Pattern 1: StateFlow-based UDF

Every ViewModel follows this exact structure:

```kotlin
private val _uiState = MutableStateFlow(PlaybackUiState())
val uiState: StateFlow<PlaybackUiState> = _uiState.asStateFlow()

fun userAction() {
    viewModelScope.launch {
        // Business logic
        _uiState.update { it.copy(field = newValue) }
    }
}
```

- `_uiState` is `private` and `MutableStateFlow` — only ViewModel writes
- `uiState` is `public` and `StateFlow` — read-only for consumers
- Updates via `.update { it.copy(...) }` — always use copy, never mutate

## Pattern 2: Flow-based playback

Morse playback uses a `Flow<Int>` that emits the current character index:

```kotlin
interface MorsePlayer {
    fun play(input: String, wpm: Int): Flow<Int>  // emits char index
}

// In ViewModel:
player.play(sanitized, wpm).collect { index ->
    _uiState.update { it.copy(playingCharIndex = index) }
}
```

This enables real-time UI highlighting: each symbol played emits its index, which drives `HighlightedMorseText`.

## Pattern 3: Hilt injection with qualifiers

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object PlayerModule {
    @Provides @Singleton @Named("audio")
    fun provideAudioPlayer(): MorsePlayer = MorseCodeConverter()

    @Provides @Singleton @Named("flash")
    fun provideFlashPlayer(): MorsePlayer = FlashlightMorsePlayer(context)
}

// In ViewModel:
@Inject constructor(
    @Named("audio") private val audioPlayer: MorsePlayer,
    @Named("flash") private val flashPlayer: MorsePlayer
)
```

## Pattern 4: Reactive settings collection

```kotlin
init {
    viewModelScope.launch {
        settingsProvider.settings.collect { appSettings ->
            _uiState.update { it.copy(wpm = appSettings.defaultWpm.coerceIn(5, 40)) }
            setPlaybackMode(/* from appSettings.defaultMode, with flash fallback */)
        }
    }
}
```

Settings flow is collected reactively — any DataStore change propagates to UI immediately.

## Pattern 5: Deduplication on save

```kotlin
suspend fun saveConversion(text: String, morse: String) {
    val existing = dao.findByTextInput(text)
    if (existing != null) {
        dao.updateTimestamp(existing.id, System.currentTimeMillis())
    } else {
        dao.insert(ConversionHistoryEntity(textInput = text, morseOutput = morse, ...))
    }
}
```

Same text → update timestamp (reorder to top). Different text → insert new row.

## Pattern 6: Snackbar with LaunchedEffect

```kotlin
val snackbarHostState = remember { SnackbarHostState() }

LaunchedEffect(uiState.error) {
    uiState.error?.let { error ->
        snackbarHostState.showSnackbar(error)
        viewModel.clearError()
    }
}
```

`LaunchedEffect` keyed on error triggers on change. Error cleared immediately to prevent re-show on recomposition.
