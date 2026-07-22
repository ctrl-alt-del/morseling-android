# Fast CI + SDK 37 — Takeaways

## What Went Well

- The sftping CI pattern translated cleanly with almost 1:1 reuse. The runner's pre-installed SDK plus platform 37 caching eliminates 2+ minutes of dead weight per CI run.
- AGP 9.x + Kotlin 2.2.x upgrade required only removing the explicit `kotlin-android` plugin (AGP applies it automatically now) — no other source code changes.
- All existing unit tests passed with zero modifications after the AGP/Kotlin/KSP bump.
- Room 2.7.1 resolved the KSP 2.3.9 annotation processor incompatibility cleanly.

## What We Learned

- **AGP 9.x requires Hilt ≥2.52+**: Hilt 2.51.1 fails with "Android BaseExtension not found". The bump to 2.59.2 was a transitive requirement, not optional.
- **Kotlin 2.2.x changes annotation defaults**: `@Inject` on constructor parameters now warns about the annotation target. Future Kotlin versions will default to `param-property` instead of `param`-only. The `@param:` target prefix silences the warning for now.
- **Room 2.6.x + KSP 2.3.x incompatible**: `java.lang.IllegalStateException: unexpected jvm signature V` — Room 2.7.1 fixes this.
- **`tinypinyin` library only on Aliyun mirror**: `com.github.promeg:tinypinyin:2.0.3` was published on jcenter (now dead) and is not on Maven Central or JitPack. The Aliyun public mirror is the only surviving source. Removing it broke resolution.
- **Configuration cache unstable with AGP 9.x + Gradle 9.5.1**: The `processDebugNavigationResources` task triggers a serialization error. Disabled until fixed upstream.

## Reusable Patterns

- **Platform SDK caching for CI**: For SDK versions not yet in Google's public repository, compress the platform directory, host it on a ci-assets branch, and cache the extracted result. Cache key ties to runner OS + a version suffix for invalidation.
- **Minimum dependency bump strategy**: When targeting a new compileSdk, bump only the chain that's mandatory (AGP → Kotlin → KSP → Hilt → Room). Leave Compose BOM, Coroutines, DataStore, and Navigation at current versions to minimize breakage.
- **Plugin repo filtering**: Using `includeGroupByRegex` in `pluginManagement.repositories` for `com.android.*`, `com.google.*`, and `androidx.*` narrows plugin resolution to google() only, avoiding unnecessary mavenCentral/GradlePluginPortal queries.

## Surprises

- The `kotlin-android` plugin removal was required but not immediately obvious from AGP release notes. AGP 9.x applies Kotlin internally, and a second explicit application conflicts.
- Gradle configuration cache isn't yet stable with the AGP 9.x + KSP + Hilt combination. This will likely resolve in future AGP/KSP releases.
