# Theme & Navigation — Tasks

## Block 0: Spec & Design
- [x] `doc-coauthoring`: spec.md + plan.md approved
- [x] `checklist.md`: requirements quality checklist verified
- [x] `test_plan.md`: test scenarios documented

## Block 1: Theme Foundation

- [x] **Task 1.1**: Define color constants for light + dark schemes — `ui/theme/Color.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.2**: Define custom typography (Morse monospace, titles, subtitle) — `ui/theme/Type.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.3**: Define corner shapes — `ui/theme/Shape.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

- [x] **Task 1.4**: Create MorselingTheme composable with dynamic color + dark mode — `ui/theme/Theme.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`

## Block 2: Navigation

- [x] **Task 2.1**: Create MorselingNavGraph with 4 routes (main, history, settings, licenses) — `navigation/MorselingNavGraph.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 2.2**: Add clock (history) and gear (settings) icon buttons to main screen top bar — `ui/MainScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 2.3**: Add back navigation to history, settings, and licenses screens — `ui/HistoryScreen.kt`, `ui/SettingsScreen.kt`, `ui/LicensesScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

## Block 3: Edge-to-Edge

- [x] **Task 3.1**: Add `enableEdgeToEdge()` in MainActivity.onCreate — `MainActivity.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 3.2**: Ensure Scaffold paddingValues handle system bar insets — all screen files
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

## Block 4: Licenses Screen

- [x] **Task 4.1**: Create LicensesScreen with 11 library entries — `ui/LicensesScreen.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 4.2**: Add licenses route to navigation — `navigation/MorselingNavGraph.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

## Block 5: Integration

- [x] **Task 5.1**: Wrap setContent with MorselingTheme and NavHost in MainActivity — `MainActivity.kt`
  - Build: `gradle assembleDebug`
  - Tests: `gradle connectedAndroidTest`

- [x] **Task 5.2**: Update base themes.xml for splash/fallback — `values/themes.xml`
  - Build: `gradle assembleDebug`
  - Tests: `gradle test`
