package io.github.mdraihan27.routinemanager.core.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseDao;
import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseEntity;
import io.github.mdraihan27.routinemanager.feature.course.data.local.TeacherDao;
import io.github.mdraihan27.routinemanager.feature.course.data.local.TeacherEntity;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.RoutineConfigEntity;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.RoutineDao;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.WeeklyClassEntity;
import io.github.mdraihan27.routinemanager.feature.schedule.data.local.ClassExceptionEntity;
import io.github.mdraihan27.routinemanager.feature.schedule.data.local.ClassOccurrenceEntity;
import io.github.mdraihan27.routinemanager.feature.schedule.data.local.OccurrenceDao;

@Database(
        entities = {
                CourseEntity.class,
                TeacherEntity.class,
                RoutineConfigEntity.class,
                WeeklyClassEntity.class,
                ClassOccurrenceEntity.class,
                ClassExceptionEntity.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CourseDao courseDao();

    public abstract TeacherDao teacherDao();

    public abstract RoutineDao routineDao();

    public abstract OccurrenceDao occurrenceDao();
}
