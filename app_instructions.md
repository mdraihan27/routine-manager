# AGENT_INSTRUCTIONS.md

You are building an Android routine manager application in Java. Follow every requirement in this document. The project is already connected to an existing GitHub repository. Inspect the existing project before making changes, preserve existing user work, and implement the app incrementally.

Do not merely generate a plan or placeholder screens. Build a working application with persistent data, functional interactions, polished animations, and tests.

## 1. Product Vision

Build a beautiful, responsive, offline-first Android application that helps students manage their courses, weekly class routines, breaks, and daily class progress.

The app must allow students to:

* Create and manage courses.
* Save teacher names for future suggestions.
* Build a weekly routine with configurable weekly holidays and daily breaks.
* Schedule classes using a visual analog clock interface.
* View and navigate their daily schedule from the home screen.
* Mark classes as completed or cancelled.
* Temporarily or permanently reschedule classes.
* Learn features through contextual onboarding guides.
* Adjust animation intensity through settings.

The experience must feel fluid, tactile, modern, and intuitive. Prioritize usability, visual consistency, smooth animations, and reliable schedule calculations.

## 2. Mandatory Technology Stack

Follow the Android architecture and technology requirements already established for this project.

* Language: Java 17 only.
* UI: XML layouts, ViewBinding, Material Components with a Material 3 theme.
* Navigation: Single Activity, Fragments, Navigation Component, XML navigation graph.
* Architecture: Clean Architecture, MVVM, package-by-feature.
* Dependency injection: Hilt with Java annotation processing.
* Local database: Room.
* Preferences: DataStore Preferences through RxJava3-compatible APIs.
* Reactive operations: RxJava3 in domain and data layers.
* ViewModel UI exposure: LiveData.
* Lists: RecyclerView, ListAdapter, DiffUtil.
* Minimum SDK: 26.
* Target SDK: latest stable SDK supported by the project toolchain.
* Testing: JUnit, Mockito, Android instrumentation tests, and UI tests.
* Use Java-compatible Android libraries only.
* No Kotlin source files, Kotlin coroutines, Kotlin Flow, Jetpack Compose, or KSP.
* Use Gradle Kotlin DSL only for build scripts where the existing project requires it.

Reuse existing design-system components and architectural abstractions. Do not create competing implementations of infrastructure that already exists.

## 3. Design System

Follow all design requirements in the existing project instructions.

### 3.1 Visual Identity

Use a warm, minimalist design with a cream background, dark text, dark primary buttons, soft pastel course colors, rounded shapes, and subtle shadows.

Use these semantic colors:

* Background: `background`.
* Primary button background: `button_background`.
* Button text and icons: `on_button`.
* Ordinary text: `text_on_surface`.
* Shadow: `shadow`.

Use the established course palette:

* Pistachio: `#CEE9BD`
* Stone: `#E0D5CF`
* Sage: `#BCC5AD`
* Lavender: `#CEC8ED`
* Sand: `#D5BE99`
* Mauve: `#CEACBD`
* Rose: `#C17596`
* Butter: `#FDF79E`
* Blush: `#EBD1C6`
* Sky: `#7EAAEE`
* Pink: `#EAAAB2`
* Aqua: `#94D3D5`
* Candy: `#FBA7C3`
* Coral: `#FBB5A7`
* Apricot: `#FBDFA7`
* Cornflower: `#B0C8F2`
* Iris: `#B9B0F2`
* Orchid: `#DAB0F2`

All color values must be declared only in the appropriate resource files. Use semantic resource references everywhere else.

### 3.2 Shape and Elevation

* No borders, outlines, strokes, or divider lines.
* Every card, button, dialog, input, image, chip, and bottom sheet must have rounded corners.
* Use the existing corner tokens: 12dp, 16dp, 24dp, and 32dp for small, medium, large, and extra-large shapes respectively.
* Use mild shadows with elevation no greater than 6dp.
* Use spacing, tonal differences, and subtle shadows to separate content.
* Never introduce sharp-cornered components.
* Never use outlined button styles or outlined input boxes.

### 3.3 Interaction Feedback

Every clickable element must provide immediate visible feedback.

