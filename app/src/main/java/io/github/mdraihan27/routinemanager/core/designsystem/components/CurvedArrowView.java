package io.github.mdraihan27.routinemanager.core.designsystem.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import io.github.mdraihan27.routinemanager.R;

public class CurvedArrowView extends View {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final Path arrowHead = new Path();

    public CurvedArrowView(@NonNull Context context) {
        this(context, null);
    }

    public CurvedArrowView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CurvedArrowView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text_on_surface));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(getResources().getDimension(R.dimen.elevation_low) * 1.5f);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) {
            return;
        }

        path.reset();
        path.moveTo(w * 0.2f, h * 0.2f);
        path.quadTo(w * 0.8f, h * 0.3f, w * 0.5f, h * 0.8f);
        canvas.drawPath(path, paint);

        arrowHead.reset();
        arrowHead.moveTo(w * 0.42f, h * 0.68f);
        arrowHead.lineTo(w * 0.5f, h * 0.82f);
        arrowHead.lineTo(w * 0.62f, h * 0.72f);
        canvas.drawPath(arrowHead, paint);
    }
}
