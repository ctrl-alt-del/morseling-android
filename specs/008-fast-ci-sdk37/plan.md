---
feature_id: "008"
name: "Fast CI + SDK 37"
status: "✅ Done"
depends_on: []
touches:
  - ".github/workflows/ci.yml"
  - "app/build.gradle.kts"
  - "gradle/libs.versions.toml"
  - "settings.gradle.kts"
  - "gradle.properties"
  - "build.gradle.kts"
created: "2026-07-22"
---

# Fast CI + SDK 37 — Implementation Plan

## Technical Context

**Language/Version**: Kotlin 2.0.21 → 2.2.10
**Primary Dependencies**: AGP 8.7.3 → 9.2.1, KSP 2.0.21-1.0.28 → 2.3.9
**Build System**: Gradle 9.5.1 (wrapper, unchanged)
**CI**: GitHub Actions, ubuntu-latest runner
**Target Platform**: Android 26+ (minSdk unchanged)
**Performance Goals**: CI median <4 minutes, SDK setup <30s
**Constraints**: No new files created; minimum dependency bumps; reuse sftping's platform 37 archive

## Constitution Check

*GATE: Must pass before implementation. Re-check after design changes.*

- [ ] **Article I (Spec-First)**: spec.md approved by stakeholder?
- [ ] **Article II (Test-First)**: test plan written before implementation code?
- [ ] **Article III (Modularity)**: feature is a self-contained package with clear boundaries?
- [ ] **Article IV (Simplicity)**: ≤5 new files? No speculative features? No future-proofing?
- [ ] **Article V (Anti-Abstraction)**: using framework directly? Interfaces only for ≥2 implementations?
- [ ] **Article VI (Integration Reality)**: using real Room database in tests? Fakes only for external APIs?
- [ ] **Article VII (Observability)**: StateFlow exposed, errors surfaced, key operations logged?

## Approach

The project's CI currently downloads a full Android SDK via `android-actions/setup-android@v3` on every run, adding 2-3 minutes of overhead. GitHub's `ubuntu-latest` runners already ship with Android SDK platforms 34-35 and build-tools pre-installed at `/usr/local/lib/android/sdk`.

The approach follows the pattern from the `sftping` project:

1. **Skip SDK download**: Reference the runner's pre-installed SDK directly via `ANDROID_SDK_ROOT`.
2. **Cache platform 37**: Since `platforms;android-37` isn't in Google's public SDK repository yet (available only via Android Studio preview), cache the extracted platform directory using `actions/cache@v4`. On cache miss, download a compressed archive from `ctrl-alt-del/sftping/ci-assets`.
3. **Use `gradle/actions/setup-gradle@v4`**: Replaces the generic `actions/cache@v4` for Gradle — it understands Gradle's cache structure and handles wrapper, dependencies, and build outputs intelligently.
4. **Add concurrency and timeouts**: Cancel stale runs on the same ref; fail after 30 minutes.
5. **Fail fast with lint**: Run lint before assemble + test so trivial issues surface immediately.

Build-side changes are the minimum needed for SDK 37:
- AGP 9.2.1 (required for `compileSdk = 37`)
- Kotlin 2.2.10 (required for AGP 9.x compatibility)
- KSP 2.3.9 (required for Kotlin 2.2.x compatibility)
- JDK 21 toolchain (required by AGP 9.x)
- `foojay-resolver-convention` (auto-provisions JDK 21)

All other libraries (Hilt, Room, Compose BOM, Coroutines, etc.) stay at current versions.

## Data Model

No new data model. No database schema changes. This is a build/CI infrastructure change.

## API / CLI Contract

No new API or CLI surface. Gradle commands remain unchanged:
- `./gradlew assembleDebug` — build
- `./gradlew test` — unit tests
- `./gradlew lint` — lint (new addition)

## Alternatives Considered

