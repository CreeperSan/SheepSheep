package com.littletree.mysunsheep.pref;

import android.content.Context;
import android.content.SharedPreferences;

import com.littletree.mysunsheep.application.SheepApplication;
import com.littletree.mysunsheep.common.SheepSchedulers;

import java.util.HashSet;

import io.reactivex.rxjava3.core.Observable;

public class PrefManager {

    private final static String PREF_CONFIG = "config";
    private final static String KEY_IS_MUTE = "is_mute";

    private static PrefManager instance = new PrefManager();

    private PrefManager() { }

    private SharedPreferences prefs;

    private HashSet<PrefUpdateListener<Boolean>> muteListener = new HashSet<>();

    public Observable<Boolean> init() {
        return Observable.just(0).observeOn(SheepSchedulers.io).map(val -> {
            prefs = SheepApplication.instance.getSharedPreferences(PREF_CONFIG, Context.MODE_PRIVATE);

            prefs.registerOnSharedPreferenceChangeListener((sp, key) -> {
                switch (key) {
                    case KEY_IS_MUTE: {
                        for (PrefUpdateListener<Boolean> listener : muteListener) {
                            listener.onUpdate(isMute());
                        }
                        break;
                    }
                }
            });

            return true;
        }).observeOn(SheepSchedulers.ui);
    }

    public boolean isMute() {
        return prefs.getBoolean(KEY_IS_MUTE, false);
    }

    public void setMute(boolean isMute) {
        prefs.edit().putBoolean(KEY_IS_MUTE, isMute).apply();
    }

    public void addMuteChangeListener(PrefUpdateListener<Boolean> listener) {
        muteListener.add(listener);
    }

    public void removeMuteChangeListener(PrefUpdateListener<Boolean> listener) {
        muteListener.remove(listener);
    }

}
