# AGENT_INSTRUCTIONS.md

You are building an Android app in Java. Follow every rule below exactly. If a rule conflicts with a library default, override the default. Do not ask for permission to follow these rules; they are requirements.

## 0. Tech Stack (assumed unless the project says otherwise)

- Language: Java 17 only. No Kotlin source files. Only build scripts (`*.gradle.kts`) may use Kotlin DSL.
- UI: XML layouts + ViewBinding + Material Components (Material 3 theme). Jetpack Compose is not allowed (it requires Kotlin).
- Screens: single Activity + Fragments + Navigation Component (XML nav graph)
- Architecture: Clean Architecture + MVVM, package-by-feature
- DI: Hilt (annotation processor, not KSP/kapt)
- Persistence: Room (+ DataStore Preferences RxJava3 for preferences)
- Async: RxJava3 in domain/data layers, converted to `LiveData` in ViewModels. No Kotlin Coroutines or Flow.
- Lists: `RecyclerView` + `ListAdapter` + `DiffUtil`
- Min SDK 26, target the latest stable SDK
- Light theme only. No `values-night`. Force `AppCompatDelegate.MODE_NIGHT_NO` in `App`.

## 1. Design System Rules

### 1.1 No borders
- Never use borders, outlines, strokes, or divider lines on any component.
- Do not use `<stroke>` in drawables, `strokeWidth`/`strokeColor` attributes, `MaterialDivider`, `DividerItemDecoration`, 1dp separator `View`s, or `Widget.Material3.*.OutlinedButton` / `*.OutlinedBox` styles.
- Separate elements using spacing, background tone differences, and shadows only.
- Text inputs use `TextInputLayout` with `boxBackgroundMode="filled"`, `boxStrokeWidth="0dp"`, `boxStrokeWidthFocused="0dp"`. No underline, no focus line.

### 1.2 Rounded corners
- Every container, button, card, input, dialog, bottom sheet, chip, and image must have rounded corners. No sharp corners anywhere.
- Define corner sizes as dimens in `res/values/dimens.xml` and shape styles in `res/values/shapes.xml`. Reuse them:
  - `corner_small` = 12dp (chips, small tags)
  - `corner_medium` = 16dp (buttons, inputs)
  - `corner_large` = 24dp (cards, dialogs)
  - `corner_extra_large` = 32dp (bottom sheets)
- Images use `ShapeableImageView` with a shape style from `shapes.xml`.
- Never hardcode corner radii in layouts or Java code.

### 1.3 Tap effects (always)
- Every clickable element must give visible tap feedback. No exceptions.
- Implement one class `TapEffect` in `core/designsystem/interaction/` that combines:
  - Scale-down on press to 0.96f using `SpringAnimation` (`androidx.dynamicanimation`), spring back on release/cancel
  - A ripple (`RippleDrawable`) masked to the view's shape
- Every `App*` component (`AppButton`, `AppCard`, `AppChip`, `AppIconButton`, `AppSwitch`, list item containers) attaches `TapEffect` in its constructor, so using the component gives the effect automatically.
- Never call `setOnClickListener` on a plain `View`, `ImageView`, or `ViewGroup` in feature code. Use an `App*` component, or call `TapEffect.attach(view)` first.
- Never set `android:background="@null"` or `foreground="@null"` on a clickable element, and never disable ripple.

### 1.4 Mild shadows
- Use soft, subtle shadows for elevation: 2dp to 6dp max.
- Define tokens in `dimens.xml`: `elevation_low` = 2dp, `elevation_medium` = 4dp, `elevation_high` = 6dp. Never exceed `elevation_high`.
- Shadow color is a semantic color token `shadow` (black at ~10% alpha), applied via `android:outlineAmbientShadowColor` and `android:outlineSpotShadowColor` in styles (API 28+; on API 26-27 the system default shadow is accepted).
- Use shadows instead of borders to separate surfaces.

### 1.5 Colors

