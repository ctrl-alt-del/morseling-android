# Morseling

An Android app that converts text to Morse code and plays it via audio beeps or camera flashlight. Supports Chinese characters through Pinyin transliteration.

## Features

- **Text → Morse** — type any text, convert to Morse with one tap
- **Pinyin for Chinese** — auto-transliterates CJK characters to Pinyin, then Morse
- **Audio playback** — listen to Morse code via tone generator
- **Flashlight playback** — blink the camera flash as Morse code
- **WPM speed control** — adjustable playback speed (5–40 WPM)
- **16 quick presets** — S.O.S., CQ, Hello, 73, Mayday, TU, HW, and more
- **Character highlighting** — real-time highlight sweeps across the Morse code during playback
- **History** — past conversions stored with favorites toggle, reorder on replay, clear with confirmation
- **Settings** — default playback mode, default speed, language (English / 简体中文)
- **Open source licenses** — viewable in-app

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3 (dynamic color)
- **Architecture:** MVVM + UDF (ViewModel + StateFlow)
- **DI:** Hilt
- **Persistence:** Room (history), DataStore (settings)
- **Pinyin:** TinyPinyin
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 35

## Build & Test

```bash
# Debug build
gradle assembleDebug

# Unit tests
gradle test

# All
gradle assembleDebug assembleAndroidTest test
```

## Project Structure

```
com.morseling/
├── audio/          MorsePlayer, MorseCodeConverter, FlashlightMorsePlayer
├── data/           Room (ConversionHistory*), DataStore (SettingsRepository)
├── di/             Hilt modules
├── model/          PlaybackUiState, PlaybackMode, AppSettings
├── navigation/     MorselingNavGraph
├── ui/             MainScreen, HistoryScreen, SettingsScreen, LicensesScreen
├── util/           MorseTranslator (text→Morse + Pinyin)
├── viewmodel/      MorseConverterVM, HistoryVM, SettingsVM
├── MainActivity.kt
└── MorselingApplication.kt
```

See [AGENTS.md](AGENTS.md) for detailed architecture docs and coding conventions.

## License

MIT
