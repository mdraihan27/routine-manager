package io.github.mdraihan27.routinemanager.feature.routine.domain.usecase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.routine.domain.model.RoutineConfig;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClass;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse;

public final class ValidateClassTimeUseCase {

    public enum Result {
        VALID,
        INVALID_END_BEFORE_START,
        OVERLAPS_EXISTING_CLASS,
        OVERLAPS_BREAK
    }

    @Inject
    public ValidateClassTimeUseCase() {
    }

    @NonNull
    public Result execute(int startHour,
                          int startMinute,
                          int endHour,
                          int endMinute,
                          long excludeClassId,
                          @NonNull List<WeeklyClassWithCourse> existingClasses,
                          @Nullable RoutineConfig routineConfig) {
        int startTotal = startHour * 60 + startMinute;
        int endTotal = endHour * 60 + endMinute;

        if (endTotal <= startTotal) {
            return Result.INVALID_END_BEFORE_START;
        }

        for (WeeklyClassWithCourse item : existingClasses) {
            WeeklyClass wc = item.getWeeklyClass();
            if (wc.getId() == excludeClassId) {
                continue;
            }
            int existingStart = wc.getStartTotalMinutes();
            int existingEnd = wc.getEndTotalMinutes();
            if (startTotal < existingEnd && endTotal > existingStart) {
                return Result.OVERLAPS_EXISTING_CLASS;
            }
        }

        if (routineConfig != null && routineConfig.hasDefaultBreak()) {
            int breakStart = routineConfig.getBreakStartHour() * 60 + routineConfig.getBreakStartMinute();
            int breakEnd = routineConfig.getBreakEndHour() * 60 + routineConfig.getBreakEndMinute();
            if (startTotal < breakEnd && endTotal > breakStart) {
                return Result.OVERLAPS_BREAK;
            }
        }

        return Result.VALID;
    }
}
