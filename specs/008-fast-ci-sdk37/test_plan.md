# Fast CI + SDK 37 — Test Plan

This feature modifies Gradle/CI configuration only — no Kotlin source code changes. The test plan focuses on build verification and CI pipeline validation.

## Unit Tests

No new unit tests — no Kotlin code is added or modified. Existing unit tests serve as regression checks.

### Existing Tests (Regression)

- [x] **Happy path**: Run `./gradlew test` — all existing unit tests pass with updated AGP/Kotlin/KSP
- [x] **JVM target compatibility**: JDK 21 toolchain does not break any existing test compilation

## CI Verification (Integration)

### ci.yml workflow

- [x] **Cache hit**: On second run, SDK platform 37 is cached — "Install Android platform 37" step is skipped
- [x] **Cache miss**: On first run (or after key change), platform 37 is downloaded and extracted
- [x] **Symlink**: `android-37` symlinks to `android-37.0` — AGP resolves the platform correctly
- [x] **Lint first**: Lint step runs before assemble; a lint failure causes CI to fail early
- [x] **Assemble succeeds**: `./gradlew assembleDebug` produces debug APK
- [x] **Tests pass**: `./gradlew test` exits 0
- [x] **Concurrency cancel**: Push two commits quickly — older run is cancelled
- [x] **Timeout**: CI times out at 30 minutes if hung
- [x] **Artifacts on failure**: If lint or test fails, test reports uploaded as artifacts

## Edge Cases

- [x] **Fork PR cache**: Gradle cache is read-only on fork PRs (checked via `cache-read-only` condition)
- [x] **Tar extraction fails**: `|| true` ensures pipeline continues even if tar warns about permissions on some skins
- [x] **Platform already exists**: Symlink step is idempotent (`[ ! -d ... ]` guard)
- [x] **SDK path changes**: If GitHub changes the runner image, CI will fail on the first SDK-using step

## Manual Verification

1. Run locally: `./gradlew clean assembleDebug test lint`
2. Push to branch `feature/fast-ci-sdk37` on GitHub
3. Open a PR to observe CI run
4. Verify CI summary shows: job duration, artifact upload on failure
5. Inspect the built APK: `apkanalyzer manifest target-sdk app/build/outputs/apk/debug/app-debug.apk` → `37`