Use the existing `TapEffect` implementation for scale-down and spring-back animations with shape-masked ripple feedback.

Use existing `App*` design-system components instead of raw interactive views.

Do not attach click listeners to plain views unless `TapEffect` has been attached first.

### 3.4 Typography

* Titles, course codes, class names, and primary values use bold typography.
* Descriptions, teacher names, timestamps, hints, and metadata use normal typography.
* Use predefined `TextAppearance.App.Primary.*` and `TextAppearance.App.Secondary.*` styles.
* Never style text directly inside layouts or Java code when a resource style exists.

### 3.5 Navigation

Do not display back arrows in top bars. Use the system back gesture or button. Handle unsaved changes and dismissible overlays through the appropriate Android navigation APIs.

## 4. Home Screen

The home screen is the main destination of the application.

It should present the current day's routine clearly, with the next upcoming class receiving the most visual attention.

### 4.1 First-Time Experience

When the user launches the application for the first time and has no courses or routine, show an onboarding experience integrated into the home screen.

Display concise, helpful instructional text explaining how to get started.

Include:

* A curved arrow pointing toward the primary Start button.
* A short explanation of what Start does.
* Guidance explaining that courses must be added before creating a routine.
* Guidance pointing toward the add-course button.
* A clear progression from adding courses to creating a routine.

Use a custom vector or drawable-based curved arrow that scales correctly across screen sizes.

The onboarding must not block the interface unnecessarily. The user must be able to dismiss it, and the application must remember completed onboarding steps.

Do not display the initial onboarding again after completion unless the user explicitly requests the tutorial.

### 4.2 Start Button

Provide a prominent Start button for the routine setup flow.

Its behavior must depend on the current application state:

* If no courses exist, guide the user to add a course first.
* If courses exist but no routine exists, begin routine creation.
* If a routine exists, open or resume the appropriate daily routine experience.

Avoid redundant setup steps for users who have already completed them.

### 4.3 Add Course Button

Display an add-course action with a plus icon.

The button must be visually obvious but must not overwhelm the home screen.

Tapping it opens the course creation form.

### 4.4 Daily Class Card

After a routine has been created, display a prominent rounded rectangular class card near the top of the home screen.

The card must show:

* Course name.
* Course code.
* Teacher name.
* Class start and end time.
* Relevant class status.
* Course color.

The course color must match the color assigned when the course was created.

When a class has been completed or cancelled, render its card in a muted grey appearance while retaining enough contrast to read its information. Do not introduce new hardcoded colors; define any additional semantic tokens in the approved color resources.

The home screen must update automatically when the current time changes, the day changes, a class is completed, a class is cancelled, or a schedule is edited.

### 4.5 Daily Class Navigation

Allow the student to browse classes from the home screen.

* Swipe right on the class card to navigate to the next class.
* Swipe left on the class card to navigate to the previous class.
* Also provide subtle visual cues that indicate more classes are available.
* Preserve the selected class when opening its actions or editing its schedule.
* Prevent a horizontal swipe from accidentally triggering a vertical gesture.
* Handle the first and last classes gracefully.

Define a clear class-ordering rule based on scheduled start time.

When no classes remain, show an appropriate empty or completed-day state rather than leaving a blank card.

The application must distinguish the next upcoming class from the currently active class and from past classes.

## 5. Course Management

Implement course management as a complete, persistent feature.

### 5.1 Course Creation Form

The form must contain:

* Course code.
* Course name.
* Teacher name.
* Course color.

Validate required fields, trim unnecessary whitespace, prevent invalid duplicate course codes according to a clearly defined policy, and provide helpful validation messages.

Use the existing rounded input components.

### 5.2 Course Color Picker

Display the default course colors as small square tiles with rounded corners.

Requirements:

* Show the entire default palette.
* Keep each swatch compact and visually distinct.
* Use `CourseColorPalette` to obtain available colors.
* Use `CourseColorResolver` to resolve the selected color.
* Indicate the selected swatch with a check icon or scale-up effect.
* Never indicate selection using a border.
* Include a Custom option that opens a custom color picker.
* Provide hue, saturation, brightness, and hexadecimal input controls.
* Validate hexadecimal input through `CustomColorParser`.
* Save custom colors as ARGB values.
* Save default palette selections as stable palette keys.

