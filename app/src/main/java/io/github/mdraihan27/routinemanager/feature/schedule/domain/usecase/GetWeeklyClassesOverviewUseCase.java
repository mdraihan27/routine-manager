package io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase;

import androidx.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse;
import io.github.mdraihan27.routinemanager.feature.routine.domain.repository.RoutineRepository;
import io.reactivex.rxjava3.core.Flowable;

public class GetWeeklyClassesOverviewUseCase {

    private final RoutineRepository routineRepository;

    @Inject
    public GetWeeklyClassesOverviewUseCase(@NonNull RoutineRepository routineRepository) {
        this.routineRepository = routineRepository;
    }

    @NonNull
    public Flowable<List<WeeklyClassWithCourse>> execute() {
        return routineRepository.getWeeklyClasses();
    }
}
