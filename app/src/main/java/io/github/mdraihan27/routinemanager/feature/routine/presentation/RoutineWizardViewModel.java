package io.github.mdraihan27.routinemanager.feature.routine.presentation;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.github.mdraihan27.routinemanager.core.base.BaseViewModel;
import io.github.mdraihan27.routinemanager.core.threading.AppSchedulers;
import io.github.mdraihan27.routinemanager.feature.course.domain.usecase.GetCoursesUseCase;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.RoutineConfig;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClass;
import io.github.mdraihan27.routinemanager.feature.routine.domain.usecase.AddWeeklyClassUseCase;
import io.github.mdraihan27.routinemanager.feature.routine.domain.usecase.DeleteWeeklyClassUseCase;
import io.github.mdraihan27.routinemanager.feature.routine.domain.usecase.GetRoutineConfigUseCase;
import io.github.mdraihan27.routinemanager.feature.routine.domain.usecase.GetWeeklyClassesForDayUseCase;
import io.github.mdraihan27.routinemanager.feature.routine.domain.usecase.SaveRoutineConfigUseCase;
import io.github.mdraihan27.routinemanager.feature.routine.domain.usecase.ValidateClassTimeUseCase;

@HiltViewModel
public final class RoutineWizardViewModel extends BaseViewModel<RoutineWizardUiState, RoutineWizardUiEvent> {

    private final GetRoutineConfigUseCase getRoutineConfigUseCase;
    private final SaveRoutineConfigUseCase saveRoutineConfigUseCase;
    private final GetWeeklyClassesForDayUseCase getWeeklyClassesForDayUseCase;
    private final AddWeeklyClassUseCase addWeeklyClassUseCase;
    private final DeleteWeeklyClassUseCase deleteWeeklyClassUseCase;
    private final GetCoursesUseCase getCoursesUseCase;
    private final ValidateClassTimeUseCase validateClassTimeUseCase;
    private final AppSchedulers schedulers;
    
    private final io.reactivex.rxjava3.disposables.SerialDisposable classesDisposable = new io.reactivex.rxjava3.disposables.SerialDisposable();

    @Inject
    public RoutineWizardViewModel(@NonNull GetRoutineConfigUseCase getRoutineConfigUseCase,
                                  @NonNull SaveRoutineConfigUseCase saveRoutineConfigUseCase,
                                  @NonNull GetWeeklyClassesForDayUseCase getWeeklyClassesForDayUseCase,
                                  @NonNull AddWeeklyClassUseCase addWeeklyClassUseCase,
                                  @NonNull DeleteWeeklyClassUseCase deleteWeeklyClassUseCase,
                                  @NonNull GetCoursesUseCase getCoursesUseCase,
                                  @NonNull ValidateClassTimeUseCase validateClassTimeUseCase,
                                  @NonNull AppSchedulers schedulers) {
        super(RoutineWizardUiState.initial());
        this.getRoutineConfigUseCase = getRoutineConfigUseCase;
        this.saveRoutineConfigUseCase = saveRoutineConfigUseCase;
        this.getWeeklyClassesForDayUseCase = getWeeklyClassesForDayUseCase;
        this.addWeeklyClassUseCase = addWeeklyClassUseCase;
        this.deleteWeeklyClassUseCase = deleteWeeklyClassUseCase;
        this.getCoursesUseCase = getCoursesUseCase;
        this.validateClassTimeUseCase = validateClassTimeUseCase;
        this.schedulers = schedulers;
        
        addDisposable(classesDisposable);
        
        loadInitialData();
    }

