# Settings — Takeaways

## What Went Well

- **DataStore with Flow collection** — the reactive pattern automatically propagates settings changes. No manual refresh or intent broadcasts needed.
- **SettingsProvider interface for testability** — `FakeSettingsProvider` with `MutableStateFlow` lets tests control settings emission. No real DataStore in unit tests.
- **FilterChips for mode** — Material 3 FilterChip is the right component for mutually-exclusive options. Tapping one deselects the other. Clear visual feedback.
- **WPM slider with steps** — `steps = 6` on a 5-40 range gives clean 5-WPM increments. Users can't set fractional WPM, which matches real-world Morse practice.

## What We Learned

- **Language change requires activity.recreate()** — `AppCompatDelegate.setApplicationLocales()` alone doesn't re-render existing Composables. Must call `recreate()` for immediate effect.
- **`runBlocking` is acceptable only in onCreate** — the startup locale read blocks the main thread for ~5ms. Anywhere else would cause jank. This is noted as a ⚡ guardrail.
- **DataStore writes need await before recreate** — `setLanguageAndAwait()` ensures the DataStore write completes before `recreate()`. Without awaiting, the new language might not be read on restart.
- **Flash fallback is ViewModel responsibility, not DataStore** — when flash is selected but unavailable, the ViewModel falls back to Audio but doesn't change the DataStore value. This preserves user intent (they want Flash) while preventing broken state.

## API / Tech Surprises

- **DataStore keys are typed** — `stringPreferencesKey()`, `intPreferencesKey()`, etc. Each key is its own type. Can't accidentally write a string to an int key.
- **AppCompatDelegate.setApplicationLocales() requires API 33+ for per-app locale** — the API used has decent backward compatibility via AppCompat.
- **BuildConfig.VERSION_NAME needs `buildConfig = true`** — the Compose build feature flag enables BuildConfig generation. Without it, VERSION_NAME is unavailable.

## Patterns Worth Reusing

- **`SettingsProvider` interface + `FakeSettingsProvider`** — extract settings behind an interface. Tests use a fake that emits configurable values. Production uses DataStore.
- **Reactive init collection** — `viewModelScope.launch { settings.collect { ... } }` in init automatically applies changes. No manual trigger needed.
- **Await-before-recreate** — for operations that need persistence before lifecycle events: suspend until write confirms, then trigger the event.
- **Slider with `steps`** — discrete steps on a continuous slider give precise control without overwhelming users.
