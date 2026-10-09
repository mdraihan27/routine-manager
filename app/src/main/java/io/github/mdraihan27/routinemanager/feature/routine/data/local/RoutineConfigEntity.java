package io.github.mdraihan27.routinemanager.feature.routine.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "routine_config")
public class RoutineConfigEntity {

    @PrimaryKey
    private long id = 1;

    @NonNull
    private String weeklyHolidays;

    private boolean hasDefaultBreak;

    private int breakStartHour;

    private int breakStartMinute;

    private int breakEndHour;

    private int breakEndMinute;

    private boolean configured;

    public RoutineConfigEntity(long id,
                               @NonNull String weeklyHolidays,
                               boolean hasDefaultBreak,
                               int breakStartHour,
                               int breakStartMinute,
                               int breakEndHour,
                               int breakEndMinute,
                               boolean configured) {
        this.id = id;
        this.weeklyHolidays = weeklyHolidays;
        this.hasDefaultBreak = hasDefaultBreak;
        this.breakStartHour = breakStartHour;
        this.breakStartMinute = breakStartMinute;
        this.breakEndHour = breakEndHour;
        this.breakEndMinute = breakEndMinute;
        this.configured = configured;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getWeeklyHolidays() {
        return weeklyHolidays;
    }

    public void setWeeklyHolidays(@NonNull String weeklyHolidays) {
        this.weeklyHolidays = weeklyHolidays;
    }

    public boolean isHasDefaultBreak() {
        return hasDefaultBreak;
    }

    public void setHasDefaultBreak(boolean hasDefaultBreak) {
        this.hasDefaultBreak = hasDefaultBreak;
    }

    public int getBreakStartHour() {
        return breakStartHour;
    }

    public void setBreakStartHour(int breakStartHour) {
        this.breakStartHour = breakStartHour;
    }

    public int getBreakStartMinute() {
        return breakStartMinute;
    }

    public void setBreakStartMinute(int breakStartMinute) {
        this.breakStartMinute = breakStartMinute;
    }

    public int getBreakEndHour() {
        return breakEndHour;
    }

    public void setBreakEndHour(int breakEndHour) {
        this.breakEndHour = breakEndHour;
    }

    public int getBreakEndMinute() {
        return breakEndMinute;
    }

    public void setBreakEndMinute(int breakEndMinute) {
        this.breakEndMinute = breakEndMinute;
    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean configured) {
        this.configured = configured;
    }
}
