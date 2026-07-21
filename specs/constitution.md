# Project Constitution

<!--
  This constitution governs ALL features in this project. Every spec.md, plan.md,
  and implementation MUST satisfy these principles. Violations require documented
  justification in plan.md's Complexity Tracking section.

  Amendment process: document rationale → review with team → update this file with
  dated entry in the Amendment Log.
-->

## Article I: Spec-First

**No implementation code shall be written before spec.md + plan.md are approved.**

- Every feature MUST have a written spec defining WHAT before HOW
- Every feature MUST have a written plan mapping requirements to technical approach
- Trivial changes (typo fix, log line, single-line config change) are exempt

## Article II: Test-First (TDD)

**Tests MUST be written and confirmed FAILING before implementation code is written.**

- Unit tests for every function/method with business logic
- Instrumented tests for every user story's happy path
- Tests MUST fail before implementation (Red-Green-Refactor)
- Uses Fake test doubles (FakeDao, FakeMorsePlayer, FakeSettingsProvider) — no mocking libraries (Mockito/MockK)
- This is NON-NEGOTIABLE for new features

## Article III: Modularity

**Every feature MUST have clear boundaries within the package structure.**

- Features are packages within `com.morseling/`, not separate Gradle modules
- Each feature package has a single responsibility
- Dependencies between packages are explicit and minimal
- New code is testable in isolation
- Shared code lives in shared packages (audio, data, model, util, di)

## Article IV: Simplicity

**Start simple. Add complexity only when proven necessary.**

- Maximum 5 new files per feature without documented justification
- No speculative or "might need" features
- No future-proofing — build for today's requirements
- If you can solve it with a function instead of a class, use a function
- If you can solve it with a standard library feature, don't pull a dependency

## Article V: Anti-Abstraction

**Use framework, language, and library features directly. Avoid unnecessary wrappers.**

- Interfaces only when ≥2 implementations exist (e.g., MorsePlayer for audio + flash)
- Single model representation until proven insufficient
- Do not create interfaces for single-implementation classes
- Framework utilities are used directly (Compose, Room, DataStore, Hilt)

## Article VI: Integration Reality

**Prefer real components over mocks. Test in realistic environments.**

- Unit tests: Room in-memory (`Room.inMemoryDatabaseBuilder`) with real DAOs
- Fake implementations for external boundaries (camera, tone generator)
- Instrumented tests: Hilt + Compose testing with real database
- Mocks are acceptable only for external Android APIs (CameraManager, ToneGenerator)

## Article VII: Observability

**Errors and state changes MUST be inspectable.**

- Every ViewModel exposes its state via `StateFlow`
- Errors surfaced as UI state (`uiState.error`) and displayed via Snackbar
- Playback exceptions caught and reported with context
- Key operations logged with context (what was attempted, what went wrong)
- All UI text via `stringResource()` with English + Chinese translations

## Amendment Log

| Date | Article | Change | Rationale |
|------|---------|--------|-----------|
| 2026-07-21 | III | Relaxed modularity for single-module Android app | Project is a single Gradle module; features are Kotlin packages |
| 2026-07-21 | IV | Relaxed file count to 5 | Compose screens typically need ViewModel + Screen + entity + DAO + module |
| 2026-07-21 | V | Interfaces require ≥2 implementations | MorsePlayer has 2 impls (audio + flash); SettingsProvider has 2 (real + test fake) |
| 2026-07-21 | VI | Adapted for Android | Room in-memory for unit tests; Fakes for external Android APIs |