#### 1.5.1 Single source of truth (mandatory)
- Every color in the app is declared exactly once, as a named resource, in `res/values/colors.xml` (app colors) or `res/values/course_colors.xml` (course colors). Hex values appear in those two files and nowhere else.
- Changing a color later must require editing one line in one file. If a color change requires touching more than one file, the implementation is wrong.
- Never write `#RRGGBB` / `#AARRGGBB` in layouts, drawables, styles, themes, or the manifest. Never write `Color.parseColor("#...")`, `Color.rgb(`, `Color.argb(`, `Color.BLACK`, `Color.WHITE`, `Color.RED` or any other literal color in Java code. Never use `android.R.color.*`.
- Use two layers inside `colors.xml`:
  1. **Palette layer** (raw values): names prefixed `palette_`, holding the hex, e.g. `palette_cream`, `palette_ink`, `palette_shadow`. Layouts, styles, drawables, and Java code never reference `palette_*`.
  2. **Semantic layer** (role-based): `background`, `button_background`, `on_button`, `text_on_surface`, `shadow`. Each is an alias to a palette entry (`@color/palette_cream`), never a hex. Layouts, styles, and Java code only use semantic names.
- Styles and themes reference semantic colors only. Component colors that change by state (disabled, pressed) go in `res/color/` color state lists that reference semantic colors and `@dimen/disabled_alpha`.
- Java code never calls `ContextCompat.getColor` directly in feature code. It goes through an injected `ColorProvider` (interface + implementation) that exposes semantic colors only.
- Do not use Material dynamic color. Never call `DynamicColors.applyToActivitiesIfAvailable`.
- Do not introduce any color not defined in the two color files.
- The single allowed hex parsing in Java is `CustomColorParser` (in `core/util/`), which parses hex typed by the user in the custom color picker. That is user data, not a hardcoded color.

#### 1.5.2 Semantic tokens

| Semantic token | Palette entry | Hex | Usage |
|---|---|---|---|
| `background` | `palette_cream` | `#F9F7F6` | App/screen background |
| `on_button` | `palette_cream` | `#F9F7F6` | Text and icons on buttons |
| `button_background` | `palette_ink` | `#050504` | All button backgrounds |
| `text_on_surface` | `palette_ink` | `#050504` | All non-button text |
| `shadow` | `palette_shadow` | `#1A000000` | Shadow color |

Rules:
- Screen background is always `@color/background`.
- Every button uses `@color/button_background` with `@color/on_button` content.
- All text that is not on a button uses `@color/text_on_surface`.
- Disabled buttons: same tokens at 40% alpha, defined once as `@dimen/disabled_alpha` (`0.4`, `format="float"`). No new colors.

Shape of `colors.xml` (follow this pattern):

```xml
<resources>
    <color name="palette_cream">#F9F7F6</color>
    <color name="palette_ink">#050504</color>
    <color name="palette_shadow">#1A000000</color>

    <color name="background">@color/palette_cream</color>
    <color name="on_button">@color/palette_cream</color>
    <color name="button_background">@color/palette_ink</color>
    <color name="text_on_surface">@color/palette_ink</color>
    <color name="shadow">@color/palette_shadow</color>
</resources>
```

### 1.6 Course colors

#### 1.6.1 Palette tokens
Every default course color is a named resource in `res/values/course_colors.xml`, declared once. `CourseColorPalette` (Java class) is an immutable ordered list that maps a stable string key to the `R.color.course_*` id. It never contains hex.

| Resource name | Key | Hex |
|---|---|---|
| `course_pistachio` | `pistachio` | `#CEE9BD` |
| `course_stone` | `stone` | `#E0D5CF` |
| `course_sage` | `sage` | `#BCC5AD` |
| `course_lavender` | `lavender` | `#CEC8ED` |
| `course_sand` | `sand` | `#D5BE99` |
| `course_mauve` | `mauve` | `#CEACBD` |
| `course_rose` | `rose` | `#C17596` |
| `course_butter` | `butter` | `#FDF79E` |
| `course_blush` | `blush` | `#EBD1C6` |
| `course_sky` | `sky` | `#7EAAEE` |
| `course_pink` | `pink` | `#EAAAB2` |
| `course_aqua` | `aqua` | `#94D3D5` |
| `course_candy` | `candy` | `#FBA7C3` |
| `course_coral` | `coral` | `#FBB5A7` |
| `course_apricot` | `apricot` | `#FBDFA7` |
| `course_cornflower` | `cornflower` | `#B0C8F2` |
| `course_iris` | `iris` | `#B9B0F2` |
| `course_orchid` | `orchid` | `#DAB0F2` |

