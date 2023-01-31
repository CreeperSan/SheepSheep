package com.guuda.sheep.customview;

import android.view.View;


public abstract class NoFastClickListener implements View.OnClickListener {
    private long mLastClickTime;
    private long timeInterval = 1000L;

    public NoFastClickListener() {

    }

    public NoFastClickListener(long interval) {
        this.timeInterval = interval;
    }

    @Override
    public void onClick(View v) {
        long nowTime = System.currentTimeMillis();
        if (nowTime - mLastClickTime > timeInterval) {
            // 单次点击事件
            onSingleClick();
            mLastClickTime = nowTime;
        }
    }

    protected abstract void onSingleClick();
}