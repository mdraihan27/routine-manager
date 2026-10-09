package io.github.mdraihan27.routinemanager.feature.course.di;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import io.github.mdraihan27.routinemanager.feature.course.data.repository.CourseRepositoryImpl;
import io.github.mdraihan27.routinemanager.feature.course.domain.repository.CourseRepository;

@Module
@InstallIn(SingletonComponent.class)
public abstract class CourseModule {

    @Binds
    public abstract CourseRepository bindCourseRepository(CourseRepositoryImpl impl);
}
