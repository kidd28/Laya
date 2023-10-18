package com.capstone.laya;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class SplashScreen extends AppCompatActivity {

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo = findViewById(R.id.imageView);
        int secondsDelayed = 1;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(SplashScreen.this,
                        MainActivity.class));
                finish();
            }
        }, secondsDelayed * 5000);
        Glide.with(this).load(R.drawable.logo).centerCrop().into(logo);
    }
}