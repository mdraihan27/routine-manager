package io.github.mdraihan27.routinemanager.feature.schedule.data.repository;

import androidx.annotation.NonNull;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.mdraihan27.routinemanager.core.time.AppClock;
import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseDao;
import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseEntity;
import io.github.mdraihan27.routinemanager.feature.course.data.mapper.CourseMapper;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.RoutineConfigEntity;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.RoutineDao;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.WeeklyClassEntity;
import io.github.mdraihan27.routinemanager.feature.routine.data.mapper.RoutineMapper;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.RoutineConfig;
import io.github.mdraihan27.routinemanager.feature.schedule.data.local.ClassExceptionEntity;
import io.github.mdraihan27.routinemanager.feature.schedule.data.local.ClassOccurrenceEntity;
import io.github.mdraihan27.routinemanager.feature.schedule.data.local.OccurrenceDao;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.ClassStatus;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailyClassItem;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailySchedule;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.repository.ScheduleRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Singleton
public final class ScheduleRepositoryImpl implements ScheduleRepository {

    private final OccurrenceDao occurrenceDao;
    private final RoutineDao routineDao;
    private final CourseDao courseDao;
    private final AppClock appClock;

    @Inject
    public ScheduleRepositoryImpl(@NonNull OccurrenceDao occurrenceDao,
                                  @NonNull RoutineDao routineDao,
                                  @NonNull CourseDao courseDao,
                                  @NonNull AppClock appClock) {
        this.occurrenceDao = occurrenceDao;
        this.routineDao = routineDao;
        this.courseDao = courseDao;
        this.appClock = appClock;
    }

    @NonNull
    @Override
    public Flowable<DailySchedule> getTodaySchedule() {
        LocalDate today = appClock.currentDate();
        DayOfWeek dayOfWeek = appClock.currentDayOfWeek();
        LocalTime now = appClock.currentTime();
        int nowMinutes = now.getHour() * 60 + now.getMinute();
        String dateStr = today.toString();

        return Flowable.combineLatest(
                routineDao.getRoutineConfig(),
                routineDao.getWeeklyClassesForDay(dayOfWeek.name()),
                courseDao.getAllCourses(),
                (configEntities, classEntities, courseEntities) -> {
                    RoutineConfig config = configEntities.isEmpty()
                            ? RoutineConfig.empty()
                            : RoutineMapper.toDomain(configEntities.get(0));

                    boolean isHoliday = config.isHoliday(dayOfWeek);
                    boolean isBreak = false;
                    if (config.hasDefaultBreak()) {
                        int bStart = config.getBreakStartHour() * 60 + config.getBreakStartMinute();
                        int bEnd = config.getBreakEndHour() * 60 + config.getBreakEndMinute();
                        isBreak = (nowMinutes >= bStart && nowMinutes < bEnd);
                    }

                    List<ClassOccurrenceEntity> occurrences = occurrenceDao.getOccurrencesForDate(dateStr).blockingGet();
                    List<ClassExceptionEntity> exceptions = occurrenceDao.getExceptionsForDate(dateStr).blockingGet();

                    Map<Long, String> occurrenceStatusMap = new HashMap<>();
                    for (ClassOccurrenceEntity occ : occurrences) {
                        occurrenceStatusMap.put(occ.getWeeklyClassId(), occ.getStatus());
                    }

                    Map<Long, ClassExceptionEntity> exceptionMap = new HashMap<>();
                    for (ClassExceptionEntity exc : exceptions) {
                        exceptionMap.put(exc.getWeeklyClassId(), exc);
                    }

                    Map<Long, Course> courseMap = new HashMap<>();
                    for (CourseEntity ce : courseEntities) {
                        courseMap.put(ce.getId(), CourseMapper.toDomain(ce));
                    }

                    List<DailyClassItem> classItems = new ArrayList<>();
                    for (WeeklyClassEntity wce : classEntities) {
                        Course course = courseMap.get(wce.getCourseId());
                        if (course == null) {
                            continue;
                        }

                        int startH = wce.getStartHour();
                        int startM = wce.getStartMinute();
                        int endH = wce.getEndHour();
                        int endM = wce.getEndMinute();
                        boolean isException = false;

                        ClassExceptionEntity exc = exceptionMap.get(wce.getId());
                        if (exc != null) {
                            startH = exc.getStartHour();
                            startM = exc.getStartMinute();
                            endH = exc.getEndHour();
                            endM = exc.getEndMinute();
                            isException = true;
                        }

                        int startMinutes = startH * 60 + startM;
                        int endMinutes = endH * 60 + endM;

                        ClassStatus status;
                        String recordedStatus = occurrenceStatusMap.get(wce.getId());
                        if ("CANCELLED".equals(recordedStatus)) {
                            status = ClassStatus.CANCELLED;
                        } else if ("COMPLETED".equals(recordedStatus)) {
                            status = ClassStatus.COMPLETED;
                        } else if (nowMinutes >= endMinutes) {
                            status = ClassStatus.COMPLETED;
                        } else if (nowMinutes >= startMinutes && nowMinutes < endMinutes) {
                            status = ClassStatus.IN_PROGRESS;
                        } else {
                            status = ClassStatus.UPCOMING;
                        }

                        classItems.add(new DailyClassItem(
                                wce.getId(),
                                course.getId(),
                                course.getCode(),
                                course.getName(),
                                course.getTeacherName(),
                                course.getColor(),
                                startH,
                                startM,
                                endH,
                                endM,
                                status,
                                isException
                        ));
                    }

                    classItems.sort(Comparator.comparingInt(DailyClassItem::getStartTotalMinutes));

                    int targetIndex = 0;
                    for (int i = 0; i < classItems.size(); i++) {
                        DailyClassItem item = classItems.get(i);
                        if (item.getStatus() == ClassStatus.IN_PROGRESS || item.getStatus() == ClassStatus.UPCOMING) {
                            targetIndex = i;
                            break;
                        }
                    }

                    return new DailySchedule(
                            today,
                            dayOfWeek,
                            isHoliday,
                            isBreak,
                            classItems,
                            targetIndex
                    );
                }
        );
    }

