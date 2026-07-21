# File Organization
**Source files**: `app/src/main/java/com/morseling/`, `app/src/test/java/com/morseling/`, `app/src/androidTest/java/com/morseling/`
**Depends on**: [[knowledge/conventions/naming]]
**Depended on by**: [[knowledge/conventions/testing]]
**See also**: [[knowledge/architecture/components]]
**Tags**: #conventions #organization #structure

## Source layout

```
app/src/main/java/com/morseling/
├── audio/              ← Playback implementations
│   ├── MorsePlayer.kt          (interface)
│   ├── MorseCodeConverter.kt   (audio impl)
│   └── FlashlightMorsePlayer.kt (flash impl)
├── data/               ← Persistence layer
│   ├── ConversionHistoryEntity.kt  (Room entity)
│   ├── ConversionHistoryDao.kt     (Room DAO)
│   ├── ConversionDatabase.kt       (Room DB)
│   ├── ConversionRepository.kt     (Repository)
│   ├── SettingsProvider.kt         (Interface)
│   ├── SettingsRepository.kt       (DataStore impl)
│   └── AppSettings.kt             (Settings model)
├── di/                 ← Hilt dependency injection
│   ├── PlayerModule.kt
│   ├── DatabaseModule.kt
│   └── SettingsModule.kt
├── model/              ← Domain models shared across packages
│   ├── PlaybackUiState.kt
│   └── PlaybackMode.kt
├── navigation/         ← Compose Navigation
│   └── MorselingNavGraph.kt
├── ui/                 ← Compose screens + theme
│   ├── MainScreen.kt
│   ├── HistoryScreen.kt
│   ├── SettingsScreen.kt
│   ├── LicensesScreen.kt
│   └── theme/
│       ├── Color.kt
│       ├── Shape.kt
│       ├── Theme.kt
│       └── Type.kt
├── util/               ← Utility/helper code
│   └── MorseTranslator.kt
├── viewmodel/          ← MVVM ViewModels
│   ├── MorseConverterViewModel.kt
│   ├── HistoryViewModel.kt
│   └── SettingsViewModel.kt
├── MainActivity.kt     ← Entry point
└── MorselingApplication.kt ← Hilt app class
```

## Resource organization

```
app/src/main/res/
├── values/
│   ├── strings.xml           ← English strings (52 entries)
│   └── themes.xml            ← Base theme
├── values-zh-rCN/
│   └── strings.xml           ← Chinese strings (52 entries)
├── drawable/
│   ├── ic_launcher_background.xml
│   └── ic_launcher_foreground.xml
└── mipmap-anydpi-v26/
    ├── ic_launcher.xml
    └── ic_launcher_round.xml
```

## Test layout

```
app/src/test/java/com/morseling/         ← Unit tests (JVM)
├── audio/
│   └── MorseCodeConverterTest.kt
├── util/
│   └── MorseTranslatorTest.kt
└── viewmodel/
    └── MorseConverterViewModelTest.kt

app/src/androidTest/java/com/morseling/  ← Instrumented tests
└── ui/
    └── MainScreenTest.kt
```

## Key organizational principles

- **Package-per-feature** — each package has a single responsibility
- **Interface + implementation co-location** — `MorsePlayer.kt` (interface) lives alongside `MorseCodeConverter.kt` and `FlashlightMorsePlayer.kt` in `audio/`
- **Test mirroring** — test files mirror the source package structure
- **No `shared/` or `common/`** packages — utilities go in `util/`, abstractions go in their domain package
- **Theme files** in `ui/theme/` sub-package
