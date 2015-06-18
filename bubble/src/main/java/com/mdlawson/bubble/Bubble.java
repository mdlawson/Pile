package com.mdlawson.bubble;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;

public class Bubble {

    Context context;
    View view;
    BubbleListener listener;
    WindowManager window;
    WindowManager.LayoutParams layout;
    SpringAnimator animator;
    Gravity gravity;

    public Bubble(View view) {
        this.view = view;
        context = view.getContext();
        window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        listener = new SimpleBubbleListener();
        animator = new SpringAnimator();
        view.setOnTouchListener(new TouchListener(context));
    }

    public void show() {
        layout = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        layout.gravity = Gravity.TOP | Gravity.LEFT;

        layout.x = 100;
        layout.y = 100;

        window.addView(view, layout);
    }

    private void move(float dx, float dy) {
        layout.x += dx;
        layout.y += dy;
        window.updateViewLayout(view, layout);
        listener.onMove(view, layout.x, layout.y);
    }

    private void moveTo(float x, float y) {
        layout.x = (int) x;
        layout.y = (int) y;
        window.updateViewLayout(view, layout);
        listener.onMove(view, layout.x, layout.y);
    }

    public void hide() {
        window.removeView(view);
        listener.onHide();

    }

    private void applySpring() {
        animator.animateTo(0,0);
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
                return true;
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    move(event.getRawX() - lastX, event.getRawY() - lastY);
                case MotionEvent.ACTION_DOWN:
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_UP:
                    applySpring();
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
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (velocityY < 0 && -velocityY > Math.abs(velocityX)) {
                Log.d("BUBBLE", "FLING DOWN");
            } else if (velocityX < 0) {
                Log.d("BUBBLE", "FLING RIGHT");
            } else {
                Log.d("BUBBLE", "FLING LEFT");
            }
            applySpring();
            return true;
        }
    }

    public class SpringAnimator extends SimpleSpringListener {

        SpringSystem system;
        Spring spring;
        double startX;
        double startY;

        public SpringAnimator() {
            system = SpringSystem.create();
            spring = system.createSpring();
            spring.addListener(this);
        }

        public void animateTo(float x, float y) {
//            double dx = layout.x - x;
//            double dy = layout.y - y;
//            double distance = Math.sqrt(dx*dx+dy*dy);
//            xRatio = dx/distance;
//            yRatio = dy/distance;
//            spring.setCurrentValue(distance);
//            spring.setEndValue(0);
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            double value = spring.getCurrentValue();
//            moveTo((float) (xRatio * value), (float) (yRatio * value));
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
