package com.mdlawson.bubble;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;

public class Bubble {
    public static final float HIDE_PROPORTION = 0.2f;
    Context context;
    View view;
    BubbleListener listener;
    WindowManager window;
    WindowManager.LayoutParams layout;
    SpringAnimator animator;
    Gravity gravity;
    DisplayMetrics dm;

    public Bubble(View view) {
        this.view = view;
        context = view.getContext();
        window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        listener = new SimpleBubbleListener();
        animator = new SpringAnimator();
        view.setOnTouchListener(new TouchListener(context));
        dm = new DisplayMetrics();
        window.getDefaultDisplay().getMetrics(dm);
    }

    public void show() {
        layout = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        layout.gravity = Gravity.TOP | Gravity.LEFT;

        layout.x = 100;
        layout.y = 100;

        window.addView(view, layout);
        animator.flingWith(0,0);
    }

    private void move(float dx, float dy) {
        layout.x += Math.round(dx);
        layout.y += Math.round(dy);
        window.updateViewLayout(view, layout);
        listener.onMove(view, layout.x, layout.y);
    }

    private void moveTo(float x, float y) {
        layout.x = Math.round(x);
        layout.y = Math.round(y);
        window.updateViewLayout(view, layout);
        listener.onMove(view, layout.x, layout.y);
        //move(x - layout.x, y - layout.y); //TODO merge
    }

    public void hide() {
        window.removeView(view);
        listener.onHide();

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
                    animator.flingWith(0,0);
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
            animator.flingWith(velocityX, velocityY);
            return true;
        }
    }

    public class SpringAnimator extends SimpleSpringListener {

        SpringSystem system;
        Spring springX;
        Spring springY;
        double lastX;
        double lastY;
        double dx;
        double dy;
        boolean isFling;

        public SpringAnimator() {
            system = SpringSystem.create();
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
            if (layout.x > (dm.widthPixels / 2) - view.getWidth() / 2) {
                animateTo(dm.widthPixels - ((1 - HIDE_PROPORTION) * view.getWidth()), layout.y);
            } else {
                animateTo(-HIDE_PROPORTION * view.getWidth(), layout.y);
            }
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
