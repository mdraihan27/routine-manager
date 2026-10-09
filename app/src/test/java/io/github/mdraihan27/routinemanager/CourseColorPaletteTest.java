package io.github.mdraihan27.routinemanager;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.mdraihan27.routinemanager.feature.course.presentation.CourseColorPalette;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CourseColorPaletteTest {

    @Test
    public void testAllKeysUniqueAndResolveToExistingResources() {
        List<CourseColorPalette.PaletteEntry> entries = CourseColorPalette.getEntries();
        assertEquals(18, entries.size());

        Set<String> uniqueKeys = new HashSet<>();
        for (CourseColorPalette.PaletteEntry entry : entries) {
            assertNotNull(entry.getKey());
            assertTrue(uniqueKeys.add(entry.getKey()));
            Integer resId = CourseColorPalette.getColorRes(entry.getKey());
            assertNotNull(resId);
            assertEquals((int) resId, entry.getColorResId());
        }
    }
}
