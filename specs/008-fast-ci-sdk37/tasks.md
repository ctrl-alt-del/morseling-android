# Fast CI + SDK 37 — Tasks

## Block 0: Spec & Design (before code)

- [x] `doc-coauthoring`: spec.md + plan.md approved
- [x] `checklist.md`: requirements quality checklist verified
- [x] `test_plan.md`: test scenarios documented

## Block 1: Setup

- [x] **Task 1.1**: Create feature branch `feature/fast-ci-sdk37` and `specs/008-fast-ci-sdk37/` directory
  - Build: N/A
  - Tests: N/A

## Block 2: Foundational

- [ ] **Task 2.1** [P]: Update version catalog — `gradle/libs.versions.toml`
  - AGP 8.7.3 → 9.2.1, Kotlin 2.0.21 → 2.2.10, KSP 2.0.21-1.0.28 → 2.3.9
  - Build: `./gradlew assembleDebug`
  - Tests: `./gradlew test`

## Block 3: User Story 1 (P1 🎯 MVP) — SDK 37 Build

- [ ] **Task 3.1** [P]: Update app build config — `app/build.gradle.kts`
  - `compileSdk { version = release(37) }`, `targetSdk = 37`, JDK 21, `testOptions`
  - Build: `./gradlew assembleDebug`
  - Tests: `./gradlew test`

- [ ] **Task 3.2** [P]: Update Gradle configs — `settings.gradle.kts`, `gradle.properties`
  - `includeGroupByRegex`, `foojay-resolver-convention`, `configuration-cache=true`, remove Aliyun mirror
  - Build: `./gradlew assembleDebug`
  - Tests: `./gradlew test`

## Block 4: User Story 2 (P2) — Fast CI

- [ ] **Task 4.1** [US2]: Rewrite CI workflow — `.github/workflows/ci.yml`
  - Runner SDK, platform 37 cache, `gradle/actions/setup-gradle@v4`, concurrency, lint, test artifacts, JDK 21
  - Build: N/A (CI YAML)
  - Tests: Push to GitHub, observe CI run

## Block 5: Polish

- [ ] **Task 5.1** [P]: Update spec index — `specs/index.md`
  - Add entry for 008-fast-ci-sdk37
  - Build: `./gradlew assembleDebug`
  - Tests: `./gradlew test`

- [ ] **Task 5.2** [P]: Full verification — all files
  - `./gradlew clean assembleDebug test lint`
  - Build: `./gradlew assembleDebug`
  - Tests: `./gradlew test`
