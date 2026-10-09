package io.github.mdraihan27.routinemanager.feature.routine.data.mapper;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

import io.github.mdraihan27.routinemanager.feature.routine.data.local.RoutineConfigEntity;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.WeeklyClassEntity;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.RoutineConfig;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClass;

public final class RoutineMapper {

    private RoutineMapper() {
    }

    @NonNull
    public static RoutineConfig toDomain(@NonNull RoutineConfigEntity entity) {
        Set<DayOfWeek> holidays = new HashSet<>();
        if (!entity.getWeeklyHolidays().isEmpty()) {
            String[] parts = entity.getWeeklyHolidays().split(",");
            for (String part : parts) {
                String trimmed = part.trim();
                if (!trimmed.isEmpty()) {
                    try {
                        holidays.add(DayOfWeek.valueOf(trimmed));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }
        }

        return new RoutineConfig(
                holidays,
                entity.isHasDefaultBreak(),
                entity.getBreakStartHour(),
                entity.getBreakStartMinute(),
                entity.getBreakEndHour(),
                entity.getBreakEndMinute(),
                entity.isConfigured()
        );
    }

    @NonNull
    public static RoutineConfigEntity toEntity(@NonNull RoutineConfig domain) {
        StringBuilder sb = new StringBuilder();
        int index = 0;
        for (DayOfWeek day : domain.getWeeklyHolidays()) {
            if (index > 0) {
                sb.append(",");
            }
            sb.append(day.name());
            index++;
        }

        return new RoutineConfigEntity(
                1L,
                sb.toString(),
                domain.hasDefaultBreak(),
                domain.getBreakStartHour(),
                domain.getBreakStartMinute(),
                domain.getBreakEndHour(),
                domain.getBreakEndMinute(),
                domain.isConfigured()
        );
    }

    @NonNull
    public static WeeklyClass toDomain(@NonNull WeeklyClassEntity entity) {
        DayOfWeek day;
        try {
            day = DayOfWeek.valueOf(entity.getDayOfWeek());
        } catch (IllegalArgumentException e) {
            day = DayOfWeek.SATURDAY;
        }

        return new WeeklyClass(
                entity.getId(),
                entity.getCourseId(),
                day,
                entity.getStartHour(),
                entity.getStartMinute(),
                entity.getEndHour(),
                entity.getEndMinute()
        );
    }

    @NonNull
    public static WeeklyClassEntity toEntity(@NonNull WeeklyClass domain) {
        return new WeeklyClassEntity(
                domain.getId(),
                domain.getCourseId(),
                domain.getDayOfWeek().name(),
                domain.getStartHour(),
                domain.getStartMinute(),
                domain.getEndHour(),
                domain.getEndMinute()
        );
    }
}