Existing courses must reflect palette changes when they use palette keys.

### 5.3 Teacher Suggestions

Teacher names must be stored locally and suggested when adding or editing courses.

When a student enters a teacher name, the application should:

* Search previously saved teacher names.
* Show matching suggestions.
* Allow selection of an existing teacher.
* Allow creation of a new teacher name.
* Avoid duplicate teacher entries caused by capitalization or extra whitespace.
* Preserve the teacher name associated with each course.

Teacher suggestions must survive application restarts.

Implement teacher storage through the existing persistence architecture. Do not use an in-memory-only list for a feature that must persist.

### 5.4 Course Management After Creation

Allow users to view their courses, edit course details, change course colors, and remove courses.

Before deleting a course referenced by a routine, explain the consequences and require confirmation.

Do not silently corrupt or delete routine data.

## 6. Routine Creation Wizard

Implement routine creation as a guided, multi-step workflow.

Use a clear progression indicator and retain entered information when navigating between steps.

### 6.1 Step One: Weekly Holidays

Ask the user which days of the week are holidays.

Display seven selectable day tiles:

* Saturday
* Sunday
* Monday
* Tuesday
* Wednesday
* Thursday
* Friday

Users can select one or more weekly holidays.

Make the selection state obvious using background tones and tap effects, never borders.

Allow the user to continue after selecting the desired days.

### 6.2 Step Two: Default Daily Break

Ask the user to configure the default daily break.

Collect:

* Break start time.
* Break end time.

Use a visual analog clock interface for time selection rather than a digital-only time picker.

Allow the user to configure a break that crosses noon or midnight only if the application explicitly supports the resulting schedule semantics. Otherwise, validate and explain the limitation.

Provide an option to skip the default break.

### 6.3 Step Three: Weekly Schedule Builder

Display the remaining days in a clear weekly schedule interface.

The user must be able to select a day and add its classes.

Weekly holidays should remain visible but clearly marked as holidays. Provide an intentional way to change them later.

For each day, show scheduled classes ordered by start time.

Each day must support:

* Adding a class.
* Editing a class.
* Removing a class.
* Viewing its schedule.
* Identifying overlaps or conflicts.

Do not force the student to configure every day before saving. Empty days must be supported.

### 6.4 Add Class Dialog

When the user adds a class, open a rounded dialog or bottom sheet.

The interaction sequence must be:

1. Select a course from the saved course list.
2. Select the class start time using an analog clock.
3. Select the class end time using an analog clock.
4. Review the selected course and time range.
5. Confirm the class.

The course list must display each course's code, name, and assigned color.

The analog clock must support hour and minute selection, AM/PM where relevant, and accessibility descriptions.

Do not use a digital-only picker as the primary time-selection interface.

The user must be able to cancel without losing the rest of the routine setup.

### 6.5 Time and Conflict Validation

Before saving a class:

* The end time must be later than the start time under the supported same-day scheduling rules.
* Detect overlapping classes on the same day.
* Warn when a class overlaps the default break.
* Prevent duplicate accidental entries.
* Explain conflicts in plain language.
* Allow intentional exceptions only through an explicit user decision.

Support consecutive classes with no unnecessary gaps.

Use a consistent internal time representation and local time zone.

### 6.6 Save Routine

When the student selects Done:

* Validate the configured routine.
* Persist weekly holidays, break settings, and classes.
* Return to the home screen.
* Show the current day's schedule.
* Calculate the next relevant class.
* Preserve all course colors and teacher names.
* Show a brief confirmation animation.

The routine must survive application restarts and device reboots.

The routine builder must support future editing.

## 7. Class Card Gesture System

Implement a deliberate gesture system for the home screen's primary class card.

Use a custom touch handler or gesture detector that tracks gesture direction, distance, and velocity. Ensure gestures work consistently without interfering with accessibility or ordinary taps.

### 7.1 Swipe Right: Next Class

Navigate to the next class in the selected day's chronological sequence.

Animate the transition with a horizontal movement and subtle scale or fade effect.

