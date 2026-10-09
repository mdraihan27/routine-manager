package io.github.mdraihan27.routinemanager.core.di;

import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import io.github.mdraihan27.routinemanager.core.database.AppDatabase;
import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseDao;
import io.github.mdraihan27.routinemanager.feature.course.data.local.TeacherDao;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.RoutineDao;
import io.github.mdraihan27.routinemanager.feature.schedule.data.local.OccurrenceDao;

@Module
@InstallIn(SingletonComponent.class)
public final class DatabaseModule {

    private DatabaseModule() {
    }

    @Provides
    @Singleton
    public static AppDatabase provideDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "routine_manager.db").build();
    }

    @Provides
    public static CourseDao provideCourseDao(AppDatabase database) {
        return database.courseDao();
    }

    @Provides
    public static TeacherDao provideTeacherDao(AppDatabase database) {
        return database.teacherDao();
    }

    @Provides
    public static RoutineDao provideRoutineDao(AppDatabase database) {
        return database.routineDao();
    }

    @Provides
    public static OccurrenceDao provideOccurrenceDao(AppDatabase database) {
        return database.occurrenceDao();
    }
}
