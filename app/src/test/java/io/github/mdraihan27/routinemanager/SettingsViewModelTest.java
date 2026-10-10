package io.github.mdraihan27.routinemanager;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;
import org.junit.Test;

import io.github.mdraihan27.routinemanager.core.datastore.PreferencesDataSource;
import io.github.mdraihan27.routinemanager.core.designsystem.motion.AnimationIntensity;
import io.github.mdraihan27.routinemanager.core.designsystem.motion.MotionPreferences;
import io.github.mdraihan27.routinemanager.core.threading.AppSchedulers;
import io.github.mdraihan27.routinemanager.feature.settings.presentation.SettingsUiEvent;
import io.github.mdraihan27.routinemanager.feature.settings.presentation.SettingsViewModel;
import io.github.mdraihan27.routinemanager.feature.settings.data.repository.BackupRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import static org.junit.Assert.assertEquals;

public class SettingsViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private static class TestSchedulers implements AppSchedulers {
        @Override
        public Scheduler io() {
            return Schedulers.trampoline();
        }

        @Override
        public Scheduler main() {
            return Schedulers.trampoline();
        }
    }

    private static class FakeMotionPreferences implements MotionPreferences {
        private AnimationIntensity current = AnimationIntensity.BALANCED;
        private final BehaviorSubject<AnimationIntensity> subject = BehaviorSubject.createDefault(AnimationIntensity.BALANCED);

        @Override
        public AnimationIntensity getIntensity() {
            return current;
        }

        @Override
        public Completable setIntensity(AnimationIntensity intensity) {
            current = intensity;
            subject.onNext(intensity);
            return Completable.complete();
        }

        @Override
        public Observable<AnimationIntensity> observeIntensity() {
            return subject;
        }

        @Override
        public float getDampingRatio() {
            return 0.75f;
        }

        @Override
        public float getStiffness() {
            return 300f;
        }
    }

    private static class FakePreferencesDataSource implements PreferencesDataSource {
        @Override
        public boolean hasCompletedInitialOnboarding() {
            return false;
        }

        @Override
        public Completable setCompletedInitialOnboarding(boolean completed) {
            return Completable.complete();
        }

        @Override
        public boolean isGuideCompleted(String guideKey) {
            return false;
        }

        @Override
        public Completable setGuideCompleted(String guideKey, boolean completed) {
            return Completable.complete();
        }

        @Override
        public Completable resetAllGuides() {
            return Completable.complete();
        }

        @Override
        public Observable<Boolean> observeInitialOnboarding() {
            return Observable.just(false);
        }

        @Override
        public boolean isRoutineOverviewVertical() {
            return true;
        }

        @Override
        public Completable setRoutineOverviewVertical(boolean vertical) {
            return Completable.complete();
        }

        @Override
        public Observable<Boolean> observeRoutineOverviewVertical() {
            return Observable.just(true);
        }

        @Override
        public boolean isPersistentNotificationEnabled() {
            return false;
        }

        @Override
        public Completable setPersistentNotificationEnabled(boolean enabled) {
            return Completable.complete();
        }

        @Override
        public Observable<Boolean> observePersistentNotificationEnabled() {
            return Observable.just(false);
        }

        @Override
        public boolean isClassAlarmEnabled() {
            return false;
        }

        @Override
        public Completable setClassAlarmEnabled(boolean enabled) {
            return Completable.complete();
        }

        @Override
        public Observable<Boolean> observeClassAlarmEnabled() {
            return Observable.just(false);
        }

        @Override
        public int getClassAlarmMinutesBefore() {
            return 15;
        }

        @Override
        public Completable setClassAlarmMinutesBefore(int minutes) {
            return Completable.complete();
        }

        @Override
        public Observable<Integer> observeClassAlarmMinutesBefore() {
            return Observable.just(15);
        }

        @Override
        public String getThemeMode() {
            return "Auto";
        }

        @Override
        public Completable setThemeMode(String mode) {
            return Completable.complete();
        }

        @Override
        public Observable<String> observeThemeMode() {
            return Observable.just("Auto");
        }
    }

    @Test
    public void testInitialStateAndIntensityChange() {
        FakeMotionPreferences motionPreferences = new FakeMotionPreferences();
        FakePreferencesDataSource preferencesDataSource = new FakePreferencesDataSource();
        TestSchedulers schedulers = new TestSchedulers();
        BackupRepository mockBackupRepository = null;

        SettingsViewModel viewModel = new SettingsViewModel(motionPreferences, preferencesDataSource, schedulers, mockBackupRepository);

        assertEquals(AnimationIntensity.BALANCED, viewModel.getState().getValue().getIntensity());

        viewModel.onEvent(new SettingsUiEvent.SetIntensity(AnimationIntensity.REDUCED));
        assertEquals(AnimationIntensity.REDUCED, viewModel.getState().getValue().getIntensity());

        viewModel.onEvent(new SettingsUiEvent.SetIntensity(AnimationIntensity.EXPRESSIVE));
        assertEquals(AnimationIntensity.EXPRESSIVE, viewModel.getState().getValue().getIntensity());
    }
}
