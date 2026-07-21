# Conversion History — Takeaways

## What Went Well

- **Room with Flow is perfect for reactive lists** — `dao.getAll(): Flow<List<...>>` auto-emits on every table change. HistoryScreen just collects — no manual refresh.
- **Dedup by textInput is intuitive** — users don't think about duplicates. Same text always shows once, always at the top. Matches mental model of "most recently used."
- **ElevatedCard with stable keys** — `key = { it.id }` in LazyColumn ensures smooth animations on item changes. Tapping favorite or delete doesn't cause full list recomposition.
- **Confirmation dialog for destructive action** — "Clear all" gets a dialog. Single delete doesn't — reasonable tradeoff between safety and friction.

## What We Learned

- **Room's OnConflictStrategy.REPLACE is sufficient for insert-or-update** — but the explicit dedup logic (find then update or insert) is clearer and more intentional than relying on conflict resolution.
- **LazyColumn is the right choice for variable-height items** — each history card has different text/Morse lengths. LazyColumn handles variable heights efficiently.
- **Empty state is essential UX** — "No history yet" is better than a blank screen. Users immediately know the feature exists and how to populate it.
- **Date formatting matters** — `MMM d, HH:mm` with `Locale.getDefault()` is readable and localized. Avoided full ISO timestamps which are technically precise but user-unfriendly.

## API / Tech Surprises

- **Room requires separate @Dao interface** — can't merge DAO into the Database class. This adds 1 file but is required by Room's annotation processor.
- **exportSchema = false hides migration info** — Room can generate JSON schema files for migration testing, but this is disabled. Future migrations will need manual version management.
- **Room Flow emits initial value immediately** — on first collection, the Flow emits the current database state. No need for a separate initial load step.

## Patterns Worth Reusing

- **Repository wrapping DAO** — keeps Room-specific code in the data layer. ViewModels see a simple repository interface. Dedup logic lives in the right place.
- **ElevatedCard for list items** — gives each history entry visual weight and touch target. Better than plain Text in a list.
- **`animateColorAsState` for star toggle** — small animation makes the favorite toggle feel responsive. Costs almost nothing in code.
- **AlertDialog for destructive confirmation** — standard Material 3 pattern. "Clear" button in error color signals destructive action.
