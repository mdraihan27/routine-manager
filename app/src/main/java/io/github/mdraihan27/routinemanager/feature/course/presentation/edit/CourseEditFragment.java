package io.github.mdraihan27.routinemanager.feature.course.presentation.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.core.base.BaseFragment;
import io.github.mdraihan27.routinemanager.core.designsystem.components.AppDialog;
import io.github.mdraihan27.routinemanager.core.navigation.Navigator;
import io.github.mdraihan27.routinemanager.databinding.FragmentCourseEditBinding;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.CourseColor;

@AndroidEntryPoint
public class CourseEditFragment extends BaseFragment<FragmentCourseEditBinding> {

    @Inject
    Navigator navigator;

    private CourseEditViewModel viewModel;
    private long courseId = -1L;
    private ArrayAdapter<String> teacherAdapter;

    @NonNull
    @Override
    protected FragmentCourseEditBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentCourseEditBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CourseEditViewModel.class);

        if (getArguments() != null) {
            courseId = getArguments().getLong("course_id", -1L);
        }

        if (courseId > 0) {
            getBinding().courseEditTopBar.setTitle(R.string.title_edit_course);
            getBinding().btnDeleteCourse.setVisibility(View.VISIBLE);
            viewModel.loadCourse(courseId);
        } else {
            getBinding().courseEditTopBar.setTitle(R.string.title_add_course);
            getBinding().btnDeleteCourse.setVisibility(View.GONE);
        }

        teacherAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line);
        getBinding().editTeacherName.setAdapter(teacherAdapter);

        getBinding().btnSaveCourse.setOnClickListener(v -> handleSave());
        getBinding().btnDeleteCourse.setOnClickListener(v -> handleDelete());

        viewModel.getState().observe(getViewLifecycleOwner(), state -> {
            if (state.isSaved() || state.isDeleted()) {
                navigator.navigateUp(this);
                return;
            }

            teacherAdapter.clear();
            teacherAdapter.addAll(state.getTeacherSuggestions());
            teacherAdapter.notifyDataSetChanged();

            Course course = state.getInitialCourse();
            if (course != null && getBinding().editCourseCode.getText().length() == 0) {
                getBinding().editCourseCode.setText(course.getCode());
                getBinding().editCourseName.setText(course.getName());
                getBinding().editTeacherName.setText(course.getTeacherName());
                getBinding().courseColorPicker.setSelectedColor(course.getColor());
            }
        });
    }

    private void handleSave() {
        String code = getBinding().editCourseCode.getText() != null
                ? getBinding().editCourseCode.getText().toString().trim()
                : "";
        String name = getBinding().editCourseName.getText() != null
                ? getBinding().editCourseName.getText().toString().trim()
                : "";
        String teacher = getBinding().editTeacherName.getText() != null
                ? getBinding().editTeacherName.getText().toString().trim()
                : "";

        boolean hasError = false;
        if (code.isEmpty()) {
            getBinding().inputCourseCodeLayout.setError(getString(R.string.error_required_field));
            hasError = true;
        } else {
            getBinding().inputCourseCodeLayout.setError(null);
        }

        if (name.isEmpty()) {
            getBinding().inputCourseNameLayout.setError(getString(R.string.error_required_field));
            hasError = true;
        } else {
            getBinding().inputCourseNameLayout.setError(null);
        }

        if (hasError) {
            return;
        }

        CourseColor color = getBinding().courseColorPicker.getSelectedColor();
        viewModel.saveCourse(courseId, code, name, teacher, color);
    }

    private void handleDelete() {
        CourseEditUiState state = viewModel.getState().getValue();
        boolean isReferenced = state != null && state.isReferencedInRoutine();

        CharSequence msg = isReferenced
                ? getString(R.string.confirm_delete_course_msg)
                : getString(R.string.confirm_delete_course_title);

        AppDialog.create(
                requireContext(),
                getString(R.string.confirm_delete_course_title),
                msg,
                getString(R.string.action_delete),
                () -> viewModel.deleteCourse(courseId),
                getString(R.string.action_cancel),
                () -> {
                }
        ).show();
    }
}
