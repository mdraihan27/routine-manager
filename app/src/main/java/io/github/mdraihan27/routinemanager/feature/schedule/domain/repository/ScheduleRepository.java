package io.github.mdraihan27.routinemanager.feature.schedule.domain.repository;

import androidx.annotation.NonNull;

import java.time.LocalDate;

import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailySchedule;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public interface ScheduleRepository {

    @NonNull
    Flowable<DailySchedule> getTodaySchedule();

    @NonNull
    Completable cancelOccurrence(long weeklyClassId, @NonNull LocalDate date);

    @NonNull
    Completable uncancelOccurrence(long weeklyClassId, @NonNull LocalDate date);

    @NonNull
    Completable completeOccurrence(long weeklyClassId, @NonNull LocalDate date);

    @NonNull
    Completable rescheduleOccurrenceOnly(long weeklyClassId, @NonNull LocalDate date, int startH, int startM, int endH, int endM);

    @NonNull
    Completable reschedulePermanent(long weeklyClassId, int startH, int startM, int endH, int endM);
}
