package io.github.mdraihan27.routinemanager.feature.home.presentation;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.github.mdraihan27.routinemanager.core.base.BaseViewModel;
import io.github.mdraihan27.routinemanager.core.datastore.PreferencesDataSource;
import io.github.mdraihan27.routinemanager.core.threading.AppSchedulers;
import io.github.mdraihan27.routinemanager.core.time.AppClock;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.domain.usecase.GetCoursesUseCase;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.RoutineConfig;
import io.github.mdraihan27.routinemanager.feature.routine.domain.usecase.GetRoutineConfigUseCase;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailyClassItem;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailySchedule;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase.CancelClassOccurrenceUseCase;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase.GetTodayScheduleUseCase;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase.RescheduleClassUseCase;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase.RestoreClassOccurrenceUseCase;
import io.reactivex.rxjava3.core.Flowable;

@HiltViewModel
public class HomeViewModel extends BaseViewModel<HomeUiState, HomeUiEvent> {

    private static final String GUIDE_GESTURE_CARD = "guide_gesture_card";

    private final GetTodayScheduleUseCase getTodayScheduleUseCase;
    private final CancelClassOccurrenceUseCase cancelClassOccurrenceUseCase;
    private final RestoreClassOccurrenceUseCase restoreClassOccurrenceUseCase;
    private final RescheduleClassUseCase rescheduleClassUseCase;
    private final GetCoursesUseCase getCoursesUseCase;
    private final GetRoutineConfigUseCase getRoutineConfigUseCase;
    private final PreferencesDataSource preferencesDataSource;
    private final AppClock appClock;
    private final AppSchedulers appSchedulers;

    @Inject
    public HomeViewModel(@NonNull GetTodayScheduleUseCase getTodayScheduleUseCase,
                         @NonNull CancelClassOccurrenceUseCase cancelClassOccurrenceUseCase,
                         @NonNull RestoreClassOccurrenceUseCase restoreClassOccurrenceUseCase,
                         @NonNull RescheduleClassUseCase rescheduleClassUseCase,
                         @NonNull GetCoursesUseCase getCoursesUseCase,
                         @NonNull GetRoutineConfigUseCase getRoutineConfigUseCase,
                         @NonNull PreferencesDataSource preferencesDataSource,
                         @NonNull AppClock appClock,
                         @NonNull AppSchedulers appSchedulers) {
        super(HomeUiState.initial());
        this.getTodayScheduleUseCase = getTodayScheduleUseCase;
        this.cancelClassOccurrenceUseCase = cancelClassOccurrenceUseCase;
        this.restoreClassOccurrenceUseCase = restoreClassOccurrenceUseCase;
        this.rescheduleClassUseCase = rescheduleClassUseCase;
        this.getCoursesUseCase = getCoursesUseCase;
        this.getRoutineConfigUseCase = getRoutineConfigUseCase;
        this.preferencesDataSource = preferencesDataSource;
        this.appClock = appClock;
        this.appSchedulers = appSchedulers;

        loadScheduleData();
    }

    @Override
    public void onEvent(@NonNull HomeUiEvent event) {
        if (event instanceof HomeUiEvent.LoadSchedule) {
            loadScheduleData();
        } else if (event instanceof HomeUiEvent.NextClass) {
            navigateNext();
        } else if (event instanceof HomeUiEvent.PreviousClass) {
            navigatePrevious();
        } else if (event instanceof HomeUiEvent.CancelCurrentClass) {
            cancelCurrent();
        } else if (event instanceof HomeUiEvent.UndoCancel) {
            undoCancel();
        } else if (event instanceof HomeUiEvent.DismissOnboarding) {
            dismissOnboarding();
        } else if (event instanceof HomeUiEvent.DismissGestureGuide) {
            dismissGestureGuide();
        } else if (event instanceof HomeUiEvent.RescheduleClass) {
            HomeUiEvent.RescheduleClass rescheduleEvent = (HomeUiEvent.RescheduleClass) event;
            reschedule(rescheduleEvent);
        }
    }

