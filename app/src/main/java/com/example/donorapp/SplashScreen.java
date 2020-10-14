package com.example.donorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.example.donorapp.util.SessionManager;

public class SplashScreen extends AppCompatActivity {
    private static int SPLASH_TIME = 3000;
    SessionManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sm = new SessionManager(SplashScreen.this);

        new Handler().postDelayed(new Runnable() {
            @Override
                public void run() {
                    sm.checkLogin();
                    finish();
                }
            }, SPLASH_TIME
        );


    }
}
