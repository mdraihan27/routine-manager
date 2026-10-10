package io.github.mdraihan27.routinemanager.feature.routine.presentation;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.core.util.DateTimeFormatter;
import io.github.mdraihan27.routinemanager.databinding.ItemWizardClassBinding;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.presentation.CourseColorResolver;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClass;
import io.github.mdraihan27.routinemanager.feature.routine.domain.model.WeeklyClassWithCourse;

public class WizardClassAdapter extends ListAdapter<WeeklyClassWithCourse, WizardClassAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(@NonNull WeeklyClassWithCourse item);
    }

    private final CourseColorResolver colorResolver;
    private final OnDeleteClickListener deleteClickListener;

    public WizardClassAdapter(@NonNull CourseColorResolver colorResolver,
                              @NonNull OnDeleteClickListener deleteClickListener) {
        super(DIFF_CALLBACK);
        this.colorResolver = colorResolver;
        this.deleteClickListener = deleteClickListener;
    }

    private static final DiffUtil.ItemCallback<WeeklyClassWithCourse> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull WeeklyClassWithCourse oldItem, @NonNull WeeklyClassWithCourse newItem) {
            return oldItem.getWeeklyClass().getId() == newItem.getWeeklyClass().getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull WeeklyClassWithCourse oldItem, @NonNull WeeklyClassWithCourse newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWizardClassBinding binding = ItemWizardClassBinding.inflate(
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
        private final ItemWizardClassBinding binding;

        ViewHolder(ItemWizardClassBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(WeeklyClassWithCourse item) {
            WeeklyClass wc = item.getWeeklyClass();
            Course course = item.getCourse();

            binding.tvWizardClassCode.setText(course.getCode());

            int color = colorResolver.resolve(course.getColor());
            binding.getRoot().setCardBackgroundColor(color);
            binding.tvWizardClassCode.setTextColor(itemView.getResources().getColor(R.color.text_on_surface));

            binding.btnDeleteWizardClass.setOnClickListener(v -> deleteClickListener.onDeleteClick(item));
        }
    }
}
