package com.guuda.sheep.utils;

import android.util.DisplayMetrics;

import com.guuda.sheep.application.SheepApplication;


public class DimensionUtils {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dp2px(float dpValue) {
        final float scale = SheepApplication.instance.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dp(float pxValue) {
        final float scale = SheepApplication.instance.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenW(){
        DisplayMetrics displayMetrics = SheepApplication.instance.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    public static int getScreenH(){
        DisplayMetrics displayMetrics = SheepApplication.instance.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

}
