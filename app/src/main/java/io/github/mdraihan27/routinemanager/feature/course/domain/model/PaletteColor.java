package io.github.mdraihan27.routinemanager.feature.course.domain.model;

import androidx.annotation.NonNull;

import java.util.Objects;

public final class PaletteColor extends CourseColor {

    private final String key;

    public PaletteColor(@NonNull String key) {
        this.key = Objects.requireNonNull(key);
    }

    @NonNull
    public String getKey() {
        return key;
    }

    @Override
    public boolean isCustom() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaletteColor that = (PaletteColor) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