    private void loadInitialData() {
        addDisposable(
                getCoursesUseCase.execute()
                        .subscribeOn(schedulers.io())
                        .observeOn(schedulers.main())
                        .subscribe(
                                courses -> {
                                    RoutineWizardUiState cur = getCurrentState();
                                    setState(new RoutineWizardUiState(
                                            cur.getCurrentStep(),
                                            cur.getHolidays(),
                                            cur.hasDefaultBreak(),
                                            cur.getBreakStartHour(),
                                            cur.getBreakStartMinute(),
                                            cur.getBreakEndHour(),
                                            cur.getBreakEndMinute(),
                                            cur.getSelectedDay(),
                                            cur.getDayClasses(),
                                            courses,
                                            false,
                                            null
                                    ));
                                },
                                throwable -> {
                                }
                        )
        );

        addDisposable(
                getRoutineConfigUseCase.execute()
                        .firstOrError()
                        .subscribeOn(schedulers.io())
                        .observeOn(schedulers.main())
                        .subscribe(
                                config -> {
                                    RoutineWizardUiState cur = getCurrentState();
                                    Set<DayOfWeek> hols = config.getWeeklyHolidays().isEmpty()
                                            ? cur.getHolidays()
                                            : config.getWeeklyHolidays();
                                    setState(new RoutineWizardUiState(
                                            cur.getCurrentStep(),
                                            hols,
                                            config.hasDefaultBreak(),
                                            config.getBreakStartHour(),
                                            config.getBreakStartMinute(),
                                            config.getBreakEndHour(),
                                            config.getBreakEndMinute(),
                                            cur.getSelectedDay(),
                                            cur.getDayClasses(),
                                            cur.getAllCourses(),
                                            false,
                                            null
                                    ));
                                    loadClassesForDay(cur.getSelectedDay());
                                },
                                throwable -> loadClassesForDay(getCurrentState().getSelectedDay())
                        )
        );
    }

    private void loadClassesForDay(DayOfWeek day) {
        classesDisposable.set(
                getWeeklyClassesForDayUseCase.execute(day)
                        .subscribeOn(schedulers.io())
                        .observeOn(schedulers.main())
                        .subscribe(
                                classes -> {
                                    RoutineWizardUiState cur = getCurrentState();
                                    setState(new RoutineWizardUiState(
                                            cur.getCurrentStep(),
                                            cur.getHolidays(),
                                            cur.hasDefaultBreak(),
                                            cur.getBreakStartHour(),
                                            cur.getBreakStartMinute(),
                                            cur.getBreakEndHour(),
                                            cur.getBreakEndMinute(),
                                            day,
                                            classes,
                                            cur.getAllCourses(),
                                            false,
                                            null
                                    ));
                                },
                                throwable -> {
                                }
                        )
        );
    }

    public ValidateClassTimeUseCase.Result validateClass(int startHour, int startMinute, int endHour, int endMinute, long excludeId) {
        RoutineWizardUiState cur = getCurrentState();
        RoutineConfig config = new RoutineConfig(
                cur.getHolidays(),
                cur.hasDefaultBreak(),
                cur.getBreakStartHour(),
                cur.getBreakStartMinute(),
                cur.getBreakEndHour(),
                cur.getBreakEndMinute(),
                true
        );
        return validateClassTimeUseCase.execute(
                startHour,
                startMinute,
                endHour,
                endMinute,
                excludeId,
                cur.getDayClasses(),
                config
        );
    }

