package io.github.mdraihan27.routinemanager.core.di;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import io.github.mdraihan27.routinemanager.core.background.BackgroundKeepAliveManager;
import io.github.mdraihan27.routinemanager.core.background.BackgroundKeepAliveManagerImpl;

@Module
@InstallIn(SingletonComponent.class)
public abstract class ServiceModule {

    @Binds
    public abstract BackgroundKeepAliveManager bindBackgroundKeepAliveManager(BackgroundKeepAliveManagerImpl impl);
}
