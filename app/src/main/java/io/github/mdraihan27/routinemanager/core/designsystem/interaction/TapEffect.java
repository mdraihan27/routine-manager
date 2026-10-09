package io.github.mdraihan27.routinemanager.core.designsystem.interaction;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public final class TapEffect {

    private static final float PRESSED_SCALE = 0.96f;
    private static final float NORMAL_SCALE = 1.0f;

    private static float currentDampingRatio = SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY;
    private static float currentStiffness = SpringForce.STIFFNESS_LOW;

    private TapEffect() {
    }

    public static void setMotionIntensity(float dampingRatio, float stiffness) {
        currentDampingRatio = dampingRatio;
        currentStiffness = stiffness;
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void attach(@NonNull View view) {
        final SpringAnimation scaleXAnimation = createSpring(view, DynamicAnimationCompat.SCALE_X);
        final SpringAnimation scaleYAnimation = createSpring(view, DynamicAnimationCompat.SCALE_Y);

        final View.OnTouchListener originalListener = null;

        view.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    scaleXAnimation.getSpring().setDampingRatio(currentDampingRatio);
                    scaleXAnimation.getSpring().setStiffness(currentStiffness);
                    scaleYAnimation.getSpring().setDampingRatio(currentDampingRatio);
                    scaleYAnimation.getSpring().setStiffness(currentStiffness);
                    scaleXAnimation.animateToFinalPosition(PRESSED_SCALE);
                    scaleYAnimation.animateToFinalPosition(PRESSED_SCALE);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    scaleXAnimation.animateToFinalPosition(NORMAL_SCALE);
                    scaleYAnimation.animateToFinalPosition(NORMAL_SCALE);
                    break;
                default:
                    break;
            }
            if (originalListener != null) {
                return originalListener.onTouch(v, event);
            }
            return false;
        });
    }

    private static SpringAnimation createSpring(View view, FloatPropertyCompat<View> property) {
        SpringAnimation animation = new SpringAnimation(view, property);
        SpringForce force = new SpringForce();
        force.setDampingRatio(currentDampingRatio);
        force.setStiffness(currentStiffness);
        animation.setSpring(force);
        return animation;
    }

    private static final class DynamicAnimationCompat {
        private static final FloatPropertyCompat<View> SCALE_X = SpringAnimation.SCALE_X;
        private static final FloatPropertyCompat<View> SCALE_Y = SpringAnimation.SCALE_Y;
    }
}
