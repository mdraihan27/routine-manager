package io.github.mdraihan27.routinemanager.core.designsystem.components;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;

import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.core.designsystem.interaction.TapEffect;

public class AppChip extends MaterialCardView {

    public AppChip(@NonNull Context context) {
        this(context, null);
    }

    public AppChip(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppChip(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setRadius(getResources().getDimension(R.dimen.corner_small));
        setCardElevation(getResources().getDimension(R.dimen.elevation_none));
        setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.surface_muted));
        setStrokeWidth(0);
        TapEffect.attach(this);
    }
}
