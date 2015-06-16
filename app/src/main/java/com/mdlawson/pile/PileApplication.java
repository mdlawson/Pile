package com.mdlawson.pile;

import android.app.Application;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.Settings;

public class PileApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Settings logger = Logger.init("PILE");
        if (!BuildConfig.DEBUG) {
            logger.setLogLevel(LogLevel.NONE);
        }
    }
}
