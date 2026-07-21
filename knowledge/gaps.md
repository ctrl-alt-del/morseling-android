# Knowledge Gaps
**Source files**: analysis
**Depends on**: none
**Depended on by**: none
**See also**: none
**Tags**: #gaps

The following uncertainties were encountered during analysis. Each is a
concrete, answerable question that would improve the knowledge model.

## Unconfirmed assumptions

1. **Why TinyPinyin specifically?** The project uses `com.github.promeg:tinypinyin:2.0.3` for Pinyin conversion. Was this chosen for its small footprint vs. alternatives like `pinyin4j`? Any issues with multi-character words or rare characters?

2. **ProGuard rules are minimal** — only keeps annotations, source file, and line numbers. Is obfuscation intentionally not configured further, or is this an oversight? The app has no network access so reverse engineering risk is low, but confirm intent.

3. **No app signing configuration in build.gradle.kts** — is signing handled via `local.properties` or CI secrets? This matters for release builds.

4. **Version code 2, version name "2.0.0"** — was there a v1? The git history shows only 7 commits. Is v2 the first public release or was v1 a different codebase/repo?

## Missing documentation

1. No test coverage report configuration (JaCoCo or similar).
2. No `CONTRIBUTING.md`.
3. No `CHANGELOG.md`.
4. No in-code architecture decision records (ADRs).

## Potential issues not verified

1. Does `FlashlightMorsePlayer` handle the case where `cameraId` is null because `isAvailable()` was never called before `play()`?
2. Does the Morse output survive `Parcelable`/`Bundle` across configuration changes, or is it ViewModel-scoped?
3. What happens if the user rapidly taps Convert while a save operation is in progress?
