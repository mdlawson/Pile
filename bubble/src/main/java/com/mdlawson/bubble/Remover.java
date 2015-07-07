package com.mdlawson.bubble;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

public class Remover extends WindowView {

    Animator enter;
    Animator exit;
    View target;
    View fade;
    float targetYHidden;
    float targetX;
    float targetY;
    Spring spring;

    public Remover(Context context, SpringSystem system) {
        super(LayoutInflater.from(context).inflate(R.layout.remove_layout, null));
        target = view.findViewById(R.id.target);
        fade = view.findViewById(R.id.fade);
        targetYHidden = target.getTranslationY();

        layout.width = layout.height = WindowManager.LayoutParams.MATCH_PARENT;
        layout.gravity = Gravity.BOTTOM | Gravity.CENTER;

        spring = system.createSpring();
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                updateTarget();
            }

            @Override
            public void onSpringAtRest(Spring spring) {
                if (spring.getCurrentValue() == 0) {
                    Remover.super.hide();
                }
            }
        });
        setEnterAnimator(AnimatorInflater.loadAnimator(context, R.animator.fade_in));
        setExitAnimator(AnimatorInflater.loadAnimator(context, R.animator.fade_out));

    }

    private void setEnterAnimator(Animator animator) {
        enter = animator;
        enter.setTarget(fade);
        enter.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                spring.setCurrentValue(0);
                spring.setEndValue(1);
            }
        });
    }

    private void setExitAnimator(Animator animator) {
        exit = animator;
        exit.setTarget(fade);
        exit.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                spring.setEndValue(0);
            }
        });
    }

    @Override
    public void show() {

        if (exit.isRunning()) {
            exit.cancel();
            enter.setupStartValues();
        }
        if (!isShown()) enter.start();
        super.show();
    }

    @Override
    public void hide() {
        if (enter.isRunning()) {
            enter.cancel();
            exit.setupStartValues();
        }
        exit.start();
    }
    public void onBubbleMove(WindowManager.LayoutParams layout, float dx, float dy) {
        targetX += dx * 0.2f;
        targetY += dy * 0.1f;
        if (spring.isAtRest()) updateTarget();
    }

    private void updateTarget() {
        float value = (float) spring.getCurrentValue();
        target.setTranslationX(value * targetX);
        target.setTranslationY((1 - value) * targetYHidden + value * targetY);
    }

    public void reset() {
        targetX = targetY = 0;
    }

    public float getTargetX() {
        return target.getX();
    }

    public float getTargetY() {
        return target.getY();
    }
}
