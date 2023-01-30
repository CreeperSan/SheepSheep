package com.littletree.mysunsheep.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.littletree.mysunsheep.R;
import com.littletree.mysunsheep.audio.AudioController;
import com.littletree.mysunsheep.common.SheepSchedulers;
import com.littletree.mysunsheep.database.DatabaseManager;
import com.littletree.mysunsheep.exception.InitializeErrorException;
import com.littletree.mysunsheep.route.SheepRoute;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class SplashActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


        Disposable disposable = Observable.just(0).observeOn(SheepSchedulers.io).concatMap(val -> {
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
            toast("初始化异常，" + throwable.getMessage());
        });
    }
}
