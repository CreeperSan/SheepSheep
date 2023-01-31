package com.guuda.sheep.route;

import android.content.Context;
import android.content.Intent;

import com.guuda.sheep.MainActivity;

public class SheepRoute {

    private SheepRoute() { }


    public static void toMain(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

}
