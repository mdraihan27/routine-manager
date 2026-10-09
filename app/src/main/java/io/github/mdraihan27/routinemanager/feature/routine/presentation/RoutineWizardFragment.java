package io.github.mdraihan27.routinemanager.feature.routine.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.core.base.BaseFragment;
import io.github.mdraihan27.routinemanager.core.designsystem.components.AnalogClockView;
import io.github.mdraihan27.routinemanager.core.designsystem.components.AppBottomSheet;
import io.github.mdraihan27.routinemanager.core.designsystem.components.AppButton;
import io.github.mdraihan27.routinemanager.core.designsystem.components.AppCard;
import io.github.mdraihan27.routinemanager.core.designsystem.components.AppChip;
import io.github.mdraihan27.routinemanager.core.navigation.Navigator;
import io.github.mdraihan27.routinemanager.core.util.DateTimeFormatter;
import io.github.mdraihan27.routinemanager.databinding.DialogAddClassBinding;
import io.github.mdraihan27.routinemanager.databinding.FragmentRoutineWizardBinding;
import io.github.mdraihan27.routinemanager.databinding.LayoutWizardStepBreakBinding;
import io.github.mdraihan27.routinemanager.databinding.LayoutWizardStepHolidaysBinding;
import io.github.mdraihan27.routinemanager.databinding.LayoutWizardStepScheduleBinding;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.presentation.CourseColorResolver;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClass;
import io.github.mdraihan27.routinemanager.feature.routine.domain.usecase.ValidateClassTimeUseCase;

@AndroidEntryPoint
public class RoutineWizardFragment extends BaseFragment<FragmentRoutineWizardBinding> {

    private static final List<DayOfWeek> WEEK_DAYS = Arrays.asList(
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY,
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
    );

    @Inject
    Navigator navigator;

    @Inject
    CourseColorResolver colorResolver;

    private RoutineWizardViewModel viewModel;
    private WizardClassAdapter classAdapter;

    private LayoutWizardStepHolidaysBinding stepHolidaysBinding;
    private LayoutWizardStepBreakBinding stepBreakBinding;
    private LayoutWizardStepScheduleBinding stepScheduleBinding;

    private boolean isBreakStartSelected = true;
    private int breakStartH = 13;
    private int breakStartM = 0;
    private int breakEndH = 14;
    private int breakEndM = 0;

    @NonNull
    @Override
    protected FragmentRoutineWizardBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentRoutineWizardBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(RoutineWizardViewModel.class);

        getBinding().wizardTopBar.setTitle(R.string.title_routine_wizard);

        stepHolidaysBinding = LayoutWizardStepHolidaysBinding.inflate(getLayoutInflater(), getBinding().wizardStepContainer, false);
        stepBreakBinding = LayoutWizardStepBreakBinding.inflate(getLayoutInflater(), getBinding().wizardStepContainer, false);
        stepScheduleBinding = LayoutWizardStepScheduleBinding.inflate(getLayoutInflater(), getBinding().wizardStepContainer, false);

        setupStepHolidays();
        setupStepBreak();
        setupStepSchedule();

