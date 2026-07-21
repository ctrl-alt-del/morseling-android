# Text Input & Presets — Takeaways

## What Went Well

- **Default text "Hello World"** — pre-filling input gives users an immediate, working example. Reduces activation energy to see the app work.
- **AssistChip in LazyRow** — Material 3's AssistChip is the right component for presets. It's tappable, accessible, and visually distinct. LazyRow handles variable chip widths without layout issues.
- **Disabled-during-playback pattern** — disabling input, Convert, and presets during playback prevents confusing states (user changes input while Morse is playing based on old input).

## What We Learned

- **OutlinedTextField defaults work well** — no need for extensive customization. The standard Material 3 OutlinedTextField with IME action Done and min/max lines handles all text input needs.
- **Presets should be convertible-only** — all 16 preset texts contain only A-Z, 0-9, and spaces. No presets produce unconvertible-character warnings. This was intentional but could have been an oversight.
- **Static presets are sufficient for MVP** — user-customizable presets would be nice but no user has requested it. The 16 cover amateur radio (CQ, 73, AR), emergency (SOS, Mayday), and common phrases (Hello, Good luck).

## API / Tech Surprises

- **AssistChip vs FilterChip vs SuggestionChip** — Material 3 has three chip types. AssistChip is the right choice for one-tap actions (fill text). FilterChip is for toggle states (used in Settings for mode). SuggestionChip is for autocomplete-style suggestions.
- **Compose LazyRow scrolls horizontally** — chips that overflow the screen width are accessible via horizontal scroll. No wrapping needed.

## Patterns Worth Reusing

- **`enabled = !uiState.isPlaying`** — simple and clear way to disable UI during busy states. Used consistently across text field, Convert button, and preset chips.
- **`updateTextInput()` clears error** — any new user input implicitly acknowledges and clears the previous error. No separate "dismiss error" action needed.
- **Preset as `data class`** — `Preset(label: String, text: String)` is simple and can be extended later (category, icon, order) without changing the rendering code.
