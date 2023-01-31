package com.guuda.sheep.activity.main.receiver;

public interface GameLevelNotifyListener {

    public static final String LEVEL_1_PASS = "LEVEL_1_PASS";
    public static final String LEVEL_2_PASS = "LEVEL_2_PASS";

    void onNotify(String event);

}
