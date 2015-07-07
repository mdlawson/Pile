package com.mdlawson.bubble;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

public class Bubble extends WindowView {
    public static final float HIDE_PROPORTION = 0.2f;
    BubbleListener listener;
    SpringAnimator animator;
    Remover remover;
    DisplayMetrics dm;
    float hidePosition;
    boolean isRemoving;

    public Bubble(View view) {
        super(view);

        layout.gravity = Gravity.CENTER | Gravity.BOTTOM;

        listener = new SimpleBubbleListener();
        view.setOnTouchListener(new TouchListener(context));

        SpringSystem system = SpringSystem.create();
        animator = new SpringAnimator(system);
        remover = new Remover(context, system);

        dm = new DisplayMetrics();
        window.getDefaultDisplay().getMetrics(dm);
        hidePosition = (dm.widthPixels / 2) + (view.getWidth() * HIDE_PROPORTION);
    }

    @Override
    public void show() {
        remover.reset();
        super.show();
        animator.flingWith(0, 0);
    }

    private void move(float dx, float dy) {
        layout.x += Math.round(dx);
        layout.y -= Math.round(dy);
        remover.onBubbleMove(layout, dx, dy);
        render();
        listener.onMove(view, layout.x, layout.y);
    }

    @Override
    public void hide() {
        super.hide();
        listener.onHide();
    }

    private boolean isCloseToRemover() {
        float dx = layout.x - remover.getTargetX();
        float dy = layout.y - remover.getTargetY();
        System.out.printf("dx: %f dy: %f\n", dx, dy);
        return Math.sqrt(dx*dx+dy*dy) < 50;
    }

    private void checkShouldSpringToRemover(float fromX, float fromY) {
        if (!isRemoving && isCloseToRemover()) {
            isRemoving = true;
            System.out.printf("close\n");
            animator.animateTo(remover.getTargetX(), remover.getTargetY());
        }
        else if (isRemoving && !isCloseToRemover()) {
//            animator.animateTo(fromX, dm.heightPixels - fromY);
        }
    }

    private class TouchListener extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {

        GestureDetector gestureDetector;
        float lastX;
        float lastY;

        public TouchListener(Context context) {
            this.gestureDetector = new GestureDetector(context, this);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (gestureDetector.onTouchEvent(event)) {
                remover.hide();
                return true;
            }
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    remover.show();
                    move(event.getRawX() - lastX, event.getRawY() - lastY);
                    checkShouldSpringToRemover(event.getRawX(), event.getRawY());
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_UP:
                    animator.flingWith(0,0);
                    remover.hide();
                    return true;
            }
            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d("BUBBLE", "TAP");
            listener.onClick(view, (int) e.getRawX(), (int) e.getRawY());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            remover.show();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (velocityY < 0 && -velocityY > Math.abs(velocityX)) {
                Log.d("BUBBLE", "FLING DOWN");
            } else if (velocityX < 0) {
                Log.d("BUBBLE", "FLING RIGHT");
            } else {
                Log.d("BUBBLE", "FLING LEFT");
            }
            animator.flingWith(velocityX, velocityY);
            return true;
        }
    }

    public class SpringAnimator extends SimpleSpringListener {

        Spring springX;
        Spring springY;
        double lastX;
        double lastY;
        double dx;
        double dy;
        boolean isFling;

        public SpringAnimator(SpringSystem system) {
            springX = system.createSpring();
            springY = system.createSpring();
            springX.addListener(this);
            springY.addListener(this);
        }

        public void animateTo(float x, float y) {
            dx = layout.x - x;
            dy = layout.y - y;
            lastX = 0;
            springX.setCurrentValue(0);
            springX.setEndValue(1);
        }

        public void flingWith(float dx, float dy) {
            isFling = true;
            animateTo(Math.copySign(hidePosition, layout.x), layout.y);
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            double diff = lastX - (lastX = spring.getCurrentValue());
            move((float) (dx * diff), (float) (dy * diff));
        }

        @Override
        public void onSpringAtRest(Spring spring) {
            isFling = false;
            if (isRemoving && !isCloseToRemover()) isRemoving = false;
        }
    }

    public static class Builder {
        Bubble bubble;

        public Builder(View view) {
            bubble = new Bubble(view);
        }

        public Builder setListener(BubbleListener listener) {
            bubble.listener = listener;
            return this;
        }

        public Bubble build() {
            return bubble;
        }

    }

    public interface BubbleListener {

        void onMove(View icon, int x, int y);

        void onClick(View icon, int x, int y);

        void onHide();
    }

    public static class SimpleBubbleListener implements BubbleListener {

        @Override
        public void onMove(View icon, int x, int y) {
        }

        @Override
        public void onClick(View icon, int x, int y) {
        }

        @Override
        public void onHide() {
        }
    }
}