#### 1.6.2 Storage (so palette edits propagate)
- Do not store the default palette color as a raw ARGB value. If it were stored that way, editing a palette hex later would only affect new courses, and existing courses would keep the old color.
- `CourseEntity` stores:
  - `colorKey` (`String`, nullable) for a palette color (the stable key from 1.6.1)
  - `customColorArgb` (`Integer`, nullable) for a user-chosen custom color
  - Exactly one of the two is non-null.
- A single `CourseColorResolver` (interface + implementation) converts the stored values into an `@ColorInt int` at runtime. UI never resolves colors itself.
- Changing a palette hex in `course_colors.xml` must update all existing courses that use that palette key.
- Never rename or reuse a palette key once released. Renaming a resource name is fine. Keys are the persisted contract.

#### 1.6.3 Behavior
- Each course gets one color. Default new courses to the first palette color, or the first unused one.
- The color picker UI shows all palette colors as rounded swatches with tap effects, plus a "Custom" option.
- "Custom" opens a color picker (hue/saturation/brightness sliders plus a hex input field with validation via `CustomColorParser`). Any valid hex is accepted.
- Text on top of a course color uses `@color/text_on_surface`, never `@color/on_button`. Course colors are light surfaces, not buttons.
- Selected swatch is indicated with a check icon or scale-up. Never with a border.

### 1.7 Typography
- Primary text (titles, headings, names, key values, main labels): bold.
- Secondary text (descriptions, captions, hints, metadata, subtitles): normal weight.
- Define two text appearance groups in `res/values/styles.xml`: `TextAppearance.App.Primary.*` (`android:textStyle="bold"`) and `TextAppearance.App.Secondary.*` (`android:textStyle="normal"`), each with Title / Body / Label sizes from dimens. Layouts must pick from these.
- Never set `android:textStyle`, `android:textSize`, `android:textColor`, `fontFamily`, or call `setTypeface` inline in layouts or Java code.

### 1.8 Navigation
- No back button in top bars. Never render a back arrow/icon in any toolbar or header.
- Do not call `setDisplayHomeAsUpEnabled`, `setNavigationIcon`, or set `navigationIcon` on any toolbar. Do not use `NavigationUI.setupActionBarWithNavController` with an up button.
- Back is handled only by the system back gesture/button. Use `OnBackPressedCallback` registered through `requireActivity().getOnBackPressedDispatcher()` where custom handling is needed (dialogs, bottom sheets, unsaved changes).
- Screens may have a title in the top area, but not a navigation icon.

## 2. Background Behavior (app always awake)

Requirement: the app must stay alive and running in the background at all times.

Implement it as follows:
1. A foreground service (`AppKeepAliveService`) with a persistent low-priority notification, started when the app launches. Declare the correct `foregroundServiceType` in the manifest and request required permissions (`FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_*` for the chosen type, `POST_NOTIFICATIONS`).
2. Return `START_STICKY` from `onStartCommand`.
3. A `BootReceiver` (`RECEIVE_BOOT_COMPLETED`) that restarts the service after reboot. Check Android 15+ restrictions on which foreground service types may start from boot, and use a type that is allowed.
4. A periodic `WorkManager` job (`KeepAliveWorker`) that checks the service is running and restarts it if not.
5. Prompt the user once to disable battery optimization for the app (`ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`) with a clear explanation. Handle denial gracefully.
6. Do not use a permanent `WakeLock` to keep the CPU awake unless a feature strictly requires it. Use a partial wake lock only for short, bounded work with a timeout.
7. Wrap all of this in `BackgroundKeepAliveManager` (interface + `BackgroundKeepAliveManagerImpl`, injected via Hilt). Fragments and ViewModels never touch Android service APIs directly.

