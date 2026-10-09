package io.github.mdraihan27.routinemanager.core.navigation;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.github.mdraihan27.routinemanager.R;

@Singleton
public final class NavigatorImpl implements Navigator {

    @Inject
    public NavigatorImpl() {
    }

    private NavController getNavController(Fragment fragment) {
        return NavHostFragment.findNavController(fragment);
    }

    @Override
    public void navigateToCourses(@NonNull Fragment fragment) {
        getNavController(fragment).navigate(R.id.action_global_courseListFragment);
    }

    @Override
    public void navigateToAddCourse(@NonNull Fragment fragment) {
        getNavController(fragment).navigate(R.id.action_global_courseEditFragment);
    }

    @Override
    public void navigateToEditCourse(@NonNull Fragment fragment, long courseId) {
        Bundle bundle = new Bundle();
        bundle.putLong("course_id", courseId);
        getNavController(fragment).navigate(R.id.action_global_courseEditFragment, bundle);
    }

    @Override
    public void navigateToRoutineWizard(@NonNull Fragment fragment) {
        getNavController(fragment).navigate(R.id.action_global_routineWizardFragment);
    }

    @Override
    public void navigateToSettings(@NonNull Fragment fragment) {
        getNavController(fragment).navigate(R.id.action_global_settingsFragment);
    }

    @Override
    public void navigateUp(@NonNull Fragment fragment) {
        getNavController(fragment).navigateUp();
    }
}
