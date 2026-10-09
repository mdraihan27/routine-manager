package io.github.mdraihan27.routinemanager.core.threading;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Singleton
public final class AppSchedulersImpl implements AppSchedulers {

    @Inject
    public AppSchedulersImpl() {
    }

    @NonNull
    @Override
    public Scheduler io() {
        return Schedulers.io();
    }

    @NonNull
    @Override
    public Scheduler main() {
        return AndroidSchedulers.mainThread();
    }
}
