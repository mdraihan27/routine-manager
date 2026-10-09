package io.github.mdraihan27.routinemanager.core.designsystem.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import io.github.mdraihan27.routinemanager.R;

public class AnalogClockView extends View {

    public interface OnTimeSelectedListener {
        void onTimeChanged(int hour24, int minute);
        void onTimeSelectionComplete(Mode modeCompleted);
    }

    public enum Mode {
        HOUR,
        MINUTE
    }

    private final Paint dialPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint handPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint selectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Rect textBounds = new Rect();

    private int selectedHour12 = 9;
    private int selectedMinute = 0;
    private boolean isAm = true;
    private Mode currentMode = Mode.HOUR;

    private OnTimeSelectedListener listener;

    public AnalogClockView(@NonNull Context context) {
        this(context, null);
    }

    public AnalogClockView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnalogClockView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        dialPaint.setColor(ContextCompat.getColor(getContext(), R.color.surface_muted));
        dialPaint.setStyle(Paint.Style.FILL);

        handPaint.setColor(ContextCompat.getColor(getContext(), R.color.button_background));
        handPaint.setStyle(Paint.Style.STROKE);
        handPaint.setStrokeWidth(getResources().getDimension(R.dimen.elevation_low) * 1.5f);
        handPaint.setStrokeCap(Paint.Cap.ROUND);

        centerPaint.setColor(ContextCompat.getColor(getContext(), R.color.button_background));
        centerPaint.setStyle(Paint.Style.FILL);

        selectionPaint.setColor(ContextCompat.getColor(getContext(), R.color.button_background));
        selectionPaint.setStyle(Paint.Style.FILL);

        textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_on_surface));
        textPaint.setTextSize(getResources().getDimension(R.dimen.text_size_body));
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setOnTimeSelectedListener(@Nullable OnTimeSelectedListener listener) {
        this.listener = listener;
    }

    public void setMode(@NonNull Mode mode) {
        this.currentMode = mode;
        invalidate();
    }

    @NonNull
    public Mode getMode() {
        return currentMode;
    }

    public void setTime(int hour24, int minute) {
        this.isAm = hour24 < 12;
        int h12 = hour24 % 12;
        this.selectedHour12 = h12 == 0 ? 12 : h12;
        this.selectedMinute = Math.max(0, Math.min(59, minute));
        invalidate();
        notifyListener();
    }

    public void setIsAm(boolean am) {
        this.isAm = am;
        invalidate();
        notifyListener();
    }

    public boolean isAm() {
        return isAm;
    }

    public int getHour24() {
        int h = selectedHour12 % 12;
        return isAm ? h : h + 12;
    }

    public int getMinute() {
        return selectedMinute;
    }

    public int getHour12() {
        return selectedHour12;
    }

    private void notifyListener() {
        if (listener != null) {
            listener.onTimeChanged(getHour24(), selectedMinute);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int defaultSize = (int) getResources().getDimension(R.dimen.analog_clock_size);
        int w = resolveSize(defaultSize, widthMeasureSpec);
        int h = resolveSize(defaultSize, heightMeasureSpec);
        int size = Math.min(w, h);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) * 0.92f;

        canvas.drawCircle(centerX, centerY, radius, dialPaint);

        float numbersRadius = radius * 0.75f;
        double currentAngleRad;

        if (currentMode == Mode.HOUR) {
            currentAngleRad = Math.toRadians((selectedHour12 % 12) * 30.0 - 90.0);
        } else {
            currentAngleRad = Math.toRadians(selectedMinute * 6.0 - 90.0);
        }

        float selX = (float) (centerX + numbersRadius * Math.cos(currentAngleRad));
        float selY = (float) (centerY + numbersRadius * Math.sin(currentAngleRad));

        float selRadius = radius * 0.16f;
        canvas.drawCircle(selX, selY, selRadius, selectionPaint);
        canvas.drawLine(centerX, centerY, selX, selY, handPaint);
        canvas.drawCircle(centerX, centerY, radius * 0.05f, centerPaint);

        if (currentMode == Mode.HOUR) {
            for (int h = 1; h <= 12; h++) {
                double angle = Math.toRadians(h * 30.0 - 90.0);
                float nx = (float) (centerX + numbersRadius * Math.cos(angle));
                float ny = (float) (centerY + numbersRadius * Math.sin(angle));

                String text = String.valueOf(h);
                textPaint.getTextBounds(text, 0, text.length(), textBounds);
                float textY = ny + textBounds.height() / 2f;

                if (h == selectedHour12) {
                    textPaint.setColor(ContextCompat.getColor(getContext(), R.color.on_button));
                } else {
                    textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_on_surface));
                }
                canvas.drawText(text, nx, textY, textPaint);
            }
        } else {
            for (int m = 0; m < 60; m += 5) {
                double angle = Math.toRadians(m * 6.0 - 90.0);
                float nx = (float) (centerX + numbersRadius * Math.cos(angle));
                float ny = (float) (centerY + numbersRadius * Math.sin(angle));

                String text = String.format(java.util.Locale.getDefault(), "%02d", m);
                textPaint.getTextBounds(text, 0, text.length(), textBounds);
                float textY = ny + textBounds.height() / 2f;

                if (m == (selectedMinute / 5) * 5 && Math.abs(selectedMinute - m) < 3) {
                    textPaint.setColor(ContextCompat.getColor(getContext(), R.color.on_button));
                } else {
                    textPaint.setColor(ContextCompat.getColor(getContext(), R.color.text_on_surface));
                }
                canvas.drawText(text, nx, textY, textPaint);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = event.getX() - (getWidth() / 2f);
            float dy = event.getY() - (getHeight() / 2f);
            double degrees = Math.toDegrees(Math.atan2(dy, dx)) + 90.0;
            if (degrees < 0) {
                degrees += 360.0;
            }

            if (currentMode == Mode.HOUR) {
                int hour = (int) Math.round(degrees / 30.0);
                if (hour == 0) {
                    hour = 12;
                }
                if (hour > 12) {
                    hour = 12;
                }
                selectedHour12 = hour;
            } else {
                int minute = (int) Math.round(degrees / 6.0);
                if (minute >= 60) {
                    minute = 0;
                }
                selectedMinute = minute;
            }

            invalidate();
            notifyListener();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (listener != null) {
                listener.onTimeSelectionComplete(currentMode);
            }
            return true;
        }
        return super.onTouchEvent(event);
    }
}
