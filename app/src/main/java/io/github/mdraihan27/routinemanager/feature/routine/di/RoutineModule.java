package io.github.mdraihan27.routinemanager.feature.routine.di;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import io.github.mdraihan27.routinemanager.feature.routine.data.repository.RoutineRepositoryImpl;
import io.github.mdraihan27.routinemanager.feature.routine.domain.repository.RoutineRepository;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RoutineModule {

    @Binds
    public abstract RoutineRepository bindRoutineRepository(RoutineRepositoryImpl impl);
}
