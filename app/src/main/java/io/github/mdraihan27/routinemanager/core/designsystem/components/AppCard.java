package io.github.mdraihan27.routinemanager.core.designsystem.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.card.MaterialCardView;

import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.core.designsystem.interaction.TapEffect;

public class AppCard extends MaterialCardView {

    public AppCard(@NonNull Context context) {
        this(context, null);
    }

    public AppCard(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, com.google.android.material.R.attr.materialCardViewStyle);
    }

    public AppCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setRadius(getResources().getDimension(R.dimen.corner_large));
        setCardElevation(getResources().getDimension(R.dimen.elevation_medium));
        setStrokeWidth(0);
        TapEffect.attach(this);
    }
}
