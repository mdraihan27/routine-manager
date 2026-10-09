package io.github.mdraihan27.routinemanager;

import org.junit.Test;

import io.github.mdraihan27.routinemanager.feature.course.data.local.CourseEntity;
import io.github.mdraihan27.routinemanager.feature.course.data.mapper.CourseMapper;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.Course;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.CustomColor;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.PaletteColor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CourseMapperTest {

    @Test
    public void testPaletteColorMapping() {
        Course course = new Course(1L, "CSE101", "Algorithms", "Prof Smith", new PaletteColor("sky"));
        CourseEntity entity = CourseMapper.toEntity(course);

        assertEquals(1L, entity.getId());
        assertEquals("CSE101", entity.getCode());
        assertEquals("Algorithms", entity.getName());
        assertEquals("Prof Smith", entity.getTeacherName());
        assertEquals("sky", entity.getColorKey());

        Course roundTrip = CourseMapper.toDomain(entity);
        assertEquals(course, roundTrip);
        assertFalse(roundTrip.getColor().isCustom());
    }

    @Test
    public void testCustomColorMapping() {
        Course course = new Course(2L, "PHY101", "Physics", "Dr Jane", new CustomColor(0xFF112233));
        CourseEntity entity = CourseMapper.toEntity(course);

        assertEquals(2L, entity.getId());
        assertEquals(Integer.valueOf(0xFF112233), entity.getCustomColorArgb());

        Course roundTrip = CourseMapper.toDomain(entity);
        assertEquals(course, roundTrip);
        assertTrue(roundTrip.getColor().isCustom());
    }
}
