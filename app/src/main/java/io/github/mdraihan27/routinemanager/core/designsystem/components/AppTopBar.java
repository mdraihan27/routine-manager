package io.github.mdraihan27.routinemanager.core.designsystem.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import io.github.mdraihan27.routinemanager.R;

public class AppTopBar extends FrameLayout {

    private final TextView titleTextView;

    public AppTopBar(@NonNull Context context) {
        this(context, null);
    }

    public AppTopBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AppTopBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        titleTextView = new TextView(context);
        init();
    }

    private void init() {
        int padding = (int) getResources().getDimension(R.dimen.spacing_medium);
        setPadding(padding, padding, padding, padding);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.background));

        titleTextView.setTextAppearance(R.style.TextAppearance_App_Primary_Headline);
        titleTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.text_on_surface));

        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.START | Gravity.CENTER_VERTICAL;
        addView(titleTextView, params);
    }

    public void setTitle(@NonNull CharSequence title) {
        titleTextView.setText(title);
    }

    public void setTitle(@StringRes int resId) {
        titleTextView.setText(resId);
    }
}
