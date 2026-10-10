package io.github.mdraihan27.routinemanager.feature.settings.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseEntity;
import io.github.mdraihan27.routinemanager.feature.course.data.local.TeacherEntity;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.RoutineConfigEntity;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.WeeklyClassEntity;
import io.github.mdraihan27.routinemanager.feature.schedule.data.local.ClassExceptionEntity;
import io.github.mdraihan27.routinemanager.feature.schedule.data.local.ClassOccurrenceEntity;

@Dao
public interface BackupDao {
    @Query("SELECT * FROM courses")
    List<CourseEntity> getAllCourses();

    @Query("SELECT * FROM teachers")
    List<TeacherEntity> getAllTeachers();

    @Query("SELECT * FROM weekly_classes")
    List<WeeklyClassEntity> getAllWeeklyClasses();

    @Query("SELECT * FROM class_occurrences")
    List<ClassOccurrenceEntity> getAllOccurrences();

    @Query("SELECT * FROM class_exceptions")
    List<ClassExceptionEntity> getAllExceptions();

    @Query("SELECT * FROM routine_config WHERE id = 1 LIMIT 1")
    RoutineConfigEntity getRoutineConfig();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertCourse(CourseEntity course);

    @Query("SELECT id FROM courses WHERE code = :code LIMIT 1")
    long getCourseIdByCode(String code);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertTeacher(TeacherEntity teacher);

    @Query("SELECT id FROM teachers WHERE name = :name LIMIT 1")
    long getTeacherIdByName(String name);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertWeeklyClass(WeeklyClassEntity weeklyClass);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertOccurrence(ClassOccurrenceEntity occurrence);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertException(ClassExceptionEntity exception);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertRoutineConfig(RoutineConfigEntity config);
}
