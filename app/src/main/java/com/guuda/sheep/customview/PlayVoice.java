package com.guuda.sheep.customview;

import android.content.Context;
import android.media.MediaPlayer;

import com.guuda.sheep.R;


public class PlayVoice {
    private static MediaPlayer clickMediaPlayer;

    public static void playClickVoice(Context context){
        try {
            if (null ==clickMediaPlayer){
                clickMediaPlayer= MediaPlayer.create(context, R.raw.effect_click);
            }
            clickMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //销毁声音
    public  static void destoryVoice(){
        if (null!= clickMediaPlayer){
            clickMediaPlayer.stop();
            clickMediaPlayer.release();
            clickMediaPlayer = null;
        }
    }
}
