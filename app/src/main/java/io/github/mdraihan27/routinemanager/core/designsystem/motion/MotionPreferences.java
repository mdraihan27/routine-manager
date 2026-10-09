package io.github.mdraihan27.routinemanager.core.designsystem.motion;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface MotionPreferences {

    @NonNull
    AnimationIntensity getIntensity();

    @NonNull
    Completable setIntensity(@NonNull AnimationIntensity intensity);

    @NonNull
    Observable<AnimationIntensity> observeIntensity();

    float getDampingRatio();

    float getStiffness();
}