Known limitation: OEM task killers (Xiaomi, Oppo, Huawei, Samsung, etc.) may still kill the app. Document this in the README and add a settings screen entry guiding the user to the OEM autostart/battery settings.

## 3. Code Style: Strict OOP

- Everything is a class or interface. No loose logic outside classes.
- Utility classes are allowed only in `core/util/`: `final`, private constructor, stateless `static` methods, named for the thing they operate on (e.g. `DateTimeFormatter`, not `Utils`).
- Apply SOLID:
  - Depend on interfaces, never on concrete implementations across layers.
  - One class, one responsibility.
  - Inject all dependencies through constructors (Hilt `@Inject`). No field injection except in Android framework classes (`@AndroidEntryPoint`).
- Use:
  - `interface` for repositories, use cases, data sources, managers, providers, resolvers
  - Abstract base class with `final` nested subclasses (or Java `sealed` classes) for UI state, results, and errors
  - `final` classes with `private final` fields and no setters for immutable models. Java `record` is allowed for domain models and UI state, but not for Room entities.
  - `abstract class` for shared base behavior (e.g. `BaseViewModel`, `BaseFragment`, `BaseUseCase`)
  - `static` only for constants (`private static final`) and factory methods
- Encapsulation: fields are `private` (and `final` wherever possible). ViewModels expose `LiveData`, never `MutableLiveData`. Use `@NonNull` / `@Nullable` on every public parameter and return type.
- Each use case is a class with a single public method `execute(...)` returning `Single`, `Completable`, `Maybe`, or `Flowable`.
- Each Fragment has exactly one ViewModel. The ViewModel exposes one `UiState` object and accepts `UiEvent` objects through one `onEvent(UiEvent)` method.
- No god classes. No static mutable state. No hand-written singletons (`getInstance()`); singletons only via Hilt `@Singleton`.
- Naming: classes `PascalCase`, files match the class name, one top-level class per file. Interface implementations use the `Impl` suffix.
- ViewModels hold a `CompositeDisposable` and clear it in `onCleared`. Never subscribe in a Fragment.
- All database and disk work runs off the main thread through an injected `AppSchedulers` interface (`io()`, `main()`), never `Schedulers.io()` directly, so tests can replace it.

### 3.1 No comments (mandatory)
- Write zero comments in any file: Java, XML, Gradle, manifest, ProGuard, YAML, and test files.
- This bans `//`, `/* */`, `/** */` (Javadoc), `<!-- -->`, and `#` comments in config files. No license headers, no TODO/FIXME markers, no commented-out code, no section divider comments.
- Remove any comment that exists in generated or template code (Android Studio templates, Gradle defaults, `proguard-rules.pro`, `AndroidManifest.xml`) before finishing a task.
- Code must explain itself through naming:
  - Descriptive class, method, variable, and constant names.
  - Small single-purpose methods with names that state their intent.
  - Named constants instead of magic numbers.
  - Enums and typed state classes instead of flag-and-comment conventions.
- Lint suppression uses annotations (`@SuppressWarnings("...")`, `@SuppressLint("...")`) or `lint.xml`, never comment directives.
- Documentation lives outside the code, in `README.md` and other `.md` files. Those files are allowed and expected.
- The structure tree annotations in section 4 are instructions to you, not code. Do not copy them into any file.

## 4. Project Structure

Use a single `app` module with package-by-feature, layered inside each feature. Root package: `com.yourcompany.yourapp`.

