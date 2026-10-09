package io.github.mdraihan27.routinemanager.core.designsystem.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;

import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.core.designsystem.interaction.TapEffect;

public class AppIconButton extends MaterialCardView {

    private final ImageView imageView;

    public AppIconButton(@NonNull Context context) {
        this(context, null);
    }

    public AppIconButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppIconButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        imageView = new ImageView(context);
        init();
    }

    private void init() {
        setRadius(getResources().getDimension(R.dimen.corner_medium));
        setCardElevation(getResources().getDimension(R.dimen.elevation_low));
        setStrokeWidth(0);
        setCardBackgroundColor(ContextCompat.getColor(getContext(), R.color.surface_muted));

        int size = (int) getResources().getDimension(R.dimen.icon_button_size);
        LayoutParams params = new LayoutParams(size, size);
        params.gravity = Gravity.CENTER;
        addView(imageView, params);

        imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.text_on_surface));
        TapEffect.attach(this);
    }

    public void setImageDrawable(@Nullable Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    public void setImageResource(int resId) {
        imageView.setImageResource(resId);
    }
}
