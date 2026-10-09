package io.github.mdraihan27.routinemanager.core.designsystem.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.button.MaterialButton;

import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.core.designsystem.interaction.TapEffect;

public class AppButton extends MaterialButton {

    public AppButton(@NonNull Context context) {
        this(context, null);
    }

    public AppButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, com.google.android.material.R.attr.materialButtonStyle);
    }

    public AppButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setElevation(getResources().getDimension(R.dimen.elevation_low));
        setStrokeWidth(0);
        TapEffect.attach(this);
    }
}