```
app/
├── build.gradle.kts
└── src/
    ├── main/
    │   ├── AndroidManifest.xml
    │   ├── java/com/yourcompany/yourapp/
    │   │   ├── App.java                        # @HiltAndroidApp Application class
    │   │   ├── MainActivity.java               # Single-activity host, @AndroidEntryPoint
    │   │   │
    │   │   ├── core/
    │   │   │   ├── designsystem/
    │   │   │   │   ├── interaction/
    │   │   │   │   │   └── TapEffect.java      # scale spring + masked ripple
    │   │   │   │   ├── color/
    │   │   │   │   │   ├── ColorProvider.java      # interface, semantic colors only
    │   │   │   │   │   └── ColorProviderImpl.java
    │   │   │   │   └── components/
    │   │   │   │       ├── AppButton.java
    │   │   │   │       ├── AppTextInput.java
    │   │   │   │       ├── AppCard.java
    │   │   │   │       ├── AppChip.java
    │   │   │   │       ├── AppIconButton.java
    │   │   │   │       ├── AppSwitch.java
    │   │   │   │       ├── AppDialog.java
    │   │   │   │       ├── AppBottomSheet.java
    │   │   │   │       ├── AppTopBar.java      # no back button
    │   │   │   │       └── ColorPickerView.java
    │   │   │   ├── base/
    │   │   │   │   ├── BaseViewModel.java
    │   │   │   │   ├── BaseFragment.java
    │   │   │   │   ├── BaseUseCase.java
    │   │   │   │   ├── UiState.java
    │   │   │   │   └── UiEvent.java
    │   │   │   ├── di/
    │   │   │   │   ├── AppModule.java
    │   │   │   │   ├── DatabaseModule.java
    │   │   │   │   └── ServiceModule.java
    │   │   │   ├── navigation/
    │   │   │   │   ├── Navigator.java          # interface
    │   │   │   │   └── NavigatorImpl.java
    │   │   │   ├── background/
    │   │   │   │   ├── BackgroundKeepAliveManager.java
    │   │   │   │   ├── BackgroundKeepAliveManagerImpl.java
    │   │   │   │   ├── AppKeepAliveService.java
    │   │   │   │   ├── BootReceiver.java
    │   │   │   │   └── KeepAliveWorker.java
    │   │   │   ├── threading/
    │   │   │   │   ├── AppSchedulers.java      # interface
    │   │   │   │   └── AppSchedulersImpl.java
    │   │   │   ├── database/
    │   │   │   │   └── AppDatabase.java
    │   │   │   ├── datastore/
    │   │   │   │   ├── PreferencesDataSource.java
    │   │   │   │   └── PreferencesDataSourceImpl.java
    │   │   │   ├── result/
    │   │   │   │   ├── AppResult.java          # abstract, Success / Failure
    │   │   │   │   └── AppError.java
    │   │   │   └── util/
    │   │   │       ├── CustomColorParser.java  # only class allowed to parse hex
    │   │   │       └── DateTimeFormatter.java
    │   │   │
    │   │   └── feature/
    │   │       └── course/                     # repeat this structure per feature
    │   │           ├── data/
    │   │           │   ├── local/
    │   │           │   │   ├── CourseDao.java
    │   │           │   │   └── CourseEntity.java   # colorKey + customColorArgb
    │   │           │   ├── mapper/
    │   │           │   │   └── CourseMapper.java
    │   │           │   └── repository/
    │   │           │       └── CourseRepositoryImpl.java
    │   │           ├── domain/
    │   │           │   ├── model/
    │   │           │   │   ├── Course.java
    │   │           │   │   ├── CourseColor.java        # abstract: PaletteColor(key) | CustomColor(argb)
    │   │           │   │   ├── PaletteColor.java
    │   │           │   │   └── CustomColor.java
    │   │           │   ├── repository/
    │   │           │   │   └── CourseRepository.java
    │   │           │   └── usecase/
    │   │           │       ├── GetCoursesUseCase.java
    │   │           │       ├── AddCourseUseCase.java
    │   │           │       └── UpdateCourseColorUseCase.java
    │   │           └── presentation/
    │   │               ├── CourseColorPalette.java     # key -> R.color.course_*
    │   │               ├── CourseColorResolver.java    # interface
    │   │               ├── CourseColorResolverImpl.java
    │   │               ├── list/
    │   │               │   ├── CourseListFragment.java
    │   │               │   ├── CourseListViewModel.java
    │   │               │   ├── CourseListAdapter.java
    │   │               │   ├── CourseListUiState.java
    │   │               │   └── CourseListUiEvent.java
    │   │               └── edit/
    │   │                   ├── CourseEditFragment.java
    │   │                   ├── CourseEditViewModel.java
    │   │                   ├── CourseEditUiState.java
    │   │                   └── CourseEditUiEvent.java
    │   │
    │   └── res/
    │       ├── drawable/
    │       ├── color/                          # color state lists (disabled alpha etc.)
    │       ├── layout/                         # fragment_*.xml, item_*.xml, view_*.xml
    │       ├── navigation/
    │       │   └── nav_graph.xml
    │       └── values/
    │           ├── colors.xml                  # palette_* + semantic aliases, only app hex
    │           ├── course_colors.xml           # course_* , only course hex
    │           ├── dimens.xml                  # spacing, corners, elevations, disabled_alpha
    │           ├── shapes.xml
    │           ├── styles.xml                  # component styles + TextAppearance.App.*
    │           ├── themes.xml
    │           └── strings.xml                 # no hardcoded strings in layouts or code
    ├── debug/
    │   └── java/com/yourcompany/yourapp/
    │       └── DesignSystemGalleryActivity.java    # renders every App* component
    ├── test/                                   # unit tests mirror main packages
    └── androidTest/                            # instrumented / UI tests
```

