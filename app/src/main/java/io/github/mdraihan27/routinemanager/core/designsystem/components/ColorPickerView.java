package io.github.mdraihan27.routinemanager.core.designsystem.components;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.core.designsystem.interaction.TapEffect;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.CourseColor;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.PaletteColor;
import io.github.mdraihan27.routinemanager.feature.course.presentation.CourseColorPalette;

public class ColorPickerView extends LinearLayout {

    public interface OnColorPickedListener {
        void onColorPicked(@NonNull CourseColor courseColor);
    }

    private final GridLayout gridLayout;

    private CourseColor selectedColor = new PaletteColor(CourseColorPalette.getDefaultKey());
    private OnColorPickedListener listener;
    private final List<View> swatchViews = new ArrayList<>();

    public ColorPickerView(@NonNull Context context) {
        this(context, null);
    }

    public ColorPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);

        gridLayout = new GridLayout(context);
        gridLayout.setColumnCount(6);

        init();
    }

    private void init() {
        int spacing = (int) getResources().getDimension(R.dimen.spacing_small);
        int swatchSize = (int) getResources().getDimension(R.dimen.swatch_size);

        List<CourseColorPalette.PaletteEntry> entries = CourseColorPalette.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            CourseColorPalette.PaletteEntry entry = entries.get(i);

            FrameLayout swatch = new FrameLayout(getContext());
            swatch.setBackground(createCircleDrawable(ContextCompat.getColor(getContext(), entry.getColorResId()), i == 0));

            GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
            gridParams.width = swatchSize;
            gridParams.height = swatchSize;
            gridParams.setMargins(spacing / 2, spacing / 2, spacing / 2, spacing / 2);
            swatch.setLayoutParams(gridParams);

            final String key = entry.getKey();
            final int index = i;
            TapEffect.attach(swatch);
            swatch.setOnClickListener(v -> selectPaletteIndex(index, key));

            swatchViews.add(swatch);
            gridLayout.addView(swatch);
        }

        addView(gridLayout);
    }

    private void selectPaletteIndex(int index, String key) {
        List<CourseColorPalette.PaletteEntry> entries = CourseColorPalette.getEntries();
        for (int i = 0; i < swatchViews.size(); i++) {
            View swatch = swatchViews.get(i);
            int color = ContextCompat.getColor(getContext(), entries.get(i).getColorResId());
            swatch.setBackground(createCircleDrawable(color, i == index));
        }
        selectedColor = new PaletteColor(key);
        if (listener != null) {
            listener.onColorPicked(selectedColor);
        }
    }

    public void setSelectedColor(@NonNull CourseColor color) {
        this.selectedColor = color;
        if (color instanceof PaletteColor) {
            String key = ((PaletteColor) color).getKey();
            List<CourseColorPalette.PaletteEntry> entries = CourseColorPalette.getEntries();
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).getKey().equals(key)) {
                    selectPaletteIndex(i, key);
                    break;
                }
            }
        }
    }

    @NonNull
    public CourseColor getSelectedColor() {
        return selectedColor;
    }

    public void setOnColorPickedListener(@Nullable OnColorPickedListener listener) {
        this.listener = listener;
    }

    private android.graphics.drawable.GradientDrawable createCircleDrawable(int color, boolean isSelected) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        gd.setColor(color);
        if (isSelected) {
            int strokeWidth = (int) (getResources().getDisplayMetrics().density * 2); // 2dp border
            gd.setStroke(strokeWidth, ContextCompat.getColor(getContext(), R.color.text_on_surface));
        }
        return gd;
    }
}
