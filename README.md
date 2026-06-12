# Morseling

An Android app that converts Morse code text to audible beeps.

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** ViewModel + StateFlow (UDF)
- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 35

## Build

```bash
# Debug build
./gradlew assembleDebug

# Run unit tests
./gradlew test

# Run instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest
```

## Usage

Enter Morse code using dots (`.`), dashes (`-`), spaces, and slashes (`/`), then tap **Convert & Play** to hear the audio playback.

Example: `... --- ...` plays the S.O.S. signal.

## License

MIT
