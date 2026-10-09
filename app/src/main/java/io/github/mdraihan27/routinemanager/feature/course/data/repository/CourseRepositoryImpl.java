package io.github.mdraihan27.routinemanager.feature.course.data.repository;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseDao;
import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseEntity;
import io.github.mdraihan27.routinemanager.feature.course.data.local.TeacherDao;
import io.github.mdraihan27.routinemanager.feature.course.data.local.TeacherEntity;
import io.github.mdraihan27.routinemanager.feature.course.data.mapper.CourseMapper;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.domain.repository.CourseRepository;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.RoutineDao;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public final class CourseRepositoryImpl implements CourseRepository {

    private final CourseDao courseDao;
    private final TeacherDao teacherDao;
    private final RoutineDao routineDao;

    @Inject
    public CourseRepositoryImpl(@NonNull CourseDao courseDao,
                                @NonNull TeacherDao teacherDao,
                                @NonNull RoutineDao routineDao) {
        this.courseDao = courseDao;
        this.teacherDao = teacherDao;
        this.routineDao = routineDao;
    }

    @NonNull
    @Override
    public Flowable<List<Course>> getCourses() {
        return courseDao.getAllCourses().map(entities -> {
            List<Course> list = new ArrayList<>();
            for (CourseEntity entity : entities) {
                list.add(CourseMapper.toDomain(entity));
            }
            return list;
        });
    }

    @NonNull
    @Override
    public Single<Course> getCourse(long id) {
        return courseDao.getCourseById(id)
                .toSingle()
                .map(CourseMapper::toDomain);
    }

    @NonNull
    @Override
    public Completable addCourse(@NonNull Course course) {
        CourseEntity entity = CourseMapper.toEntity(course);
        return courseDao.insertCourse(entity)
                .flatMapCompletable(id -> saveTeacherInternal(course.getTeacherName()));
    }

    @NonNull
    @Override
    public Completable updateCourse(@NonNull Course course) {
        CourseEntity entity = CourseMapper.toEntity(course);
        return courseDao.updateCourse(entity)
                .andThen(saveTeacherInternal(course.getTeacherName()));
    }

    @NonNull
    @Override
    public Completable deleteCourse(long id) {
        return courseDao.getCourseById(id)
                .flatMapCompletable(entity ->
                        routineDao.deleteWeeklyClassesForCourse(id)
                                .andThen(courseDao.deleteCourse(entity))
                );
    }

    @NonNull
    @Override
    public Flowable<List<String>> getTeacherSuggestions() {
        return teacherDao.getAllTeachers().map(teachers -> {
            List<String> names = new ArrayList<>();
            for (TeacherEntity teacher : teachers) {
                names.add(teacher.getName());
            }
            return names;
        });
    }

    @NonNull
    @Override
    public Single<Integer> getCourseCount() {
        return courseDao.getCourseCount();
    }

    @NonNull
    @Override
    public Single<Boolean> isCourseReferencedInRoutine(long courseId) {
        return routineDao.countWeeklyClassesForCourse(courseId)
                .map(count -> count > 0);
    }

    private Completable saveTeacherInternal(String teacherName) {
        String trimmed = teacherName.trim();
        if (trimmed.isEmpty()) {
            return Completable.complete();
        }
        return teacherDao.insertTeacher(new TeacherEntity(0, trimmed));
    }
}
