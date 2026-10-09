# AGENT_INSTRUCTIONS.md

You are building an Android app. Follow every rule below exactly. If a rule conflicts with a library default, override the default. Do not ask for permission to follow these rules; they are requirements.

## 0. Tech Stack (assumed unless the project says otherwise)

- Language: Kotlin
- UI: Jetpack Compose + Material 3 (all default Material styling must be overridden to match this document)
- Architecture: Clean Architecture + MVVM, package-by-feature
- DI: Hilt
- Persistence: Room (+ DataStore for preferences)
- Async: Kotlin Coroutines + Flow
- Min SDK 26, target the latest stable SDK

## 1. Design System Rules

### 1.1 No borders
- Never use borders, outlines, strokes, or divider lines on any component.
- Do not use `OutlinedButton`, `OutlinedTextField`, `BorderStroke`, `Modifier.border`, `HorizontalDivider`, or `Divider`.
- Separate elements using spacing, background tone differences, and shadows only.
- Text fields must be filled (tonal background), never outlined. Remove focus indicator lines and set indicator colors to `Color.Transparent`.

### 1.2 Rounded corners
- Every container, button, card, input, dialog, bottom sheet, chip, and image must have rounded corners. No sharp corners anywhere.
- Define shapes once in `core/designsystem/theme/AppShapes.kt` and reuse them:
  - `small` = 12.dp (chips, small tags)
  - `medium` = 16.dp (buttons, inputs)
  - `large` = 24.dp (cards, dialogs)
  - `extraLarge` = 32.dp (bottom sheets)
- Never hardcode corner radii in feature code.

### 1.3 Tap effects (always)
- Every clickable element must give visible tap feedback. No exceptions.
- Implement a single reusable modifier `Modifier.appClickable(...)` in `core/designsystem/modifier/` that combines:
  - Scale-down animation on press (to 0.96f) using `spring` animation
  - Ripple indication, bounded to the element's shape
- Never use raw `Modifier.clickable` in feature code. Always use `appClickable`.
- Never set `indication = null` on a clickable element.
- Also apply it to cards, list items, icons, chips, and toggles.

### 1.4 Mild shadows
- Use soft, subtle shadows for elevation: elevation 2.dp to 6.dp max.
- Shadow color: black at ~8–12% alpha (`ambientColor` / `spotColor`).
- Define elevation tokens in `AppElevation.kt`: `low = 2.dp`, `medium = 4.dp`, `high = 6.dp`. Never exceed `high`.
- Use shadows instead of borders to separate surfaces.

### 1.5 Colors

Define all colors in `core/designsystem/theme/AppColors.kt` as constants. Never hardcode hex values elsewhere.

| Token | Hex | Usage |
|---|---|---|
| `Background` | `#F9F7F6` | App background, and text/icons on top of buttons |
| `OnButton` | `#F9F7F6` | Text and icons on buttons |
| `ButtonBackground` | `#050504` | All button backgrounds |
| `TextPrimaryColor` / `TextOnSurface` | `#050504` | All non-button text |

Rules:
- Screen background is always `#F9F7F6`.
- Every button has background `#050504` and content (text/icons) `#F9F7F6`.
- All text that is not on a button is `#050504`.
- Disabled buttons: keep the same colors at 40% alpha. Do not introduce new colors.
- Do not use Material dynamic color. Disable it.
- Do not introduce any color not listed in this document.

### 1.6 Course colors

Default course color palette (store in `CourseColorPalette.kt` as an immutable list):

```
#CEE9BD
#E0D5CF
#BCC5AD
#CEC8ED
#D5BE99
#CEACBD
#C17596
#FDF79E
#EBD1C6
#7EAAEE
#EAAAB2
#94D3D5
#FBA7C3
#FBB5A7
#FBDFA7
#B0C8F2
#B9B0F2
#DAB0F2
```

Rules:
- Each course gets one color. Default new courses to the first palette color, or the first unused one.
- The color picker UI shows all palette colors as rounded swatches with tap effects, plus a "Custom" option.
- "Custom" opens a color picker (hue/saturation/brightness slider plus hex input field with validation). User-chosen colors are accepted as any valid hex.
- Persist the color as an ARGB `Int` in the Course entity.
- Text on top of a course color uses `#050504`, never `#F9F7F6`. Course colors are light surfaces, not buttons.
- Selected swatch is indicated with a check icon or scale-up. Never with a border.

### 1.7 Typography
- Primary text (titles, headings, names, key values, main labels): **Bold** (`FontWeight.Bold`).
- Secondary text (descriptions, captions, hints, metadata, subtitles): normal weight (`FontWeight.Normal`).
- Define two text style groups in `AppTypography.kt`: `Primary*` (bold) and `Secondary*` (regular). Feature code must pick from these. Never override `fontWeight` inline.

