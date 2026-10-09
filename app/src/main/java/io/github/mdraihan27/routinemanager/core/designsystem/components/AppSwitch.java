package io.github.mdraihan27.routinemanager.core.designsystem.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.materialswitch.MaterialSwitch;

import io.github.mdraihan27.routinemanager.core.designsystem.interaction.TapEffect;

public class AppSwitch extends MaterialSwitch {

    public AppSwitch(@NonNull Context context) {
        this(context, null);
    }

    public AppSwitch(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, com.google.android.material.R.attr.materialSwitchStyle);
    }

    public AppSwitch(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        TapEffect.attach(this);
    }
}
