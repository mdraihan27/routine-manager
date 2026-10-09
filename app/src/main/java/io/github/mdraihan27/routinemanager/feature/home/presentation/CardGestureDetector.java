package io.github.mdraihan27.routinemanager.feature.home.presentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public final class CardGestureDetector {

    public interface OnCardGestureListener {
        void onSwipeRight();
        void onSwipeLeft();
        void onSwipeUp();
        void onSwipeDown();
        void onClick();
    }

    private static final int SWIPE_THRESHOLD = 80;
    private static final int SWIPE_VELOCITY_THRESHOLD = 80;

    private CardGestureDetector() {
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void attach(@NonNull View view, @NonNull OnCardGestureListener listener) {
        Context context = view.getContext();
        GestureDetector detector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                listener.onClick();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) {
                    return false;
                }
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            listener.onSwipeRight();
                        } else {
                            listener.onSwipeLeft();
                        }
                        return true;
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            listener.onSwipeDown();
                        } else {
                            listener.onSwipeUp();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

        view.setOnTouchListener((v, event) -> {
            boolean handled = detector.onTouchEvent(event);
            if (!handled && event.getAction() == MotionEvent.ACTION_UP) {
                v.performClick();
            }
            return handled;
        });
    }
}