| Alternative | Pros | Cons | Why Rejected |
|-------------|------|------|--------------|
| Keep `android-actions/setup-android` + `sdkmanager` to install platform 37 | Simple workflow change | 2-3 min SDK download every run; `sdkmanager` can't install platform 37 (not in public repo) | Too slow; platform 37 isn't installable via sdkmanager |
| Host platform 37 archive in morseling-android's own ci-assets branch | Self-contained, no cross-repo dependency | Duplicates the 60 MB binary; need to create + maintain a ci-assets branch | Unnecessary duplication — both repos share an owner |
| Use `setup-java` with `cache: 'gradle'` instead of `gradle/actions/setup-gradle@v4` | Familiar API | Less sophisticated caching; doesn't handle Gradle wrapper or build outputs as well | `gradle/actions/setup-gradle` is the recommended action by the Gradle team |
| Bump all libraries to sftping versions | Gets everything fresh | High risk of breaking changes in Room, Hilt, Compose, etc. — orthogonal to the feature goal | Violates Article IV (Simplicity) — only bump what's needed |
| Use Kotlin 2.1.x instead of 2.2.10 | Closer to existing version | Kotlin 2.1.x may not be compatible with AGP 9.2.1 | AGP 9.2.1 requires Kotlin ≥2.2.0 |

**Decision**: Reuse sftping's platform 37 archive; bump only AGP/Kotlin/KSP; use `gradle/actions/setup-gradle@v4` for cache. This minimizes risk while achieving the speed target.

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| GitHub changes runner's pre-installed SDK path | Low | High — CI breaks | CI will fail loudly (missing SDK); path is documented upstream in `actions/runner-images` |
| sftping's ci-assets branch is deleted | Low | Med — CI breaks on cache miss | If it happens, create a ci-assets branch in this repo |
| Kotlin 2.2.10 breaks existing libs (Room 2.6.1, Hilt 2.51.1) | Med | High — build fails | Verify locally before pushing; rollback if needed |
| AGP 9.2.1 requires Gradle wrapper config changes | Low | Med — build fails | Gradle 9.5.1 is recent, should be compatible |
| `compileSdk { version = release(37) }` syntax unavailable in AGP 9.2.1 | Low | Low — fall back to `compileSdk = 37` | Both syntaxes work in AGP 9.x |

## Dependencies

| Depends On | Status | Blocking? |
|-----------|--------|-----------|
| sftping ci-assets branch (platform 37 archive) | Ready | No — only needed on CI cache miss |
| AGP 9.2.1 release | Ready | No |
| Kotlin 2.2.10 release | Ready | No |
| KSP 2.3.9 release | Ready | No |

## Complexity Tracking

| Article | Violation | Why Needed | Simpler Alternative Rejected Because |
|---------|-----------|------------|--------------------------------------|
| Art. IV | 5+ files touched | All are single-property or single-section modifications to existing files — no new files created. The touch count is high because SDK 37 requires coordinated changes across build files (version catalog, app config, Gradle config, CI). | Changing only one file would leave the project in an inconsistent state (e.g., SDK 37 in build.gradle.kts but CI still downloads SDK 35). |
| Art. II | Test plan is CI-verification, not code-level unit tests | No Kotlin code is modified — only Gradle/XAML config and CI YAML. Unit tests still pass as regression check. | Writing unit tests for Gradle config or CI YAML is not practical. Circuit-breaker: CI pipeline acts as the verification. |

## Files to Create / Change

| Action | File | Rationale |
|--------|------|-----------|
| Modify | `.github/workflows/ci.yml` | Rewrite for speed: runner SDK, platform 37 cache, gradle/setup-gradle, concurrency, lint, test artifacts |
| Modify | `app/build.gradle.kts` | compileSdk 37, targetSdk 37, JDK 21, testOptions |
| Modify | `gradle/libs.versions.toml` | AGP 9.2.1, Kotlin 2.2.10, KSP 2.3.9 |
| Modify | `settings.gradle.kts` | includeGroupByRegex for plugins, foojay-resolver-convention, remove Aliyun mirror |
| Modify | `gradle.properties` | Add configuration-cache=true |
| Modify | `build.gradle.kts` | No changes needed (AGP plugin version comes from .toml) |

## Quickstart Validation

1. `./gradlew clean assembleDebug` — builds successfully with SDK 37
2. `./gradlew test` — all unit tests pass
3. `./gradlew lint` — no new lint errors
4. Push to GitHub — observe CI job completes successfully
5. Check CI duration on the run summary page