Do not alter the schedule or class status.

### 7.2 Swipe Left: Previous Class

Navigate to the previous class.

Use a directionally consistent animation.

If no previous class exists, provide subtle feedback without crashing or unexpectedly changing the selected day.

### 7.3 Swipe Down: Cancel Class

A downward swipe must initiate the cancellation interaction.

The behavior must be safe and reversible:

1. Detect a deliberate downward swipe.
2. Apply a muted grey treatment to the selected class.
3. Record the class as cancelled for the relevant occurrence.
4. Advance to the next relevant class.
5. Provide a brief Undo action.

Cancellation must not delete the original recurring schedule.

The implementation must distinguish a cancelled class occurrence from a completed class occurrence.

### 7.4 Swipe Up: Edit Schedule

An upward swipe must open the schedule-editing interface for the selected class.

Show two explicit choices:

* This occurrence only.
* All future occurrences or the permanent weekly schedule.

Explain the consequences before applying the change.

If the user chooses This occurrence only, update only the selected date's class instance.

If the user chooses the permanent option, update the underlying recurring schedule and recalculate future occurrences.

Past classes must remain unchanged.

If the schedule contains a conflict after editing, display the conflict and request a decision before saving.

Do not silently overwrite another class or create overlapping entries.

### 7.5 Gesture Discoverability

Users must discover the gestures through contextual onboarding.

Show a brief first-use overlay explaining the available directions:

* Left and right for browsing.
* Down for cancellation.
* Up for editing.

Use animated arrows and concise text.

Allow users to dismiss the guide and revisit it through a Help or Settings entry.

Never rely exclusively on gestures. Provide accessible action buttons or a menu for browsing, cancellation, and editing.

## 8. Schedule Occurrences and Persistence

Design the data model to support both recurring weekly classes and date-specific exceptions.

A recurring class must not be represented as a collection of unrelated copies for every date.

Use separate concepts for:

* Course.
* Teacher.
* Weekly routine.
* Weekly class schedule.
* Weekly holiday.
* Daily break configuration.
* Date-specific class occurrence.
* Class occurrence status.
* Schedule exception.
* User preferences.
* Onboarding progress.

A scheduled class must reference its course and recurring schedule.

Class occurrence statuses must distinguish at least:

* Upcoming.
* In progress.
* Completed.
* Cancelled.

Use explicit domain models or typed states rather than scattered boolean flags.

### 8.1 Completion

A class becomes eligible for completion when its scheduled end time is reached.

The application must not mark a class completed merely because its card is grey.

Keep visual presentation separate from domain status.

Where appropriate, support automatic status transitions based on time and explicit user completion actions.

Completed and cancelled occurrences must remain distinguishable.

### 8.2 Editing Recurring Schedules

An occurrence-specific edit must create or update a date-specific exception.

A permanent edit must update the recurring weekly schedule and recalculate future occurrences.

Store sufficient information to preserve historical classes and avoid retroactively changing past occurrences.

### 8.3 Daily Breaks

Break settings must be persisted separately from course data.

The home screen must not present a normal class as the next class when the current time is inside the configured break and another class begins later.

Display the current break or the time until the next class where useful.

### 8.4 Date and Time Handling

Use the device's local time zone.

Handle:

* Application launches during a class.
* Application launches after the last class.
* Midnight and day changes.
* Time zone changes.
* Device time changes.
* Empty days.
* Holidays.
* Cancelled occurrences.
* Completed occurrences.

Use a testable clock abstraction rather than directly relying on system time throughout the domain layer.

## 9. Animation and Motion System

Animation quality is a core product requirement.

Aim for premium, frame-conscious motion comparable to well-designed modern mobile applications.

### 9.1 Performance

* Support 60 Hz, 90 Hz, and 120 Hz displays where available.
* Never hardcode a 120 FPS update loop.
* Allow Android's rendering pipeline to synchronize animations with the display refresh rate.
* Prefer hardware-accelerated property animations and Android-compatible animation APIs.
* Use `SpringAnimation` for tactile interactions where appropriate.
* Keep animations lightweight and avoid unnecessary allocations during frame rendering.
* Never perform database, disk, or network operations on the main thread.
* Avoid long-running animations that delay input or make navigation feel sluggish.
* Profile expensive transitions and fix dropped frames where practical.
* Respect Android system animator settings and reduced-motion accessibility preferences.

