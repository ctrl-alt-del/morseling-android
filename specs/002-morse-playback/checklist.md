# Morse Playback — Requirements Quality Checklist

## Requirement Completeness

- [x] ALL `[NEEDS CLARIFICATION]` markers in spec.md and plan.md are resolved
- [x] Every user story has at least 2 acceptance scenarios
- [x] Every functional requirement (FR-010 through FR-023) has a corresponding acceptance scenario
- [x] Edge cases are enumerated for each user story (boundary, error, race condition)
- [x] Non-functional requirements are specified: performance, security, accessibility, observability
- [x] Data model entities are defined with key attributes and relationships

## Clarity & Unambiguity

- [x] No vague terms without definition ("fast," "user-friendly," "scalable")
- [x] Acceptance scenarios use concrete values (WPM=5 → 240ms dot, WPM=40 → 30ms dot)
- [x] Every "When" action has a specific, observable "Then" outcome
- [x] Error handling behavior is defined (CancellationException vs Exception, job guard)
- [x] Scope boundaries are explicit — flash is optional, app works without it

## Consistency

- [x] User story priorities (P1/P2/P3) reflect actual business value
- [x] Independent test descriptions for each story describe a complete, useful slice
- [x] No contradictory acceptance scenarios
- [x] Plan.md technical choices don't contradict spec.md requirements
- [x] Constitution gates are either passed or justified in Complexity Tracking

## Edge Case Coverage

- [x] What happens on extreme input (very long Morse sequences)? — Playback proportional, cancellable via Stop
- [x] What happens on empty state? — "Enter Morse code to play" error
- [x] What happens when hardware is unavailable (no flash)? — Fallback to Audio mode
- [x] What happens on rapid Play→Stop→Play? — Job guard prevents stale state cleanup
- [x] What happens if WPM produces sub-1ms dot? — Coerced to minimum 1ms

## Non-Functional Coverage

- [x] Performance target is specified and measurable — ±5ms timing accuracy
- [x] Security concerns are identified — CAMERA permission declared, flash optional
- [x] Accessibility requirements are explicit — content descriptions on buttons, visual highlighting
- [x] Observability requirements are explicit — StateFlow exposes playback state, errors surfaced
- [x] Platform/API level support is specified — ToneGenerator (API 1+), Camera2 (API 21+), minSdk 26

## Process

- [x] No speculative or "might need in the future" features
- [x] No implementation details have leaked into spec.md
- [x] plan.md Alternatives Considered section is filled
- [x] plan.md Complexity Tracking justifies 6-file count violation
