package com.capstone.laya;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class HowToUse extends AppCompatActivity {
ImageView img2;
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_to_use);
        img2= findViewById(R.id.img2);

        Glide.with(this).load(getDrawable(R.drawable.img2)).into(img2);
    }
}