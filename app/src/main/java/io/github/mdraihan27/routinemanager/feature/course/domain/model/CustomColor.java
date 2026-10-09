package io.github.mdraihan27.routinemanager.feature.course.domain.model;

import androidx.annotation.ColorInt;

public final class CustomColor extends CourseColor {

    private final int colorArgb;

    public CustomColor(@ColorInt int colorArgb) {
        this.colorArgb = colorArgb;
    }

    @ColorInt
    public int getColorArgb() {
        return colorArgb;
    }

    @Override
    public boolean isCustom() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomColor that = (CustomColor) o;
        return colorArgb == that.colorArgb;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(colorArgb);
    }
}
