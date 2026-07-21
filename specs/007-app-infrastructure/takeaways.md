# App Infrastructure — Takeaways

## What Went Well

- **Version catalog** — `libs.versions.toml` centralizes all dependency versions. Adding a new library means one change in the catalog, not multiple build.gradle.kts files.
- **Hilt @Singleton scope** — players and database are singletons. MorseCodeConverter's ToneGenerator is created once and reused. FlashlightMorsePlayer's camera probe runs once at construction.
- **Single Activity, Compose-only** — no fragment lifecycle complexity. All navigation is Compose-managed. Hilt injection just works.
- **DataStore + Room = clean persistence** — two storage backends for two different kinds of data (key-value vs relational). No shoehorning settings into Room or history into DataStore.

## What We Learned

- **Hilt KSP is faster than Hilt KAPT** — Kotlin Symbol Processing avoids Java annotation processing overhead. Builds are noticeably faster with KSP.
- **ProGuard minimal rules are sufficient** — `proguard-rules.pro` keeps only annotations, source file, and line numbers. No complex rules needed because the app has no reflection-heavy libraries.
- **`runBlocking` in onCreate is acceptable once** — the startup locale read is the ONLY place `runBlocking` appears. It's documented as a ⚡ guardrail. Any future addition of `runBlocking` would be a code smell.
- **`exportSchema = false` is a tradeoff** — it simplifies the build but removes Room's migration testing support. Future schema changes will need manual migration objects instead of Room's auto-generation.

## API / Tech Surprises

- **`@HiltViewModel` automatically scopes to the NavBackStackEntry** — no manual scoping needed. Compose Navigation's `hiltViewModel()` finds the right entry.
- **KSP requires separate declaration for test variants** — `kspTest` in `build.gradle.kts` is needed for Hilt's test compiler. Without it, instrumented tests fail.
- **DataStore file path is app-internal** — `context.preferencesDataStore("settings")` creates the file in the app's internal storage. No file permissions needed.
- **Gradle 9.5.1 requires AGP 8.7.3+** — these versions are coupled. Upgrading one without the other causes build failures.

## Patterns Worth Reusing

- **Convention plugin pattern** — root `build.gradle.kts` declares plugins with `apply false`; app module `build.gradle.kts` applies them. Standard Gradle multi-module setup.
- **Version catalog with aliases** — `libs.plugins.hilt`, `libs.versions.room` — aliases are cleaner than bare version strings scattered across build files.
- **DI module per domain** — `PlayerModule`, `DatabaseModule`, `SettingsModule` — each module provides related dependencies. Easier to find and maintain than one monolithic AppModule.
- **Manifest features as optional** — `required="false"` for camera.flash — the app installs and works on all devices. No Google Play filtering for flash-less devices.
