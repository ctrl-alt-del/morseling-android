# App Infrastructure — Test Plan

## Unit Tests

### ViewModel Lifecycle
- [x] **onCleared cancels job**: After play, onCleared → isPlaying is false
- [x] **onCleared releases players**: After onCleared, player.release() called
- [x] **onCleared handles null job**: onCleared with no active playback → no crash

## Instrumented Tests

### Hilt Integration
- [x] **Hilt injects dependencies**: @HiltAndroidTest with HiltAndroidRule → injection succeeds
- [x] **ViewModel created with Hilt**: hiltViewModel() returns properly injected ViewModel

### Manifest Verification
- [x] **Camera permission declared**: Use PackageManager to check declared permissions
- [x] **Flash feature optional**: App installs on devices without flash

## Edge Cases
- [x] **Build with ProGuard**: Release build compiles and doesn't strip required classes
- [x] **Cold start**: App launches without crash on first install
- [x] **DataStore migration**: Setting changed in old version, survives app update (version 2 with same DataStore schema)
- [x] **Multiple Hilt components**: No duplicate bindings from overlapping modules
