---
feature_id: "006"
name: "Theme & Navigation"
status: "✅ Done"
depends_on: ["001", "002", "003", "004", "005"]
touches:
  - "app/src/main/java/com/morseling/ui/theme/Color.kt"
  - "app/src/main/java/com/morseling/ui/theme/Shape.kt"
  - "app/src/main/java/com/morseling/ui/theme/Theme.kt"
  - "app/src/main/java/com/morseling/ui/theme/Type.kt"
  - "app/src/main/java/com/morseling/navigation/MorselingNavGraph.kt"
  - "app/src/main/java/com/morseling/ui/LicensesScreen.kt"
  - "app/src/main/java/com/morseling/MainActivity.kt"
created: "2026-07-21"
---

# Theme & Navigation — Implementation Plan

## Technical Context

**Language/Version**: Kotlin 2.0.21
**Primary Dependencies**: Jetpack Compose + Material 3, Compose Navigation 2.8.5, Activity Compose 1.9.3
**Storage**: N/A (theme is computed, navigation is runtime)
**Testing**: Compose UI Test (instrumented)
**Target Platform**: Android 26+
**Performance Goals**: Theme computation < 5ms, navigation transitions < 100ms
**Constraints**: Material 3 only (no Material 2), dynamic color soft dependency (Android 12+)
**Scale/Scope**: 4 routes, 1 theme config, 11 license entries

## Constitution Check

- [x] **Article I (Spec-First)**: spec.md written. Retroactive.
- [x] **Article II (Test-First)**: Instrumented tests cover navigation. Retroactive.
- [x] **Article III (Modularity)**: Theme in `ui/theme/`, navigation in `navigation/`, clear boundaries.
- [x] **Article IV (Simplicity)**: 5 files (Color, Shape, Theme, Type, MorselingNavGraph) + 2 screens. At boundary but justified.
- [x] **Article V (Anti-Abstraction)**: Using Compose MaterialTheme and NavHost directly. No custom wrappers.
- [x] **Article VI (Integration Reality)**: Theme tested visually via Compose tests. Navigation tested via Compose test rule.
- [x] **Article VII (Observability)**: Route state managed by NavController. Top bar titles reflect current route.

## Approach

**Theme**: A custom `MorselingTheme` composable wraps the entire app. It computes the color scheme based on:
1. Android 12+ dynamic color availability → `dynamicDarkColorScheme()` / `dynamicLightColorScheme()`
2. Fallback → custom `LightScheme` / `DarkScheme` with 65 color constants
3. Dark mode → `isSystemInDarkTheme()` system setting

**Typography**: Custom `Typography` object with overrides for Morse code (bodyLarge = Monospace, 16sp, 2sp letter spacing), titles (titleMedium, headlineLarge), and subtitle (displaySmall).

**Shapes**: `Shapes` object with 5 corner radii (extraSmall=4dp through extraLarge=24dp).

**Navigation**: `NavHost` with 4 routes. `mainViewModel` is scoped to the NavHost composable (shared across routes). History and Settings ViewModels are created per-route via `hiltViewModel()`.

**Licenses**: Static list of 11 `data class` entries rendered as Cards in a LazyColumn.

**Edge-to-edge**: `enableEdgeToEdge()` in `MainActivity.onCreate()`. Scaffolds use `paddingValues` for inset handling.

## Data Model

| Entity | Key Attributes | Relationships |
|--------|---------------|---------------|
| LicenseEntry | name: String, license: String, url: String | Static list, 11 entries |

## API / CLI Contract

```kotlin
sealed class Route(val route: String) {
    object Main : Route("main")
    object History : Route("history")
    object Settings : Route("settings")
    object Licenses : Route("licenses")
}
```

## Alternatives Considered

| Alternative | Pros | Cons | Why Rejected |
|-------------|------|------|--------------|
| Hardcoded light scheme only | Simpler | No dark mode, no Material You | Dynamic color is a Material 3 hallmark |
| Navigation via Activities | Simpler routing | No shared ViewModel, heavy transitions | Single Activity + Compose Navigation is the modern standard |
| Generated licenses via Gradle plugin | Auto-updates, complete | Adds build complexity, unreliable with R8 | Static list is simple and covers major deps |
| Separate theme per screen | Flexibility | Inconsistent UX, duplicated code | Single theme composable wraps entire app |

**Decision**: Single `MorselingTheme` composable with dynamic color + dark mode + custom typography/shapes. Compose Navigation with single Activity.

## Risks

| Risk | Likelihood | Impact | Mitigation |
|------|-----------|--------|------------|
| Dynamic color produces unreadable contrast | Low | Medium | Material 3 color roles ensure minimum contrast ratios. Custom scheme fallback available. |
| Navigation state loss on process death | Low | Medium | Compose Navigation + SavedStateHandle handles this. ViewModel-scoped state survives. |
| License list becomes outdated | Medium | Low | Static list — update manually when deps change. Low priority since licenses are compliance, not user-facing. |

## Dependencies

| Depends On | Status | Blocking? |
|-----------|--------|-----------|
| 001-004 (all features use theme + navigation) | ✅ Done | No |
| Material 3 Compose | Ready | No |
| Compose Navigation 2.8.5 | Ready | No |

## Complexity Tracking

| Article | Violation | Why Needed | Simpler Alternative Rejected Because |
|---------|-----------|------------|--------------------------------------|
| Art. IV | 5 files (at boundary) | Theme requires Color + Shape + Theme + Type files (Material 3 convention). Navigation graph is separate. | Could merge Color/Shape/Type into Theme.kt, but separate files follow Material 3 convention and are easier to maintain. |

## Files to Create / Change

| Action | File | Rationale |
|--------|------|-----------|
| Create | `ui/theme/Color.kt` | Color constants for custom schemes |
| Create | `ui/theme/Shape.kt` | Corner radii shape definitions |
| Create | `ui/theme/Theme.kt` | MorselingTheme composable with dynamic color |
| Create | `ui/theme/Type.kt` | Custom typography with monospace Morse |
| Create | `navigation/MorselingNavGraph.kt` | NavHost with 4 routes |
| Create | `ui/LicensesScreen.kt` | Static library attribution list |
| Modify | `MainActivity.kt` | Add enableEdgeToEdge(), set MorselingTheme, NavHost |
| Modify | `values/themes.xml` | Base theme for splash/compat |

## Quickstart Validation

1. App opens → blue/teal theme (or dynamic color on Android 12+) → title "Morseling" with subtitle
2. Toggle system dark mode → app switches to dark color scheme
3. Tap clock icon → History screen opens → back arrow → Main screen
4. Tap gear icon → Settings screen → tap Licenses → Licenses screen → back → Settings → back → Main
5. On Licenses screen → 11 library entries visible with name, license type, URL
6. On any screen → content extends behind status bar, no obscuring
