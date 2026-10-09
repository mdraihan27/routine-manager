package io.github.mdraihan27.routinemanager.feature.schedule.di;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import io.github.mdraihan27.routinemanager.feature.schedule.data.repository.ScheduleRepositoryImpl;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.repository.ScheduleRepository;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ScheduleModule {

    @Binds
    public abstract ScheduleRepository bindScheduleRepository(ScheduleRepositoryImpl impl);
}
