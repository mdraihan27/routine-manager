package io.github.mdraihan27.routinemanager.core.di;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import io.github.mdraihan27.routinemanager.core.navigation.Navigator;
import io.github.mdraihan27.routinemanager.core.navigation.NavigatorImpl;

@Module
@InstallIn(SingletonComponent.class)
public abstract class NavigationModule {

    @Binds
    public abstract Navigator bindNavigator(NavigatorImpl impl);
}
