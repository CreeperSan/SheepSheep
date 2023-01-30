package com.littletree.mysunsheep.audio;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;

import com.littletree.mysunsheep.R;
import com.littletree.mysunsheep.application.SheepApplication;
import com.littletree.mysunsheep.common.SheepSchedulers;
import com.littletree.mysunsheep.pref.PrefManager;

import java.io.IOException;

import io.reactivex.rxjava3.core.Observable;

public class AudioController {
    public static AssetFileDescriptor BACKGROUND_MENU;
    public static AssetFileDescriptor BACKGROUND_GAME;

    public static int SOUND_CLICK;
    public static int SOUND_COMPLETE;
    public static int SOUND_FAIL;

    public static AudioController instance = new AudioController();

    private AudioController() { }

    private float volume = 0f;

    private MediaPlayer mediaPlayer;
    private SoundPool soundPool;


    /**
     * 初始化
     * @return
     */
    public Observable<Boolean> init() {
        return Observable.just(0).observeOn(SheepSchedulers.io).map(val -> {
            // 配置
            volume = PrefManager.instance.isMute() ? 0f : 1f;
            // 初始化背景音乐
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setLooping(true);
            BACKGROUND_MENU = SheepApplication.instance.getResources().openRawResourceFd(R.raw.background_menu);
            BACKGROUND_GAME = SheepApplication.instance.getResources().openRawResourceFd(R.raw.background_game);
            // 初始化音效
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(5)
                    .build();
            SOUND_CLICK = soundPool.load(SheepApplication.instance, R.raw.effect_click, 1);
            SOUND_COMPLETE = soundPool.load(SheepApplication.instance, R.raw.effect_complete, 1);
            SOUND_FAIL = soundPool.load(SheepApplication.instance, R.raw.effect_fail, 1);
            // 返回结果
            return true;
        }).observeOn(SheepSchedulers.ui);
    }

    public void playBackground(AssetFileDescriptor backgroundMusic) {
        if (mediaPlayer == null) {
            return;
        }

        try {
            mediaPlayer.reset();
            mediaPlayer.setLooping(true);
            mediaPlayer.setDataSource(backgroundMusic);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pauseBackground() {
        if (mediaPlayer == null) {
            return;
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void resumeBackground() {
        if (mediaPlayer == null) {
            return;
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
        }
    }

    public void playEffect(int effectSoundID) {
        soundPool.play(effectSoundID, volume, volume, 1, 0, 1);
    }

    public void mute() {
        volume = 0f;

        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }

    }

    public void unmute() {
        volume = 1f;

        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    public boolean toggleMute() {
        if (isMute()) {
            unmute();
        } else {
            mute();
        }

        return isMute();
    }

    public boolean isMute() {
        return volume <= 0;
    }

}
