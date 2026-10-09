package io.github.mdraihan27.routinemanager.core.util;

import androidx.annotation.NonNull;

import java.util.Locale;

public final class DateTimeFormatter {

    private DateTimeFormatter() {
    }

    @NonNull
    public static String formatTime12Hour(int hour24, int minute) {
        int h12 = hour24 % 12;
        if (h12 == 0) {
            h12 = 12;
        }
        String amPm = hour24 < 12 ? "AM" : "PM";
        return String.format(Locale.US, "%02d:%02d %s", h12, minute, amPm);
    }

    @NonNull
    public static String formatTimeRange(int startHour24, int startMinute, int endHour24, int endMinute) {
        return formatTime12Hour(startHour24, startMinute) + " - " + formatTime12Hour(endHour24, endMinute);
    }
}
