package io.github.mdraihan27.routinemanager.feature.home.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.core.base.BaseFragment;
import io.github.mdraihan27.routinemanager.core.designsystem.components.AnalogClockView;
import io.github.mdraihan27.routinemanager.core.navigation.Navigator;
import io.github.mdraihan27.routinemanager.databinding.DialogRescheduleBinding;
import io.github.mdraihan27.routinemanager.databinding.FragmentHomeBinding;
import io.github.mdraihan27.routinemanager.feature.course.presentation.CourseColorResolver;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.ClassStatus;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailyClassItem;

@AndroidEntryPoint
public class HomeFragment extends BaseFragment<FragmentHomeBinding> {

    @Inject
    Navigator navigator;

    @Inject
    CourseColorResolver colorResolver;

    private HomeViewModel viewModel;
    
    private final BroadcastReceiver rescheduleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long classId = intent.getLongExtra("EXTRA_CLASS_ID", -1);
            if (classId != -1) {
                HomeUiState state = viewModel.getState().getValue();
                if (state != null && state.getClasses() != null) {
                    for (DailyClassItem item : state.getClasses()) {
                        if (item.getWeeklyClassId() == classId) {
                            showRescheduleDialog(item);
                            break;
                        }
                    }
                }
            }
        }
    };

    @NonNull
    @Override
    protected FragmentHomeBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupTopBar();
        setupGestureHandling();
        setupActionButtons();

        viewModel.getState().observe(getViewLifecycleOwner(), this::renderState);
        
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(rescheduleReceiver, new IntentFilter("io.github.mdraihan27.routinemanager.SHOW_RESCHEDULE"));
    }

    @Override
    public void onDestroyView() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(rescheduleReceiver);
        super.onDestroyView();
    }

    private void setupTopBar() {
        FragmentHomeBinding binding = getBinding();

        binding.btnHomeViewCourses.setOnClickListener(v ->
                navigator.navigateToCourses(this));

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault());
        binding.tvHomeDateSubtitle.setText(today.format(formatter));

        binding.btnDismissOnboarding.setImageResource(R.drawable.ic_close);
        binding.btnDismissGestureTip.setImageResource(R.drawable.ic_close);
    }

    private void setupGestureHandling() {
        FragmentHomeBinding binding = getBinding();
        CardGestureDetector.attach(binding.cardDailyClass, new CardGestureDetector.OnCardGestureListener() {
            @Override
            public void onSwipeRight() {
                viewModel.onEvent(new HomeUiEvent.PreviousClass());
            }

            @Override
            public void onSwipeLeft() {
                viewModel.onEvent(new HomeUiEvent.NextClass());
            }

            @Override
            public void onSwipeUp() {
                HomeUiState state = viewModel.getState().getValue();
                if (state != null && state.getCurrentClass() != null) {
                    showRescheduleDialog(state.getCurrentClass());
                }
            }

            @Override
            public void onSwipeDown() {
                viewModel.onEvent(new HomeUiEvent.CancelCurrentClass());
            }

            @Override
            public void onClick() {
            }

            @Override
            public boolean canSwipeRight() {
                HomeUiState state = viewModel.getState().getValue();
                return state != null && state.getCurrentIndex() > 0;
            }

            @Override
            public boolean canSwipeLeft() {
                HomeUiState state = viewModel.getState().getValue();
                return state != null && state.getCurrentIndex() < state.getClasses().size() - 1;
            }
        });
    }

    private void setupActionButtons() {
        FragmentHomeBinding binding = getBinding();

        binding.btnBottomSettings.setOnClickListener(v ->
                navigator.navigateToSettings(this));

        binding.btnBottomAdjustRoutine.setOnClickListener(v ->
                navigator.navigateToRoutineWizard(this));

        binding.btnDismissOnboarding.setOnClickListener(v ->
                viewModel.onEvent(new HomeUiEvent.DismissOnboarding()));

        binding.btnOnboardingAddCourse.setOnClickListener(v ->
                navigator.navigateToAddCourse(this));

        binding.btnOnboardingStart.setOnClickListener(v -> {
            HomeUiState state = viewModel.getState().getValue();
            if (state == null) return;
            if (!state.hasCourses()) {
                navigator.navigateToAddCourse(this);
            } else if (!state.hasRoutine()) {
                navigator.navigateToRoutineWizard(this);
            } else {
                viewModel.onEvent(new HomeUiEvent.DismissOnboarding());
            }
        });

        binding.btnDismissGestureTip.setOnClickListener(v ->
                viewModel.onEvent(new HomeUiEvent.DismissGestureGuide()));
    }

    private void renderState(@NonNull HomeUiState state) {
        FragmentHomeBinding binding = getBinding();

        binding.progressHome.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
        binding.cardOnboarding.setVisibility(state.isShowOnboarding() ? View.VISIBLE : View.GONE);
        binding.cardGestureTip.setVisibility(state.isShowGestureGuide() ? View.VISIBLE : View.GONE);

        if (state.hasClasses()) {
            binding.layoutClassSection.setVisibility(View.VISIBLE);
            binding.cardHoliday.setVisibility(View.GONE);
            binding.cardBreak.setVisibility(View.GONE);
            binding.cardNoClasses.setVisibility(View.GONE);

            DailyClassItem current = state.getCurrentClass();
            if (current != null) {
                bindCurrentClass(binding, current, state);
            }
            renderWeeklyOverview(binding, state);
        } else {
            binding.layoutClassSection.setVisibility(View.GONE);
            if (state.isHoliday()) {
                binding.cardHoliday.setVisibility(View.VISIBLE);
                binding.cardBreak.setVisibility(View.GONE);
                binding.cardNoClasses.setVisibility(View.GONE);
            } else if (state.isBreakActive()) {
                binding.cardHoliday.setVisibility(View.GONE);
                binding.cardBreak.setVisibility(View.VISIBLE);
                binding.cardNoClasses.setVisibility(View.GONE);
            } else if (state.hasRoutine()) {
                binding.cardHoliday.setVisibility(View.GONE);
                binding.cardBreak.setVisibility(View.GONE);
                binding.cardNoClasses.setVisibility(View.VISIBLE);
            } else {
                binding.cardHoliday.setVisibility(View.GONE);
                binding.cardBreak.setVisibility(View.GONE);
                binding.cardNoClasses.setVisibility(View.GONE);
            }
        }
    }

    private void bindCurrentClass(@NonNull FragmentHomeBinding binding,
                                  @NonNull DailyClassItem item,
                                  @NonNull HomeUiState state) {
        binding.tvClassCourseCode.setText(item.getCourseCode());
        binding.tvClassCourseName.setText(item.getCourseName());
        binding.tvClassTeacherName.setText(item.getTeacherName());

        String startTime = formatTime(item.getStartHour(), item.getStartMinute());
        String endTime = formatTime(item.getEndHour(), item.getEndMinute());
        binding.tvClassTime.setText(String.format("%s - %s", startTime, endTime));

        binding.tvClassPosition.setText(String.format(
                Locale.getDefault(),
                "%d / %d",
                state.getCurrentIndex() + 1,
                state.getClasses().size()
        ));

        ClassStatus status = item.getStatus();
        binding.btnUndoCancel.setVisibility(status == ClassStatus.CANCELLED ? View.VISIBLE : View.GONE);
        binding.btnUndoCancel.setOnClickListener(v -> 
                viewModel.onEvent(new HomeUiEvent.UndoCancel(item.getWeeklyClassId())));

        if (status == ClassStatus.CANCELLED) {
            binding.tvClassStatusLabel.setText(R.string.label_cancelled);
            binding.cardDailyClass.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.surface_muted));
        } else if (status == ClassStatus.COMPLETED) {
            binding.tvClassStatusLabel.setText(R.string.label_completed);
            binding.cardDailyClass.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.surface_muted));
        } else if (status == ClassStatus.IN_PROGRESS) {
            binding.tvClassStatusLabel.setText(R.string.label_in_progress);
            int color = colorResolver.resolve(item.getCourseColor());
            binding.cardDailyClass.setCardBackgroundColor(color);
        } else {
            binding.tvClassStatusLabel.setText(R.string.label_upcoming);
            int color = colorResolver.resolve(item.getCourseColor());
            binding.cardDailyClass.setCardBackgroundColor(color);
        }
    }

    private void renderWeeklyOverview(@NonNull FragmentHomeBinding binding, @NonNull HomeUiState state) {
        binding.layoutWeeklyOverview.removeAllViews();

        java.time.DayOfWeek currentDay = java.time.LocalDate.now().getDayOfWeek();
        java.time.DayOfWeek[] days = {
                java.time.DayOfWeek.SATURDAY,
                java.time.DayOfWeek.SUNDAY,
                java.time.DayOfWeek.MONDAY,
                java.time.DayOfWeek.TUESDAY,
                java.time.DayOfWeek.WEDNESDAY,
                java.time.DayOfWeek.THURSDAY,
                java.time.DayOfWeek.FRIDAY
        };

        java.util.Set<Integer> uniqueTimes = new java.util.TreeSet<>();
        java.util.List<java.time.DayOfWeek> activeDays = new java.util.ArrayList<>();

        for (java.time.DayOfWeek day : days) {
            List<io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse> classes = state.getWeeklyClasses().get(day);
            if (classes != null && !classes.isEmpty()) {
                activeDays.add(day);
                for (io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse c : classes) {
                    uniqueTimes.add(c.getWeeklyClass().getStartHour() * 60 + c.getWeeklyClass().getStartMinute());
                }
            }
        }

        if (activeDays.isEmpty() || uniqueTimes.isEmpty()) {
            return;
        }

        List<Integer> sortedTimes = new java.util.ArrayList<>(uniqueTimes);

        LinearLayout tableLayout = new LinearLayout(requireContext());
        tableLayout.setOrientation(LinearLayout.VERTICAL);
        tableLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        int headerWidth = (int) (48 * getResources().getDisplayMetrics().density);
        int paddingSmall = getResources().getDimensionPixelSize(R.dimen.spacing_small);
        int paddingTiny = getResources().getDimensionPixelSize(R.dimen.spacing_tiny);

        if (state.isRoutineOverviewVertical()) {
            // Rows = Times, Cols = Days

            // Header Row (Days)
            LinearLayout headerRow = new LinearLayout(requireContext());
            headerRow.setOrientation(LinearLayout.HORIZONTAL);
            headerRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
            headerRow.setPadding(0, 0, 0, paddingSmall);

            // Top-left empty corner
            View corner = new View(requireContext());
            corner.setLayoutParams(new LinearLayout.LayoutParams(headerWidth, 1));
            headerRow.addView(corner);

            for (java.time.DayOfWeek day : activeDays) {
                TextView tvDay = new TextView(requireContext());
                tvDay.setText(day.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault()));
                tvDay.setTextAppearance(requireContext(), R.style.TextAppearance_App_Secondary_Caption);
                tvDay.setGravity(android.view.Gravity.CENTER);
                
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.weight = 1;
                tvDay.setLayoutParams(lp);

                if (day == currentDay) {
                    tvDay.setTypeface(null, android.graphics.Typeface.BOLD);
                    tvDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_surface));
                }
                headerRow.addView(tvDay);
            }
            tableLayout.addView(headerRow);

            // Rows (Times)
            for (Integer time : sortedTimes) {
                LinearLayout rowLayout = new LinearLayout(requireContext());
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
                rowLayout.setPadding(0, paddingTiny, 0, paddingTiny);

                TextView tvTime = new TextView(requireContext());
                int h = time / 60;
                int m = time % 60;
                tvTime.setText(formatTime(h, m));
                tvTime.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 10);
                tvTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted));
                tvTime.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.START);
                tvTime.setLayoutParams(new LinearLayout.LayoutParams(headerWidth, LinearLayout.LayoutParams.WRAP_CONTENT));
                rowLayout.addView(tvTime);

                for (java.time.DayOfWeek day : activeDays) {
                    List<io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse> classesForDay = state.getWeeklyClasses().get(day);
                    List<io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse> sortedClasses = classesForDay != null ? new java.util.ArrayList<>(classesForDay) : new java.util.ArrayList<>();

                    io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse matchingClass = null;
                    int matchingIndex = -1;

                    for (int i = 0; i < sortedClasses.size(); i++) {
                        io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse c = sortedClasses.get(i);
                        if ((c.getWeeklyClass().getStartHour() * 60 + c.getWeeklyClass().getStartMinute()) == time) {
                            matchingClass = c;
                            matchingIndex = i;
                            break;
                        }
                    }

                    TextView tvCell = new TextView(requireContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.weight = 1;
                    tvCell.setLayoutParams(lp);
                    tvCell.setGravity(android.view.Gravity.CENTER);
                    tvCell.setMaxLines(2);
                    tvCell.setEllipsize(android.text.TextUtils.TruncateAt.END);

                    if (matchingClass != null) {
                        tvCell.setText(matchingClass.getCourse().getCode());
                        tvCell.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12);
                        tvCell.setTextColor(colorResolver.resolve(matchingClass.getCourse().getColor()));
                        tvCell.setTypeface(null, android.graphics.Typeface.BOLD);

                        if (day == currentDay && matchingIndex == state.getCurrentIndex()) {
                            tvCell.setPaintFlags(tvCell.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
                        }
                    } else {
                        tvCell.setText("-");
                        tvCell.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12);
                        tvCell.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted));
                    }

                    rowLayout.addView(tvCell);
                }

                tableLayout.addView(rowLayout);
            }
        } else {
            // Horizontal Overview: Rows = Days, Cols = Times

            // Header Row (Times)
            LinearLayout headerRow = new LinearLayout(requireContext());
            headerRow.setOrientation(LinearLayout.HORIZONTAL);
            headerRow.setGravity(android.view.Gravity.CENTER_VERTICAL);
            headerRow.setPadding(0, 0, 0, paddingSmall);

            // Top-left empty corner
            View corner = new View(requireContext());
            corner.setLayoutParams(new LinearLayout.LayoutParams(headerWidth, 1));
            headerRow.addView(corner);

            for (Integer time : sortedTimes) {
                TextView tvTime = new TextView(requireContext());
                int h = time / 60;
                int m = time % 60;
                tvTime.setText(formatTime(h, m));
                tvTime.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 10);
                tvTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted));
                tvTime.setGravity(android.view.Gravity.CENTER);
                
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.weight = 1;
                tvTime.setLayoutParams(lp);
                headerRow.addView(tvTime);
            }
            tableLayout.addView(headerRow);

            for (java.time.DayOfWeek day : activeDays) {
                LinearLayout rowLayout = new LinearLayout(requireContext());
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setGravity(android.view.Gravity.CENTER_VERTICAL);
                rowLayout.setPadding(0, paddingTiny, 0, paddingTiny);

                TextView tvDay = new TextView(requireContext());
                tvDay.setText(day.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale.getDefault()));
                tvDay.setTextAppearance(requireContext(), R.style.TextAppearance_App_Secondary_Caption);
                tvDay.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.START);
                tvDay.setLayoutParams(new LinearLayout.LayoutParams(headerWidth, LinearLayout.LayoutParams.WRAP_CONTENT));

                if (day == currentDay) {
                    tvDay.setTypeface(null, android.graphics.Typeface.BOLD);
                    tvDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_on_surface));
                }
                rowLayout.addView(tvDay);

                List<io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse> classesForDay = state.getWeeklyClasses().get(day);
                List<io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse> sortedClasses = classesForDay != null ? new java.util.ArrayList<>(classesForDay) : new java.util.ArrayList<>();

                for (Integer time : sortedTimes) {
                    io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse matchingClass = null;
                    int matchingIndex = -1;

                    for (int i = 0; i < sortedClasses.size(); i++) {
                        io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse c = sortedClasses.get(i);
                        if ((c.getWeeklyClass().getStartHour() * 60 + c.getWeeklyClass().getStartMinute()) == time) {
                            matchingClass = c;
                            matchingIndex = i;
                            break;
                        }
                    }

                    TextView tvCell = new TextView(requireContext());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.weight = 1;
                    tvCell.setLayoutParams(lp);
                    tvCell.setGravity(android.view.Gravity.CENTER);
                    tvCell.setMaxLines(2);
                    tvCell.setEllipsize(android.text.TextUtils.TruncateAt.END);

                    if (matchingClass != null) {
                        tvCell.setText(matchingClass.getCourse().getCode());
                        tvCell.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12);
                        tvCell.setTextColor(colorResolver.resolve(matchingClass.getCourse().getColor()));
                        tvCell.setTypeface(null, android.graphics.Typeface.BOLD);

                        if (day == currentDay && matchingIndex == state.getCurrentIndex()) {
                            tvCell.setPaintFlags(tvCell.getPaintFlags() | android.graphics.Paint.UNDERLINE_TEXT_FLAG);
                        }
                    } else {
                        tvCell.setText("-");
                        tvCell.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 12);
                        tvCell.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_muted));
                    }

                    rowLayout.addView(tvCell);
                }

                tableLayout.addView(rowLayout);
            }
        }

        binding.layoutWeeklyOverview.addView(tableLayout);
    }


    private void showRescheduleDialog(DailyClassItem current) {
        if (current == null) return;

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        DialogRescheduleBinding dialogBinding = DialogRescheduleBinding.inflate(inflater);

        final int[] startHour = {current.getStartHour()};
        final int[] startMinute = {current.getStartMinute()};
        final int[] endHour = {current.getEndHour()};
        final int[] endMinute = {current.getEndMinute()};
        final boolean[] pickingStart = {true};

        dialogBinding.clockReschedule.setTime(startHour[0], startMinute[0]);
        dialogBinding.clockReschedule.setMode(io.github.mdraihan27.routinemanager.core.designsystem.components.AnalogClockView.Mode.HOUR);

        dialogBinding.clockReschedule.setOnTimeSelectedListener(new io.github.mdraihan27.routinemanager.core.designsystem.components.AnalogClockView.OnTimeSelectedListener() {
            @Override
            public void onTimeChanged(int h, int m) {
                if (pickingStart[0]) {
                    startHour[0] = h;
                    startMinute[0] = m;
                } else {
                    endHour[0] = h;
                    endMinute[0] = m;
                }
                
                String s = formatTime(startHour[0], startMinute[0]);
                String e = formatTime(endHour[0], endMinute[0]);
                
                updateRescheduleTimePreview(
                        dialogBinding,
                        startHour[0], startMinute[0],
                        endHour[0], endMinute[0],
                        pickingStart[0]
                );
            }

            @Override
            public void onTimeSelectionComplete(io.github.mdraihan27.routinemanager.core.designsystem.components.AnalogClockView.Mode modeCompleted) {
                if (modeCompleted == io.github.mdraihan27.routinemanager.core.designsystem.components.AnalogClockView.Mode.HOUR) {
                    dialogBinding.clockReschedule.setMode(io.github.mdraihan27.routinemanager.core.designsystem.components.AnalogClockView.Mode.MINUTE);
                }
            }
        });

        Runnable updatePreview = () -> {
            updateRescheduleTimePreview(
                    dialogBinding,
                    startHour[0], startMinute[0],
                    endHour[0], endMinute[0],
                    pickingStart[0]
            );
        };
        updatePreview.run();

        dialogBinding.btnRescheduleStartTab.setOnClickListener(v -> {
            pickingStart[0] = true;
            dialogBinding.clockReschedule.setTime(startHour[0], startMinute[0]);
            dialogBinding.clockReschedule.setMode(io.github.mdraihan27.routinemanager.core.designsystem.components.AnalogClockView.Mode.HOUR);
            updatePreview.run();
        });

        dialogBinding.btnRescheduleEndTab.setOnClickListener(v -> {
            pickingStart[0] = false;
            dialogBinding.clockReschedule.setTime(endHour[0], endMinute[0]);
            dialogBinding.clockReschedule.setMode(io.github.mdraihan27.routinemanager.core.designsystem.components.AnalogClockView.Mode.HOUR);
            updatePreview.run();
        });

        dialogBinding.tvRescheduleStartTime.setOnClickListener(v -> dialogBinding.btnRescheduleStartTab.performClick());
        dialogBinding.tvRescheduleEndTime.setOnClickListener(v -> dialogBinding.btnRescheduleEndTab.performClick());

        dialogBinding.btnSelectAm.setOnClickListener(v -> {
            dialogBinding.clockReschedule.setIsAm(true);
            updatePreview.run();
        });

        dialogBinding.btnSelectPm.setOnClickListener(v -> {
            dialogBinding.clockReschedule.setIsAm(false);
            updatePreview.run();
        });

        com.google.android.material.bottomsheet.BottomSheetDialog dialog = io.github.mdraihan27.routinemanager.core.designsystem.components.AppBottomSheet.create(requireContext(), dialogBinding.getRoot());

        dialogBinding.btnCancelReschedule.setOnClickListener(v -> dialog.dismiss());

        dialogBinding.btnConfirmReschedule.setOnClickListener(v -> {
            int sMin = startHour[0] * 60 + startMinute[0];
            int eMin = endHour[0] * 60 + endMinute[0];
            if (eMin <= sMin) {
                dialogBinding.tvRescheduleError.setVisibility(View.VISIBLE);
                dialogBinding.tvRescheduleError.setText(R.string.error_end_time_before_start);
                return;
            }

            boolean isPermanent = dialogBinding.rbPermanent.isChecked();
            viewModel.onEvent(new HomeUiEvent.RescheduleClass(
                    current.getWeeklyClassId(),
                    startHour[0],
                    startMinute[0],
                    endHour[0],
                    endMinute[0],
                    isPermanent
            ));
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateRescheduleTimePreview(DialogRescheduleBinding dBinding, int sh, int sm, int eh, int em, boolean isPickingStart) {
        dBinding.tvRescheduleStartTime.setText(io.github.mdraihan27.routinemanager.core.util.DateTimeFormatter.formatTime12Hour(sh, sm));
        dBinding.tvRescheduleEndTime.setText(io.github.mdraihan27.routinemanager.core.util.DateTimeFormatter.formatTime12Hour(eh, em));

        if (isPickingStart) {
            dBinding.btnRescheduleStartTab.setBackgroundResource(R.drawable.bg_toggle_pill);
            dBinding.btnRescheduleStartTab.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.background));
            dBinding.btnRescheduleEndTab.setBackground(null);
            dBinding.btnRescheduleEndTab.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_on_surface));

            dBinding.tvRescheduleStartTime.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_on_surface));
            dBinding.tvRescheduleEndTime.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_muted));
        } else {
            dBinding.btnRescheduleEndTab.setBackgroundResource(R.drawable.bg_toggle_pill);
            dBinding.btnRescheduleEndTab.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.background));
            dBinding.btnRescheduleStartTab.setBackground(null);
            dBinding.btnRescheduleStartTab.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_on_surface));

            dBinding.tvRescheduleStartTime.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_muted));
            dBinding.tvRescheduleEndTime.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_on_surface));
        }

        if (dBinding.clockReschedule.isAm()) {
            dBinding.btnSelectAm.setBackgroundResource(R.drawable.bg_toggle_pill);
            dBinding.btnSelectAm.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.background));
            dBinding.btnSelectPm.setBackground(null);
            dBinding.btnSelectPm.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_on_surface));
        } else {
            dBinding.btnSelectPm.setBackgroundResource(R.drawable.bg_toggle_pill);
            dBinding.btnSelectPm.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.background));
            dBinding.btnSelectAm.setBackground(null);
            dBinding.btnSelectAm.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_on_surface));
        }

        dBinding.tvRescheduleError.setVisibility(android.view.View.GONE);
    }

    @androidx.annotation.NonNull
    private String formatTime(int hour, int minute) {
        String period = (hour >= 12) ? "PM" : "AM";
        int displayHour = hour % 12;
        if (displayHour == 0) {
            displayHour = 12;
        }
        return String.format(java.util.Locale.getDefault(), "%02d:%02d %s", displayHour, minute, period);
    }
}
