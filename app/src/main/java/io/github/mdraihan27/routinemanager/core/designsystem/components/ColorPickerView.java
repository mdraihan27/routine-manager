package io.github.mdraihan27.routinemanager.core.designsystem.components;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import io.github.mdraihan27.routinemanager.R;
import io.github.mdraihan27.routinemanager.core.designsystem.interaction.TapEffect;
import io.github.mdraihan27.routinemanager.core.util.CustomColorParser;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.CourseColor;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.CustomColor;
import io.github.mdraihan27.routinemanager.feature.course.domain.model.PaletteColor;
import io.github.mdraihan27.routinemanager.feature.course.presentation.CourseColorPalette;

public class ColorPickerView extends LinearLayout {

    public interface OnColorPickedListener {
        void onColorPicked(@NonNull CourseColor courseColor);
    }

    private final GridLayout gridLayout;
    private final LinearLayout customPanel;
    private final AppButton customToggleBtn;
    private final View customPreviewView;
    private final EditText hexEditText;
    private final SeekBar hueSeekBar;
    private final SeekBar satSeekBar;
    private final SeekBar valSeekBar;

    private CourseColor selectedColor = new PaletteColor(CourseColorPalette.getDefaultKey());
    private OnColorPickedListener listener;
    private final List<View> swatchViews = new ArrayList<>();
    private final List<ImageView> checkViews = new ArrayList<>();

    public ColorPickerView(@NonNull Context context) {
        this(context, null);
    }

