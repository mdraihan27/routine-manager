package io.github.mdraihan27.routinemanager.feature.course.presentation;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.mdraihan27.routinemanager.R;

public final class CourseColorPalette {

    public static final class PaletteEntry {
        private final String key;
        private final int colorResId;

        public PaletteEntry(@NonNull String key, @ColorRes int colorResId) {
            this.key = key;
            this.colorResId = colorResId;
        }

        @NonNull
        public String getKey() {
            return key;
        }

        @ColorRes
        public int getColorResId() {
            return colorResId;
        }
    }

    private static final List<PaletteEntry> ENTRIES;
    private static final Map<String, Integer> KEY_TO_RES;

    static {
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("pistachio", R.color.course_pistachio);
        map.put("stone", R.color.course_stone);
        map.put("sage", R.color.course_sage);
        map.put("lavender", R.color.course_lavender);
        map.put("sand", R.color.course_sand);
        map.put("mauve", R.color.course_mauve);
        map.put("rose", R.color.course_rose);
        map.put("butter", R.color.course_butter);
        map.put("blush", R.color.course_blush);
        map.put("sky", R.color.course_sky);
        map.put("pink", R.color.course_pink);
        map.put("aqua", R.color.course_aqua);
        map.put("candy", R.color.course_candy);
        map.put("coral", R.color.course_coral);
        map.put("apricot", R.color.course_apricot);
        map.put("cornflower", R.color.course_cornflower);
        map.put("iris", R.color.course_iris);
        map.put("orchid", R.color.course_orchid);

        List<PaletteEntry> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            list.add(new PaletteEntry(entry.getKey(), entry.getValue()));
        }

        ENTRIES = Collections.unmodifiableList(list);
        KEY_TO_RES = Collections.unmodifiableMap(map);
    }

    private CourseColorPalette() {
    }

    @NonNull
    public static List<PaletteEntry> getEntries() {
        return ENTRIES;
    }

    @Nullable
    @ColorRes
    public static Integer getColorRes(@NonNull String key) {
        return KEY_TO_RES.get(key);
    }

    @NonNull
    public static String getDefaultKey() {
        return ENTRIES.get(0).getKey();
    }
}
