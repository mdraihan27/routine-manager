package io.github.mdraihan27.routinemanager.core.designsystem.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

import io.github.mdraihan27.routinemanager.R;

public class AppTextInput extends TextInputLayout {

    public AppTextInput(@NonNull Context context) {
        this(context, null);
    }

    public AppTextInput(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, com.google.android.material.R.attr.textInputStyle);
    }

    public AppTextInput(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setBoxBackgroundMode(BOX_BACKGROUND_FILLED);
        setBoxStrokeWidth(0);
        setBoxStrokeWidthFocused(0);
        setBoxBackgroundColor(ContextCompat.getColor(getContext(), R.color.surface_muted));
        float corner = getResources().getDimension(R.dimen.corner_medium);
        setBoxCornerRadii(corner, corner, corner, corner);
    }
}
