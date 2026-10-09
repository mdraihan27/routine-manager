package io.github.mdraihan27.routinemanager.core.di;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import io.github.mdraihan27.routinemanager.core.threading.AppSchedulers;
import io.github.mdraihan27.routinemanager.core.threading.AppSchedulersImpl;
import io.github.mdraihan27.routinemanager.core.time.AppClock;
import io.github.mdraihan27.routinemanager.core.time.AppClockImpl;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ThreadingModule {

    @Binds
    public abstract AppSchedulers bindAppSchedulers(AppSchedulersImpl impl);

    @Binds
    public abstract AppClock bindAppClock(AppClockImpl impl);
}
