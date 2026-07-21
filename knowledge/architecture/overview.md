# Architecture Overview
**Source files**: `app/src/main/java/com/morseling/`
**Depends on**: none (this overview depends on all components)
**Depended on by**: [[knowledge/data/entities]], [[knowledge/apis/interfaces]], [[knowledge/conventions/testing]]
**See also**: [[knowledge/architecture/components]], [[knowledge/architecture/data-flow]]
**Tags**: #architecture #mvvm #hilt #compose

## What it does

Morseling uses **MVVM with Unidirectional Data Flow (UDF)**. ViewModels expose `StateFlow<T>`, Compose screens observe them via `collectAsStateWithLifecycle()`, and user actions flow through ViewModel methods back into the StateFlow.

## How it works

```
┌─────────────────────────────────────────────────────┐
│  Compose Screen (MainScreen, HistoryScreen, etc.)   │
│  ┌──────────────┐  ┌──────────────┐                 │
│  │ UI Events     │  │ State        │                 │
│  │ (onClick,     │  │ (StateFlow)  │                 │
│  │  onValueChange)│  │             │                 │
│  └──────┬───────┘  └──────▲──────┘                 │
│         │                  │                         │
│         ▼                  │ collectAsStateWithLifecycle()
│  ┌─────────────────────────┴───────┐                │
│  │         ViewModel               │                │
│  │  ┌──────────────┐  ┌──────────┐ │               │
│  │  │ Business logic│  │ State    │ │               │
│  │  │ (play, convert,│  │ (Mutable │ │               │
│  │  │  save, etc.)  │  │ StateFlow)│ │               │
│  │  └──────┬───────┘  └──────────┘ │               │
│  │         │                        │               │
│  │         ▼                        │               │
│  │  ┌──────────────┐                │               │
│  │  │ Repository   │                │               │
│  │  │ + DataStore  │                │               │
│  │  │ + Room       │                │               │
│  │  └──────────────┘                │               │
│  └──────────────────────────────────┘               │
└─────────────────────────────────────────────────────┘
```

## Top-level modules

| Module | Type | Role |
|--------|------|------|
| `audio/` | Feature | Morse code audio + flashlight playback |
| `data/` | Data | Room DB, DataStore, repositories |
| `di/` | DI | Hilt modules for player, database, settings |
| `model/` | Domain | UI state classes, enums |
| `navigation/` | Routing | Compose Navigation graph |
| `ui/` | Presentation | Composable screens + theme |
| `util/` | Utility | Text-to-Morse translation + Pinyin |
| `viewmodel/` | ViewModel | MVVM ViewModels with StateFlow |

## Entry points

- `MainActivity.kt` — Android Activity entry, locale application, edge-to-edge
- `MorselingApplication.kt` — `@HiltAndroidApp` initialization
- `MorselingNavGraph.kt` — Navigation graph, `main` is start destination

## Module boundaries

- **audio ↔ viewmodel**: `MorsePlayer` interface (with Flow<Int> for character highlighting)
- **data ↔ viewmodel**: `ConversionRepository` (Room) + `SettingsProvider` (DataStore)
- **ui ↔ viewmodel**: `StateFlow<PlaybackUiState>` / `StateFlow<List<...>>` via `collectAsStateWithLifecycle()`
- **di → all**: Hilt provides dependencies, no module directly depends on di package

## Key architectural decisions

1. **UDF via StateFlow** — Bidirectional data flow is prohibited. Screens only observe state and emit events.
2. **Fake doubles, not mocks** — Tests use hand-written fake classes (FakeMorsePlayer, FakeDao, FakeSettingsProvider) instead of Mockito/MockK.
3. **Single Activity** — No fragments. All navigation is Compose-based.
4. **Reactive settings** — ViewModel.init collects DataStore Flow reactively rather than reading once.
