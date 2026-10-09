package io.github.mdraihan27.routinemanager.core.designsystem.color;

import androidx.annotation.ColorInt;

public interface ColorProvider {

    @ColorInt
    int getBackground();

    @ColorInt
    int getButtonBackground();

    @ColorInt
    int getOnButton();

    @ColorInt
    int getTextOnSurface();

    @ColorInt
    int getShadow();

    @ColorInt
    int getSurfaceCard();

    @ColorInt
    int getSurfaceMuted();

    @ColorInt
    int getTextMuted();

    @ColorInt
    int getSurfaceSubtle();
}