Layer rules:
- `domain` is pure Java. No `android.*` or `androidx.*` imports. It depends on nothing except RxJava. `CourseColor` holds only a key or an `int`.
- `data` depends on `domain`. It implements domain interfaces.
- `presentation` depends on `domain` only. It never touches `data` directly.
- `core` has no dependency on any `feature`.
- Features never depend on each other directly. Share via `core` or domain interfaces.

## 5. Quality Rules

- Zero hardcoded colors, dimensions, corner radii, strings, text sizes, or font styles in layouts or Java code. Use resources and design system tokens.
- Every color is a named resource first, then referenced. No hex outside `colors.xml` and `course_colors.xml`.
- Every screen is built from design system components (`AppButton`, `AppCard`, etc.), never raw `Button`, `MaterialButton`, `CardView`, or `EditText` in feature layouts.
- Use ViewBinding everywhere. No `findViewById`. Null out the binding in `onDestroyView`.
- `DesignSystemGalleryActivity` in `src/debug` shows every `App*` component in every state (enabled, disabled, pressed).
- Handle loading, empty, and error states in every screen via `UiState`.
- Write unit tests for every use case, mapper, and ViewModel (JUnit + Mockito, `AppSchedulers` replaced with a trivial scheduler).
- Add a unit test that every key in `CourseColorPalette` is unique and resolves to an existing `R.color.course_*` resource.
- Add a lint/Checkstyle/CI check that fails the build if any of these appear outside `colors.xml` and `course_colors.xml`: `#[0-9A-Fa-f]{6,8}`, `Color.parseColor`, `Color.rgb(`, `Color.argb(`, `Color.BLACK`, `Color.WHITE`, `android.R.color`, `@color/palette_`. The only exception is `CustomColorParser`.
- Add a lint/Checkstyle/CI check that fails the build if any comment token (`//`, `/*`, `<!--`, or `#` comment lines in config files) appears in any source or config file. The check must ignore occurrences inside string literals (e.g. URLs).
- Add a CI check that fails the build on any `.kt` file under `src/` and on any `import androidx.compose`.
- Lint must pass. No warnings left unresolved.

## 6. Git Workflow: Commit and Push After Every Change

The project is already connected to an existing GitHub repository. Use the existing repository, remote, branch, and authenticated GitHub account. Never initialize a new repository or replace the existing remote.

### 6.1 Mandatory Workflow

