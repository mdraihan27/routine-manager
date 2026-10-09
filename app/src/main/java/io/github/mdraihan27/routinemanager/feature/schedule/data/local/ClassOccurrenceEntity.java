package io.github.mdraihan27.routinemanager.feature.schedule.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import io.github.mdraihan27.routinemanager.feature.routine.data.local.WeeklyClassEntity;

@Entity(
        tableName = "class_occurrences",
        foreignKeys = @ForeignKey(
                entity = WeeklyClassEntity.class,
                parentColumns = "id",
                childColumns = "weeklyClassId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index(value = {"weeklyClassId", "date"}, unique = true)}
)
public class ClassOccurrenceEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long weeklyClassId;

    @NonNull
    private String date;

    @NonNull
    private String status;

    public ClassOccurrenceEntity(long id,
                                 long weeklyClassId,
                                 @NonNull String date,
                                 @NonNull String status) {
        this.id = id;
        this.weeklyClassId = weeklyClassId;
        this.date = date;
        this.status = status;
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

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }
}
