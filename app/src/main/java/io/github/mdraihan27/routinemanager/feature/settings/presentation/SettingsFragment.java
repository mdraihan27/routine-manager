package io.github.mdraihan27.routinemanager.feature.settings.presentation;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.core.base.BaseFragment;
import io.github.mdraihan27.routinemanager.databinding.FragmentSettingsBinding;

@AndroidEntryPoint
public class SettingsFragment extends BaseFragment<FragmentSettingsBinding> {

    @NonNull
    @Override
    protected FragmentSettingsBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentSettingsBinding.inflate(inflater, container, false);
    }
}
