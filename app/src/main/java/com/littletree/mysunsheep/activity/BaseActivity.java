package com.littletree.mysunsheep.activity;

import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {


    protected void toast(String message) {
        Toast.makeText(BaseActivity.this, message, Toast.LENGTH_SHORT).show();
    }

}
