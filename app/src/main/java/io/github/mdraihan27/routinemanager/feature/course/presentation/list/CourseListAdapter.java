package io.github.mdraihan27.routinemanager.feature.course.presentation.list;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.databinding.ItemCourseBinding;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.presentation.CourseColorResolver;

public class CourseListAdapter extends ListAdapter<Course, CourseListAdapter.ViewHolder> {

    public interface OnCourseClickListener {
        void onCourseClick(@NonNull Course course);
    }

    private final CourseColorResolver colorResolver;
    private final OnCourseClickListener clickListener;

    public CourseListAdapter(@NonNull CourseColorResolver colorResolver,
                             @NonNull OnCourseClickListener clickListener) {
        super(DIFF_CALLBACK);
        this.colorResolver = colorResolver;
        this.clickListener = clickListener;
    }

    private static final DiffUtil.ItemCallback<Course> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Course oldItem, @NonNull Course newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCourseBinding binding = ItemCourseBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCourseBinding binding;

        ViewHolder(ItemCourseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Course course) {
            binding.tvCourseCode.setText(course.getCode());
            binding.tvCourseName.setText(course.getName());
            binding.tvTeacherName.setText(course.getTeacherName());

            int color = colorResolver.resolve(course.getColor());
            binding.getRoot().setCardBackgroundColor(color);

            itemView.setOnClickListener(v -> clickListener.onCourseClick(course));
        }
    }
}
