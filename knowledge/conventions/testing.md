# Testing Conventions
**Source files**: `app/src/test/java/com/morseling/`, `app/src/androidTest/java/com/morseling/`
**Depends on**: [[knowledge/conventions/naming]], [[knowledge/conventions/file-organization]]
**Depended on by**: [[knowledge/patterns/common]]
**See also**: [[knowledge/apis/interfaces]], [[knowledge/architecture/overview]]
**Tags**: #testing #junit #compose #coroutines

## Test types

| Type | Location | Runner | Dependencies |
|------|----------|--------|-------------|
| Unit tests | `app/src/test/` | JUnit 4 | `kotlinx-coroutines-test`, Robolectric, AndroidX Test Core |
| Instrumented tests | `app/src/androidTest/` | `AndroidJUnit4` | Compose UI Test, Hilt Testing, Espresso |

## Fake test doubles pattern

**No mocking libraries (no Mockito, no MockK)**. Tests use hand-written fake classes:

```kotlin
class FakeMorsePlayer(private val available: Boolean = true) : MorsePlayer {
    val playCalls = mutableListOf<Pair<String, Int>>()
    override fun play(input: String, wpm: Int): Flow<Int> {
        playCalls.add(input to wpm)
        return flow { input.indices.forEach { emit(it) } }
    }
    override fun isAvailable() = available
    override fun release() {}
}

class FakeDao : ConversionHistoryDao {
    val items = mutableListOf<ConversionHistoryEntity>()
    // ... implements all DAO methods against items list
}

class FakeSettingsProvider : SettingsProvider {
    private val _settings = MutableStateFlow(AppSettings())
    override val settings = _settings.asStateFlow()
    // ... implements all setter methods updating _settings
}
```

## Coroutine test conventions

```kotlin
// Use StandardTestDispatcher + runTest + advanceUntilIdle()
private val testDispatcher = StandardTestDispatcher()

@Before
fun setup() {
    Dispatchers.setMain(testDispatcher)
}

@After
fun tearDown() {
    Dispatchers.resetMain()
}

@Test
fun testSomeFlow() = runTest {
    // Create ViewModel with fake dependencies
    // Trigger action
    advanceUntilIdle()
    // Assert state
}
```

## ViewModel test structure

1. Create fake dependencies (FakeMorsePlayer, FakeDao, FakeSettingsProvider)
2. Instantiate ViewModel with fakes
3. Verify initial state
4. Trigger action (e.g., `convertText()`, `play()`)
5. Advance coroutine dispatcher
6. Assert `uiState.value` matches expected

## Instrumented test conventions

```kotlin
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MainScreenTest {
    @get:Rule val hiltRule = HiltAndroidRule(this)
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun testWidgetsDisplayed() {
        composeRule.setContent { MainScreen(viewModel) }
        composeRule.onNodeWithText("Convert").assertIsDisplayed()
        composeRule.onNodeWithText("Play").assertIsDisplayed()
    }
}
```

## Test commands

```bash
gradle test                        # Unit tests (JVM)
gradle connectedAndroidTest        # Instrumented tests (requires emulator/device)
```

## Current test coverage

| Test file | Tests | Covers |
|-----------|-------|--------|
| `MorseCodeConverterTest.kt` | 3 | sanitize() replacements, length preservation |
| `MorseTranslatorTest.kt` | 9 | text→Morse, Pinyin, hasConvertibleChars, unconvertibleChars |
| `MorseConverterViewModelTest.kt` | 22 | initial state, convert text, play/stop, WPM clamping, history load, error clearing, mode switching, player delegation |
| `MainScreenTest.kt` | 3 | Convert/Play button display, text input + convert flow |
