package io.github.mdraihan27.routinemanager.feature.course.domain.repository;

import androidx.annotation.NonNull;

import java.util.List;

import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface CourseRepository {

    @NonNull
    Flowable<List<Course>> getCourses();

    @NonNull
    Single<Course> getCourse(long id);

    @NonNull
    Completable addCourse(@NonNull Course course);

    @NonNull
    Completable updateCourse(@NonNull Course course);

    @NonNull
    Completable deleteCourse(long id);

    @NonNull
    Flowable<List<String>> getTeacherSuggestions();

    @NonNull
    Single<Integer> getCourseCount();

    @NonNull
    Single<Boolean> isCourseReferencedInRoutine(long courseId);
}
