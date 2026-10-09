package io.github.mdraihan27.routinemanager.feature.course.presentation;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import io.github.mdraihan27.routinemanager.feature.course.domain.model.CourseColor;

public interface CourseColorResolver {

    @ColorInt
    int resolve(@NonNull CourseColor courseColor);

    @ColorInt
    int resolvePaletteKey(@NonNull String key);
}