    private void loadScheduleData() {
        addDisposable(
                Flowable.combineLatest(
                        getCoursesUseCase.execute(),
                        getRoutineConfigUseCase.execute(),
                        getTodayScheduleUseCase.execute(),
                        (List<Course> courses, RoutineConfig routineConfig, DailySchedule dailySchedule) -> {
                            boolean hasCourses = !courses.isEmpty();
                            boolean hasRoutine = routineConfig.isConfigured();
                            boolean showOnboarding = (!preferencesDataSource.hasCompletedInitialOnboarding() || !hasCourses || !hasRoutine);
                            boolean showGestureGuide = (hasRoutine && !dailySchedule.getClasses().isEmpty() && !preferencesDataSource.isGuideCompleted(GUIDE_GESTURE_CARD));

                            int index = dailySchedule.getCurrentClassIndex();
                            HomeUiState current = getState().getValue();
                            if (current != null && current.getClasses().size() == dailySchedule.getClasses().size() && current.getCurrentIndex() >= 0 && current.getCurrentIndex() < dailySchedule.getClasses().size()) {
                                index = current.getCurrentIndex();
                            }

                            return new HomeUiState(
                                    false,
                                    showOnboarding,
                                    showGestureGuide,
                                    hasCourses,
                                    hasRoutine,
                                    dailySchedule.isHoliday(),
                                    dailySchedule.isBreakActive(),
                                    dailySchedule.getClasses(),
                                    index,
                                    current != null && current.isCanUndoCancel(),
                                    current != null ? current.getLastCancelledClassId() : -1L,
                                    null
                            );
                        }
                )
                .subscribeOn(appSchedulers.io())
                .observeOn(appSchedulers.main())
                .subscribe(
                        this::setState,
                        throwable -> {
                            HomeUiState current = getState().getValue();
                            if (current != null) {
                                setState(new HomeUiState(
                                        false,
                                        current.isShowOnboarding(),
                                        current.isShowGestureGuide(),
                                        current.hasCourses(),
                                        current.hasRoutine(),
                                        current.isHoliday(),
                                        current.isBreakActive(),
                                        current.getClasses(),
                                        current.getCurrentIndex(),
                                        false,
                                        -1L,
                                        throwable.getMessage()
                                ));
                            }
                        }
                )
        );
    }

    private void navigateNext() {
        HomeUiState state = getState().getValue();
        if (state == null) return;
        List<DailyClassItem> classes = state.getClasses();
        if (state.getCurrentIndex() < classes.size() - 1) {
            setState(state.copyWithIndex(state.getCurrentIndex() + 1));
        }
    }

    private void navigatePrevious() {
        HomeUiState state = getState().getValue();
        if (state == null) return;
        if (state.getCurrentIndex() > 0) {
            setState(state.copyWithIndex(state.getCurrentIndex() - 1));
        }
    }

    private void cancelCurrent() {
        HomeUiState state = getState().getValue();
        if (state == null) return;
        DailyClassItem current = state.getCurrentClass();
        if (current == null) return;

        long classId = current.getWeeklyClassId();
        LocalDate today = appClock.currentDate();

        addDisposable(
                cancelClassOccurrenceUseCase.execute(classId, today)
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(
                                () -> {
                                    HomeUiState latest = getState().getValue();
                                    if (latest != null) {
                                        setState(latest.copyWithUndo(true, classId));
                                    }
                                },
                                throwable -> {}
                        )
        );
    }

    private void undoCancel() {
        HomeUiState state = getState().getValue();
        if (state == null || !state.isCanUndoCancel() || state.getLastCancelledClassId() <= 0) return;

        long classId = state.getLastCancelledClassId();
        LocalDate today = appClock.currentDate();

        addDisposable(
                restoreClassOccurrenceUseCase.execute(classId, today)
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(
                                () -> {
                                    HomeUiState latest = getState().getValue();
                                    if (latest != null) {
                                        setState(latest.copyWithUndo(false, -1L));
                                    }
                                },
                                throwable -> {}
                        )
        );
    }

    private void reschedule(@NonNull HomeUiEvent.RescheduleClass event) {
        LocalDate today = appClock.currentDate();
        addDisposable(
                rescheduleClassUseCase.execute(
                        event.getWeeklyClassId(),
                        today,
                        !event.isPermanent(),
                        event.getStartHour(),
                        event.getStartMinute(),
                        event.getEndHour(),
                        event.getEndMinute()
                )
                .subscribeOn(appSchedulers.io())
                .observeOn(appSchedulers.main())
                .subscribe(
                        () -> {},
                        throwable -> {}
                )
        );
    }

    private void dismissOnboarding() {
        addDisposable(
                preferencesDataSource.setCompletedInitialOnboarding(true)
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(
                                () -> {
                                    HomeUiState state = getState().getValue();
                                    if (state != null) {
                                        setState(state.copyWithGuides(false, state.isShowGestureGuide()));
                                    }
                                },
                                throwable -> {}
                        )
        );
    }

    private void dismissGestureGuide() {
        addDisposable(
                preferencesDataSource.setGuideCompleted(GUIDE_GESTURE_CARD, true)
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(
                                () -> {
                                    HomeUiState state = getState().getValue();
                                    if (state != null) {
                                        setState(state.copyWithGuides(state.isShowOnboarding(), false));
                                    }
                                },
                                throwable -> {}
                        )
        );
    }
}