### 1.8 Navigation
- No back button in top bars. Never render a back arrow/icon in any top app bar or header.
- Navigation back is handled only by the system back gesture/button. Implement it correctly with `BackHandler` / `OnBackPressedDispatcher` where custom handling is needed (dialogs, bottom sheets, unsaved changes).
- Screens may have a title in the top area, but not a navigation icon.

## 2. Background Behavior (app always awake)

Requirement: the app must stay alive and running in the background at all times.

Implement it as follows:
1. A foreground service (`AppKeepAliveService`) with a persistent low-priority notification, started when the app launches. Declare the correct `foregroundServiceType` in the manifest and request required permissions (`FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_*` for the chosen type, `POST_NOTIFICATIONS`).
2. Return `START_STICKY` from `onStartCommand`.
3. A `BootReceiver` (`RECEIVE_BOOT_COMPLETED`) that restarts the service after reboot. Check Android 15+ restrictions on which foreground service types may start from boot, and use a type that is allowed.
4. A periodic `WorkManager` job that checks the service is running and restarts it if not.
5. Prompt the user once to disable battery optimization for the app (`ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`) with a clear explanation. Handle denial gracefully.
6. Do not use a permanent `WakeLock` to keep the CPU awake unless a feature strictly requires it. Use a partial wake lock only for short, bounded work.
7. Wrap all of this in an OOP class `BackgroundKeepAliveManager` (interface + implementation, injected via Hilt). UI/ViewModels never touch Android service APIs directly.

Known limitation: OEM task killers (Xiaomi, Oppo, Huawei, Samsung, etc.) may still kill the app. Document this in the README and add a settings screen entry guiding the user to the OEM autostart/battery settings.

## 3. Code Style: Strict OOP

- Everything is modeled as classes and interfaces. Behavior lives in classes, not in loose top-level functions.
- Allowed exceptions: `@Composable` functions (required by Compose), extension functions on framework types placed in `core/util/` inside a named `object`.
- Apply SOLID:
  - Depend on interfaces, never on concrete implementations across layers.
  - One class, one responsibility.
  - Inject all dependencies through constructors (Hilt).
- Use:
  - `interface` for repositories, use cases, data sources, managers
  - `sealed class` / `sealed interface` for UI state, events, results, and errors
  - `data class` for immutable models only
  - `abstract class` / `open class` for shared base behavior (e.g. `BaseViewModel`, `BaseUseCase`)
  - `object`/`companion object` only for constants and factories
- Encapsulation: fields are `private` by default. Expose state via `StateFlow` (read-only). ViewModels never expose `MutableStateFlow`.
- Each use case is a class with a single `operator fun invoke(...)`.
- Each Compose screen has exactly one ViewModel. The ViewModel exposes one `UiState` sealed class/data class and accepts `UiEvent` objects.
- No god classes. No static mutable state. No singletons outside Hilt.
- Naming: classes `PascalCase`, files match the main class name, one top-level class per file.
- Write KDoc for every public class and interface.

## 4. Project Structure

Use a single `app` module with package-by-feature, layered inside each feature. Root package: `com.yourcompany.yourapp`.

