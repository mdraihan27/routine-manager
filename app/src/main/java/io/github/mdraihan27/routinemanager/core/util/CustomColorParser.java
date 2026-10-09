package io.github.mdraihan27.routinemanager.core.util;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class CustomColorParser {

    private CustomColorParser() {
    }

    public static boolean isValidHex(@Nullable String input) {
        if (input == null) {
            return false;
        }
        String cleaned = input.trim();
        if (cleaned.startsWith("#")) {
            cleaned = cleaned.substring(1);
        }
        if (cleaned.length() != 6 && cleaned.length() != 8) {
            return false;
        }
        for (int i = 0; i < cleaned.length(); i++) {
            char c = cleaned.charAt(i);
            boolean isHexDigit = (c >= '0' && c <= '9')
                    || (c >= 'a' && c <= 'f')
                    || (c >= 'A' && c <= 'F');
            if (!isHexDigit) {
                return false;
            }
        }
        return true;
    }

    @Nullable
    @ColorInt
    public static Integer parseHex(@Nullable String input) {
        if (!isValidHex(input)) {
            return null;
        }
        String cleaned = input.trim();
        if (!cleaned.startsWith("#")) {
            cleaned = "#" + cleaned;
        }
        try {
            return Color.parseColor(cleaned);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @NonNull
    public static String toHexString(@ColorInt int colorArgb) {
        return String.format("#%06X", (0xFFFFFF & colorArgb));
    }
}
