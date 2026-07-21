# App Infrastructure — Specification

## User Stories

### User Story 1 — Hilt Dependency Injection (Priority: P1 🎯 MVP)

As a developer, I want dependency injection configured via Hilt so that dependencies are provided automatically.

**Why this priority**: Hilt is the backbone of the app. All ViewModels, players, and repositories depend on it.

**Independent Test**: App compiles and runs with Hilt KSP annotation processing. All `@Inject` constructors are satisfied.

**Acceptance Scenarios**:

1. **Given** the app is annotated with `@HiltAndroidApp`, **When** the application starts, **Then** Hilt initializes and provides all dependencies
2. **Given** a ViewModel is annotated with `@HiltViewModel`, **When** the screen requests it via `hiltViewModel()`, **Then** all constructor-injected dependencies are available
3. **Given** `PlayerModule` provides `@Named("audio")` and `@Named("flash")` MorsePlayer instances, **When** MorseConverterViewModel requests both, **Then** the correct instances are injected

---

### User Story 2 — Room Database (Priority: P1 🎯 MVP)

As a developer, I want a Room database for history persistence so that conversion history survives app restarts.

**Why this priority**: Required by 004-conversion-history. Without it, history is lost on every app restart.

**Independent Test**: Room database created at `morseling.db`, `conversion_history` table exists, DAO operations work.

**Acceptance Scenarios**:

1. **Given** the database is version 1, **When** the app starts for the first time, **Then** the `conversion_history` table is created
2. **Given** the database exists, **When** a DAO operation is called, **Then** the query executes correctly

---

### User Story 3 — DataStore Preferences (Priority: P1 🎯 MVP)

As a developer, I want settings persisted via DataStore so that user preferences survive app restarts.

**Why this priority**: Required by 005-settings. Without it, settings reset on every launch.

**Independent Test**: DataStore file created at `settings.preferences_pb`, keys are readable/writable.

**Acceptance Scenarios**:

1. **Given** the app starts for the first time, **When** no DataStore file exists, **Then** defaults are returned (mode=audio, wpm=20, language=system)
2. **Given** a setting is written, **When** the app restarts, **Then** the written value is returned

---

### User Story 4 — Locale Application on Startup (Priority: P2)

As a user, I want my language preference applied on startup so that the app opens in my chosen language.

**Why this priority**: Required for 005-settings language feature. Only matters if user has changed language from default.

**Independent Test**: Set language to Chinese → restart app → app opens in Chinese immediately.

**Acceptance Scenarios**:

1. **Given** language is set to "en", **When** the app starts, **Then** all UI text is in English
2. **Given** language is set to "zh-rCN", **When** the app starts, **Then** all UI text is in Chinese
3. **Given** language is set to "system", **When** the app starts, **Then** no locale override is applied

---

### User Story 5 — ViewModel Lifecycle Cleanup (Priority: P3)

As a developer, I want resources cleaned up when the ViewModel is destroyed so that there are no resource leaks.

**Why this priority**: Hygiene. Without it, ToneGenerator might leak or playJob might not be cancelled.

**Independent Test**: ViewModel destroyed → `onCleared()` called → players released, playJob cancelled.

**Acceptance Scenarios**:

1. **Given** the main ViewModel is being destroyed, **When** `onCleared()` is called, **Then** the play job is cancelled and both audio/flash players are released
2. **Given** the audio player's `release()` is called, **When** executed, **Then** `ToneGenerator.release()` is called

---

## Edge Cases

- What happens if Hilt compilation fails? Build fails — caught at compile time, not runtime.
- What happens if Room schema version mismatch? With version 1 and no migrations, app crashes. Future schema changes need manual migration.
- What happens if DataStore write fails? Exception propagates — app would crash. No retry or fallback.
- What happens if viewModelScope is cancelled before onCleared? `super.onCleared()` handles this — cleanup still runs.

## Functional Requirements

- **FR-065**: System MUST initialize Hilt with `@HiltAndroidApp` on Application class
- **FR-066**: System MUST annotate Activity with `@AndroidEntryPoint`
- **FR-067**: System MUST annotate all ViewModels with `@HiltViewModel`
- **FR-068**: System MUST provide Room database (version 1, morseling.db) as a singleton via Hilt
- **FR-069**: System MUST provide DataStore instance via Hilt singleton
- **FR-070**: System MUST provide `@Named("audio")` and `@Named("flash")` MorsePlayer singletons
- **FR-071**: System MUST declare CAMERA permission in AndroidManifest
- **FR-072**: System MUST declare `uses-feature android.hardware.camera.flash` with `required="false"`
- **FR-073**: System MUST apply saved locale before `setContent` on startup
- **FR-074**: System MUST cancel play job and release players in ViewModel.onCleared()
- **FR-075**: System MUST enable `allowBackup` and `supportsRtl` in AndroidManifest

## Non-Functional Requirements

- **Performance**: App startup < 2 seconds cold start. Hilt initialization is async (component creation happens on first use). DataStore first read < 10ms.
- **Security**: No exported components in manifest. No network permissions. App-private database and preferences.
- **Observability**: Hilt provides compile-time dependency graph validation. Room validates queries at compile time via KSP.

## Success Criteria

- **SC-024**: App compiles and runs with all Hilt dependencies satisfied
- **SC-025**: Room database survives app restart with all data intact
- **SC-026**: DataStore settings survive app restart with correct values
- **SC-027**: App does not crash when camera flash is unavailable (graceful degradation)
- **SC-028**: No resource leaks on ViewModel destruction (verified via memory profiling)

## Assumptions

- Hilt KSP is configured correctly in build.gradle.kts
- Room KSP generates DAO implementations at compile time
- DataStore file creation doesn't fail under normal conditions
- CameraManager.getCameraIdList() is available on all API 26+ devices
