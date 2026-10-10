package io.github.mdraihan27.routinemanager.core.datastore;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

@Singleton
public final class PreferencesDataSourceImpl implements PreferencesDataSource {

    private static final String PREFS_NAME = "app_preferences";
    private static final String KEY_INITIAL_ONBOARDING = "initial_onboarding_completed";
    private static final String PREFIX_GUIDE = "guide_";

    private static final String KEY_ROUTINE_OVERVIEW_VERTICAL = "routine_overview_vertical";
    private static final String KEY_PERSISTENT_NOTIFICATION = "persistent_notification_enabled";

    private final SharedPreferences preferences;
    private final BehaviorSubject<Boolean> onboardingSubject;
    private final BehaviorSubject<Boolean> verticalOverviewSubject;
    private final BehaviorSubject<Boolean> persistentNotificationSubject;

    @Inject
    public PreferencesDataSourceImpl(@NonNull @ApplicationContext Context context) {
        this.preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean initial = preferences.getBoolean(KEY_INITIAL_ONBOARDING, false);
        this.onboardingSubject = BehaviorSubject.createDefault(initial);
        boolean isVertical = preferences.getBoolean(KEY_ROUTINE_OVERVIEW_VERTICAL, true);
        this.verticalOverviewSubject = BehaviorSubject.createDefault(isVertical);
        boolean isPersistentNotif = preferences.getBoolean(KEY_PERSISTENT_NOTIFICATION, false);
        this.persistentNotificationSubject = BehaviorSubject.createDefault(isPersistentNotif);
    }

    @Override
    public boolean hasCompletedInitialOnboarding() {
        return preferences.getBoolean(KEY_INITIAL_ONBOARDING, false);
    }

    @NonNull
    @Override
    public Completable setCompletedInitialOnboarding(boolean completed) {
        return Completable.fromAction(() -> {
            preferences.edit().putBoolean(KEY_INITIAL_ONBOARDING, completed).apply();
            onboardingSubject.onNext(completed);
        });
    }

    @Override
    public boolean isGuideCompleted(@NonNull String guideKey) {
        return preferences.getBoolean(PREFIX_GUIDE + guideKey, false);
    }

    @NonNull
    @Override
    public Completable setGuideCompleted(@NonNull String guideKey, boolean completed) {
        return Completable.fromAction(() ->
                preferences.edit().putBoolean(PREFIX_GUIDE + guideKey, completed).apply()
        );
    }

    @NonNull
    @Override
    public Completable resetAllGuides() {
        return Completable.fromAction(() -> {
            SharedPreferences.Editor editor = preferences.edit();
            for (String key : preferences.getAll().keySet()) {
                if (key.startsWith(PREFIX_GUIDE)) {
                    editor.remove(key);
                }
            }
            editor.apply();
        });
    }

    @NonNull
    @Override
    public Observable<Boolean> observeInitialOnboarding() {
        return onboardingSubject.hide();
    }

    @Override
    public boolean isRoutineOverviewVertical() {
        return preferences.getBoolean(KEY_ROUTINE_OVERVIEW_VERTICAL, true);
    }

    @NonNull
    @Override
    public Completable setRoutineOverviewVertical(boolean vertical) {
        return Completable.fromAction(() -> {
            preferences.edit().putBoolean(KEY_ROUTINE_OVERVIEW_VERTICAL, vertical).apply();
            verticalOverviewSubject.onNext(vertical);
        });
    }

    @NonNull
    @Override
    public Observable<Boolean> observeRoutineOverviewVertical() {
        return verticalOverviewSubject.hide();
    }

    @Override
    public boolean isPersistentNotificationEnabled() {
        return preferences.getBoolean(KEY_PERSISTENT_NOTIFICATION, false);
    }

    @NonNull
    @Override
    public Completable setPersistentNotificationEnabled(boolean enabled) {
        return Completable.fromAction(() -> {
            preferences.edit().putBoolean(KEY_PERSISTENT_NOTIFICATION, enabled).apply();
            persistentNotificationSubject.onNext(enabled);
        });
    }

    @NonNull
    @Override
    public Observable<Boolean> observePersistentNotificationEnabled() {
        return persistentNotificationSubject.hide();
    }
}
