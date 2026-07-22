---
last_updated: "2026-07-22"
---

# Feature Index

| ID | Feature | Status | Touches | Depends On |
|----|---------|--------|---------|------------|
| 001 | text-to-morse-conversion | ✅ Done | MorseTranslator.kt, MorseConverterViewModel.kt, MainScreen.kt, PlaybackUiState.kt | — |
| 002 | morse-playback | ✅ Done | MorsePlayer.kt, MorseCodeConverter.kt, FlashlightMorsePlayer.kt, MorseConverterViewModel.kt, MainScreen.kt, PlayerModule.kt, PlaybackMode.kt, PlaybackUiState.kt, AndroidManifest.xml | 001 |
| 003 | text-input-and-presets | ✅ Done | MainScreen.kt, MorseConverterViewModel.kt, PlaybackUiState.kt, strings.xml | 001 |
| 004 | conversion-history | ✅ Done | ConversionHistoryEntity.kt, ConversionHistoryDao.kt, ConversionDatabase.kt, ConversionRepository.kt, HistoryScreen.kt, HistoryViewModel.kt, MorseConverterViewModel.kt, MorselingNavGraph.kt, DatabaseModule.kt | 001, 005 |
| 005 | settings | ✅ Done | SettingsProvider.kt, SettingsRepository.kt, AppSettings.kt, SettingsScreen.kt, SettingsViewModel.kt, MorseConverterViewModel.kt, MainActivity.kt, SettingsModule.kt | 001, 002 |
| 006 | theme-and-navigation | ✅ Done | Color.kt, Shape.kt, Theme.kt, Type.kt, MorselingNavGraph.kt, LicensesScreen.kt, MainActivity.kt | 001, 002, 003, 004, 005 |
| 007 | app-infrastructure | ✅ Done | MorselingApplication.kt, MainActivity.kt, PlayerModule.kt, DatabaseModule.kt, SettingsModule.kt, ConversionDatabase.kt, SettingsRepository.kt, MorseConverterViewModel.kt, AndroidManifest.xml, build.gradle.kts | 001, 002, 004, 005, 006 |
| 008 | fast-ci-sdk37 | ✅ Done | ci.yml, app/build.gradle.kts, libs.versions.toml, settings.gradle.kts, build.gradle.kts | — |

Status: 📋 Planned → 🚧 In Progress → ✅ Done → 📦 Archived
