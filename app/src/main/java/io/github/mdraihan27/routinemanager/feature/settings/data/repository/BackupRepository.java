package io.github.mdraihan27.routinemanager.feature.settings.data.repository;

import android.content.Context;
import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.inject.Inject;

import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseEntity;
import io.github.mdraihan27.routinemanager.feature.course.data.local.TeacherEntity;
import io.github.mdraihan27.routinemanager.feature.routine.data.local.WeeklyClassEntity;
import io.github.mdraihan27.routinemanager.feature.schedule.data.local.ClassExceptionEntity;
import io.github.mdraihan27.routinemanager.feature.schedule.data.local.ClassOccurrenceEntity;
import io.github.mdraihan27.routinemanager.feature.settings.data.ExportData;
import io.github.mdraihan27.routinemanager.feature.settings.data.local.BackupDao;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class BackupRepository {

    private final BackupDao backupDao;
    private final Gson gson;

    @Inject
    public BackupRepository(BackupDao backupDao) {
        this.backupDao = backupDao;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public Completable exportData(Context context, Uri targetUri) {
        return Completable.fromAction(() -> {
            ExportData data = new ExportData();
            data.courses = backupDao.getAllCourses();
            data.teachers = backupDao.getAllTeachers();
            data.weeklyClasses = backupDao.getAllWeeklyClasses();
            data.occurrences = backupDao.getAllOccurrences();
            data.exceptions = backupDao.getAllExceptions();
            data.config = backupDao.getRoutineConfig();

            try (OutputStream os = context.getContentResolver().openOutputStream(targetUri);
                 OutputStreamWriter writer = new OutputStreamWriter(os)) {
                gson.toJson(data, writer);
            }
        });
    }

    public Completable importData(Context context, Uri sourceUri) {
        return Completable.fromAction(() -> {
            ExportData data;
            try (InputStream is = context.getContentResolver().openInputStream(sourceUri);
                 InputStreamReader reader = new InputStreamReader(is)) {
                data = gson.fromJson(reader, ExportData.class);
            }

            if (data == null) throw new IllegalArgumentException("Invalid backup file");

            if (data.teachers != null) {
                for (TeacherEntity teacher : data.teachers) {
                    long oldId = teacher.getId();
                    teacher.setId(0); // auto-generate
                    long newId = backupDao.insertTeacher(teacher);
                    if (newId == -1) {
                        newId = backupDao.getTeacherIdByName(teacher.getName());
                    }
                }
            }

            if (data.courses != null) {
                for (CourseEntity course : data.courses) {
                    long oldId = course.getId();
                    course.setId(0);
                    long newId = backupDao.insertCourse(course);
                    if (newId == -1) {
                        newId = backupDao.getCourseIdByCode(course.getCode());
                    }
                    
                    // Now map weekly classes that belonged to this course
                    if (data.weeklyClasses != null) {
                        for (WeeklyClassEntity wc : data.weeklyClasses) {
                            if (wc.getCourseId() == oldId) {
                                wc.setCourseId(newId);
                            }
                        }
                    }
                }
            }

            if (data.weeklyClasses != null) {
                for (WeeklyClassEntity wc : data.weeklyClasses) {
                    long oldId = wc.getId();
                    wc.setId(0);
                    long newId = backupDao.insertWeeklyClass(wc);
                    
                    if (newId != -1) {
                        if (data.occurrences != null) {
                            for (ClassOccurrenceEntity occ : data.occurrences) {
                                if (occ.getWeeklyClassId() == oldId) {
                                    occ.setWeeklyClassId(newId);
                                }
                            }
                        }
                        if (data.exceptions != null) {
                            for (ClassExceptionEntity exc : data.exceptions) {
                                if (exc.getWeeklyClassId() == oldId) {
                                    exc.setWeeklyClassId(newId);
                                }
                            }
                        }
                    }
                }
            }

            if (data.occurrences != null) {
                for (ClassOccurrenceEntity occ : data.occurrences) {
                    occ.setId(0);
                    backupDao.insertOccurrence(occ);
                }
            }

            if (data.exceptions != null) {
                for (ClassExceptionEntity exc : data.exceptions) {
                    exc.setId(0);
                    backupDao.insertException(exc);
                }
            }

            if (data.config != null) {
                backupDao.insertRoutineConfig(data.config);
            }
        });
    }
}
