package com.guuda.sheep.application;

import android.app.Application;

public class SheepApplication extends Application {

    public static SheepApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


}
