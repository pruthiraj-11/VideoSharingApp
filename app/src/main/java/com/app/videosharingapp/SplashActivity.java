package com.app.videosharingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        sharedPreferences=getSharedPreferences("appCache", Context.MODE_PRIVATE);
        boolean flag=sharedPreferences.getBoolean("isIntroSeen",false);

        Thread thread= new Thread(() -> {
            try {
                Thread.sleep(2000);
                if(flag){
                    startActivity(new Intent(SplashActivity.this, AuthActivity.class));
                    finish();
                } else {
                    editor=getSharedPreferences("appCache",MODE_PRIVATE).edit();
                    editor.putBoolean("isIntroSeen",true);
                    editor.apply();
                    startActivity(new Intent(SplashActivity.this, IntroActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        });
        thread.start();
    }
}