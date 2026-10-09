package io.github.mdraihan27.routinemanager.feature.routine.data.local;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoutineDao {

    @Query("SELECT * FROM routine_config WHERE id = 1 LIMIT 1")
    Flowable<List<RoutineConfigEntity>> getRoutineConfig();

    @Query("SELECT * FROM routine_config WHERE id = 1 LIMIT 1")
    Maybe<RoutineConfigEntity> getRoutineConfigMaybe();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrUpdateConfig(RoutineConfigEntity config);

    @Query("SELECT * FROM weekly_classes ORDER BY startHour ASC, startMinute ASC")
    Flowable<List<WeeklyClassEntity>> getAllWeeklyClasses();

    @Query("SELECT * FROM weekly_classes WHERE dayOfWeek = :dayOfWeek ORDER BY startHour ASC, startMinute ASC")
    Flowable<List<WeeklyClassEntity>> getWeeklyClassesForDay(String dayOfWeek);

    @Query("SELECT * FROM weekly_classes WHERE dayOfWeek = :dayOfWeek ORDER BY startHour ASC, startMinute ASC")
    Single<List<WeeklyClassEntity>> getWeeklyClassesForDaySingle(String dayOfWeek);

    @Query("SELECT * FROM weekly_classes WHERE id = :id LIMIT 1")
    Maybe<WeeklyClassEntity> getWeeklyClassById(long id);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Single<Long> insertWeeklyClass(WeeklyClassEntity weeklyClass);

    @Update
    Completable updateWeeklyClass(WeeklyClassEntity weeklyClass);

    @Delete
    Completable deleteWeeklyClass(WeeklyClassEntity weeklyClass);

    @Query("SELECT COUNT(*) FROM weekly_classes WHERE courseId = :courseId")
    Single<Integer> countWeeklyClassesForCourse(long courseId);

    @Query("DELETE FROM weekly_classes WHERE courseId = :courseId")
    Completable deleteWeeklyClassesForCourse(long courseId);
}
