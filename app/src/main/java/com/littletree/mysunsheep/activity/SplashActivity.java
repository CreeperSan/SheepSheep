package com.littletree.mysunsheep.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.littletree.mysunsheep.R;
import com.littletree.mysunsheep.database.DatabaseManager;
import com.littletree.mysunsheep.route.SheepRoute;

import io.reactivex.rxjava3.disposables.Disposable;

public class SplashActivity extends BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);


        Disposable disposable = DatabaseManager.instance.init().subscribe(result -> {
            if (result) {
                SheepRoute.toMain(SplashActivity.this);
                finish();
            } else {
                toast("初始化失败");
            }
        }, throwable -> {
            throwable.printStackTrace();
            toast("初始化异常");
        });
    }
}