120 Hz support means smooth rendering on compatible displays, not a guarantee that every device will sustain 120 frames per second.

### 9.2 Animation Coverage

Provide appropriate motion for:

* Button presses.
* Card selection.
* Course color selection.
* Dialog and bottom-sheet presentation.
* Screen transitions.
* Routine wizard progression.
* Adding or removing classes.
* Swiping between classes.
* Completing or cancelling a class.
* Opening and closing the course picker.
* Displaying teacher suggestions.
* Showing onboarding overlays.
* Updating the next-class card.
* Empty-state transitions.
* Undo actions.

Animations must reinforce user actions instead of being decorative distractions.

### 9.3 Motion Design

Use a consistent motion system with reusable duration, spring, and transition definitions.

Prefer:

* Spring-based scale interactions.
* Short fades and translations.
* Directional transitions for horizontal navigation.
* Subtle scale and fade transitions for selection.
* Smooth expansion and collapse for inline content.
* Controlled overshoot only for appropriate tactile feedback.

Avoid excessive bouncing, long transitions, unnecessary parallax, and motion that delays the user's next action.

Do not animate every text element independently without a usability reason.

### 9.4 Animation Intensity Setting

Add an Animation Intensity setting with three options:

* Reduced.
* Balanced.
* Expressive.

Balanced must be the default.

Reduced mode must minimize nonessential movement and disable exaggerated spring overshoot.

Balanced mode must provide polished, restrained motion.

Expressive mode may use stronger spring responses and slightly more pronounced transitions without sacrificing usability.

The selected preference must persist across restarts.

All components must obtain their animation configuration from a shared motion-preference abstraction. Do not implement unrelated intensity settings independently in each Fragment.

Changes to the preference must update applicable animations without requiring an application restart.

The setting must not override Android's system-level reduced-motion or animation-disabled preferences.

## 10. First-Use Feature Guides

Implement a reusable contextual onboarding framework.

Every major feature must have a first-use guide, including:

* Adding a course.
* Choosing a course color.
* Using teacher suggestions.
* Creating a routine.
* Selecting weekly holidays.
* Configuring breaks.
* Adding classes.
* Navigating the daily schedule.
* Cancelling a class.
* Editing a class schedule.
* Using the color picker.
* Adjusting animation intensity.

Do not display every guide consecutively on the first launch.

Display a guide when the user first enters the relevant feature or performs the relevant action.

Each guide should:

* Use a translucent overlay where appropriate.
* Highlight the relevant control.
* Display concise instructional text.
* Use a curved arrow or directional cue when useful.
* Animate the appearance and dismissal.
* Provide a clear dismissal action.
* Support the system back gesture.
* Persist its completion state.
* Be accessible through a help entry where appropriate.

Do not obscure critical actions or leave the user trapped inside an overlay.

Onboarding progress must be stored persistently using the established preferences architecture.

## 11. Settings

Create a dedicated settings screen with rounded, consistent components.

Include:

* Animation intensity.
* Replay onboarding guides.
* Manage courses.
* Edit weekly routine.
* Configure weekly holidays.
* Configure default breaks.
* Reset onboarding progress.
* App information.

Any destructive reset must require confirmation.

Resetting onboarding must not delete courses, class schedules, or user preferences unrelated to onboarding.

If an existing background keep-alive implementation is present in the project, preserve its abstraction and implementation. Do not add unnecessary services or permissions for routine management.

## 12. Accessibility and Usability

* All controls must have meaningful accessibility descriptions.
* Gesture-only operations must have equivalent accessible actions.
* Support large font settings without clipping critical content.
* Maintain readable contrast on every course color.
* Do not rely on color alone to convey class status.
* Provide haptic feedback only where supported and appropriate.
* Respect reduced-motion preferences.
* Make tap targets sufficiently large.
* Support Android system back behavior consistently.
* Preserve user input when a dialog is dismissed accidentally or a configuration step is revisited.
* Display useful empty, loading, success, and error states.
* Do not show fake loading indicators for operations that complete immediately.

