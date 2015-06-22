package com.mdlawson.bubble;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;

public class WindowView {
    WindowManager window;
    WindowManager.LayoutParams layout;
    Context context;
    View view;
    public WindowView(View view) {
        this.view = view;
        context = view.getContext();
        window = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        layout = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                        | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
    }

    public void show() {
        window.addView(view, layout);
    }

    public void hide() {
        window.removeView(view);
    }

    protected void render() {
        window.updateViewLayout(view, layout);
    }

}
