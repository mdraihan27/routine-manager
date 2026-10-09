package io.github.mdraihan27.routinemanager.core.time;

import androidx.annotation.NonNull;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class AppClockImpl implements AppClock {

    private final Clock clock;

    @Inject
    public AppClockImpl() {
        this.clock = Clock.systemDefaultZone();
    }

    public AppClockImpl(@NonNull Clock clock) {
        this.clock = clock;
    }

    @Override
    public long currentTimeMillis() {
        return clock.millis();
    }

    @NonNull
    @Override
    public LocalDate currentDate() {
        return LocalDate.now(clock);
    }

    @NonNull
    @Override
    public LocalTime currentTime() {
        return LocalTime.now(clock);
    }

    @NonNull
    @Override
    public DayOfWeek currentDayOfWeek() {
        return LocalDate.now(clock).getDayOfWeek();
    }
}
