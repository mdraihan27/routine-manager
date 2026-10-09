package io.github.mdraihan27.routinemanager.core.threading;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.core.Scheduler;

public interface AppSchedulers {

    @NonNull
    Scheduler io();

    @NonNull
    Scheduler main();
}
