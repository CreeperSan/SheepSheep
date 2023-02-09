package com.guuda.sheep.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.guuda.sheep.application.listener.ApplicationBackgroundListener;
import com.guuda.sheep.audio.AudioController;
import com.guuda.sheep.pref.PrefManager;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public class SheepApplication extends Application {

    public static SheepApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        initApplicationBackgroundListener();

        registerGameAudioController();
    }


    /***********************************************************************************************
     * 应用前台转后台的监听器
     */
    private HashSet<ApplicationBackgroundListener> backgroundListenerSet = new HashSet<>();
    private volatile AtomicInteger mForegroundActivityCount = new AtomicInteger(0);
    private void initApplicationBackgroundListener() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) { }
            @Override
            public void onActivityResumed(@NonNull Activity activity) { }
            @Override
            public void onActivityPaused(@NonNull Activity activity) { }
            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) { }
            @Override
            public void onActivityDestroyed(@NonNull Activity activity) { }
            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                int prevValue = mForegroundActivityCount.get();
                int currentValue = mForegroundActivityCount.incrementAndGet();
                onChange(prevValue, currentValue);
            }
            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                int prevValue = mForegroundActivityCount.get();
                int currentValue =  mForegroundActivityCount.decrementAndGet();
                onChange(prevValue, currentValue);
            }
            private void onChange(int prev, int current) {
                if (prev <= 0 && current > 0) {
                    // 从 0 变 1
                    for (ApplicationBackgroundListener listener : backgroundListenerSet) {
                        try {
                            listener.onForeground();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (prev > 0 && current <= 0) {
                    // 从 1 变 0
                    for (ApplicationBackgroundListener listener : backgroundListenerSet) {
                        try {
                            listener.onBackground();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
    public void registerBackgroundListener(ApplicationBackgroundListener listener) {
        backgroundListenerSet.add(listener);
    }
    public void unregisterBackgroundListener(ApplicationBackgroundListener listener) {
        backgroundListenerSet.remove(listener);
    }


    /***********************************************************************************************
     * 音频播放设置
     */
    private void registerGameAudioController() {
        registerBackgroundListener(new ApplicationBackgroundListener() {
            @Override
            public void onBackground() {
                if (!PrefManager.instance.isMute()) {
                    AudioController.instance.mute();
                }
            }

            @Override
            public void onForeground() {
                if(!PrefManager.instance.isMute()) {
                    AudioController.instance.unmute();
                }
            }
        });
    }

}
