package io.github.mdraihan27.routinemanager;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Collections;

import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailySchedule;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.repository.ScheduleRepository;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.usecase.CancelClassOccurrenceUseCase;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

import static org.junit.Assert.assertTrue;

public class CancelClassOccurrenceUseCaseTest {

    private static class FakeScheduleRepository implements ScheduleRepository {
        boolean cancelCalled = false;
        long lastWeeklyClassId = -1L;
        LocalDate lastDate = null;

        @Override
        public Flowable<DailySchedule> getTodaySchedule() {
            return Flowable.empty();
        }

        @Override
        public Completable cancelOccurrence(long weeklyClassId, LocalDate date) {
            cancelCalled = true;
            lastWeeklyClassId = weeklyClassId;
            lastDate = date;
            return Completable.complete();
        }

        @Override
        public Completable uncancelOccurrence(long weeklyClassId, LocalDate date) {
            return Completable.complete();
        }

        @Override
        public Completable completeOccurrence(long weeklyClassId, LocalDate date) {
            return Completable.complete();
        }

        @Override
        public Completable rescheduleOccurrenceOnly(long weeklyClassId, LocalDate date, int startH, int startM, int endH, int endM) {
            return Completable.complete();
        }

        @Override
        public Completable reschedulePermanent(long weeklyClassId, int startH, int startM, int endH, int endM) {
            return Completable.complete();
        }
    }

    @Test
    public void testCancelOccurrenceDelegation() {
        FakeScheduleRepository repository = new FakeScheduleRepository();
        CancelClassOccurrenceUseCase useCase = new CancelClassOccurrenceUseCase(repository);
        LocalDate today = LocalDate.of(2026, 10, 9);

        useCase.execute(42L, today).test().assertComplete();

        assertTrue(repository.cancelCalled);
        org.junit.Assert.assertEquals(42L, repository.lastWeeklyClassId);
        org.junit.Assert.assertEquals(today, repository.lastDate);
    }
}
