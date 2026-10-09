package io.github.mdraihan27.routinemanager.feature.routine.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseEntity;

@Entity(
        tableName = "weekly_classes",
        foreignKeys = @ForeignKey(
                entity = CourseEntity.class,
                parentColumns = "id",
                childColumns = "courseId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"courseId"})}
)
public class WeeklyClassEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long courseId;

    @NonNull
    private String dayOfWeek;

    private int startHour;

    private int startMinute;

    private int endHour;

    private int endMinute;

    public WeeklyClassEntity(long id,
                             long courseId,
                             @NonNull String dayOfWeek,
                             int startHour,
                             int startMinute,
                             int endHour,
                             int endMinute) {
        this.id = id;
        this.courseId = courseId;
        this.dayOfWeek = dayOfWeek;
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

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    @NonNull
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(@NonNull String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
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
