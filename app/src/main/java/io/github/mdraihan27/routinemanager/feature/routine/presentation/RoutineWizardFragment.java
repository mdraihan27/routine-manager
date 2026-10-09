package io.github.mdraihan27.routinemanager.feature.routine.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.core.base.BaseFragment;
import io.github.mdraihan27.routinemanager.databinding.FragmentRoutineWizardBinding;

@AndroidEntryPoint
public class RoutineWizardFragment extends BaseFragment<FragmentRoutineWizardBinding> {

    @NonNull
    @Override
    protected FragmentRoutineWizardBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentRoutineWizardBinding.inflate(inflater, container, false);
    }
}