* After every single code change, file creation, file modification, file deletion, configuration change, or other implementation change, use the terminal to commit the change to Git and push it to the existing GitHub repository.
* Do not accumulate multiple independent changes before committing. Each individual change must be committed and pushed before starting the next change.
* Use the terminal and the existing Git configuration. Do not use a separate GitHub account, create another identity, or switch authentication methods.
* Before the first change, inspect the repository status, current branch, configured remotes, and Git author identity using terminal commands.
* Use the GitHub account and author identity already configured for this project. Never replace the configured user name or email with the AI agent's name, an AI-generated identity, or a bot identity.
* Never add AI attribution, `Co-Authored-By` trailers, agent names, bot names, or any other attribution to commit messages or commit metadata.
* Never modify Git configuration or authentication credentials to establish a new identity.
* Before committing, inspect the changes and stage only the files related to the current change. Never accidentally include unrelated modifications, secrets, credentials, local environment files, generated build artifacts, or other unintended files.
* Commit and push using the existing branch and remote configuration. Do not force-push, rewrite history, reset user changes, or overwrite remote changes.
* If a commit or push fails, inspect the error, resolve the issue safely, and retry. Never claim a change was pushed unless the terminal confirms the push succeeded.
* If authentication requires user interaction, request the necessary action rather than changing accounts or attempting to bypass authentication.
* After every successful push, verify the resulting repository state before proceeding to the next change.

### 6.2 Commit Message Format

Use plain-text, change-type commit messages. Every commit message must start with one of these conventional change types:

* `feat:` for a new feature
* `fix:` for a bug fix
* `refactor:` for restructuring without changing behavior
* `style:` for formatting or UI styling changes
* `test:` for tests
* `docs:` for documentation
* `build:` for build system or dependency changes
* `ci:` for CI/CD configuration
* `perf:` for performance improvements
* `chore:` for maintenance tasks

Use the format:

`type: concise description of the change`

Examples:

`feat: add course color picker`

`fix: correct course color resolution`

`style: apply rounded card styling`

`test: add course repository tests`

`docs: update project setup instructions`

`build: configure Room dependencies`

Commit messages must be concise, descriptive, and written in plain text. Do not include emojis, decorative symbols, markdown formatting, AI attribution, or unnecessary prefixes. Use lowercase descriptions where practical.

### 6.3 Terminal-First Execution

* All Git operations must be performed through the project's terminal.
* Do not assume a commit or push succeeded without checking the command result.
* Never skip a commit or push because a change appears small, trivial, or limited to one line.
* For changes that require multiple coordinated file edits to remain valid, complete the smallest logically consistent change, verify its affected files, and commit and push that change before proceeding to the next independent change.
* Before finishing a task, verify that all intended changes have been committed and pushed, and that the working tree contains no unintended modifications.
* Report the actual commit and push status accurately. Never fabricate commit hashes, repository URLs, branch names, or successful Git operations.

### 6.4 Safety and Existing Work

* Preserve all pre-existing user modifications.
* Never discard changes you did not create.
* Never commit secrets, API keys, access tokens, passwords, private keys, or sensitive local configuration.
* If unrelated uncommitted changes exist, leave them untouched and exclude them from the current commit.
* If a push would overwrite remote work or require a potentially destructive history rewrite, stop and explain the issue before proceeding.
* Follow this workflow for every task performed in the repository, including implementation, refactoring, testing, documentation, and configuration work.



## 7. Checklist (verify before finishing any task)

- [ ] 100% Java source, no Kotlin files, no Compose
- [ ] No borders, strokes, outlines, or dividers anywhere
- [ ] All elements have rounded corners from the shape/corner tokens
- [ ] All clickables use `App*` components or `TapEffect` (scale + ripple)
- [ ] Shadows are mild, max 6dp
- [ ] Every color is a named resource; zero hex in layouts, styles, drawables, manifest, or Java code
- [ ] Changing one color value updates the whole app (verified by changing one and checking)
- [ ] Layouts, styles, and code use semantic color names only, never `palette_*`
- [ ] Background `background`; buttons `button_background` with `on_button` content; other text `text_on_surface`
- [ ] Course colors stored as `colorKey` or `customColorArgb`, resolved via `CourseColorResolver`; palette edits propagate to existing courses
- [ ] Primary text bold, secondary text normal, only via `TextAppearance.App.*`
- [ ] No top back buttons, no up navigation icon
- [ ] Background keep-alive implemented via `BackgroundKeepAliveManager`
- [ ] Zero comments in any file (no `//`, `/* */`, Javadoc, `<!-- -->`, TODOs, commented-out code)
- [ ] Code is OOP, interface-driven, injected, and follows the folder structure above


