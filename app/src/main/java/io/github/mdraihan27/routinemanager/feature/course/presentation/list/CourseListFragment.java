package io.github.mdraihan27.routinemanager.feature.course.presentation.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.core.base.BaseFragment;
import io.github.mdraihan27.routinemanager.core.navigation.Navigator;
import io.github.mdraihan27.routinemanager.databinding.FragmentCourseListBinding;
import io.github.mdraihan27.routinemanager.feature.course.presentation.CourseColorResolver;

@AndroidEntryPoint
public class CourseListFragment extends BaseFragment<FragmentCourseListBinding> {

    @Inject
    Navigator navigator;

    @Inject
    CourseColorResolver colorResolver;

    private CourseListViewModel viewModel;
    private CourseListAdapter adapter;

    @NonNull
    @Override
    protected FragmentCourseListBinding inflateBinding(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentCourseListBinding.inflate(inflater, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(CourseListViewModel.class);

        getBinding().courseListTopBar.setTitle(R.string.title_courses);

        adapter = new CourseListAdapter(colorResolver, course ->
                navigator.navigateToEditCourse(this, course.getId())
        );

        getBinding().coursesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        getBinding().coursesRecyclerView.setAdapter(adapter);

        getBinding().btnAddCourse.setOnClickListener(v ->
                navigator.navigateToAddCourse(this)
        );

        viewModel.getState().observe(getViewLifecycleOwner(), state ->
                adapter.submitList(state.getCourses())
        );
    }
}
