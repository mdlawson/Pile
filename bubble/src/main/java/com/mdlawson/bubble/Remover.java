package com.mdlawson.bubble;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class Remover extends WindowView {

    Animator enter;
    Animator exit;
    View target;

    public Remover(Context context) {
        super(LayoutInflater.from(context).inflate(R.layout.remove_layout, null));
        target = view.findViewById(R.id.target);

        enter = AnimatorInflater.loadAnimator(context, R.animator.fade_in);
        enter.setTarget(view);
        enter.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                //view.setTranslationY();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }
        });
        exit = AnimatorInflater.loadAnimator(context, R.animator.fade_out);
        exit.setTarget(view);
        exit.addListener(new AnimatorListenerAdapter() {
            boolean canceled = false;

            @Override
            public void onAnimationCancel(Animator animation) {
                canceled = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (isShown() && !canceled) Remover.super.hide();
                canceled = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });
    }

    @Override
    public void show() {
        if (exit.isRunning()) {
            exit.cancel();
            enter.setupStartValues();
            enter.start();
        } else if (!isShown()){
            layout.width = WindowManager.LayoutParams.MATCH_PARENT;
            layout.gravity = Gravity.BOTTOM | Gravity.LEFT;
            super.show();
            enter.start();
        }
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

    }
}
