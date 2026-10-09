package io.github.mdraihan27.routinemanager.feature.home.presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    }

    private void setupTopBar() {
        FragmentHomeBinding binding = getBinding();

        binding.btnHomeViewCourses.setOnClickListener(v ->
                navigator.navigateToCourses(this));

        binding.btnHomeSettings.setOnClickListener(v ->
                navigator.navigateToSettings(this));

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault());
        binding.tvHomeDateSubtitle.setText(today.format(formatter));

        binding.btnDismissOnboarding.setImageResource(R.drawable.ic_close);
        binding.btnDismissGestureTip.setImageResource(R.drawable.ic_close);
        binding.btnClassPrevious.setImageResource(R.drawable.ic_chevron_left);
        binding.btnClassNext.setImageResource(R.drawable.ic_chevron_right);
        binding.btnClassEditAction.setImageResource(R.drawable.ic_edit);
    }

    private void setupGestureHandling() {
        FragmentHomeBinding binding = getBinding();
        CardGestureDetector.attach(binding.cardDailyClass, new CardGestureDetector.OnCardGestureListener() {
            @Override
            public void onSwipeRight() {
                animateCardSwipe(binding.cardDailyClass, true, () ->
                        viewModel.onEvent(new HomeUiEvent.NextClass()));
            }

            @Override
            public void onSwipeLeft() {
                animateCardSwipe(binding.cardDailyClass, false, () ->
                        viewModel.onEvent(new HomeUiEvent.PreviousClass()));
            }

            @Override
            public void onSwipeUp() {
                showRescheduleDialog();
            }

            @Override
            public void onSwipeDown() {
                viewModel.onEvent(new HomeUiEvent.CancelCurrentClass());
            }

            @Override
            public void onClick() {
            }
        });
    }

    private void setupActionButtons() {
        FragmentHomeBinding binding = getBinding();

        binding.btnBottomAddCourse.setOnClickListener(v ->
                navigator.navigateToAddCourse(this));

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

        binding.btnClassPrevious.setOnClickListener(v ->
                viewModel.onEvent(new HomeUiEvent.PreviousClass()));

        binding.btnClassNext.setOnClickListener(v ->
                viewModel.onEvent(new HomeUiEvent.NextClass()));

        binding.btnClassCancelAction.setOnClickListener(v ->
                viewModel.onEvent(new HomeUiEvent.CancelCurrentClass()));

        binding.btnClassEditAction.setOnClickListener(v ->
                showRescheduleDialog());

        binding.btnUndoCancel.setOnClickListener(v ->
                viewModel.onEvent(new HomeUiEvent.UndoCancel()));
    }

    private void renderState(@NonNull HomeUiState state) {
        FragmentHomeBinding binding = getBinding();

        binding.progressHome.setVisibility(state.isLoading() ? View.VISIBLE : View.GONE);
        binding.cardOnboarding.setVisibility(state.isShowOnboarding() ? View.VISIBLE : View.GONE);
        binding.cardGestureTip.setVisibility(state.isShowGestureGuide() ? View.VISIBLE : View.GONE);
        binding.cardUndoBanner.setVisibility(state.isCanUndoCancel() ? View.VISIBLE : View.GONE);

        if (state.hasClasses()) {
            binding.layoutClassSection.setVisibility(View.VISIBLE);
            binding.cardHoliday.setVisibility(View.GONE);
            binding.cardBreak.setVisibility(View.GONE);
            binding.cardNoClasses.setVisibility(View.GONE);

            DailyClassItem current = state.getCurrentClass();
            if (current != null) {
                bindCurrentClass(binding, current, state);
            }
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

        binding.btnClassPrevious.setEnabled(!state.isFirstClass());
        binding.btnClassNext.setEnabled(!state.isLastClass());

        ClassStatus status = item.getStatus();
        if (status == ClassStatus.CANCELLED) {
            binding.tvClassStatusLabel.setText(R.string.label_cancelled);
            binding.cardDailyClass.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.surface_muted));
            binding.btnClassCancelAction.setText(R.string.action_undo);
        } else if (status == ClassStatus.COMPLETED) {
            binding.tvClassStatusLabel.setText(R.string.label_completed);
            binding.cardDailyClass.setCardBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.surface_muted));
            binding.btnClassCancelAction.setText(R.string.action_cancel);
        } else if (status == ClassStatus.IN_PROGRESS) {
            binding.tvClassStatusLabel.setText(R.string.label_in_progress);
            int color = colorResolver.resolve(item.getCourseColor());
            binding.cardDailyClass.setCardBackgroundColor(color);
            binding.btnClassCancelAction.setText(R.string.action_cancel);
        } else {
            binding.tvClassStatusLabel.setText(R.string.label_upcoming);
            int color = colorResolver.resolve(item.getCourseColor());
            binding.cardDailyClass.setCardBackgroundColor(color);
            binding.btnClassCancelAction.setText(R.string.action_cancel);
        }
    }

    private void animateCardSwipe(@NonNull View view, boolean toRight, @NonNull Runnable onEnd) {
        float translation = toRight ? view.getWidth() * 0.3f : -view.getWidth() * 0.3f;
        view.animate()
                .translationX(translation)
                .alpha(0.6f)
                .setDuration(120)
                .withEndAction(() -> {
                    onEnd.run();
                    view.setTranslationX(-translation * 0.5f);
                    view.animate()
                            .translationX(0f)
                            .alpha(1f)
                            .setDuration(150)
                            .start();
                })
                .start();
    }

    private void showRescheduleDialog() {
        HomeUiState state = viewModel.getState().getValue();
        if (state == null) return;
        DailyClassItem current = state.getCurrentClass();
        if (current == null) return;

        LayoutInflater inflater = LayoutInflater.from(requireContext());
        DialogRescheduleBinding dialogBinding = DialogRescheduleBinding.inflate(inflater);

        final int[] startHour = {current.getStartHour()};
        final int[] startMinute = {current.getStartMinute()};
        final int[] endHour = {current.getEndHour()};
        final int[] endMinute = {current.getEndMinute()};
        final boolean[] pickingStart = {true};

        dialogBinding.clockReschedule.setTime(startHour[0], startMinute[0]);

        Runnable updatePreview = () -> {
            String s = formatTime(startHour[0], startMinute[0]);
            String e = formatTime(endHour[0], endMinute[0]);
            dialogBinding.tvReschedulePreview.setText(String.format("%s - %s", s, e));
        };
        updatePreview.run();

        dialogBinding.btnRescheduleStartTab.setOnClickListener(v -> {
            pickingStart[0] = true;
            dialogBinding.clockReschedule.setTime(startHour[0], startMinute[0]);
        });

        dialogBinding.btnRescheduleEndTab.setOnClickListener(v -> {
            pickingStart[0] = false;
            dialogBinding.clockReschedule.setTime(endHour[0], endMinute[0]);
        });

        dialogBinding.clockReschedule.setOnTimeSelectedListener(new AnalogClockView.OnTimeSelectedListener() {
            @Override
            public void onTimeChanged(int h, int m) {
                if (pickingStart[0]) {
                    startHour[0] = h;
                    startMinute[0] = m;
                } else {
                    endHour[0] = h;
                    endMinute[0] = m;
                }
                updatePreview.run();
            }

            @Override
            public void onTimeSelectionComplete(AnalogClockView.Mode modeCompleted) {
                if (modeCompleted == AnalogClockView.Mode.HOUR) {
                    dialogBinding.clockReschedule.setMode(AnalogClockView.Mode.MINUTE);
                }
            }
        });

        dialogBinding.chipRescheduleHour.setOnClickListener(v ->
                dialogBinding.clockReschedule.setMode(AnalogClockView.Mode.HOUR));

        dialogBinding.chipRescheduleMinute.setOnClickListener(v ->
                dialogBinding.clockReschedule.setMode(AnalogClockView.Mode.MINUTE));

        dialogBinding.chipRescheduleAm.setOnClickListener(v -> {
            if (pickingStart[0] && startHour[0] >= 12) {
                startHour[0] -= 12;
            } else if (!pickingStart[0] && endHour[0] >= 12) {
                endHour[0] -= 12;
            }
            dialogBinding.clockReschedule.setIsAm(true);
            dialogBinding.clockReschedule.setTime(
                    pickingStart[0] ? startHour[0] : endHour[0],
                    pickingStart[0] ? startMinute[0] : endMinute[0]
            );
            updatePreview.run();
        });

        dialogBinding.chipReschedulePm.setOnClickListener(v -> {
            if (pickingStart[0] && startHour[0] < 12) {
                startHour[0] += 12;
            } else if (!pickingStart[0] && endHour[0] < 12) {
                endHour[0] += 12;
            }
            dialogBinding.clockReschedule.setIsAm(false);
            dialogBinding.clockReschedule.setTime(
                    pickingStart[0] ? startHour[0] : endHour[0],
                    pickingStart[0] ? startMinute[0] : endMinute[0]
            );
            updatePreview.run();
        });

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogBinding.getRoot())
                .setCancelable(true)
                .create();

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

    @NonNull
    private String formatTime(int hour, int minute) {
        String period = (hour >= 12) ? "PM" : "AM";
        int displayHour = hour % 12;
        if (displayHour == 0) {
            displayHour = 12;
        }
        return String.format(Locale.getDefault(), "%02d:%02d %s", displayHour, minute, period);
    }
}
