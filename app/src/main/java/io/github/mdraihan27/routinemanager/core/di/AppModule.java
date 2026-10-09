package io.github.mdraihan27.routinemanager.core.di;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import io.github.mdraihan27.routinemanager.core.designsystem.color.ColorProvider;
import io.github.mdraihan27.routinemanager.core.designsystem.color.ColorProviderImpl;
import io.github.mdraihan27.routinemanager.core.designsystem.motion.MotionPreferences;
import io.github.mdraihan27.routinemanager.core.designsystem.motion.MotionPreferencesImpl;
import io.github.mdraihan27.routinemanager.feature.course.presentation.CourseColorResolver;
import io.github.mdraihan27.routinemanager.feature.course.presentation.CourseColorResolverImpl;

@Module
@InstallIn(SingletonComponent.class)
public abstract class AppModule {

    @Binds
    public abstract ColorProvider bindColorProvider(ColorProviderImpl impl);

    @Binds
    public abstract CourseColorResolver bindCourseColorResolver(CourseColorResolverImpl impl);

    @Binds
    public abstract MotionPreferences bindMotionPreferences(MotionPreferencesImpl impl);

    @Binds
    public abstract io.github.mdraihan27.routinemanager.core.datastore.PreferencesDataSource bindPreferencesDataSource(
            io.github.mdraihan27.routinemanager.core.datastore.PreferencesDataSourceImpl impl);
}
