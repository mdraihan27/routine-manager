package io.github.mdraihan27.routinemanager;

import org.junit.Test;
import org.mockito.Mockito;

import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.PaletteColor;
import io.github.mdraihan27.routinemanager.feature.course.domain.repository.CourseRepository;
import io.github.mdraihan27.routinemanager.feature.course.domain.usecase.AddCourseUseCase;
import io.reactivex.rxjava3.core.Completable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AddCourseUseCaseTest {

    @Test
    public void testExecuteDelegatesToRepository() {
        CourseRepository repository = Mockito.mock(CourseRepository.class);
        AddCourseUseCase useCase = new AddCourseUseCase(repository);

        Course course = new Course(0L, "CSE102", "Data Structures", "Prof Turing", new PaletteColor("pistachio"));
        when(repository.addCourse(course)).thenReturn(Completable.complete());

        useCase.execute(course).test().assertComplete();
        verify(repository).addCourse(course);
    }
}
