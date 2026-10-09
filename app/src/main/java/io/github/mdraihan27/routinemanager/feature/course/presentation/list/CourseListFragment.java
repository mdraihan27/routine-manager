package io.github.mdraihan27.routinemanager.feature.course.presentation.list;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.core.base.BaseFragment;
import io.github.mdraihan27.routinemanager.databinding.FragmentCourseListBinding;

@AndroidEntryPoint
public class CourseListFragment extends BaseFragment<FragmentCourseListBinding> {

    @NonNull
    @Override
    protected FragmentCourseListBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentCourseListBinding.inflate(inflater, container, false);
    }
}
