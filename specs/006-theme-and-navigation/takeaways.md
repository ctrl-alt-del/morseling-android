# Theme & Navigation — Takeaways

## What Went Well

- **Dynamic color with fallback** — `dynamicColor && Build.VERSION.SDK_INT >= 31` elegantly handles the availability check. Fallback custom schemes ensure the app looks good on all Android versions.
- **Single NavHost, scoped ViewModel** — `mainViewModel` is created once and shared. History and Settings ViewModels are route-scoped via `hiltViewModel()`. Clean lifecycle management.
- **Edge-to-edge with paddingValues** — `enableEdgeToEdge()` + Scaffold `paddingValues` gives full-screen content without manual inset calculation. Simple and correct.
- **Static licenses list** — 11 entries as a `data class` is simple and covers all major dependencies. No unreliable Gradle license plugins needed.

## What We Learned

- **Material 3 color roles are extensive** — 65 color constants for both light and dark schemes. Most go unused but having the full set is cleaner than partial.
- **Typography for Morse code is critical** — `bodyLarge` set to `Monospace` with 2sp letter spacing makes dots and dashes align vertically. Without monospace, Morse output is hard to read.
- **Navigation icons need discovery** — clock for history, gear for settings — these are Android conventions but new users might not immediately understand. Icons are always visible which helps discovery.
- **Licenses screen URLs are not clickable** — this was noted as a sharp edge. Future enhancement: use `ClickableText` or `UriHandler` to open URLs in browser.

## API / Tech Surprises

- **Compose Navigation's route strings are just strings** — no type-safe navigation (type-safe routes are available in newer Navigation Compose versions but 2.8.5 uses string routes).
- **`enableEdgeToEdge()` must be called before `setContent`** — calling it after setContent doesn't work. Order matters in `onCreate`.
- **Material 3 dynamic color requires `dynamicColor = true`** — MediaQuery disables it. This flag goes in the theme composable.

## Patterns Worth Reusing

- **Theme composable wrapping NavHost** — `MorselingTheme { NavHost { ... } }` ensures theme is applied to all screens without per-screen setup.
- **Sealed class for routes** — `sealed class Route(val route: String)` is cleaner than string constants scattered across files. Add new routes by adding sealed class members.
- **`data class` for static display data** — `LicenseEntry(name, license, url)` is better than a map or Pair. Self-documenting and easy to render in a LazyColumn.
- **Icons from Material Icons Extended** — the expanded icon set has clock, gear, translate, and other useful icons. No need for custom icon assets for common actions.
