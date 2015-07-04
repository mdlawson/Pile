package com.mdlawson.bubble;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Scroller;

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
        layout.gravity = Gravity.BOTTOM | Gravity.LEFT;

        spring = system.createSpring();
        spring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                float value = (float) spring.getCurrentValue();
                target.setTranslationX((1 - value) * targetX);
                target.setTranslationY(value * (targetYHidden - targetY));
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
                spring.setCurrentValue(1);
                spring.setEndValue(0);
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
                spring.setEndValue(1);
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

    public void onBubbleMove(float x, float y) {
        float dx = x - target.getX() - targetX;
        float dy = y - target.getY() - targetY;
        targetX = dx * 0.2f;
        targetY = dy * 0.2f;
        if (!enter.isRunning() && !exit.isRunning() && spring.isAtRest()) {
            target.setTranslationX(targetX);
            target.setTranslationY(targetY);
        }
    }
}
