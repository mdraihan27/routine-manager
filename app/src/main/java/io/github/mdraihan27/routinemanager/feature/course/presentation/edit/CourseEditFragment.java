package io.github.mdraihan27.routinemanager.feature.course.presentation.edit;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.core.base.BaseFragment;
import io.github.mdraihan27.routinemanager.databinding.FragmentCourseEditBinding;

@AndroidEntryPoint
public class CourseEditFragment extends BaseFragment<FragmentCourseEditBinding> {

    @NonNull
    @Override
    protected FragmentCourseEditBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentCourseEditBinding.inflate(inflater, container, false);
    }
}