    @NonNull
    @Override
    public Completable cancelOccurrence(long weeklyClassId, @NonNull LocalDate date) {
        ClassOccurrenceEntity entity = new ClassOccurrenceEntity(0, weeklyClassId, date.toString(), "CANCELLED");
        return occurrenceDao.insertOrUpdateOccurrence(entity);
    }

    @NonNull
    @Override
    public Completable uncancelOccurrence(long weeklyClassId, @NonNull LocalDate date) {
        return occurrenceDao.deleteOccurrence(weeklyClassId, date.toString());
    }

    @NonNull
    @Override
    public Completable completeOccurrence(long weeklyClassId, @NonNull LocalDate date) {
        ClassOccurrenceEntity entity = new ClassOccurrenceEntity(0, weeklyClassId, date.toString(), "COMPLETED");
        return occurrenceDao.insertOrUpdateOccurrence(entity);
    }

    @NonNull
    @Override
    public Completable rescheduleOccurrenceOnly(long weeklyClassId, @NonNull LocalDate date, int startH, int startM, int endH, int endM) {
        ClassExceptionEntity entity = new ClassExceptionEntity(0, weeklyClassId, date.toString(), startH, startM, endH, endM);
        return occurrenceDao.insertOrUpdateException(entity);
    }

    @NonNull
    @Override
    public Completable reschedulePermanent(long weeklyClassId, int startH, int startM, int endH, int endM) {
        return routineDao.getWeeklyClassById(weeklyClassId)
                .flatMapCompletable(entity -> {
                    entity.setStartHour(startH);
                    entity.setStartMinute(startM);
                    entity.setEndHour(endH);
                    entity.setEndMinute(endM);
                    return routineDao.updateWeeklyClass(entity);
                });
    }
}
