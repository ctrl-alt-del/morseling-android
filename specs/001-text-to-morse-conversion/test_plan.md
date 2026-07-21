# Text-to-Morse Conversion — Test Plan

## Unit Tests

### MorseTranslatorTest
- [x] **Happy path**: Single word conversion — "HELLO" → `.... . .-.. .-.. ---`
- [x] **Happy path**: Multi-word conversion — "HELLO WORLD" → `.... . .-.. .-.. --- / .-- --- .-. .-.. -..`
- [x] **Happy path**: Numbers — "123" → `.---- ..--- ...--`
- [x] **Lowercase handling**: "hello" produces same output as "HELLO"
- [x] **Punctuation skipping**: "A!B" → Morse for "A" and "B", punctuation skipped
- [x] **Chinese Pinyin**: "你好" → valid Morse code output (Pinyin-derived)
- [x] **Mixed CJK/ASCII**: "Hello世界" → both English and Pinyin Morse
- [x] **hasConvertibleChars true**: Text with letters → true
- [x] **hasConvertibleChars false**: Text with only punctuation → false
- [x] **unconvertibleChars**: "A!B" → returns set containing '!'
- [x] **Whitespace-only input**: No convertible chars, whitespace filtered

### MorseConverterViewModelTest
- [x] **Initial state**: `morseOutput` is empty, `textInput` is "Hello World"
- [x] **updateTextInput**: Sets textInput and clears error
- [x] **convertText empty input**: Error "Enter text to convert", morseOutput cleared
- [x] **convertText no convertible chars**: Error "No text can be converted to Morse code"
- [x] **convertText valid input**: morseOutput populated with correct Morse, no error
- [x] **convertText with unconvertible chars**: morseOutput produced + warning error set
- [x] **clearError**: Resets error to null

## Instrumented Tests

### MainScreen
- [x] **Convert button displayed**: `onNodeWithText("Convert").assertIsDisplayed()`
- [x] **Text input + Convert flow**: Type text → tap Convert → Morse output appears

## Edge Cases
- [x] **Empty string after trimming**: Whitespace-only input → "Enter text to convert" error
- [x] **All punctuation**: `"!!!"` → "No text can be converted to Morse code" error
- [x] **Very long input**: No explicit limit, but synchronous conversion on main thread
- [x] **Emoji**: Emoji characters have no Morse mapping → passed through as-is or detected as unconvertible
- [x] **Surrogate pairs**: `text.codePoints()` handles multi-char Unicode sequences correctly