    @Override
    public void onEvent(@NonNull RoutineWizardUiEvent event) {
        if (event instanceof RoutineWizardUiEvent.ToggleHoliday) {
            DayOfWeek day = ((RoutineWizardUiEvent.ToggleHoliday) event).getDay();
            RoutineWizardUiState cur = getCurrentState();
            Set<DayOfWeek> updated = new HashSet<>(cur.getHolidays());
            if (updated.contains(day)) {
                updated.remove(day);
            } else {
                updated.add(day);
            }
            setState(new RoutineWizardUiState(
                    cur.getCurrentStep(),
                    updated,
                    cur.hasDefaultBreak(),
                    cur.getBreakStartHour(),
                    cur.getBreakStartMinute(),
                    cur.getBreakEndHour(),
                    cur.getBreakEndMinute(),
                    cur.getSelectedDay(),
                    cur.getDayClasses(),
                    cur.getAllCourses(),
                    false,
                    null
            ));
        } else if (event instanceof RoutineWizardUiEvent.SetStep) {
            int step = ((RoutineWizardUiEvent.SetStep) event).getStep();
            RoutineWizardUiState cur = getCurrentState();
            setState(new RoutineWizardUiState(
                    step,
                    cur.getHolidays(),
                    cur.hasDefaultBreak(),
                    cur.getBreakStartHour(),
                    cur.getBreakStartMinute(),
                    cur.getBreakEndHour(),
                    cur.getBreakEndMinute(),
                    cur.getSelectedDay(),
                    cur.getDayClasses(),
                    cur.getAllCourses(),
                    false,
                    null
            ));
        } else if (event instanceof RoutineWizardUiEvent.SetBreakTime) {
            RoutineWizardUiEvent.SetBreakTime bt = (RoutineWizardUiEvent.SetBreakTime) event;
            RoutineWizardUiState cur = getCurrentState();
            setState(new RoutineWizardUiState(
                    cur.getCurrentStep(),
                    cur.getHolidays(),
                    bt.hasBreak(),
                    bt.getStartHour(),
                    bt.getStartMinute(),
                    bt.getEndHour(),
                    bt.getEndMinute(),
                    cur.getSelectedDay(),
                    cur.getDayClasses(),
                    cur.getAllCourses(),
                    false,
                    null
            ));
        } else if (event instanceof RoutineWizardUiEvent.SelectDay) {
            DayOfWeek day = ((RoutineWizardUiEvent.SelectDay) event).getDay();
            loadClassesForDay(day);
        } else if (event instanceof RoutineWizardUiEvent.AddClass) {
            WeeklyClass wc = ((RoutineWizardUiEvent.AddClass) event).getWeeklyClass();
            addDisposable(
                    addWeeklyClassUseCase.execute(wc)
                            .subscribeOn(schedulers.io())
                            .observeOn(schedulers.main())
                            .subscribe(
                                    () -> loadClassesForDay(getCurrentState().getSelectedDay()),
                                    throwable -> {
                                    }
                            )
        );
        } else if (event instanceof RoutineWizardUiEvent.DeleteClass) {
            long classId = ((RoutineWizardUiEvent.DeleteClass) event).getClassId();
            addDisposable(
                    deleteWeeklyClassUseCase.execute(classId)
                            .subscribeOn(schedulers.io())
                            .observeOn(schedulers.main())
                            .subscribe(
                                    () -> loadClassesForDay(getCurrentState().getSelectedDay()),
                                    throwable -> {
                                    }
                            )
            );
        } else if (event instanceof RoutineWizardUiEvent.FinishWizard) {
            RoutineWizardUiState cur = getCurrentState();
            RoutineConfig config = new RoutineConfig(
                    cur.getHolidays(),
                    cur.hasDefaultBreak(),
                    cur.getBreakStartHour(),
                    cur.getBreakStartMinute(),
                    cur.getBreakEndHour(),
                    cur.getBreakEndMinute(),
                    true
            );
            addDisposable(
                    saveRoutineConfigUseCase.execute(config)
                            .subscribeOn(schedulers.io())
                            .observeOn(schedulers.main())
                            .subscribe(
                                    () -> {
                                        RoutineWizardUiState s = getCurrentState();
                                        setState(new RoutineWizardUiState(
                                                s.getCurrentStep(),
                                                s.getHolidays(),
                                                s.hasDefaultBreak(),
                                                s.getBreakStartHour(),
                                                s.getBreakStartMinute(),
                                                s.getBreakEndHour(),
                                                s.getBreakEndMinute(),
                                                s.getSelectedDay(),
                                                s.getDayClasses(),
                                                s.getAllCourses(),
                                                true,
                                                null
                                        ));
                                    },
                                    throwable -> {
                                    }
                            )
            );
        }
    }
}
