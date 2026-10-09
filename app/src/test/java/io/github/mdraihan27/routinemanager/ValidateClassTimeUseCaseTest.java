package io.github.mdraihan27.routinemanager;

import org.junit.Test;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;

import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.PaletteColor;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.RoutineConfig;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClass;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse;
import io.github.mdraihan27.routinemanager.feature.routine.domain.usecase.ValidateClassTimeUseCase;

import static org.junit.Assert.assertEquals;

public class ValidateClassTimeUseCaseTest {

    private final ValidateClassTimeUseCase useCase = new ValidateClassTimeUseCase();
    private final Course testCourse = new Course(1L, "CS101", "CS", "Dr Turing", new PaletteColor("sky"));

    @Test
    public void testValidConsecutiveClasses() {
        WeeklyClass existing = new WeeklyClass(1L, 1L, DayOfWeek.SATURDAY, 9, 0, 10, 0);
        List<WeeklyClassWithCourse> existingList = Collections.singletonList(
                new WeeklyClassWithCourse(existing, testCourse)
        );

        ValidateClassTimeUseCase.Result result = useCase.execute(
                10, 0, 11, 0, -1L, existingList, null
        );
        assertEquals(ValidateClassTimeUseCase.Result.VALID, result);
    }

    @Test
    public void testEndBeforeStart() {
        ValidateClassTimeUseCase.Result result = useCase.execute(
                11, 0, 10, 0, -1L, Collections.emptyList(), null
        );
        assertEquals(ValidateClassTimeUseCase.Result.INVALID_END_BEFORE_START, result);
    }

    @Test
    public void testOverlapsExistingClass() {
        WeeklyClass existing = new WeeklyClass(1L, 1L, DayOfWeek.SATURDAY, 9, 0, 10, 30);
        List<WeeklyClassWithCourse> existingList = Collections.singletonList(
                new WeeklyClassWithCourse(existing, testCourse)
        );

        ValidateClassTimeUseCase.Result result = useCase.execute(
                10, 0, 11, 0, -1L, existingList, null
        );
        assertEquals(ValidateClassTimeUseCase.Result.OVERLAPS_EXISTING_CLASS, result);
    }

    @Test
    public void testOverlapsDailyBreak() {
        RoutineConfig config = new RoutineConfig(Collections.emptySet(), true, 13, 0, 14, 0, true);

        ValidateClassTimeUseCase.Result result = useCase.execute(
                13, 30, 14, 30, -1L, Collections.emptyList(), config
        );
        assertEquals(ValidateClassTimeUseCase.Result.OVERLAPS_BREAK, result);
    }
}
