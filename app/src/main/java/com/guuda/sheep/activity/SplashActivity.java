package com.guuda.sheep.activity;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.guuda.sheep.R;
import com.guuda.sheep.audio.AudioController;
import com.guuda.sheep.common.SheepSchedulers;
import com.guuda.sheep.database.DatabaseManager;
import com.guuda.sheep.exception.InitializeErrorException;
import com.guuda.sheep.pref.PrefManager;
import com.guuda.sheep.route.SheepRoute;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class SplashActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);


        Disposable disposable = Observable.just(0).observeOn(SheepSchedulers.io).concatMap(val -> {
            return PrefManager.instance.init();
        }).doOnError(throwable -> {
            throw new InitializeErrorException("配置初始化失败", throwable);
        }).concatMap(val -> {
            return DatabaseManager.instance.init();
        }).doOnError((throwable -> {
            throw new InitializeErrorException("数据库初始化失败", throwable);
        })).concatMap(val -> {
            return AudioController.instance.init();
        }).doOnError((throwable -> {
            throw new InitializeErrorException("音频初始化失败", throwable);
        })).observeOn(SheepSchedulers.ui).subscribe(result -> {
            if (result) {
                SheepRoute.toMain(SplashActivity.this);
                finish();
            } else {
                toast("初始化失败");
            }
        }, throwable -> {
            throwable.printStackTrace();
            toast("初始化异常，" + throwable.getMessage());
        });
    }
}