## 13. Architecture and Project Structure

Follow the existing package-by-feature Clean Architecture structure.

Organize the implementation into:

* `core/designsystem`
* `core/base`
* `core/di`
* `core/navigation`
* `core/threading`
* `core/database`
* `core/datastore`
* `core/result`
* `core/util`
* `core/background`
* `feature/course`
* `feature/routine`
* `feature/schedule`
* `feature/home`
* `feature/onboarding`
* `feature/settings`

Each feature must separate:

* `data`
* `domain`
* `presentation`

Use interfaces for repositories, use cases, data sources, managers, providers, and resolvers.

Use constructor injection through Hilt.

Each Fragment must have exactly one ViewModel.

Each ViewModel must expose one immutable `UiState` object and receive `UiEvent` objects through a single `onEvent(UiEvent)` method.

ViewModels must use `CompositeDisposable` and clear it in `onCleared`.

Fragments must not subscribe directly to RxJava streams.

Use `AppSchedulers` for scheduling work.

Keep domain logic independent of Android UI classes.

Use Room entities for persistence, mappers for conversion, and repositories for data access.

Never introduce god classes, static mutable state, hand-written singletons, or unnecessary abstractions.

Use `@NonNull` and `@Nullable` consistently on public parameters and return values.

## 14. Code Quality Rules

* No comments in source or configuration files.
* This includes Java, XML, Gradle, manifests, ProGuard files, YAML, and test files.
* No TODO or FIXME markers.
* No commented-out code.
* No license headers or section-divider comments.
* Markdown documentation is permitted.
* Every file must have a clear responsibility.
* Use descriptive names.
* Avoid magic numbers.
* Use named constants and resource tokens.
* Never hardcode colors, dimensions, strings, corner radii, or typography.
* Use ViewBinding everywhere.
* Never use `findViewById`.
* Clear Fragment binding references in `onDestroyView`.
* Do not use raw `Button`, `MaterialButton`, `CardView`, or `EditText` in feature layouts when the corresponding design-system component exists.
* Never introduce borders or divider lines.
* Keep lint warnings resolved.
* Add tests for use cases, mappers, ViewModels, schedule calculations, gesture interpretation, and recurring schedule exceptions.

## 15. Git Workflow

The project is already connected to an existing GitHub repository.

After every individual change, use the terminal to commit and push it to the existing repository before starting the next independent change.

* Inspect the current branch, remotes, working tree, and configured Git author before modifying the project.
* Preserve the existing repository and authentication.
* Use the already configured GitHub account and author identity.
* Never use the AI agent's name or a bot identity.
* Never add AI attribution or `Co-Authored-By` trailers.
* Stage only files related to the current change.
* Preserve unrelated user modifications.
* Never commit secrets or credentials.
* Do not force-push, rewrite history, or discard existing changes.
* Verify successful commits and pushes through terminal output.
* If a push fails, resolve the problem safely before continuing.
* Do not claim that a change was pushed unless the operation succeeded.

Use plain-text conventional commit messages in this format:

`type: concise description of the change`

Allowed types:

* `feat:` new features.
* `fix:` bug fixes.
* `refactor:` internal restructuring.
* `style:` styling and formatting.
* `test:` tests.
* `docs:` documentation.
* `build:` dependencies and build configuration.
* `ci:` CI/CD changes.
* `perf:` performance improvements.
* `chore:` maintenance.

Examples:

`feat: add course creation form`

`feat: implement weekly routine builder`

`feat: add class navigation gestures`

`fix: preserve historical class occurrences`

`style: improve schedule card animations`

`test: cover recurring schedule exceptions`

`docs: document routine creation flow`

Do not use emojis, decorative symbols, AI attribution, or unnecessary prefixes.

When multiple file edits are required for one logically consistent feature, complete the smallest working change, verify the affected files, and commit and push before proceeding to the next independent change.

Before finishing a task, verify the repository state and report the actual commit and push status.

## 16. Implementation Order

Build the app incrementally in the following order.

### Phase 1: Inspect and Prepare

