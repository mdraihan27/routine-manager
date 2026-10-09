package io.github.mdraihan27.routinemanager.feature.course.presentation;

import android.content.Context;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.CourseColor;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.CustomColor;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.PaletteColor;

@Singleton
public final class CourseColorResolverImpl implements CourseColorResolver {

    private final Context context;

    @Inject
    public CourseColorResolverImpl(@NonNull @ApplicationContext Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    @ColorInt
    public int resolve(@NonNull CourseColor courseColor) {
        if (courseColor instanceof CustomColor) {
            return ((CustomColor) courseColor).getColorArgb();
        }
        if (courseColor instanceof PaletteColor) {
            return resolvePaletteKey(((PaletteColor) courseColor).getKey());
        }
        return ContextCompat.getColor(context, R.color.course_pistachio);
    }

    @Override
    @ColorInt
    public int resolvePaletteKey(@NonNull String key) {
        Integer resId = CourseColorPalette.getColorRes(key);
        if (resId != null) {
            return ContextCompat.getColor(context, resId);
        }
        return ContextCompat.getColor(context, R.color.course_pistachio);
    }
}
