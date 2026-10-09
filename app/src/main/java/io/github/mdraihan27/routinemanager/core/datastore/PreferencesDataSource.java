package io.github.mdraihan27.routinemanager.core.datastore;

import androidx.annotation.NonNull;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public interface PreferencesDataSource {

    boolean hasCompletedInitialOnboarding();

    @NonNull
    Completable setCompletedInitialOnboarding(boolean completed);

    boolean isGuideCompleted(@NonNull String guideKey);

    @NonNull
    Completable setGuideCompleted(@NonNull String guideKey, boolean completed);

    @NonNull
    Completable resetAllGuides();

    @NonNull
    Observable<Boolean> observeInitialOnboarding();
}
