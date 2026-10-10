package io.github.mdraihan27.routinemanager.feature.settings.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.widget.ArrayAdapter;
import android.widget.Toast;

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

    private ActivityResultLauncher<String> exportLauncher;
    private ActivityResultLauncher<String[]> importLauncher;

    @NonNull
    @Override
    protected FragmentSettingsBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentSettingsBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        exportLauncher = registerForActivityResult(new ActivityResultContracts.CreateDocument("application/json"), uri -> {
            if (uri != null) {
                viewModel.onEvent(new SettingsUiEvent.ExportData(requireContext(), uri));
            }
        });

        importLauncher = registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
            if (uri != null) {
                viewModel.onEvent(new SettingsUiEvent.ImportData(requireContext(), uri));
            }
        });

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
        // Setup Animation Intensity Dropdown
        String[] intensities = {
            getString(R.string.setting_intensity_reduced),
            getString(R.string.setting_intensity_balanced),
            getString(R.string.setting_intensity_expressive)
        };
        ArrayAdapter<String> intensityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, intensities);
        binding.dropdownAnimationIntensity.setAdapter(intensityAdapter);
        binding.dropdownAnimationIntensity.setOnItemClickListener((parent, view, position, id) -> {
            AnimationIntensity intensity;
            if (position == 0) intensity = AnimationIntensity.REDUCED;
            else if (position == 1) intensity = AnimationIntensity.BALANCED;
            else intensity = AnimationIntensity.EXPRESSIVE;
            viewModel.onEvent(new SettingsUiEvent.SetIntensity(intensity));
        });

        // Setup Layout Dropdown
        String[] layouts = {
            getString(R.string.setting_layout_vertical),
            getString(R.string.setting_layout_horizontal)
        };
        ArrayAdapter<String> layoutAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, layouts);
        binding.dropdownRoutineLayout.setAdapter(layoutAdapter);
        binding.dropdownRoutineLayout.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.onEvent(new SettingsUiEvent.SetRoutineOverviewVertical(position == 0));
        });

        // Setup Theme Dropdown
        String[] themes = {"Auto", "Light", "Dark"};
        ArrayAdapter<String> themeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, themes);
        binding.dropdownTheme.setAdapter(themeAdapter);
        binding.dropdownTheme.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.onEvent(new SettingsUiEvent.SetThemeMode(themes[position]));
        });

        binding.switchPersistentNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.onEvent(new SettingsUiEvent.SetPersistentNotification(isChecked));
        });

        binding.switchClassAlarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.onEvent(new SettingsUiEvent.SetClassAlarm(isChecked));
        });

        binding.inputAlarmMinutes.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    int minutes = Integer.parseInt(binding.inputAlarmMinutes.getText().toString());
                    viewModel.onEvent(new SettingsUiEvent.SetClassAlarmMinutes(minutes));
                } catch (NumberFormatException ignored) {}
            }
        });

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

        binding.btnExportBackup.setOnClickListener(v -> 
                exportLauncher.launch("RoutineManager_Backup.json"));

        binding.btnImportBackup.setOnClickListener(v -> 
                importLauncher.launch(new String[]{"application/json"}));

        binding.tvGithubLink.setOnClickListener(v -> {
            android.content.Intent browserIntent = new android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://github.com/mdraihan27"));
            startActivity(browserIntent);
        });
    }

    private void renderState(@NonNull SettingsUiState state) {
        FragmentSettingsBinding binding = getBinding();
        AnimationIntensity intensity = state.getIntensity();
        String intensityText;
        if (intensity == AnimationIntensity.REDUCED) intensityText = getString(R.string.setting_intensity_reduced);
        else if (intensity == AnimationIntensity.BALANCED) intensityText = getString(R.string.setting_intensity_balanced);
        else intensityText = getString(R.string.setting_intensity_expressive);
        
        if (!binding.dropdownAnimationIntensity.getText().toString().equals(intensityText)) {
            binding.dropdownAnimationIntensity.setText(intensityText, false);
        }

        boolean isVertical = state.isRoutineOverviewVertical();
        String layoutText = isVertical ? getString(R.string.setting_layout_vertical) : getString(R.string.setting_layout_horizontal);
        
        if (!binding.dropdownRoutineLayout.getText().toString().equals(layoutText)) {
            binding.dropdownRoutineLayout.setText(layoutText, false);
        }

        String themeText = state.getThemeMode();
        if (!binding.dropdownTheme.getText().toString().equals(themeText)) {
            binding.dropdownTheme.setText(themeText, false);
        }

        if (binding.switchPersistentNotification.isChecked() != state.isPersistentNotificationEnabled()) {
            binding.switchPersistentNotification.setChecked(state.isPersistentNotificationEnabled());
        }

        if (binding.switchClassAlarm.isChecked() != state.isClassAlarmEnabled()) {
            binding.switchClassAlarm.setChecked(state.isClassAlarmEnabled());
        }

        if (!binding.inputAlarmMinutes.hasFocus()) {
            binding.inputAlarmMinutes.setText(String.valueOf(state.getClassAlarmMinutes()));
        }

        if (state.getMessage() != null) {
            Toast.makeText(requireContext(), state.getMessage(), Toast.LENGTH_SHORT).show();
            viewModel.onEvent(new SettingsUiEvent.ClearMessage());
        }
    }
}
