package io.github.mdraihan27.routinemanager.feature.routine.data.repository;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseDao;
import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseEntity;
import io.github.mdraihan27.routinemanager.feature.course.data.mapper.CourseMapper;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.RoutineConfigEntity;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.RoutineDao;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.WeeklyClassEntity;
import io.github.mdraihan27.routinemanager.feature.routine.data.mapper.RoutineMapper;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.RoutineConfig;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClass;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse;
import io.github.mdraihan27.routinemanager.feature.routine.domain.repository.RoutineRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Singleton
public final class RoutineRepositoryImpl implements RoutineRepository {

    private final RoutineDao routineDao;
    private final CourseDao courseDao;

    @Inject
    public RoutineRepositoryImpl(@NonNull RoutineDao routineDao,
                                 @NonNull CourseDao courseDao) {
        this.routineDao = routineDao;
        this.courseDao = courseDao;
    }

    @NonNull
    @Override
    public Flowable<RoutineConfig> getRoutineConfig() {
        return routineDao.getRoutineConfig()
                .map(list -> {
                    if (list.isEmpty()) {
                        return RoutineConfig.empty();
                    }
                    return RoutineMapper.toDomain(list.get(0));
                });
    }

    @NonNull
    @Override
    public Single<RoutineConfig> getRoutineConfigSingle() {
        return routineDao.getRoutineConfigMaybe()
                .map(RoutineMapper::toDomain)
                .defaultIfEmpty(RoutineConfig.empty());
    }

    @NonNull
    @Override
    public Completable saveRoutineConfig(@NonNull RoutineConfig config) {
        RoutineConfigEntity entity = RoutineMapper.toEntity(config);
        return routineDao.insertOrUpdateConfig(entity);
    }

    @NonNull
    @Override
    public Flowable<List<WeeklyClassWithCourse>> getWeeklyClasses() {
        return Flowable.combineLatest(
                routineDao.getAllWeeklyClasses(),
                courseDao.getAllCourses(),
                (classEntities, courseEntities) -> mapClassesWithCourses(classEntities, courseEntities)
        );
    }

    @NonNull
    @Override
    public Flowable<List<WeeklyClassWithCourse>> getWeeklyClassesForDay(@NonNull DayOfWeek dayOfWeek) {
        return Flowable.combineLatest(
                routineDao.getWeeklyClassesForDay(dayOfWeek.name()),
                courseDao.getAllCourses(),
                (classEntities, courseEntities) -> mapClassesWithCourses(classEntities, courseEntities)
        );
    }

    private List<WeeklyClassWithCourse> mapClassesWithCourses(List<WeeklyClassEntity> classEntities,
                                                             List<CourseEntity> courseEntities) {
        List<WeeklyClassWithCourse> result = new ArrayList<>();
        for (WeeklyClassEntity ce : classEntities) {
            Course matchedCourse = null;
            for (CourseEntity courseEntity : courseEntities) {
                if (courseEntity.getId() == ce.getCourseId()) {
                    matchedCourse = CourseMapper.toDomain(courseEntity);
                    break;
                }
            }
            if (matchedCourse != null) {
                result.add(new WeeklyClassWithCourse(RoutineMapper.toDomain(ce), matchedCourse));
            }
        }
        return result;
    }

    @NonNull
    @Override
    public Completable addWeeklyClass(@NonNull WeeklyClass weeklyClass) {
        return routineDao.insertWeeklyClass(RoutineMapper.toEntity(weeklyClass)).ignoreElement();
    }

    @NonNull
    @Override
    public Completable updateWeeklyClass(@NonNull WeeklyClass weeklyClass) {
        return routineDao.updateWeeklyClass(RoutineMapper.toEntity(weeklyClass));
    }

    @NonNull
    @Override
    public Completable deleteWeeklyClass(long id) {
        return routineDao.getWeeklyClassById(id)
                .flatMapCompletable(routineDao::deleteWeeklyClass);
    }

    @NonNull
    @Override
    public Single<Boolean> isRoutineConfigured() {
        return routineDao.getRoutineConfigMaybe()
                .map(RoutineConfigEntity::isConfigured)
                .defaultIfEmpty(false);
    }
}
