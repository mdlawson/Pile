package com.mdlawson.pile.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
import android.widget.ImageView;

import com.mdlawson.bubble.Bubble;
import com.mdlawson.pile.R;
import com.orhanobut.logger.Logger;

public class BubbleService extends Service {

    Bubble bubble;

    @Override
    public void onCreate() {
        super.onCreate();
        ImageView iconView = new ImageView(this);
        iconView.setImageResource(R.mipmap.ic_launcher);
        bubble = new Bubble.Builder(iconView).build();
        Logger.d("Bubble service created");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        bubble.show();
        Logger.d("Bubble service started");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Logger.d("Bubble service stopped");
        bubble.hide();
    }
}
