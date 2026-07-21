---
last_analyzed: "2026-07-21"
files_count: 14
domains: [architecture, data, apis, conventions, edges, ownership, patterns]
coverage: 0.85
language: kotlin
---

# Knowledge Index

## Architecture
- [[knowledge/architecture/overview]] — MVVM + UDF architecture, module overview, navigation graph
- [[knowledge/architecture/components]] — Package-level modules and their responsibilities
- [[knowledge/architecture/data-flow]] — How data moves: user input → ViewModel → StateFlow → Compose

## Data
- [[knowledge/data/entities]] — Room entities (ConversionHistoryEntity), DataStore models (AppSettings), UI state (PlaybackUiState)

## APIs / Interfaces
- [[knowledge/apis/interfaces]] — MorsePlayer interface, SettingsProvider interface, internal contracts

## Conventions
- [[knowledge/conventions/naming]] — Kotlin naming, file organization, import conventions
- [[knowledge/conventions/file-organization]] — Package structure, resource organization
- [[knowledge/conventions/testing]] — Test patterns: Fake doubles, coroutine testing, Compose UI testing
- [[knowledge/conventions/error-handling]] — ViewModel error flow, CancellationException handling, Snackbar display

## Patterns
- [[knowledge/patterns/common]] — Recurring patterns: StateFlow UDF, Flow-based playback, Hilt injection, DataStore reactive collection

## Edges
- [[knowledge/edges/gotchas]] — Sharp edges: runBlocking in onCreate, CancellationException catch, flash availability check, job guard in finally

## Ownership
- [[knowledge/ownership/owners]] — File/directory ownership from git history

## Gaps
- [[knowledge/gaps]] — Uncertainties and questions for the user
