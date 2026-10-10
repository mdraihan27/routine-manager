package io.github.mdraihan27.routinemanager.feature.settings.presentation;

import androidx.annotation.NonNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.github.mdraihan27.routinemanager.core.base.BaseViewModel;
import io.github.mdraihan27.routinemanager.core.datastore.PreferencesDataSource;
import io.github.mdraihan27.routinemanager.core.designsystem.motion.AnimationIntensity;
import io.github.mdraihan27.routinemanager.core.designsystem.motion.MotionPreferences;
import io.github.mdraihan27.routinemanager.core.threading.AppSchedulers;
import io.github.mdraihan27.routinemanager.feature.settings.data.repository.BackupRepository;
import io.reactivex.rxjava3.disposables.Disposable;

@HiltViewModel
public class SettingsViewModel extends BaseViewModel<SettingsUiState, SettingsUiEvent> {

    private final MotionPreferences motionPreferences;
    private final PreferencesDataSource preferencesDataSource;
    private final AppSchedulers appSchedulers;
    private final BackupRepository backupRepository;

    @Inject
    public SettingsViewModel(@NonNull MotionPreferences motionPreferences,
                             @NonNull PreferencesDataSource preferencesDataSource,
                             @NonNull AppSchedulers appSchedulers,
                             @NonNull BackupRepository backupRepository) {
        super(new SettingsUiState(motionPreferences.getIntensity(), 
              preferencesDataSource.isRoutineOverviewVertical(), 
              preferencesDataSource.isPersistentNotificationEnabled(),
              preferencesDataSource.isClassAlarmEnabled(),
              preferencesDataSource.getClassAlarmMinutesBefore(),
              preferencesDataSource.getThemeMode(),
              false, null));
        this.motionPreferences = motionPreferences;
        this.preferencesDataSource = preferencesDataSource;
        this.appSchedulers = appSchedulers;
        this.backupRepository = backupRepository;

        observeIntensity();
        observeRoutineOverviewVertical();
        observePersistentNotificationEnabled();
        observeClassAlarmEnabled();
        observeClassAlarmMinutes();
        observeThemeMode();
    }

    private void observeIntensity() {
        addDisposable(
                motionPreferences.observeIntensity()
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(
                                intensity -> setState(getState().getValue().copyWithIntensity(intensity)),
                                throwable -> {}
                        )
        );
    }

    private void observeRoutineOverviewVertical() {
        addDisposable(
                preferencesDataSource.observeRoutineOverviewVertical()
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(
                                vertical -> setState(getState().getValue().copyWithRoutineOverviewVertical(vertical)),
                                throwable -> {}
                        )
        );
    }

    private void observePersistentNotificationEnabled() {
        addDisposable(
                preferencesDataSource.observePersistentNotificationEnabled()
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(
                                enabled -> setState(getState().getValue().copyWithPersistentNotificationEnabled(enabled)),
                                throwable -> {}
                        )
        );
    }

    private void observeClassAlarmEnabled() {
        addDisposable(
                preferencesDataSource.observeClassAlarmEnabled()
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(
                                enabled -> setState(getState().getValue().copyWithClassAlarmEnabled(enabled)),
                                throwable -> {}
                        )
        );
    }

    private void observeClassAlarmMinutes() {
        addDisposable(
                preferencesDataSource.observeClassAlarmMinutesBefore()
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(
                                minutes -> setState(getState().getValue().copyWithClassAlarmMinutes(minutes)),
                                throwable -> {}
                        )
        );
    }

    private void observeThemeMode() {
        addDisposable(
                preferencesDataSource.observeThemeMode()
                        .subscribeOn(appSchedulers.io())
                        .observeOn(appSchedulers.main())
                        .subscribe(
                                mode -> setState(getState().getValue().copyWithThemeMode(mode)),
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
                                    () -> {},
                                    throwable -> {}
                            )
            );
        } else if (event instanceof SettingsUiEvent.SetRoutineOverviewVertical) {
            boolean vertical = ((SettingsUiEvent.SetRoutineOverviewVertical) event).isVertical();
            addDisposable(
                    preferencesDataSource.setRoutineOverviewVertical(vertical)
                            .subscribeOn(appSchedulers.io())
                            .observeOn(appSchedulers.main())
                            .subscribe(
                                    () -> {},
                                    throwable -> {}
                            )
            );
        } else if (event instanceof SettingsUiEvent.SetPersistentNotification) {
            boolean enabled = ((SettingsUiEvent.SetPersistentNotification) event).isEnabled();
            addDisposable(
                    preferencesDataSource.setPersistentNotificationEnabled(enabled)
                            .subscribeOn(appSchedulers.io())
                            .observeOn(appSchedulers.main())
                            .subscribe(
                                    () -> {},
                                    throwable -> {}
                            )
            );
        } else if (event instanceof SettingsUiEvent.ReplayOnboarding) {
            replayOnboarding();
        } else if (event instanceof SettingsUiEvent.ResetOnboarding) {
            resetOnboarding();
        } else if (event instanceof SettingsUiEvent.SetClassAlarm) {
            boolean enabled = ((SettingsUiEvent.SetClassAlarm) event).isEnabled();
            addDisposable(
                    preferencesDataSource.setClassAlarmEnabled(enabled)
                            .subscribeOn(appSchedulers.io())
                            .observeOn(appSchedulers.main())
                            .subscribe(
                                    () -> {},
                                    throwable -> {}
                            )
            );
        } else if (event instanceof SettingsUiEvent.SetClassAlarmMinutes) {
            int minutes = ((SettingsUiEvent.SetClassAlarmMinutes) event).getMinutes();
            addDisposable(
                    preferencesDataSource.setClassAlarmMinutesBefore(minutes)
                            .subscribeOn(appSchedulers.io())
                            .observeOn(appSchedulers.main())
                            .subscribe(
                                    () -> {},
                                    throwable -> {}
                            )
            );
        } else if (event instanceof SettingsUiEvent.SetThemeMode) {
            String mode = ((SettingsUiEvent.SetThemeMode) event).getMode();
            addDisposable(
                    preferencesDataSource.setThemeMode(mode)
                            .subscribeOn(appSchedulers.io())
                            .observeOn(appSchedulers.main())
                            .subscribe(
                                    () -> {},
                                    throwable -> {}
                            )
            );
        } else if (event instanceof SettingsUiEvent.ExportData) {
            SettingsUiEvent.ExportData exportEvent = (SettingsUiEvent.ExportData) event;
            addDisposable(
                    backupRepository.exportData(exportEvent.getContext(), exportEvent.getUri())
                            .subscribeOn(appSchedulers.io())
                            .observeOn(appSchedulers.main())
                            .subscribe(
                                    () -> setState(getState().getValue().copyWithMessage("Backup exported successfully!")),
                                    throwable -> setState(getState().getValue().copyWithMessage("Failed to export backup"))
                            )
            );
        } else if (event instanceof SettingsUiEvent.ImportData) {
            SettingsUiEvent.ImportData importEvent = (SettingsUiEvent.ImportData) event;
            addDisposable(
                    backupRepository.importData(importEvent.getContext(), importEvent.getUri())
                            .subscribeOn(appSchedulers.io())
                            .observeOn(appSchedulers.main())
                            .subscribe(
                                    () -> setState(getState().getValue().copyWithMessage("Backup imported successfully!")),
                                    throwable -> setState(getState().getValue().copyWithMessage("Failed to import backup"))
                            )
            );
        } else if (event instanceof SettingsUiEvent.ClearMessage) {
            setState(getState().getValue().copyWithMessage(null));
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
