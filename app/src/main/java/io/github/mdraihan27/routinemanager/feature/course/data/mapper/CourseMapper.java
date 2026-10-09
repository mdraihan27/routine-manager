package io.github.mdraihan27.routinemanager.feature.course.data.mapper;

import androidx.annotation.NonNull;

import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseEntity;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.CourseColor;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.CustomColor;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.PaletteColor;
import io.github.mdraihan27.routinemanager.feature.course.presentation.CourseColorPalette;

public final class CourseMapper {

    private CourseMapper() {
    }

    @NonNull
    public static Course toDomain(@NonNull CourseEntity entity) {
        CourseColor color;
        if (entity.getCustomColorArgb() != null) {
            color = new CustomColor(entity.getCustomColorArgb());
        } else if (entity.getColorKey() != null) {
            color = new PaletteColor(entity.getColorKey());
        } else {
            color = new PaletteColor(CourseColorPalette.getDefaultKey());
        }

        return new Course(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getTeacherName(),
                color
        );
    }

    @NonNull
    public static CourseEntity toEntity(@NonNull Course domain) {
        String colorKey = null;
        Integer customColorArgb = null;

        if (domain.getColor() instanceof CustomColor) {
            customColorArgb = ((CustomColor) domain.getColor()).getColorArgb();
        } else if (domain.getColor() instanceof PaletteColor) {
            colorKey = ((PaletteColor) domain.getColor()).getKey();
        } else {
            colorKey = CourseColorPalette.getDefaultKey();
        }

        return new CourseEntity(
                domain.getId() > 0 ? domain.getId() : 0L,
                domain.getCode(),
                domain.getName(),
                domain.getTeacherName(),
                colorKey,
                customColorArgb
        );
    }
}
