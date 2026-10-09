package io.github.mdraihan27.routinemanager.core.designsystem.color;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.github.mdraihan27.routinemanager.R;

@Singleton
public final class ColorProviderImpl implements ColorProvider {

    private final Context context;

    @Inject
    public ColorProviderImpl(@NonNull @ApplicationContext Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public int getBackground() {
        return ContextCompat.getColor(context, R.color.background);
    }

    @Override
    public int getButtonBackground() {
        return ContextCompat.getColor(context, R.color.button_background);
    }

    @Override
    public int getOnButton() {
        return ContextCompat.getColor(context, R.color.on_button);
    }

    @Override
    public int getTextOnSurface() {
        return ContextCompat.getColor(context, R.color.text_on_surface);
    }

    @Override
    public int getShadow() {
        return ContextCompat.getColor(context, R.color.shadow);
    }

    @Override
    public int getSurfaceCard() {
        return ContextCompat.getColor(context, R.color.surface_card);
    }

    @Override
    public int getSurfaceMuted() {
        return ContextCompat.getColor(context, R.color.surface_muted);
    }

    @Override
    public int getTextMuted() {
        return ContextCompat.getColor(context, R.color.text_muted);
    }

    @Override
    public int getSurfaceSubtle() {
        return ContextCompat.getColor(context, R.color.surface_subtle);
    }
}
