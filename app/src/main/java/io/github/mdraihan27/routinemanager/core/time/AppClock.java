package io.github.mdraihan27.routinemanager.core.time;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public interface AppClock {

    long currentTimeMillis();

    @NonNull
    LocalDate currentDate();

    @NonNull
    LocalTime currentTime();

    @NonNull
    DayOfWeek currentDayOfWeek();
}
