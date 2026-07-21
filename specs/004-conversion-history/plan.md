---
feature_id: "004"
name: "Conversion History"
status: "✅ Done"
depends_on: ["001", "005"]
touches:
  - "app/src/main/java/com/morseling/data/ConversionHistoryEntity.kt"
  - "app/src/main/java/com/morseling/data/ConversionHistoryDao.kt"
  - "app/src/main/java/com/morseling/data/ConversionDatabase.kt"
  - "app/src/main/java/com/morseling/data/ConversionRepository.kt"
  - "app/src/main/java/com/morseling/ui/HistoryScreen.kt"
  - "app/src/main/java/com/morseling/viewmodel/HistoryViewModel.kt"
  - "app/src/main/java/com/morseling/viewmodel/MorseConverterViewModel.kt"
  - "app/src/main/java/com/morseling/navigation/MorselingNavGraph.kt"
  - "app/src/main/java/com/morseling/di/DatabaseModule.kt"
  - "app/src/main/res/values/strings.xml"
  - "app/src/main/res/values-zh-rCN/strings.xml"
created: "2026-07-21"
---

# Conversion History — Implementation Plan

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Room 2.6.1, KSP (annotation processing), Kotlin Coroutines
**Storage**: Room database (`morseling.db`, version 1, table `conversion_history`)
**Testing**: JUnit 4 + kotlinx-coroutines-test (unit, with FakeDao)
**Target Platform**: Android 26+
**Performance Goals**: Room query < 50ms for 1000 entries, History screen renders without jank
**Constraints**: Fully offline, schema version 1 with no migrations
**Scale/Scope**: Single-user, local database

## Constitution Check

- [x] **Article I (Spec-First)**: spec.md written. Retroactive.
- [x] **Article II (Test-First)**: FakeDao used in ViewModel tests. Retroactive.
- [x] **Article III (Modularity)**: History is self-contained in data/ + HistoryViewModel + HistoryScreen.
- [x] **Article IV (Simplicity)**: 6 new files (entity, DAO, database, repository, screen, ViewModel) + 2 modified. At 5-limit boundary but justified by Room boilerplate needs.
- [x] **Article V (Anti-Abstraction)**: Using Room DAO annotations directly. No unnecessary repository interface (ConversionRepository is a class, not an interface).
- [x] **Article VI (Integration Reality)**: FakeDao in tests simulates Room behavior; Room tested via instrumented tests with Hilt.
- [x] **Article VII (Observability)**: Room Flow auto-emits on table changes. History screen collects reactively.

## Approach

Room is used as the local persistence layer. `ConversionHistoryEntity` maps to the `conversion_history` table. `ConversionHistoryDao` provides all CRUD operations with Room annotations. `ConversionRepository` wraps the DAO with business logic (dedup on save). `ConversionDatabase` is a Room database singleton provided by Hilt.

The dedup strategy is central: `saveConversion()` checks `findByTextInput()` before inserting. Same text → `updateTimestamp()` (reorder to top). Different text → `insert()`.

History screen uses `LazyColumn` with `ElevatedCard` items, keyed by entity ID for stable recomposition. Each card shows input text, Morse output (monospace, max 2 lines), formatted date, favorite toggle, and delete button.

## Data Model

| Entity | Key Attributes | Relationships |
|--------|---------------|---------------|
| ConversionHistoryEntity | id (PK, auto-generate), textInput: String, morseOutput: String, timestamp: Long, isFavorite: Boolean | None (flat table) |

## API / CLI Contract

Room DAO serves as the internal contract:

```kotlin
interface ConversionHistoryDao {
    fun getAll(): Flow<List<ConversionHistoryEntity>>
    suspend fun insert(entity: ConversionHistoryEntity)
    suspend fun deleteById(id: Int)
    suspend fun deleteAll()
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)
    suspend fun findByTextInput(textInput: String): ConversionHistoryEntity?
    suspend fun updateTimestamp(id: Int, timestamp: Long)
}
```

## Alternatives Considered

| Alternative | Pros | Cons | Why Rejected |
|-------------|------|------|--------------|
| SharedPreferences for history | Simpler | Not designed for list data; no query support | Room is purpose-built for this |
| SQLDelight | Multiplatform, generated code | Less Android-native; fewer integrations with Compose/Hilt | Room is the official Android standard |
| No dedup — always insert | Simpler code | Clutters history with duplicates | Dedup is a small, high-value optimization |
| History as a side panel (not separate screen) | More discoverable | Takes screen real estate from conversion UI | Separate screen with navigation is cleaner |

**Decision**: Room database with dedup via timestamp update. Hilt provides DAO as singleton. History is a separate navigation destination.

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Schema migration needed in future | High | Medium | Version 1 with `exportSchema = false`. Future migrations need manual Room migration objects. |
| Large history slowing UI | Low | Low | LazyColumn renders only visible items. Room Flow is efficient for 1000+ rows. |
| Coroutine cancellation during save | Low | Low | Save is fire-and-forget in viewModelScope; app lifecycle handles cancellation. |

## Dependencies

| Depends On | Status | Blocking? |
|-----------|--------|-----------|
| 001-text-to-morse-conversion | ✅ Done | No |
| 005-settings (Hilt DI pattern) | ✅ Done | No |
| Room 2.6.1 + KSP | Ready | No |

## Complexity Tracking

| Article | Violation | Why Needed | Simpler Alternative Rejected Because |
|---------|-----------|------------|--------------------------------------|
| Art. IV | 6 new files (>5) | Room requires Entity + DAO + Database + Repository classes. Screen + ViewModel are Compose patterns. | Could merge DAO into Database class, but Room requires separate @Dao interface for annotation processing. |

## Files to Create / Change

| Action | File | Rationale |
|--------|------|-----------|
| Create | `data/ConversionHistoryEntity.kt` | Room entity class |
| Create | `data/ConversionHistoryDao.kt` | Room DAO interface with CRUD queries |
| Create | `data/ConversionDatabase.kt` | Room database singleton |
| Create | `data/ConversionRepository.kt` | Repository with dedup logic |
| Create | `ui/HistoryScreen.kt` | History list UI with cards, favorites, delete |
| Create | `viewmodel/HistoryViewModel.kt` | History operations ViewModel |
| Modify | `viewmodel/MorseConverterViewModel.kt` | Add saveConversion call in convertText() and loadFromHistory() |
| Modify | `navigation/MorselingNavGraph.kt` | Add history route and onItemClick callback |
| Create | `di/DatabaseModule.kt` | Hilt module providing Room DB and DAO |
| Modify | `values/strings.xml` | Add history-related strings |
| Modify | `values-zh-rCN/strings.xml` | Add Chinese translations |

## Quickstart Validation

1. Convert "SOS" → navigate to History → see one entry with "SOS" text and Morse output
2. Convert "SOS" again → navigate to History → still one entry but at top (timestamp updated)
3. Tap star on entry → star turns purple → tap again → star turns gray
4. Tap trash on entry → entry removed → empty state "No history yet" shown
5. Create 3 conversions → tap "Clear all" → confirm dialog → tap "Clear" → all gone
6. Tap history entry → navigated back to main screen → text and Morse populated
7. Rotate device on History screen → state preserved (ViewModel-scoped)
