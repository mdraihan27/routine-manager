package io.github.mdraihan27.routinemanager.feature.routine.domain.repository;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.util.List;

import io.github.mdraihan27.routinemanager.feature.routine.domain.model.RoutineConfig;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClass;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface RoutineRepository {

    @NonNull
    Flowable<RoutineConfig> getRoutineConfig();

    @NonNull
    Single<RoutineConfig> getRoutineConfigSingle();

    @NonNull
    Completable saveRoutineConfig(@NonNull RoutineConfig config);

    @NonNull
    Flowable<List<WeeklyClassWithCourse>> getWeeklyClasses();

    @NonNull
    Flowable<List<WeeklyClassWithCourse>> getWeeklyClassesForDay(@NonNull DayOfWeek dayOfWeek);

    @NonNull
    Completable addWeeklyClass(@NonNull WeeklyClass weeklyClass);

    @NonNull
    Completable updateWeeklyClass(@NonNull WeeklyClass weeklyClass);

    @NonNull
    Completable deleteWeeklyClass(long id);

    @NonNull
    Single<Boolean> isRoutineConfigured();
}