* Inspect the existing project, architecture, resources, dependencies, and Git configuration.
* Preserve existing code and user modifications.
* Identify reusable components.
* Confirm that the project builds.
* Fix foundational build problems before adding features.

### Phase 2: Design System

* Complete the theme, colors, dimensions, shapes, typography, and reusable components.
* Implement or verify `TapEffect`.
* Implement shared motion configuration and animation intensity persistence.
* Build or update the debug design-system gallery.

### Phase 3: Data and Domain

* Define course and teacher models.
* Implement Room entities, DAOs, mappers, and repositories.
* Implement recurring schedules, holidays, breaks, class occurrences, and date-specific exceptions.
* Implement course color resolution.
* Add schedule calculation and time abstraction.
* Write domain and repository tests.

### Phase 4: Course Management

* Implement the course list and course creation form.
* Implement default and custom color selection.
* Implement teacher suggestions.
* Implement course editing and deletion behavior.
* Add contextual guides.
* Verify persistence and tests.

### Phase 5: Routine Builder

* Implement weekly holiday selection.
* Implement break configuration with an analog clock.
* Implement the weekly schedule editor.
* Implement the add-class flow.
* Implement time validation and conflict detection.
* Implement saving and restoring routine setup.
* Add contextual guides and tests.

### Phase 6: Home Screen

* Implement the first-time home experience.
* Implement the Start button.
* Implement the add-course button.
* Implement the daily class card.
* Calculate the next upcoming class.
* Implement previous and next class navigation.
* Implement empty-day, holiday, break, and completed-day states.

### Phase 7: Gesture Actions

* Implement horizontal class navigation.
* Implement downward cancellation with Undo.
* Implement upward schedule editing.
* Implement occurrence-only and permanent schedule changes.
* Implement accessible alternatives for all gestures.
* Test gesture conflicts and boundary cases.

### Phase 8: Onboarding and Settings

* Implement reusable contextual guides.
* Persist guide completion.
* Implement replay controls.
* Implement animation intensity settings.
* Verify reduced-motion behavior.

### Phase 9: Animation and Performance

* Refine transitions and touch feedback.
* Profile performance on available devices.
* Test 60 Hz and high-refresh-rate behavior where hardware is available.
* Remove unnecessary main-thread work.
* Verify that gestures remain responsive during transitions.

### Phase 10: Final Verification

* Run unit tests.
* Run instrumented tests where the environment supports them.
* Run lint and build checks.
* Test application restart and persistence.
* Test recurring schedules and exceptions.
* Test cancellation, completion, and Undo.
* Test schedule changes across day boundaries.
* Verify the absence of hardcoded colors, forbidden borders, comments, Kotlin source files, and Compose dependencies.
* Commit and push every individual fix.
* Verify that all intended changes are pushed.

## 17. Definition of Done

The application is not complete until the following conditions are satisfied:

* Users can create, edit, and delete courses.
* Teacher suggestions persist across restarts.
* Course colors support the default palette and custom colors.
* Users can create a weekly routine with holidays and breaks.
* Class start and end times are selected using a visual analog clock.
* Classes are ordered correctly.
* The home screen identifies the next relevant class.
* Previous and next class navigation works.
* Cancellation greys out the relevant occurrence without deleting the recurring schedule.
* Undo restores a cancelled occurrence.
* Occurrence-only edits do not modify the permanent weekly routine.
* Permanent edits preserve historical occurrences.
* Overlapping classes are detected and handled safely.
* First-use guides are displayed at the appropriate times and can be replayed.
* Animation intensity settings persist and affect the application consistently.
* Animations respect system accessibility settings and use the display's available refresh rate appropriately.
* All data persists across application restarts.
* All interactive elements provide tap feedback.
* No borders, outlines, or divider lines are introduced.
* All colors and dimensions use the established resources.
* The architecture and code-quality requirements are satisfied.
* Tests and build checks pass, or any environment limitations are explicitly documented.
* Every completed change has been committed and successfully pushed to the existing GitHub repository using the existing configured identity.

Implement a production-quality application, not a static mockup. Prioritize correct scheduling behavior, data integrity, intuitive interactions, and polished motion over unnecessary visual complexity.
