package com.guuda.sheep.activity.main.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GameLevelReceiver extends BroadcastReceiver {
    public final static String ACTION = "com.guuda.sheep.receiver.GameLevelReceiver";

    private GameLevelNotifyListener listener;

    public GameLevelReceiver(GameLevelNotifyListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String eventStr = intent.getStringExtra("event");

        if (eventStr == null || listener == null) {
            return;
        }

        listener.onNotify(eventStr);
    }

}
