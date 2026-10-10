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
        default boolean canSwipeRight() { return true; }
        default boolean canSwipeLeft() { return true; }
    }

    private static final int SWIPE_THRESHOLD = 80;
    private static final int SWIPE_VELOCITY_THRESHOLD = 80;

    private CardGestureDetector() {
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void attach(@NonNull View view, @NonNull OnCardGestureListener listener) {
        view.setOnTouchListener(new View.OnTouchListener() {
            private float initialX;
            private float initialY;
            private float initialTouchX;
            private float initialTouchY;
            private boolean isDragging;
            private long touchDownTime;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = v.getTranslationX();
                        initialY = v.getTranslationY();
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        isDragging = false;
                        touchDownTime = System.currentTimeMillis();
                        v.animate().cancel();
                        android.view.ViewParent parent = v.getParent();
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                        // Slight scale down on touch for liquid feel
                        v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(100).start();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float dx = event.getRawX() - initialTouchX;
                        float dy = event.getRawY() - initialTouchY;

                        if (!isDragging && (Math.abs(dx) > 15 || Math.abs(dy) > 15)) {
                            isDragging = true;
                        }

                        if (isDragging) {
                            // Apply resistance for a liquid, heavy feel
                            float translationX = initialX + dx * 0.6f;
                            float translationY = initialY + dy * 0.6f;

                            v.setTranslationX(translationX);
                            v.setTranslationY(translationY);

                            // Add a slight rotation for more fluidity
                            v.setRotation(translationX * 0.03f);
                        }
                        return true;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        v.animate().scaleX(1f).scaleY(1f).setDuration(150).start();

                        if (!isDragging) {
                            long duration = System.currentTimeMillis() - touchDownTime;
                            if (duration < 300) {
                                listener.onClick();
                                v.performClick();
                            }
                            resetView(v);
                            return true;
                        }

                        float finalDx = event.getRawX() - initialTouchX;
                        float finalDy = event.getRawY() - initialTouchY;

                        float thresholdX = v.getWidth() * 0.25f;
                        float thresholdY = v.getHeight() * 0.25f;

                        if (Math.abs(finalDx) > Math.abs(finalDy)) {
                            if (Math.abs(finalDx) > thresholdX) {
                                if (finalDx > 0) {
                                    if (listener.canSwipeRight()) {
                                        animateSwipe(v, v.getWidth(), 0, listener::onSwipeRight, -v.getWidth() * 0.5f);
                                    } else {
                                        resetView(v);
                                    }
                                } else {
                                    if (listener.canSwipeLeft()) {
                                        animateSwipe(v, -v.getWidth(), 0, listener::onSwipeLeft, v.getWidth() * 0.5f);
                                    } else {
                                        resetView(v);
                                    }
                                }
                            } else {
                                resetView(v);
                            }
                        } else {
                            if (Math.abs(finalDy) > thresholdY) {
                                if (finalDy > 0) {
                                    animateSwipe(v, 0, v.getHeight(), listener::onSwipeDown, -v.getHeight() * 0.5f);
                                } else {
                                    animateSwipe(v, 0, -v.getHeight(), listener::onSwipeUp, v.getHeight() * 0.5f);
                                }
                            } else {
                                resetView(v);
                            }
                        }

                        return true;
                }
                return false;
            }

            private void resetView(View v) {
                v.animate()
                        .translationX(0f)
                        .translationY(0f)
                        .rotation(0f)
                        .alpha(1f)
                        .setDuration(300)
                        .setInterpolator(new android.view.animation.OvershootInterpolator(1.2f))
                        .start();
            }

            private void animateSwipe(View v, float targetTx, float targetTy, Runnable action, float resetTx) {
                v.animate()
                        .translationX(targetTx)
                        .translationY(targetTy)
                        .alpha(0f)
                        .setDuration(200)
                        .withEndAction(() -> {
                            action.run();
                            // Reset state invisibly for the next item
                            v.setTranslationX(resetTx);
                            v.setTranslationY(0); // Only reset X for sliding back in effect, Y stays 0
                            v.setRotation(0);
                            v.animate()
                                    .translationX(0f)
                                    .translationY(0f)
                                    .alpha(1f)
                                    .setDuration(250)
                                    .setInterpolator(new android.view.animation.DecelerateInterpolator())
                                    .start();
                        })
                        .start();
            }
        });
    }
}