    public ColorPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);

        gridLayout = new GridLayout(context);
        gridLayout.setColumnCount(6);

        customToggleBtn = new AppButton(context);
        customPanel = new LinearLayout(context);
        customPreviewView = new View(context);
        hexEditText = new EditText(context);
        hueSeekBar = new SeekBar(context);
        satSeekBar = new SeekBar(context);
        valSeekBar = new SeekBar(context);

        init();
    }

    private void init() {
        int spacing = (int) getResources().getDimension(R.dimen.spacing_small);
        int swatchSize = (int) getResources().getDimension(R.dimen.swatch_size);
        float cornerRadius = getResources().getDimension(R.dimen.corner_small);

        List<CourseColorPalette.PaletteEntry> entries = CourseColorPalette.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            CourseColorPalette.PaletteEntry entry = entries.get(i);

            FrameLayout swatch = new FrameLayout(getContext());
            swatch.setBackground(createRoundedDrawable(ContextCompat.getColor(getContext(), entry.getColorResId()), cornerRadius));

            ImageView checkIcon = new ImageView(getContext());
            checkIcon.setImageResource(android.R.drawable.checkbox_on_background);
            checkIcon.setColorFilter(ContextCompat.getColor(getContext(), R.color.text_on_surface));
            checkIcon.setVisibility(i == 0 ? VISIBLE : GONE);

            FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(swatchSize / 2, swatchSize / 2);
            iconParams.gravity = Gravity.CENTER;
            swatch.addView(checkIcon, iconParams);

            GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
            gridParams.width = swatchSize;
            gridParams.height = swatchSize;
            gridParams.setMargins(spacing / 2, spacing / 2, spacing / 2, spacing / 2);
            swatch.setLayoutParams(gridParams);

            final String key = entry.getKey();
            final int index = i;
            TapEffect.attach(swatch);
            swatch.setOnClickListener(v -> selectPaletteIndex(index, key));

            swatchViews.add(swatch);
            checkViews.add(checkIcon);
            gridLayout.addView(swatch);
        }

        addView(gridLayout);

        customToggleBtn.setText(R.string.action_custom_color);
        LayoutParams btnParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        btnParams.topMargin = spacing;
        addView(customToggleBtn, btnParams);

        setupCustomPanel();
        addView(customPanel);

        customToggleBtn.setOnClickListener(v -> {
            boolean isVisible = customPanel.getVisibility() == VISIBLE;
            customPanel.setVisibility(isVisible ? GONE : VISIBLE);
        });
    }

    private void setupCustomPanel() {
        int spacing = (int) getResources().getDimension(R.dimen.spacing_small);
        customPanel.setOrientation(VERTICAL);
        customPanel.setVisibility(GONE);
        customPanel.setPadding(0, spacing, 0, 0);

        int previewHeight = (int) getResources().getDimension(R.dimen.button_height);
        float cornerRadius = getResources().getDimension(R.dimen.corner_small);
        customPreviewView.setBackground(createRoundedDrawable(Color.HSVToColor(new float[]{0f, 0.5f, 0.8f}), cornerRadius));
        LayoutParams previewParams = new LayoutParams(LayoutParams.MATCH_PARENT, previewHeight);
        customPanel.addView(customPreviewView, previewParams);

        hexEditText.setHint(R.string.hint_hex_color);
        hexEditText.setTextAppearance(R.style.TextAppearance_App_Secondary_Body);
        hexEditText.setBackground(createRoundedDrawable(ContextCompat.getColor(getContext(), R.color.surface_muted), cornerRadius));
        int pad = (int) getResources().getDimension(R.dimen.spacing_compact);
        hexEditText.setPadding(pad, pad, pad, pad);
        LayoutParams editParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        editParams.topMargin = spacing;
        customPanel.addView(hexEditText, editParams);

        hueSeekBar.setMax(360);
        hueSeekBar.setProgress(180);
        satSeekBar.setMax(100);
        satSeekBar.setProgress(50);
        valSeekBar.setMax(100);
        valSeekBar.setProgress(80);

        addLabeledSeekBar("Hue", hueSeekBar);
        addLabeledSeekBar("Saturation", satSeekBar);
        addLabeledSeekBar("Brightness", valSeekBar);

        SeekBar.OnSeekBarChangeListener seekListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    updateFromHsv();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        };

        hueSeekBar.setOnSeekBarChangeListener(seekListener);
        satSeekBar.setOnSeekBarChangeListener(seekListener);
        valSeekBar.setOnSeekBarChangeListener(seekListener);

        hexEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Integer color = CustomColorParser.parseHex(s.toString());
                if (color != null) {
                    applyCustomColor(color, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void addLabeledSeekBar(String label, SeekBar seekBar) {
        TextView tv = new TextView(getContext());
        tv.setText(label);
        tv.setTextAppearance(R.style.TextAppearance_App_Secondary_Label);
        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.text_muted));
        customPanel.addView(tv);
        customPanel.addView(seekBar);
    }

    private void updateFromHsv() {
        float h = hueSeekBar.getProgress();
        float s = satSeekBar.getProgress() / 100f;
        float v = valSeekBar.getProgress() / 100f;
        int color = Color.HSVToColor(new float[]{h, s, v});
        applyCustomColor(color, true);
    }

    private void applyCustomColor(int color, boolean updateText) {
        float cornerRadius = getResources().getDimension(R.dimen.corner_small);
        customPreviewView.setBackground(createRoundedDrawable(color, cornerRadius));
        if (updateText) {
            hexEditText.setText(CustomColorParser.toHexString(color));
        }
        for (ImageView check : checkViews) {
            check.setVisibility(GONE);
        }
        selectedColor = new CustomColor(color);
        if (listener != null) {
            listener.onColorPicked(selectedColor);
        }
    }

    private void selectPaletteIndex(int index, String key) {
        for (int i = 0; i < checkViews.size(); i++) {
            checkViews.get(i).setVisibility(i == index ? VISIBLE : GONE);
        }
        selectedColor = new PaletteColor(key);
        if (listener != null) {
            listener.onColorPicked(selectedColor);
        }
    }

    public void setSelectedColor(@NonNull CourseColor color) {
        this.selectedColor = color;
        if (color instanceof PaletteColor) {
            String key = ((PaletteColor) color).getKey();
            List<CourseColorPalette.PaletteEntry> entries = CourseColorPalette.getEntries();
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).getKey().equals(key)) {
                    selectPaletteIndex(i, key);
                    break;
                }
            }
        } else if (color instanceof CustomColor) {
            int argb = ((CustomColor) color).getColorArgb();
            applyCustomColor(argb, true);
        }
    }

    @NonNull
    public CourseColor getSelectedColor() {
        return selectedColor;
    }

    public void setOnColorPickedListener(@Nullable OnColorPickedListener listener) {
        this.listener = listener;
    }

    private android.graphics.drawable.GradientDrawable createRoundedDrawable(int color, float radius) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        gd.setCornerRadius(radius);
        gd.setColor(color);
        return gd;
    }
}
