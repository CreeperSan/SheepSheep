package com.guuda.sheep.customview;

import android.view.View;


public abstract class NoFastClickListener implements View.OnClickListener {
    private long mLastClickTime;

    private final static long TIME_INTERVAL = 1000L;

    public NoFastClickListener() { }

    @Override
    public void onClick(View v) {
        long nowTime = System.currentTimeMillis();
        if (nowTime - mLastClickTime > TIME_INTERVAL) {
            // 单次点击事件
            onSingleClick();
            mLastClickTime = nowTime;
        }
    }

    protected abstract void onSingleClick();
}