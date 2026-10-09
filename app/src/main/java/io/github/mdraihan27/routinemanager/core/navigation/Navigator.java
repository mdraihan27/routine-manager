package io.github.mdraihan27.routinemanager.core.navigation;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public interface Navigator {

    void navigateToCourses(@NonNull Fragment fragment);

    void navigateToAddCourse(@NonNull Fragment fragment);

    void navigateToEditCourse(@NonNull Fragment fragment, long courseId);

    void navigateToRoutineWizard(@NonNull Fragment fragment);

    void navigateToSettings(@NonNull Fragment fragment);

    void navigateUp(@NonNull Fragment fragment);
}
