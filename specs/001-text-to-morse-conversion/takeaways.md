# Text-to-Morse Conversion — Takeaways

## What Went Well

- **Synchronous conversion is fast enough** — no measurable lag for typical text lengths. Avoided coroutine overhead for a CPU-bound operation.
- **Singleton object pattern** — `MorseTranslator` as a Kotlin `object` is clean, thread-safe, and requires no dependency injection.
- **TinyPinyin integration** — the library is minimal (~200KB) and handles CJK detection robustly. `text.codePoints()` iteration correctly handles surrogate pairs.
- **Validation before conversion** — separate `hasConvertibleChars()` check prevents producing empty Morse output silently.

## What We Learned

- **Punctuation passthrough is the simplest approach** — rather than trying to map all Unicode to Morse (impossible), unconvertible characters pass through. The warning system alerts users without blocking conversion.
- **Word separation convention matters** — using `" / "` (space-slash-space) for word boundaries is readable and visually separates words in Morse output.
- **Default text input is useful UX** — pre-filling with "Hello World" gives users an immediate example they can convert to see the feature work.

## API / Tech Surprises

- **TinyPinyin's `toPinyin(char)` returns Pinyin without tone marks** — exactly what's needed for Morse conversion (tone marks would add unconvertible characters).
- **DataStore keys use raw strings for mode** — `"audio"`/`"flash"` stored as strings rather than an enum because DataStore only supports primitive types. Would have preferred enum for type safety.
- **ViewModel errors cannot use `stringResource()`** — no Context access. Error strings are hardcoded English. The `strings.xml` has matching strings for UI labels but ViewModel code can't reference them.

## Patterns Worth Reusing

- **Static `CHAR_TO_MORSE` map** — a `Map<Char, String>` as a companion/private val is the right pattern for fixed lookup tables. Fast, immutable, no allocation per conversion.
- **Validation before mutation** — always check preconditions (empty input, no convertible chars) before updating state. Prevents confusing partial states.
- **Warning vs blocking error** — `"Some characters could not be converted"` is a warning (non-blocking) while `"Enter text to convert"` is blocking. Using the same `error` field for both with appropriate Boolean state tracking works well.
