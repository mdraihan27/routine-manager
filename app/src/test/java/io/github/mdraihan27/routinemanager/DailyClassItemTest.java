package io.github.mdraihan27.routinemanager;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.github.mdraihan27.routinemanager.feature.course.domain.model.PaletteColor;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.ClassStatus;
import io.github.mdraihan27.routinemanager.feature.schedule.domain.model.DailyClassItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DailyClassItemTest {

    @Test
    public void testMinutesCalculation() {
        DailyClassItem item = new DailyClassItem(
                1L, 10L, "CS101", "Algorithms", "Dr Turing",
                new PaletteColor("sky"), 9, 30, 11, 0, ClassStatus.UPCOMING, false
        );

        assertEquals(9 * 60 + 30, item.getStartTotalMinutes());
        assertEquals(11 * 60, item.getEndTotalMinutes());
        assertEquals(ClassStatus.UPCOMING, item.getStatus());
    }

    @Test
    public void testSortingByStartTime() {
        DailyClassItem item1 = new DailyClassItem(
                1L, 10L, "CS101", "Algorithms", "Dr Turing",
                new PaletteColor("sky"), 11, 0, 12, 30, ClassStatus.UPCOMING, false
        );
        DailyClassItem item2 = new DailyClassItem(
                2L, 20L, "MATH201", "Calculus", "Dr Euler",
                new PaletteColor("rose"), 9, 0, 10, 30, ClassStatus.UPCOMING, false
        );

        List<DailyClassItem> list = new ArrayList<>();
        list.add(item1);
        list.add(item2);
        list.sort(Comparator.comparingInt(DailyClassItem::getStartTotalMinutes));

        assertEquals("MATH201", list.get(0).getCourseCode());
        assertEquals("CS101", list.get(1).getCourseCode());
    }

    @Test
    public void testEquality() {
        DailyClassItem item1 = new DailyClassItem(
                1L, 10L, "CS101", "Algorithms", "Dr Turing",
                new PaletteColor("sky"), 9, 30, 11, 0, ClassStatus.UPCOMING, false
        );
        DailyClassItem item2 = new DailyClassItem(
                1L, 10L, "CS101", "Algorithms", "Dr Turing",
                new PaletteColor("sky"), 9, 30, 11, 0, ClassStatus.UPCOMING, false
        );
        DailyClassItem item3 = new DailyClassItem(
                1L, 10L, "CS101", "Algorithms", "Dr Turing",
                new PaletteColor("sky"), 9, 30, 11, 0, ClassStatus.CANCELLED, false
        );

        assertEquals(item1, item2);
        assertNotEquals(item1, item3);
    }
}
