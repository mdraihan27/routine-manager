package io.github.mdraihan27.routinemanager.feature.course.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface TeacherDao {

    @Query("SELECT * FROM teachers ORDER BY name ASC")
    Flowable<List<TeacherEntity>> getAllTeachers();

    @Query("SELECT * FROM teachers WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    Single<List<TeacherEntity>> searchTeachers(String query);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertTeacher(TeacherEntity teacher);
}
