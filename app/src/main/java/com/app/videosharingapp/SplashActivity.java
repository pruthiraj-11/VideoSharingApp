package com.app.videosharingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Objects.requireNonNull(getSupportActionBar()).hide();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        sharedPreferences=getSharedPreferences("save", Context.MODE_PRIVATE);
        boolean flag=sharedPreferences.getBoolean("value",false);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if(flag){
                startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                finish();
            } else {
                editor=getSharedPreferences("save",MODE_PRIVATE).edit();
                editor.putBoolean("value",true);
                editor.apply();
                startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                finish();
            }
        }, 2000);
    }
}