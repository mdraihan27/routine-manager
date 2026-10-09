package io.github.mdraihan27.routinemanager.feature.schedule.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface OccurrenceDao {

    @Query("SELECT * FROM class_occurrences WHERE date = :date")
    Single<List<ClassOccurrenceEntity>> getOccurrencesForDate(String date);

    @Query("SELECT * FROM class_occurrences WHERE weeklyClassId = :weeklyClassId AND date = :date LIMIT 1")
    Maybe<ClassOccurrenceEntity> getOccurrence(long weeklyClassId, String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrUpdateOccurrence(ClassOccurrenceEntity occurrence);

    @Query("DELETE FROM class_occurrences WHERE weeklyClassId = :weeklyClassId AND date = :date")
    Completable deleteOccurrence(long weeklyClassId, String date);

    @Query("SELECT * FROM class_exceptions WHERE date = :date")
    Single<List<ClassExceptionEntity>> getExceptionsForDate(String date);

    @Query("SELECT * FROM class_exceptions WHERE weeklyClassId = :weeklyClassId AND date = :date LIMIT 1")
    Maybe<ClassExceptionEntity> getException(long weeklyClassId, String date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrUpdateException(ClassExceptionEntity exception);

    @Query("DELETE FROM class_exceptions WHERE weeklyClassId = :weeklyClassId AND date = :date")
    Completable deleteException(long weeklyClassId, String date);
}
