package io.github.mdraihan27.routinemanager.feature.settings.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.core.base.BaseFragment;
import io.github.mdraihan27.routinemanager.core.designsystem.motion.AnimationIntensity;
import io.github.mdraihan27.routinemanager.core.navigation.Navigator;
import io.github.mdraihan27.routinemanager.databinding.FragmentSettingsBinding;

@AndroidEntryPoint
public class SettingsFragment extends BaseFragment<FragmentSettingsBinding> {

    @Inject
    Navigator navigator;

    private SettingsViewModel viewModel;

    @NonNull
    @Override
    protected FragmentSettingsBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentSettingsBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        setupTopBar();
        setupClickListeners();

        viewModel.getState().observe(getViewLifecycleOwner(), this::renderState);
    }

    private void setupTopBar() {
        FragmentSettingsBinding binding = getBinding();
        binding.settingsTopBar.setTitle(R.string.title_settings);
    }

    private void setupClickListeners() {
        FragmentSettingsBinding binding = getBinding();

        binding.btnIntensityReduced.setOnClickListener(v ->
                viewModel.onEvent(new SettingsUiEvent.SetIntensity(AnimationIntensity.REDUCED)));

        binding.btnIntensityBalanced.setOnClickListener(v ->
                viewModel.onEvent(new SettingsUiEvent.SetIntensity(AnimationIntensity.BALANCED)));

        binding.btnIntensityExpressive.setOnClickListener(v ->
                viewModel.onEvent(new SettingsUiEvent.SetIntensity(AnimationIntensity.EXPRESSIVE)));

        binding.btnManageCourses.setOnClickListener(v ->
                navigator.navigateToCourses(this));

        binding.btnEditRoutine.setOnClickListener(v ->
                navigator.navigateToRoutineWizard(this));

        binding.btnReplayOnboarding.setOnClickListener(v ->
                viewModel.onEvent(new SettingsUiEvent.ReplayOnboarding()));

        binding.btnResetOnboarding.setOnClickListener(v ->
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.setting_reset_onboarding)
                        .setMessage(R.string.confirm_reset_onboarding)
                        .setPositiveButton(R.string.action_reset, (dialog, which) ->
                                viewModel.onEvent(new SettingsUiEvent.ResetOnboarding()))
                        .setNegativeButton(R.string.action_cancel, (dialog, which) ->
                                dialog.dismiss())
                        .show());
    }

    private void renderState(@NonNull SettingsUiState state) {
        FragmentSettingsBinding binding = getBinding();
        AnimationIntensity intensity = state.getIntensity();

        binding.btnIntensityReduced.setAlpha(intensity == AnimationIntensity.REDUCED ? 1.0f : 0.45f);
        binding.btnIntensityBalanced.setAlpha(intensity == AnimationIntensity.BALANCED ? 1.0f : 0.45f);
        binding.btnIntensityExpressive.setAlpha(intensity == AnimationIntensity.EXPRESSIVE ? 1.0f : 0.45f);
    }
}
