package com.littletree.mysunsheep.route;

import android.content.Context;
import android.content.Intent;

import com.littletree.mysunsheep.MainActivity;
import com.littletree.mysunsheep.activity.SplashActivity;

public class SheepRoute {

    private SheepRoute() { }


    public static void toMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

}
