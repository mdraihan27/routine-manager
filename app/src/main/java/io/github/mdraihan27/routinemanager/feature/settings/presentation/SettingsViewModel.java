package io.github.mdraihan27.routinemanager.feature.settings.presentation;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.github.mdraihan27.routinemanager.core.base.BaseViewModel;
import io.github.mdraihan27.routinemanager.core.datastore.PreferencesDataSource;
import io.github.mdraihan27.routinemanager.core.designsystem.motion.AnimationIntensity;
import io.github.mdraihan27.routinemanager.core.designsystem.motion.MotionPreferences;
import io.github.mdraihan27.routinemanager.core.threading.AppSchedulers;

@HiltViewModel
public class SettingsViewModel extends BaseViewModel<SettingsUiState, SettingsUiEvent> {

    private final MotionPreferences motionPreferences;
    private final PreferencesDataSource preferencesDataSource;
    private final AppSchedulers appSchedulers;

    @Inject
    public SettingsViewModel(@NonNull MotionPreferences motionPreferences,
                             @NonNull PreferencesDataSource preferencesDataSource,
                             @NonNull AppSchedulers appSchedulers) {
        super(new SettingsUiState(motionPreferences.getIntensity(), false, null));
        this.motionPreferences = motionPreferences;
        this.preferencesDataSource = preferencesDataSource;
        this.appSchedulers = appSchedulers;

        observeIntensity();
    }

    private void observeIntensity() {
        addDisposable(
                motionPreferences.observeIntensity()
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(
                                intensity -> setState(new SettingsUiState(intensity, false, null)),
                                throwable -> {}
                        )
        );
    }

    @Override
    public void onEvent(@NonNull SettingsUiEvent event) {
        if (event instanceof SettingsUiEvent.SetIntensity) {
            AnimationIntensity intensity = ((SettingsUiEvent.SetIntensity) event).getIntensity();
            addDisposable(
                    motionPreferences.setIntensity(intensity)
                            .subscribeOn(appSchedulers.io())
                            .observeOn(appSchedulers.main())
                            .subscribe(
                                    () -> setState(new SettingsUiState(intensity, false, null)),
                                    throwable -> {}
                            )
            );
        } else if (event instanceof SettingsUiEvent.ReplayOnboarding) {
            replayOnboarding();
        } else if (event instanceof SettingsUiEvent.ResetOnboarding) {
            resetOnboarding();
        }
    }

    private void replayOnboarding() {
        addDisposable(
                preferencesDataSource.setCompletedInitialOnboarding(false)
                        .andThen(preferencesDataSource.resetAllGuides())
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(
                                () -> {},
                                throwable -> {}
                        )
        );
    }

    private void resetOnboarding() {
        addDisposable(
                preferencesDataSource.resetAllGuides()
                        .andThen(preferencesDataSource.setCompletedInitialOnboarding(false))
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(
                                () -> {},
                                throwable -> {}
                        )
        );
    }
}