```
app/
├── build.gradle.kts
└── src/
    ├── main/
    │   ├── AndroidManifest.xml
    │   ├── java/com/yourcompany/yourapp/
    │   │   ├── App.kt                          # @HiltAndroidApp Application class
    │   │   ├── MainActivity.kt                 # Single-activity host
    │   │   │
    │   │   ├── core/
    │   │   │   ├── designsystem/
    │   │   │   │   ├── theme/
    │   │   │   │   │   ├── AppTheme.kt
    │   │   │   │   │   ├── AppColors.kt
    │   │   │   │   │   ├── AppShapes.kt
    │   │   │   │   │   ├── AppElevation.kt
    │   │   │   │   │   ├── AppTypography.kt
    │   │   │   │   │   └── CourseColorPalette.kt
    │   │   │   │   ├── modifier/
    │   │   │   │   │   └── AppClickable.kt     # tap effect modifier
    │   │   │   │   └── components/
    │   │   │   │       ├── AppButton.kt
    │   │   │   │       ├── AppTextField.kt
    │   │   │   │       ├── AppCard.kt
    │   │   │   │       ├── AppDialog.kt
    │   │   │   │       ├── AppBottomSheet.kt
    │   │   │   │       ├── AppTopBar.kt        # no back button
    │   │   │   │       └── ColorPicker.kt
    │   │   │   ├── base/
    │   │   │   │   ├── BaseViewModel.kt
    │   │   │   │   ├── BaseUseCase.kt
    │   │   │   │   └── UiState.kt
    │   │   │   ├── di/
    │   │   │   │   ├── AppModule.kt
    │   │   │   │   ├── DatabaseModule.kt
    │   │   │   │   └── ServiceModule.kt
    │   │   │   ├── navigation/
    │   │   │   │   ├── AppNavHost.kt
    │   │   │   │   ├── Destination.kt          # sealed class of routes
    │   │   │   │   └── Navigator.kt
    │   │   │   ├── background/
    │   │   │   │   ├── BackgroundKeepAliveManager.kt
    │   │   │   │   ├── BackgroundKeepAliveManagerImpl.kt
    │   │   │   │   ├── AppKeepAliveService.kt
    │   │   │   │   ├── BootReceiver.kt
    │   │   │   │   └── KeepAliveWorker.kt
    │   │   │   ├── database/
    │   │   │   │   └── AppDatabase.kt
    │   │   │   ├── datastore/
    │   │   │   │   └── PreferencesDataSource.kt
    │   │   │   ├── result/
    │   │   │   │   ├── AppResult.kt            # sealed Success/Error
    │   │   │   │   └── AppError.kt
    │   │   │   └── util/
    │   │   │       └── DateTimeUtils.kt        # inside a named object
    │   │   │
    │   │   └── feature/
    │   │       └── course/                     # repeat this structure per feature
    │   │           ├── data/
    │   │           │   ├── local/
    │   │           │   │   ├── CourseDao.kt
    │   │           │   │   └── CourseEntity.kt
    │   │           │   ├── mapper/
    │   │           │   │   └── CourseMapper.kt
    │   │           │   └── repository/
    │   │           │       └── CourseRepositoryImpl.kt
    │   │           ├── domain/
    │   │           │   ├── model/
    │   │           │   │   └── Course.kt
    │   │           │   ├── repository/
    │   │           │   │   └── CourseRepository.kt
    │   │           │   └── usecase/
    │   │           │       ├── GetCoursesUseCase.kt
    │   │           │       ├── AddCourseUseCase.kt
    │   │           │       └── UpdateCourseColorUseCase.kt
    │   │           └── presentation/
    │   │               ├── list/
    │   │               │   ├── CourseListScreen.kt
    │   │               │   ├── CourseListViewModel.kt
    │   │               │   ├── CourseListUiState.kt
    │   │               │   └── CourseListUiEvent.kt
    │   │               └── edit/
    │   │                   ├── CourseEditScreen.kt
    │   │                   ├── CourseEditViewModel.kt
    │   │                   ├── CourseEditUiState.kt
    │   │                   └── CourseEditUiEvent.kt
    │   │
    │   └── res/
    │       ├── drawable/
    │       ├── values/
    │       │   ├── strings.xml                 # no hardcoded strings in code
    │       │   └── themes.xml
    │       └── xml/
    ├── test/                                   # unit tests mirror main packages
    └── androidTest/                            # instrumented / UI tests
```

Layer rules:
- `domain` is pure Kotlin. No Android imports. It depends on nothing.
- `data` depends on `domain`. It implements domain interfaces.
- `presentation` depends on `domain` only. It never touches `data` directly.
- `core` has no dependency on any `feature`.
- Features never depend on each other directly. Share via `core` or domain interfaces.

## 5. Quality Rules

- Zero hardcoded colors, dimensions, shapes, strings, or font weights in feature code. Use design system tokens and resources.
- Every screen is built from design system components (`AppButton`, `AppCard`, etc.), never raw Material components.
- Add `@Preview` for every reusable component.
- Handle loading, empty, and error states in every screen via `UiState`.
- Write unit tests for every use case, mapper, and ViewModel.
- Lint and Detekt must pass. No warnings left unresolved.

## 6. Checklist (verify before finishing any task)

- [ ] No borders, outlines, or dividers anywhere
- [ ] All elements have rounded corners from `AppShapes`
- [ ] All clickables use `appClickable` (scale + ripple)
- [ ] Shadows are mild, max 6.dp
- [ ] Background `#F9F7F6`; buttons `#050504` with `#F9F7F6` content; other text `#050504`
- [ ] Course colors use the palette, with custom color support
- [ ] Primary text bold, secondary text normal
- [ ] No top back buttons
- [ ] Background keep-alive implemented via `BackgroundKeepAliveManager`
- [ ] Code is OOP, interface-driven, injected, and follows the folder structure above


## 7. No comments anywhere at all