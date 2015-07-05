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
    float targetYHidden;
    float targetX;
    float targetY;
    Spring spring;

    public Remover(Context context, SpringSystem system) {
        super(LayoutInflater.from(context).inflate(R.layout.remove_layout, null));
        target = view.findViewById(R.id.target);
        targetYHidden = target.getTranslationY();

        layout.width = WindowManager.LayoutParams.MATCH_PARENT;
        layout.gravity = Gravity.BOTTOM | Gravity.CENTER;

        spring = system.createSpring();
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                updateTarget();
            }
        });
        setEnterAnimator(AnimatorInflater.loadAnimator(context, R.animator.fade_in));
        setExitAnimator(AnimatorInflater.loadAnimator(context, R.animator.fade_out));

    }

    private void setEnterAnimator(Animator animator) {
        enter = animator;
        enter.setTarget(view);
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
        exit.setTarget(view);
        exit.addListener(new AnimatorListenerAdapter() {
            boolean canceled = false;

            @Override
            public void onAnimationCancel(Animator animation) {
                canceled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!canceled) Remover.super.hide();
                canceled = false;
            }

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

    public boolean isClose() {
        float dx = target.getTranslationX();
        float dy = target.getTranslationY();
        float d = (float) Math.sqrt(dx * dx + dy * dy);
        return d < 50;
    }


}