        viewModel.getState().observe(getViewLifecycleOwner(), state -> {
            if (state.isSaved()) {
                navigator.navigateUp(this);
                return;
            }
            renderState(state);
        });
    }

    private void renderState(RoutineWizardUiState state) {
        getBinding().wizardStepContainer.removeAllViews();
        switch (state.getCurrentStep()) {
            case 0:
                getBinding().wizardStepContainer.addView(stepHolidaysBinding.getRoot());
                updateHolidaysView(state);
                break;
            case 1:
                getBinding().wizardStepContainer.addView(stepBreakBinding.getRoot());
                updateBreakView(state);
                break;
            case 2:
            default:
                getBinding().wizardStepContainer.addView(stepScheduleBinding.getRoot());
                updateScheduleView(state);
                break;
        }
    }

    private void setupStepHolidays() {
        stepHolidaysBinding.btnContinueFromHolidays.setOnClickListener(v ->
                viewModel.onEvent(new RoutineWizardUiEvent.SetStep(1))
        );
    }

    private void updateHolidaysView(RoutineWizardUiState state) {
        stepHolidaysBinding.holidaysContainer.removeAllViews();
        int margin = (int) getResources().getDimension(R.dimen.spacing_tiny);

        for (DayOfWeek day : WEEK_DAYS) {
            AppCard card = new AppCard(requireContext());
            LinearLayout layout = new LinearLayout(requireContext());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            int pad = (int) getResources().getDimension(R.dimen.spacing_medium);
            layout.setPadding(pad, pad, pad, pad);

            TextView tv = new TextView(requireContext());
            tv.setText(formatDayName(day));
            tv.setTextAppearance(R.style.TextAppearance_App_Primary_Body);

            boolean isHoliday = state.getHolidays().contains(day);
            if (isHoliday) {
                card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.surface_subtle));
            } else {
                card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.surface_card));
            }

            layout.addView(tv);
            card.addView(layout);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.bottomMargin = margin;
            stepHolidaysBinding.holidaysContainer.addView(card, lp);

            card.setOnClickListener(v ->
                    viewModel.onEvent(new RoutineWizardUiEvent.ToggleHoliday(day))
            );
        }
    }

    private void setupStepBreak() {
        stepBreakBinding.breakClock.setOnTimeSelectedListener(new AnalogClockView.OnTimeSelectedListener() {
            @Override
            public void onTimeChanged(int h24, int m) {
                if (isBreakStartSelected) {
                    breakStartH = h24;
                    breakStartM = m;
                } else {
                    breakEndH = h24;
                    breakEndM = m;
                }
                updateBreakDisplay();
                updateAmPmToggleUI();
            }

            @Override
            public void onTimeSelectionComplete(AnalogClockView.Mode modeCompleted) {
                if (modeCompleted == AnalogClockView.Mode.HOUR) {
                    stepBreakBinding.breakClock.setMode(AnalogClockView.Mode.MINUTE);
                }
            }
        });

        stepBreakBinding.btnSelectBreakStart.setOnClickListener(v -> {
            isBreakStartSelected = true;
            stepBreakBinding.breakClock.setTime(breakStartH, breakStartM);
            stepBreakBinding.breakClock.setMode(AnalogClockView.Mode.HOUR);
            updateBreakDisplay();
            updateToggleUI();
            updateAmPmToggleUI();
        });

        stepBreakBinding.btnSelectBreakEnd.setOnClickListener(v -> {
            isBreakStartSelected = false;
            stepBreakBinding.breakClock.setTime(breakEndH, breakEndM);
            stepBreakBinding.breakClock.setMode(AnalogClockView.Mode.HOUR);
            updateBreakDisplay();
            updateToggleUI();
            updateAmPmToggleUI();
        });

        stepBreakBinding.tvBreakStartTime.setOnClickListener(v -> stepBreakBinding.btnSelectBreakStart.performClick());
        stepBreakBinding.tvBreakEndTime.setOnClickListener(v -> stepBreakBinding.btnSelectBreakEnd.performClick());

        stepBreakBinding.btnSelectAm.setOnClickListener(v -> {
            stepBreakBinding.breakClock.setIsAm(true);
            updateAmPmToggleUI();
        });

        stepBreakBinding.btnSelectPm.setOnClickListener(v -> {
            stepBreakBinding.breakClock.setIsAm(false);
            updateAmPmToggleUI();
        });

        stepBreakBinding.btnSkipBreak.setOnClickListener(v -> {
            viewModel.onEvent(new RoutineWizardUiEvent.SetBreakTime(false, 0, 0, 0, 0));
            viewModel.onEvent(new RoutineWizardUiEvent.SetStep(2));
        });

        stepBreakBinding.btnContinueFromBreak.setOnClickListener(v -> {
            viewModel.onEvent(new RoutineWizardUiEvent.SetBreakTime(true, breakStartH, breakStartM, breakEndH, breakEndM));
            viewModel.onEvent(new RoutineWizardUiEvent.SetStep(2));
        });
    }

    private void updateBreakView(RoutineWizardUiState state) {
        breakStartH = state.getBreakStartHour();
        breakStartM = state.getBreakStartMinute();
        breakEndH = state.getBreakEndHour();
        breakEndM = state.getBreakEndMinute();
        stepBreakBinding.breakClock.setTime(
                isBreakStartSelected ? breakStartH : breakEndH,
                isBreakStartSelected ? breakStartM : breakEndM
        );
        updateBreakDisplay();
        updateToggleUI();
        updateAmPmToggleUI();
    }

    private void updateToggleUI() {
        if (isBreakStartSelected) {
            stepBreakBinding.btnSelectBreakStart.setBackgroundResource(R.drawable.bg_toggle_pill);
            stepBreakBinding.btnSelectBreakStart.setTextColor(ContextCompat.getColor(requireContext(), R.color.background));
            stepBreakBinding.btnSelectBreakEnd.setBackground(null);
            stepBreakBinding.btnSelectBreakEnd.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_surface));
        } else {
            stepBreakBinding.btnSelectBreakEnd.setBackgroundResource(R.drawable.bg_toggle_pill);
            stepBreakBinding.btnSelectBreakEnd.setTextColor(ContextCompat.getColor(requireContext(), R.color.background));
            stepBreakBinding.btnSelectBreakStart.setBackground(null);
            stepBreakBinding.btnSelectBreakStart.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_surface));
        }
    }

    private void updateAmPmToggleUI() {
        if (stepBreakBinding.breakClock.isAm()) {
            stepBreakBinding.btnSelectAm.setBackgroundResource(R.drawable.bg_toggle_pill);
            stepBreakBinding.btnSelectAm.setTextColor(ContextCompat.getColor(requireContext(), R.color.background));
            stepBreakBinding.btnSelectPm.setBackground(null);
            stepBreakBinding.btnSelectPm.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_surface));
        } else {
            stepBreakBinding.btnSelectPm.setBackgroundResource(R.drawable.bg_toggle_pill);
            stepBreakBinding.btnSelectPm.setTextColor(ContextCompat.getColor(requireContext(), R.color.background));
            stepBreakBinding.btnSelectAm.setBackground(null);
            stepBreakBinding.btnSelectAm.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_surface));
        }
    }

    private void updateBreakDisplay() {
        stepBreakBinding.tvBreakStartTime.setText(
                DateTimeFormatter.formatTime12Hour(breakStartH, breakStartM)
        );
        stepBreakBinding.tvBreakEndTime.setText(
                DateTimeFormatter.formatTime12Hour(breakEndH, breakEndM)
        );
        
        if (isBreakStartSelected) {
            stepBreakBinding.tvBreakStartTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_surface));
            stepBreakBinding.tvBreakEndTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted));
        } else {
            stepBreakBinding.tvBreakStartTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted));
            stepBreakBinding.tvBreakEndTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_surface));
        }
    }

    private void setupStepSchedule() {
        classAdapter = new WizardClassAdapter(colorResolver, item ->
                viewModel.onEvent(new RoutineWizardUiEvent.DeleteClass(item.getWeeklyClass().getId()))
        );

        stepScheduleBinding.rvDayClasses.setLayoutManager(new LinearLayoutManager(requireContext()));
        stepScheduleBinding.rvDayClasses.setAdapter(classAdapter);

        stepScheduleBinding.btnAddClassToDay.setOnClickListener(v -> showAddClassDialog());

        stepScheduleBinding.btnDoneWizard.setOnClickListener(v ->
                viewModel.onEvent(new RoutineWizardUiEvent.FinishWizard())
        );
    }

    private void updateScheduleView(RoutineWizardUiState state) {
        stepScheduleBinding.dayChipsContainer.removeAllViews();
        int margin = (int) getResources().getDimension(R.dimen.spacing_tiny);

        for (DayOfWeek day : WEEK_DAYS) {
            AppChip chip = new AppChip(requireContext());
            TextView tv = new TextView(requireContext());
            int pad = (int) getResources().getDimension(R.dimen.spacing_small);
            tv.setPadding(pad, pad, pad, pad);
            tv.setText(formatShortDayName(day));
            tv.setTextAppearance(R.style.TextAppearance_App_Primary_Label);

            boolean isSelected = day == state.getSelectedDay();
            if (isSelected) {
                chip.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.button_background));
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_button));
            } else {
                chip.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.surface_muted));
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_surface));
            }

            chip.addView(tv);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.rightMargin = margin;
            stepScheduleBinding.dayChipsContainer.addView(chip, lp);

            chip.setOnClickListener(v -> viewModel.onEvent(new RoutineWizardUiEvent.SelectDay(day)));
        }

        boolean isHoliday = state.getHolidays().contains(state.getSelectedDay());
        stepScheduleBinding.tvHolidayNotice.setVisibility(isHoliday ? View.VISIBLE : View.GONE);
        classAdapter.submitList(state.getDayClasses());
    }

    private void showAddClassDialog() {
        RoutineWizardUiState state = viewModel.getState().getValue();
        if (state == null || state.getAllCourses().isEmpty()) {
            Toast.makeText(requireContext(), R.string.onboarding_course_first, Toast.LENGTH_SHORT).show();
            return;
        }

        DialogAddClassBinding dBinding = DialogAddClassBinding.inflate(getLayoutInflater());
        BottomSheetDialog dialog = AppBottomSheet.create(requireContext(), dBinding.getRoot());

        final List<Course> courses = state.getAllCourses();
        final long[] selectedCourseId = {courses.get(0).getId()};
        final int[] startH = {9};
        final int[] startM = {0};
        final int[] endH = {10};
        final int[] endM = {0};
        final boolean[] isPickingStart = {true};

        setupDialogCourses(dBinding, courses, selectedCourseId);

        dBinding.dialogClassClock.setTime(startH[0], startM[0]);
        updateDialogTimePreview(dBinding, startH[0], startM[0], endH[0], endM[0]);

        dBinding.dialogClassClock.setOnTimeSelectedListener(new AnalogClockView.OnTimeSelectedListener() {
            @Override
            public void onTimeChanged(int h24, int m) {
                if (isPickingStart[0]) {
                    startH[0] = h24;
                    startM[0] = m;
                } else {
                    endH[0] = h24;
                    endM[0] = m;
                }
                updateDialogTimePreview(dBinding, startH[0], startM[0], endH[0], endM[0]);
            }

            @Override
            public void onTimeSelectionComplete(AnalogClockView.Mode modeCompleted) {
                if (modeCompleted == AnalogClockView.Mode.HOUR) {
                    dBinding.dialogClassClock.setMode(AnalogClockView.Mode.MINUTE);
                }
            }
        });

        dBinding.btnTabStartTime.setOnClickListener(v -> {
            isPickingStart[0] = true;
            dBinding.dialogClassClock.setTime(startH[0], startM[0]);
        });

        dBinding.btnTabEndTime.setOnClickListener(v -> {
            isPickingStart[0] = false;
            dBinding.dialogClassClock.setTime(endH[0], endM[0]);
        });

        dBinding.chipClockHour.setOnClickListener(v ->
                dBinding.dialogClassClock.setMode(AnalogClockView.Mode.HOUR)
        );

        dBinding.chipClockMinute.setOnClickListener(v ->
                dBinding.dialogClassClock.setMode(AnalogClockView.Mode.MINUTE)
        );

        dBinding.chipClockAm.setOnClickListener(v ->
                dBinding.dialogClassClock.setIsAm(true)
        );

        dBinding.chipClockPm.setOnClickListener(v ->
                dBinding.dialogClassClock.setIsAm(false)
        );

        dBinding.btnCancelAddClass.setOnClickListener(v -> dialog.dismiss());

        dBinding.btnConfirmAddClass.setOnClickListener(v -> {
            ValidateClassTimeUseCase.Result result = viewModel.validateClass(
                    startH[0], startM[0], endH[0], endM[0], -1L
            );

            if (result == ValidateClassTimeUseCase.Result.INVALID_END_BEFORE_START) {
                dBinding.tvClassTimeError.setText(R.string.error_end_time_before_start);
                dBinding.tvClassTimeError.setVisibility(View.VISIBLE);
                return;
            } else if (result == ValidateClassTimeUseCase.Result.OVERLAPS_EXISTING_CLASS) {
                dBinding.tvClassTimeError.setText(R.string.error_class_conflict);
                dBinding.tvClassTimeError.setVisibility(View.VISIBLE);
                return;
            } else if (result == ValidateClassTimeUseCase.Result.OVERLAPS_BREAK) {
                dBinding.tvClassTimeError.setText(R.string.error_break_conflict);
                dBinding.tvClassTimeError.setVisibility(View.VISIBLE);
                return;
            }

            WeeklyClass wc = new WeeklyClass(
                    0L,
                    selectedCourseId[0],
                    state.getSelectedDay(),
                    startH[0],
                    startM[0],
                    endH[0],
                    endM[0]
            );

            viewModel.onEvent(new RoutineWizardUiEvent.AddClass(wc));
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupDialogCourses(DialogAddClassBinding dBinding,
                                    List<Course> courses,
                                    long[] selectedCourseId) {
        dBinding.dialogCourseChipsContainer.removeAllViews();
        int margin = (int) getResources().getDimension(R.dimen.spacing_tiny);

        List<AppChip> chipViews = new ArrayList<>();
        List<TextView> textViews = new ArrayList<>();

        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            AppChip chip = new AppChip(requireContext());
            TextView tv = new TextView(requireContext());
            int pad = (int) getResources().getDimension(R.dimen.spacing_small);
            tv.setPadding(pad, pad, pad, pad);
            tv.setText(c.getCode());
            tv.setTextAppearance(R.style.TextAppearance_App_Primary_Label);

            boolean isFirst = i == 0;
            if (isFirst) {
                chip.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.button_background));
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.on_button));
                dBinding.tvSelectedCourseName.setText(c.getName() + " (" + c.getTeacherName() + ")");
            } else {
                chip.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.surface_muted));
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_surface));
            }

            chip.addView(tv);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.rightMargin = margin;
            dBinding.dialogCourseChipsContainer.addView(chip, lp);

            chipViews.add(chip);
            textViews.add(tv);

            final int idx = i;
            chip.setOnClickListener(v -> {
                selectedCourseId[0] = c.getId();
                dBinding.tvSelectedCourseName.setText(c.getName() + " (" + c.getTeacherName() + ")");
                for (int j = 0; j < chipViews.size(); j++) {
                    if (j == idx) {
                        chipViews.get(j).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.button_background));
                        textViews.get(j).setTextColor(ContextCompat.getColor(requireContext(), R.color.on_button));
                    } else {
                        chipViews.get(j).setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.surface_muted));
                        textViews.get(j).setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_surface));
                    }
                }
            });
        }
    }

    private void updateDialogTimePreview(DialogAddClassBinding dBinding, int sh, int sm, int eh, int em) {
        dBinding.tvClassTimePreview.setText(DateTimeFormatter.formatTimeRange(sh, sm, eh, em));
        dBinding.tvClassTimeError.setVisibility(View.GONE);
    }

    private String formatDayName(DayOfWeek day) {
        switch (day) {
            case SATURDAY:
                return getString(R.string.day_saturday);
            case SUNDAY:
                return getString(R.string.day_sunday);
            case MONDAY:
                return getString(R.string.day_monday);
            case TUESDAY:
                return getString(R.string.day_tuesday);
            case WEDNESDAY:
                return getString(R.string.day_wednesday);
            case THURSDAY:
                return getString(R.string.day_thursday);
            case FRIDAY:
            default:
                return getString(R.string.day_friday);
        }
    }

    private String formatShortDayName(DayOfWeek day) {
        switch (day) {
            case SATURDAY:
                return getString(R.string.day_short_sat);
            case SUNDAY:
                return getString(R.string.day_short_sun);
            case MONDAY:
                return getString(R.string.day_short_mon);
            case TUESDAY:
                return getString(R.string.day_short_tue);
            case WEDNESDAY:
                return getString(R.string.day_short_wed);
            case THURSDAY:
                return getString(R.string.day_short_thu);
            case FRIDAY:
            default:
                return getString(R.string.day_short_fri);
        }
    }
}
