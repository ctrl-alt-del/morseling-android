# Fast CI + SDK 37 — Specification

## User Stories

### User Story 1 — Build targets Android SDK 37 (Priority: P1 🎯 MVP)

As a developer, I want the project to compile against Android SDK 37 so that it uses the latest platform APIs and meets Google Play's target SDK requirements.

**Why this priority**: SDK 37 is a hard requirement for future Play Store submissions. Without it the project is stuck on SDK 35.

**Independent Test**: Run `./gradlew assembleDebug` — the APK's `compileSdkVersion` is 37 and `targetSdkVersion` is 37.

**Acceptance Scenarios**:

1. **Given** the project is configured for SDK 35, **When** `compileSdk` and `targetSdk` are updated to 37 and AGP/Kotlin/KSP are bumped to compatible versions, **Then** `./gradlew assembleDebug` succeeds and the resulting APK targets SDK 37.
2. **Given** the SDK 37 platform is not available on the CI runner's pre-installed SDK, **When** a CI job runs, **Then** it downloads and caches the platform from the ci-assets branch before building.

---

### User Story 2 — CI completes in under 4 minutes (Priority: P2)

As a developer, I want CI builds to complete quickly so that pull request feedback is fast and iteration velocity stays high.

**Why this priority**: Fast CI is a productivity multiplier. Every minute saved per run compounds across all PRs. It's P2 because the project already has a working CI — this is an optimization, not a blocker.

**Independent Test**: Push a commit, observe the CI job duration on GitHub Actions. Median time should be under 4 minutes.

**Acceptance Scenarios**:

1. **Given** the CI workflow uses `android-actions/setup-android` to download a full SDK, **When** the workflow is rewritten to use the runner's pre-installed SDK with platform 37 cached, **Then** the SDK setup step takes under 30 seconds (vs. 2+ minutes previously).
2. **Given** a PR is open, **When** a new commit is pushed while a previous CI run is still in progress, **Then** the previous run is automatically cancelled (concurrency group).
3. **Given** a build fails, **When** CI completes, **Then** test reports and lint HTML are uploaded as workflow artifacts for inspection.

---

## Edge Cases

- What happens when GitHub updates the runner image and removes/changes the pre-installed SDK path (`/usr/local/lib/android/sdk`)?
- What happens when the `android-platform-37.tar.gz` download fails (sftping's ci-assets branch deleted, network error)?
- What happens when two PRs are opened simultaneously — do they share the SDK cache correctly?
- What about Kotlin compiler incompatibility between Kotlin 2.2.10 and existing libraries (Room 2.6.1, Hilt 2.51.1, Compose BOM 2024.12.01)?
- What if AGP 9.2.1 requires Gradle configuration changes not yet present in the project?

## Functional Requirements

- **FR-001**: Project MUST compile against `compileSdk = 37` using AGP 9.x.
- **FR-002**: `targetSdk` MUST be set to 37.
- **FR-003**: JVM toolchain MUST target JDK 21 (required by AGP 9.x + SDK 37).
- **FR-004**: CI workflow MUST leverage the GitHub runner's pre-installed Android SDK at `/usr/local/lib/android/sdk`.
- **FR-005**: CI workflow MUST cache the android-37 platform using `actions/cache@v4` with a unique key.
- **FR-006**: CI workflow MUST download the platform 37 archive from sftping's ci-assets branch on cache miss.
- **FR-007**: CI workflow MUST symlink `android-37` → `android-37.0` for AGP compatibility.
- **FR-008**: CI workflow MUST use `gradle/actions/setup-gradle@v4` for Gradle caching.
- **FR-009**: CI workflow MUST use JDK 21 (Temurin distribution).
- **FR-010**: CI workflow MUST run lint before assemble and test (fail fast).
- **FR-011**: CI workflow MUST set a 30-minute timeout.
- **FR-012**: CI workflow MUST cancel in-progress runs for the same ref (concurrency group).
- **FR-013**: CI workflow MUST upload test reports and lint results as artifacts on failure.
- **FR-014**: Gradle configuration cache MUST be enabled (`org.gradle.configuration-cache=true`).
- **FR-015**: The `foojay-resolver-convention` plugin MUST be applied for JDK toolchain provisioning.
- **FR-016**: `settings.gradle.kts` plugin repositories MUST use `includeGroupByRegex` filtering for Google artifacts.

## Non-Functional Requirements

- **Performance**: CI median wall-clock time under 4 minutes (down from ~6+ minutes). SDK platform 37 cache hit rate >80%.
- **Security**: No credentials exposed in CI. Cache reads are read-only on fork PRs to prevent cache poisoning.
- **Observability**: Lint results and test reports available as downloadable artifacts on failure. Build output clearly shows `compileSdkVersion` and `targetSdkVersion`.
- **Compatibility**: Existing tests must continue to pass. No library version bumps beyond AGP/Kotlin/KSP (minimal risk).

## Success Criteria

- **SC-001**: `./gradlew assembleDebug` produces an APK with compileSdk=37 and targetSdk=37.
- **SC-002**: `./gradlew test` passes with zero failures.
- **SC-003**: CI workflow completes in under 4 minutes median (measured over 5 runs).
- **SC-004**: On cache miss, platform 37 download + extraction takes under 60 seconds.
- **SC-005**: On cache hit, SDK setup takes under 10 seconds.

## Assumptions

- The GitHub Actions `ubuntu-latest` runner continues to ship Android SDK pre-installed at `/usr/local/lib/android/sdk` with at least platforms 34-35, build-tools, and cmdline-tools.
- The platform 37 archive hosted at `ctrl-alt-del/sftping/ci-assets/android-platform-37.tar.gz` (~60 MB compressed) remains available.
- AGP 9.2.1 is compatible with the existing Gradle wrapper (9.5.1).
- Kotlin 2.2.10 + KSP 2.3.9 are backward-compatible with all existing dependencies at their current versions.
- No new Kotlin or Gradle files need to be created — all changes are modifications to existing files.
