---
feature_id: "NNN"
name: "Feature Name"
status: "🚧 In Progress"
depends_on: []
touches:
  - "FileA.kt"
  - "FileB.kt"
created: "YYYY-MM-DD"
---

# [Feature Name] — Implementation Plan

<!--
  INSTRUCTIONS FOR AI:
  - Fill every section. Use [NEEDS CLARIFICATION: ...] for unknown details.
  - Run the Constitution Check gates FIRST. If any gate fails, document in
    Complexity Tracking with justification.
  - Keep the main document high-level. Extract lengthy code samples, detailed
    algorithms, or schema definitions to the relevant section or reference
    files.
  - Delete this comment block when done.
-->

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Jetpack Compose, Material 3, Hilt 2.51.1, Room 2.6.1, DataStore 1.1.1, Navigation Compose 2.8.5
**Storage**: Room (history), DataStore Preferences (settings)
**Testing**: JUnit 4 + kotlinx-coroutines-test (unit), AndroidX Test + Compose UI Test + Hilt Testing (instrumented)
**Target Platform**: Android 26+ (Android 8.0+)
**Performance Goals**: Conversion synchronous (< 1ms for typical input), UI 60fps, playback timing accurate to ±5ms
**Constraints**: Fully offline, minSdk 26, single Activity, < 5MB APK
**Scale/Scope**: Single-user app, local data only, no server backend

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

[High-level strategy. What's the main design idea? How does it fit into the existing architecture?]

## Data Model

<!-- Entities, their key attributes, and relationships. No DDL or code — describe at design level. -->

| Entity | Key Attributes | Relationships |
|--------|---------------|---------------|
| [Entity1] | [attr1, attr2, attr3] | belongs to [Entity2] |
| [Entity2] | [attr1, attr2] | has many [Entity1] |

## API / CLI Contract

<!-- For API projects: list endpoints, methods, request/response shapes. For CLI: list commands, flags, input/output formats. Keep high-level — not full OpenAPI spec. -->

| Method | Path / Command | Purpose | Input | Output |
|--------|---------------|---------|-------|--------|
| GET | /api/items | List items | query params | Item[] |
| POST | /api/items | Create item | Item body | Item |

## Alternatives Considered

<!-- One of the MOST IMPORTANT sections. Document what you considered and WHY you chose this approach. -->

| Alternative | Pros | Cons | Why Rejected |
|-------------|------|------|--------------|
| [Alternative A] | [advantages] | [disadvantages] | [reason] |
| [Alternative B] | [advantages] | [disadvantages] | [reason] |

**Decision**: [Which approach was chosen and why the trade-offs are acceptable]

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| [Risk description] | Low/Med/High | Low/Med/High | [How we mitigate] |

## Dependencies

| Depends On | Status | Blocking? |
|-----------|--------|-----------|
| [Feature, API, library, or team] | Ready/In Progress/Blocked | Yes/No |

## Complexity Tracking

> Fill ONLY if Constitution Check has violations that must be justified

| Article | Violation | Why Needed | Simpler Alternative Rejected Because |
|---------|-----------|------------|--------------------------------------|
| Art. IV | 5 new files | [justification] | [why simpler approach doesn't work] |

## Files to Create / Change

| Action | File | Rationale |
|--------|------|-----------|
| Create | src/models/entity.kt | New data model |
| Modify | src/api/routes.kt | Add new endpoint |

## Quickstart Validation

<!-- Key scenarios to verify the feature works end-to-end after implementation -->
1. [Validation scenario 1 — step by step]
2. [Validation scenario 2 — step by step]
