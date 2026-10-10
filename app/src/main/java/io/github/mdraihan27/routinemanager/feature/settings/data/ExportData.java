package io.github.mdraihan27.routinemanager.feature.settings.data;

import java.util.List;

import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseEntity;
import io.github.mdraihan27.routinemanager.feature.course.data.local.TeacherEntity;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.RoutineConfigEntity;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.WeeklyClassEntity;
import io.github.mdraihan27.routinemanager.feature.schedule.data.local.ClassExceptionEntity;
import io.github.mdraihan27.routinemanager.feature.schedule.data.local.ClassOccurrenceEntity;

public class ExportData {
    public List<CourseEntity> courses;
    public List<TeacherEntity> teachers;
    public List<WeeklyClassEntity> weeklyClasses;
    public List<ClassOccurrenceEntity> occurrences;
    public List<ClassExceptionEntity> exceptions;
    public RoutineConfigEntity config;
}
