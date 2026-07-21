# Naming Conventions
**Source files**: `app/src/main/java/com/morseling/`
**Depends on**: [[knowledge/architecture/overview]]
**Depended on by**: [[knowledge/conventions/file-organization]], [[knowledge/conventions/testing]]
**See also**: [[knowledge/patterns/common]]
**Tags**: #conventions #naming #kotlin

## File naming

| Pattern | Example |
|---------|---------|
| `*Screen.kt` | `MainScreen.kt`, `HistoryScreen.kt`, `SettingsScreen.kt` |
| `*ViewModel.kt` | `MorseConverterViewModel.kt`, `HistoryViewModel.kt` |
| `*Player.kt` | `MorsePlayer.kt`, `FlashlightMorsePlayer.kt` |
| `*Repository.kt` | `ConversionRepository.kt`, `SettingsRepository.kt` |
| `*Module.kt` | `PlayerModule.kt`, `DatabaseModule.kt`, `SettingsModule.kt` |
| `*Entity.kt` | `ConversionHistoryEntity.kt` |
| `*Dao.kt` | `ConversionHistoryDao.kt` |
| `*Database.kt` | `ConversionDatabase.kt` |

## Kotlin conventions

- **PascalCase**: classes, interfaces, objects, enums, composable functions
- **camelCase**: regular functions, properties, parameters, local variables
- **UPPER_SNAKE_CASE**: companion object constants (e.g., `DEFAULT_TEXT`, `validMorseChars`, `KEY_LANGUAGE`)
- **No wildcard imports** — enforcement configured

## Composable naming

- Composable functions use PascalCase (standard Compose convention)
- Private sub-components prefixed with composable name context (e.g., `HighlightedMorseText`)
- Screen composables match their file name: `MainScreen()`, `HistoryScreen()`, etc.

## Test naming

- Test file: `*Test.kt` suffix in matching package structure
- Test class: `*Test` suffix (e.g., `MorseTranslatorTest`, `MorseConverterViewModelTest`)
- Test methods: descriptive camelCase (e.g., `testHasConvertibleCharactersReturnsTrue()`, `testSetWpmClampsToMinimum()`)

## Hilt naming

- Module files: `*Module.kt` (e.g., `PlayerModule`)
- `@Named("audio")` and `@Named("flash")` qualifiers for distinguishing `MorsePlayer` implementations
- `@Provides` methods: descriptive lowercase (e.g., `provideConversionDatabase`, `provideSettingsDataStore`)
