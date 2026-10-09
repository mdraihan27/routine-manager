package io.github.mdraihan27.routinemanager.core.designsystem.motion;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.SpringForce;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.github.mdraihan27.routinemanager.core.designsystem.interaction.TapEffect;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

@Singleton
public final class MotionPreferencesImpl implements MotionPreferences {

    private static final String PREFS_NAME = "motion_preferences";
    private static final String KEY_INTENSITY = "animation_intensity";

    private final SharedPreferences preferences;
    private final BehaviorSubject<AnimationIntensity> subject;
    private AnimationIntensity cachedIntensity;

    @Inject
    public MotionPreferencesImpl(@NonNull @ApplicationContext Context context) {
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String name = preferences.getString(KEY_INTENSITY, AnimationIntensity.BALANCED.name());
        AnimationIntensity initial;
        try {
            initial = AnimationIntensity.valueOf(name);
        } catch (IllegalArgumentException e) {
            initial = AnimationIntensity.BALANCED;
        }
        this.cachedIntensity = initial;
        this.subject = BehaviorSubject.createDefault(initial);
        updateTapEffect(initial);
    }

    @NonNull
    @Override
    public AnimationIntensity getIntensity() {
        return cachedIntensity;
    }

    @NonNull
    @Override
    public Completable setIntensity(@NonNull AnimationIntensity intensity) {
        return Completable.fromAction(() -> {
            this.cachedIntensity = intensity;
            preferences.edit().putString(KEY_INTENSITY, intensity.name()).apply();
            updateTapEffect(intensity);
            subject.onNext(intensity);
        });
    }

    @NonNull
    @Override
    public Observable<AnimationIntensity> observeIntensity() {
        return subject.hide();
    }

    @Override
    public float getDampingRatio() {
        switch (cachedIntensity) {
            case REDUCED:
                return SpringForce.DAMPING_RATIO_NO_BOUNCY;
            case EXPRESSIVE:
                return SpringForce.DAMPING_RATIO_HIGH_BOUNCY;
            case BALANCED:
            default:
                return SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY;
        }
    }

    @Override
    public float getStiffness() {
        switch (cachedIntensity) {
            case REDUCED:
                return SpringForce.STIFFNESS_MEDIUM;
            case EXPRESSIVE:
                return SpringForce.STIFFNESS_VERY_LOW;
            case BALANCED:
            default:
                return SpringForce.STIFFNESS_LOW;
        }
    }

    private void updateTapEffect(AnimationIntensity intensity) {
        float damping;
        float stiffness;
        switch (intensity) {
            case REDUCED:
                damping = SpringForce.DAMPING_RATIO_NO_BOUNCY;
                stiffness = SpringForce.STIFFNESS_MEDIUM;
                break;
            case EXPRESSIVE:
                damping = SpringForce.DAMPING_RATIO_HIGH_BOUNCY;
                stiffness = SpringForce.STIFFNESS_VERY_LOW;
                break;
            case BALANCED:
            default:
                damping = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY;
                stiffness = SpringForce.STIFFNESS_LOW;
                break;
        }
        TapEffect.setMotionIntensity(damping, stiffness);
    }
}
