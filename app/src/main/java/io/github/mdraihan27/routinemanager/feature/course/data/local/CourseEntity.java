package io.github.mdraihan27.routinemanager.feature.course.data.local;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "courses", indices = {@Index(value = {"code"}, unique = true)})
public class CourseEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @NonNull
    private String code;

    @NonNull
    private String name;

    @NonNull
    private String teacherName;

    @Nullable
    private String colorKey;

    @Nullable
    private Integer customColorArgb;

    public CourseEntity(long id,
                        @NonNull String code,
                        @NonNull String name,
                        @NonNull String teacherName,
                        @Nullable String colorKey,
                        @Nullable Integer customColorArgb) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.teacherName = teacherName;
        this.colorKey = colorKey;
        this.customColorArgb = customColorArgb;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    public void setCode(@NonNull String code) {
        this.code = code;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(@NonNull String teacherName) {
        this.teacherName = teacherName;
    }

    @Nullable
    public String getColorKey() {
        return colorKey;
    }

    public void setColorKey(@Nullable String colorKey) {
        this.colorKey = colorKey;
    }

    @Nullable
    public Integer getCustomColorArgb() {
        return customColorArgb;
    }

    public void setCustomColorArgb(@Nullable Integer customColorArgb) {
        this.customColorArgb = customColorArgb;
    }
}
