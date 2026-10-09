package io.github.mdraihan27.routinemanager.feature.course.presentation.list;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.github.mdraihan27.routinemanager.core.base.BaseViewModel;
import io.github.mdraihan27.routinemanager.core.threading.AppSchedulers;
import io.github.mdraihan27.routinemanager.feature.course.domain.usecase.GetCoursesUseCase;

@HiltViewModel
public final class CourseListViewModel extends BaseViewModel<CourseListUiState, CourseListUiEvent> {

    private final GetCoursesUseCase getCoursesUseCase;
    private final AppSchedulers schedulers;

    @Inject
    public CourseListViewModel(@NonNull GetCoursesUseCase getCoursesUseCase,
                               @NonNull AppSchedulers schedulers) {
        super(CourseListUiState.initial());
        this.getCoursesUseCase = getCoursesUseCase;
        this.schedulers = schedulers;
        loadCourses();
    }

    private void loadCourses() {
        addDisposable(
                getCoursesUseCase.execute()
                        .subscribeOn(schedulers.io())
                        .observeOn(schedulers.main())
                        .subscribe(
                                courses -> setState(new CourseListUiState(
                                        false,
                                        courses,
                                        courses.isEmpty(),
                                        null
                                )),
                                throwable -> setState(new CourseListUiState(
                                        false,
                                        getCurrentState().getCourses(),
                                        false,
                                        throwable.getMessage()
                                ))
                        )
        );
    }

    @Override
    public void onEvent(@NonNull CourseListUiEvent event) {
        if (event instanceof CourseListUiEvent.LoadCourses) {
            loadCourses();
        }
    }
}
