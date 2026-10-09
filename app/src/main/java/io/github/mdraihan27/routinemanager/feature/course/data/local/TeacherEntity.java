package io.github.mdraihan27.routinemanager.feature.course.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "teachers", indices = {@Index(value = {"name"}, unique = true)})
public class TeacherEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String name;

    public TeacherEntity(long id, @NonNull String name) {
        this.id = id;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }
}
