package io.github.mdraihan27.routinemanager.feature.course.presentation.edit;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.github.mdraihan27.routinemanager.core.base.BaseViewModel;
import io.github.mdraihan27.routinemanager.core.threading.AppSchedulers;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.domain.repository.CourseRepository;
import io.github.mdraihan27.routinemanager.feature.course.domain.usecase.AddCourseUseCase;
import io.github.mdraihan27.routinemanager.feature.course.domain.usecase.DeleteCourseUseCase;
import io.github.mdraihan27.routinemanager.feature.course.domain.usecase.GetTeacherSuggestionsUseCase;
import io.github.mdraihan27.routinemanager.feature.course.domain.usecase.UpdateCourseUseCase;

@HiltViewModel
public final class CourseEditViewModel extends BaseViewModel<CourseEditUiState, CourseEditUiEvent> {

    private final CourseRepository repository;
    private final AddCourseUseCase addCourseUseCase;
    private final UpdateCourseUseCase updateCourseUseCase;
    private final DeleteCourseUseCase deleteCourseUseCase;
    private final GetTeacherSuggestionsUseCase getTeacherSuggestionsUseCase;
    private final AppSchedulers schedulers;

    @Inject
    public CourseEditViewModel(@NonNull CourseRepository repository,
                               @NonNull AddCourseUseCase addCourseUseCase,
                               @NonNull UpdateCourseUseCase updateCourseUseCase,
                               @NonNull DeleteCourseUseCase deleteCourseUseCase,
                               @NonNull GetTeacherSuggestionsUseCase getTeacherSuggestionsUseCase,
                               @NonNull AppSchedulers schedulers) {
        super(CourseEditUiState.initial());
        this.repository = repository;
        this.addCourseUseCase = addCourseUseCase;
        this.updateCourseUseCase = updateCourseUseCase;
        this.deleteCourseUseCase = deleteCourseUseCase;
        this.getTeacherSuggestionsUseCase = getTeacherSuggestionsUseCase;
        this.schedulers = schedulers;
        loadTeacherSuggestions();
    }

    private void loadTeacherSuggestions() {
        addDisposable(
                getTeacherSuggestionsUseCase.execute()
                        .subscribeOn(schedulers.io())
                        .observeOn(schedulers.main())
                        .subscribe(
                                suggestions -> {
                                    CourseEditUiState current = getCurrentState();
                                    setState(new CourseEditUiState(
                                            current.getInitialCourse(),
                                            suggestions,
                                            current.isSaving(),
                                            current.isSaved(),
                                            current.isDeleted(),
                                            current.isReferencedInRoutine(),
                                            null
                                    ));
                                },
                                throwable -> {
                                }
                        )
        );
    }

    public void loadCourse(long courseId) {
        if (courseId <= 0) {
            return;
        }
        addDisposable(
                repository.getCourse(courseId)
                        .subscribeOn(schedulers.io())
                        .observeOn(schedulers.main())
                        .subscribe(
                                course -> checkRoutineReference(course),
                                throwable -> {
                                    CourseEditUiState current = getCurrentState();
                                    setState(new CourseEditUiState(
                                            current.getInitialCourse(),
                                            current.getTeacherSuggestions(),
                                            false,
                                            false,
                                            false,
                                            false,
                                            throwable.getMessage()
                                    ));
                                }
                        )
        );
    }

    private void checkRoutineReference(Course course) {
        addDisposable(
                repository.isCourseReferencedInRoutine(course.getId())
                        .subscribeOn(schedulers.io())
                        .observeOn(schedulers.main())
                        .subscribe(
                                referenced -> {
                                    CourseEditUiState current = getCurrentState();
                                    setState(new CourseEditUiState(
                                            course,
                                            current.getTeacherSuggestions(),
                                            false,
                                            false,
                                            false,
                                            referenced,
                                            null
                                    ));
                                },
                                throwable -> {
                                    CourseEditUiState current = getCurrentState();
                                    setState(new CourseEditUiState(
                                            course,
                                            current.getTeacherSuggestions(),
                                            false,
                                            false,
                                            false,
                                            false,
                                            null
                                    ));
                                }
                        )
        );
    }

    public void saveCourse(long id, @NonNull String code, @NonNull String name, @NonNull String teacher, @NonNull io.github.mdraihan27.routinemanager.feature.course.domain.model.CourseColor color) {
        Course course = new Course(id, code, name, teacher, color);
        CourseEditUiState current = getCurrentState();
        setState(new CourseEditUiState(
                current.getInitialCourse(),
                current.getTeacherSuggestions(),
                true,
                false,
                false,
                current.isReferencedInRoutine(),
                null
        ));

        io.reactivex.rxjava3.core.Completable action = id > 0
                ? updateCourseUseCase.execute(course)
                : addCourseUseCase.execute(course);

        addDisposable(
                action.subscribeOn(schedulers.io())
                        .observeOn(schedulers.main())
                        .subscribe(
                                () -> {
                                    CourseEditUiState s = getCurrentState();
                                    setState(new CourseEditUiState(
                                            s.getInitialCourse(),
                                            s.getTeacherSuggestions(),
                                            false,
                                            true,
                                            false,
                                            s.isReferencedInRoutine(),
                                            null
                                    ));
                                },
                                throwable -> {
                                    CourseEditUiState s = getCurrentState();
                                    setState(new CourseEditUiState(
                                            s.getInitialCourse(),
                                            s.getTeacherSuggestions(),
                                            false,
                                            false,
                                            false,
                                            s.isReferencedInRoutine(),
                                            throwable.getMessage()
                                    ));
                                }
                        )
        );
    }

    public void deleteCourse(long courseId) {
        if (courseId <= 0) {
            return;
        }
        addDisposable(
                deleteCourseUseCase.execute(courseId)
                        .subscribeOn(schedulers.io())
                        .observeOn(schedulers.main())
                        .subscribe(
                                () -> {
                                    CourseEditUiState current = getCurrentState();
                                    setState(new CourseEditUiState(
                                            current.getInitialCourse(),
                                            current.getTeacherSuggestions(),
                                            false,
                                            false,
                                            true,
                                            current.isReferencedInRoutine(),
                                            null
                                    ));
                                },
                                throwable -> {
                                    CourseEditUiState current = getCurrentState();
                                    setState(new CourseEditUiState(
                                            current.getInitialCourse(),
                                            current.getTeacherSuggestions(),
                                            false,
                                            false,
                                            false,
                                            current.isReferencedInRoutine(),
                                            throwable.getMessage()
                                    ));
                                }
                        )
        );
    }

    @Override
    public void onEvent(@NonNull CourseEditUiEvent event) {
        if (event instanceof CourseEditUiEvent.LoadCourse) {
            loadCourse(((CourseEditUiEvent.LoadCourse) event).getCourseId());
        } else if (event instanceof CourseEditUiEvent.SaveCourse) {
            CourseEditUiEvent.SaveCourse save = (CourseEditUiEvent.SaveCourse) event;
            saveCourse(save.getId(), save.getCode(), save.getName(), save.getTeacherName(), save.getColor());
        } else if (event instanceof CourseEditUiEvent.DeleteCourse) {
            deleteCourse(((CourseEditUiEvent.DeleteCourse) event).getCourseId());
        }
    }
}
