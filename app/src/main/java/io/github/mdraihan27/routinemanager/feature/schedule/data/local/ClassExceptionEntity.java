package io.github.mdraihan27.routinemanager.feature.schedule.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import io.github.mdraihan27.routinemanager.feature.routine.data.local.WeeklyClassEntity;

@Entity(
        tableName = "class_exceptions",
        foreignKeys = @ForeignKey(
                entity = WeeklyClassEntity.class,
                parentColumns = "id",
                childColumns = "weeklyClassId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"weeklyClassId", "date"}, unique = true)}
)
public class ClassExceptionEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long weeklyClassId;

    @NonNull
    private String date;

    private int startHour;

    private int startMinute;

    private int endHour;

    private int endMinute;

    public ClassExceptionEntity(long id,
                                long weeklyClassId,
                                @NonNull String date,
                                int startHour,
                                int startMinute,
                                int endHour,
                                int endMinute) {
        this.id = id;
        this.weeklyClassId = weeklyClassId;
        this.date = date;
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getWeeklyClassId() {
        return weeklyClassId;
    }

    public void setWeeklyClassId(long weeklyClassId) {
        this.weeklyClassId = weeklyClassId;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    public void setDate(@NonNull String date) {
        this.date = date;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }
}
