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
        layout.y = Math.round(remover.targetYHidden/2); // line up with remover initially to ease calculation
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

    private void checkShouldSpringToRemover() {

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
                    checkShouldSpringToRemover();
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

        public void animateTo(float dx, float dy) {
            this.dx = dx;
            this.dy = dy;
            lastX = 0;
            springX.setCurrentValue(0);
            springX.setEndValue(1);
        }

        public void flingWith(float dx, float dy) {
            isFling = true;
            animateTo(layout.x - Math.copySign(hidePosition, layout.x), 0);
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            double diff = lastX - (lastX = spring.getCurrentValue());
            move((float) (dx * diff), (float) (dy * diff));
        }

        @Override
        public void onSpringAtRest(Spring spring) {
            isFling = false;
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
