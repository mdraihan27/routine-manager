package io.github.mdraihan27.routinemanager.feature.course.data.local;

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
public interface CourseDao {

    @Query("SELECT * FROM courses ORDER BY code ASC")
    Flowable<List<CourseEntity>> getAllCourses();

    @Query("SELECT * FROM courses ORDER BY code ASC")
    Single<List<CourseEntity>> getAllCoursesSingle();

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    Maybe<CourseEntity> getCourseById(long id);

    @Query("SELECT * FROM courses WHERE code = :code LIMIT 1")
    Maybe<CourseEntity> getCourseByCode(String code);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    Single<Long> insertCourse(CourseEntity course);

    @Update
    Completable updateCourse(CourseEntity course);

    @Delete
    Completable deleteCourse(CourseEntity course);

    @Query("SELECT COUNT(*) FROM courses")
    Single<Integer> getCourseCount();
}
