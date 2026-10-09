package io.github.mdraihan27.routinemanager;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import io.github.mdraihan27.routinemanager.core.threading.AppSchedulers;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.PaletteColor;
import io.github.mdraihan27.routinemanager.feature.course.domain.repository.CourseRepository;
import io.github.mdraihan27.routinemanager.feature.course.domain.usecase.GetCoursesUseCase;
import io.github.mdraihan27.routinemanager.feature.course.presentation.list.CourseListUiState;
import io.github.mdraihan27.routinemanager.feature.course.presentation.list.CourseListViewModel;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class CourseListViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private final AppSchedulers testSchedulers = new AppSchedulers() {
        @NonNull
        @Override
        public Scheduler io() {
            return Schedulers.trampoline();
        }

        @NonNull
        @Override
        public Scheduler main() {
            return Schedulers.trampoline();
        }
    };

    @Test
    public void testLoadsCoursesSuccessfully() {
        Course course = new Course(1L, "ENG101", "English", "Ms Davis", new PaletteColor("rose"));
        List<Course> list = Collections.singletonList(course);

        CourseRepository fakeRepo = new CourseRepository() {
            @NonNull
            @Override
            public Flowable<List<Course>> getCourses() {
                return Flowable.just(list);
            }

            @NonNull
            @Override
            public Single<Course> getCourse(long id) {
                return Single.just(course);
            }

            @NonNull
            @Override
            public Completable addCourse(@NonNull Course c) {
                return Completable.complete();
            }

            @NonNull
            @Override
            public Completable updateCourse(@NonNull Course c) {
                return Completable.complete();
            }

            @NonNull
            @Override
            public Completable deleteCourse(long id) {
                return Completable.complete();
            }

            @NonNull
            @Override
            public Flowable<List<String>> getTeacherSuggestions() {
                return Flowable.just(Collections.emptyList());
            }

            @NonNull
            @Override
            public Single<Integer> getCourseCount() {
                return Single.just(1);
            }

            @NonNull
            @Override
            public Single<Boolean> isCourseReferencedInRoutine(long courseId) {
                return Single.just(false);
            }
        };

        GetCoursesUseCase getCoursesUseCase = new GetCoursesUseCase(fakeRepo);
        CourseListViewModel viewModel = new CourseListViewModel(getCoursesUseCase, testSchedulers);
        CourseListUiState state = viewModel.getState().getValue();

        assertNotNull(state);
        assertFalse(state.isLoading());
        assertFalse(state.isEmpty());
        assertEquals(1, state.getCourses().size());
        assertEquals(course, state.getCourses().get(0));
    }
}
