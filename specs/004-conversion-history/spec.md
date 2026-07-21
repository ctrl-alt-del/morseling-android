# Conversion History — Specification

## User Stories

### User Story 1 — Auto-Save Conversion History (Priority: P1 🎯 MVP)

As a user, I want my conversions automatically saved so that I can revisit past conversions without re-typing.

**Why this priority**: Core value proposition for repeated use. Without history, users must re-type every conversion.

**Independent Test**: Convert "SOS" → navigate to History → see "SOS" entry listed with Morse output and timestamp.

**Acceptance Scenarios**:

1. **Given** the user converts text for the first time, **When** conversion completes, **Then** the entry is saved to history with text, Morse, and current timestamp
2. **Given** the user converts the same text again, **When** conversion completes, **Then** no duplicate is created — the existing entry's timestamp is updated (moves to top)
3. **Given** the user loads a history entry, **When** the entry is loaded to the main screen, **Then** the entry's timestamp is updated (moves to top, "recently used")

---

### User Story 2 — History Screen (Priority: P2)

As a user, I want to view my conversion history in a scrollable list so that I can browse past conversions.

**Why this priority**: Auto-save is useless without a way to view saved entries.

**Independent Test**: Navigate to History → see card-based list of past conversions sorted by most recent first.

**Acceptance Scenarios**:

1. **Given** there are past conversions, **When** the user navigates to History, **Then** entries are displayed in a scrollable list, most recent first
2. **Given** each entry, **When** displayed, **Then** it shows the input text, Morse output (monospace, max 2 lines), and formatted date (MMM d, HH:mm)
3. **Given** there are no past conversions, **When** the user navigates to History, **Then** "No history yet" message is displayed centered
4. **Given** the user taps a history entry, **When** the entry is selected, **Then** the user navigates back to main screen with the entry's text and Morse populated
5. **Given** the user taps the back button, **When** navigating back, **Then** the user returns to the main screen

---

### User Story 3 — Favorite Toggle (Priority: P3)

As a user, I want to mark conversions as favorites so that I can find important ones quickly.

**Why this priority**: Nice-to-have organization. History works without it.

**Independent Test**: Tap star icon on a history entry → star turns purple (tertiary color) → tap again → star returns to gray.

**Acceptance Scenarios**:

1. **Given** a history entry is not favorited, **When** the user taps the star icon, **Then** the star animates to tertiary color and the entry is marked as favorite
2. **Given** a history entry is favorited, **When** the user taps the star icon again, **Then** the star animates back to onSurfaceVariant color

---

### User Story 4 — Delete & Clear All (Priority: P3)

As a user, I want to delete individual or all history entries so that I can manage my conversion history.

**Why this priority**: Privacy and organization. History works without delete but would become cluttered.

**Independent Test**: Tap trash on an entry → entry removed. Tap "Clear all" in top bar → confirm dialog → all entries removed.

**Acceptance Scenarios**:

1. **Given** a history entry exists, **When** the user taps the trash icon, **Then** the entry is immediately removed from the list
2. **Given** history contains entries, **When** the user taps "Clear all" in the top bar, **Then** a confirmation dialog appears
3. **Given** the confirmation dialog is shown, **When** the user taps "Clear", **Then** all history entries are deleted and empty state is shown
4. **Given** the confirmation dialog is shown, **When** the user taps "Cancel", **Then** the dialog dismisses without deleting
5. **Given** history is empty, **When** the user views History, **Then** the "Clear all" button is hidden

---

## Edge Cases

- What happens if a history entry's text is exactly the same as another entry? Deduplication by `textInput` — only one entry exists, timestamp updated on each save.
- What happens if the database is corrupted? Room with `exportSchema = false` and no migrations — app would crash on next launch. Version 1 with no migrations means this is a clean start each time.
- What happens on rapid delete? Each delete is a separate Room operation — fine for single-user app.
- What happens if favorites toggle is tapped during a delete? Room operations are sequential — favorite toggle completes, then delete (or vice versa).

## Functional Requirements

- **FR-034**: System MUST persist conversion history using Room database (morseling.db, version 1)
- **FR-035**: System MUST save conversion entry (textInput, morseOutput, timestamp, isFavorite) on each conversion
- **FR-036**: System MUST deduplicate by textInput — update timestamp if same text exists, insert new otherwise
- **FR-037**: System MUST display history entries in reverse chronological order (most recent first)
- **FR-038**: System MUST display each entry as an ElevatedCard showing input text, Morse output, and formatted date
- **FR-039**: System MUST allow tapping a history entry to load it into the main screen and navigate back
- **FR-040**: System MUST allow toggling favorite state on each entry with animated star color
- **FR-041**: System MUST allow deleting individual entries immediately
- **FR-042**: System MUST allow clearing all entries with confirmation dialog
- **FR-043**: System MUST hide "Clear all" button when history is empty
- **FR-044**: System MUST show empty state message when no history exists

## Non-Functional Requirements

- **Performance**: Room query returns Flow immediately; LazyColumn renders efficiently. History list < 50ms to render for up to 1000 entries.
- **Security**: History stored in app-private Room database. No network access — data never leaves device.
- **Accessibility**: Star button has "Favorite" content description. Delete button has "Delete" content description. Date formatted with `Locale.getDefault()`.
- **Observability**: History state exposed as `StateFlow<List<ConversionHistoryEntity>>`. Delete/clear operations complete asynchronously with Room auto-invalidation.

## Success Criteria

- **SC-012**: Users can access past conversion from history with 2 taps (History → tap entry)
- **SC-013**: Deduplication correctly handles repeated conversions (verified: no duplicate rows for same textInput)
- **SC-014**: Clear all removes all entries within 100ms (single DELETE query)
- **SC-015**: History persists across app restarts (Room database is durable)

## Assumptions

- Room auto-generates the database schema from entity annotations — no manual SQL needed
- `OnConflictStrategy.REPLACE` correctly handles the insert-or-update pattern
- Users want chronologically reverse-ordered history (most recent first)
- No history search/filter needed in MVP (future feature)
- No data export/import needed in MVP (future feature)
