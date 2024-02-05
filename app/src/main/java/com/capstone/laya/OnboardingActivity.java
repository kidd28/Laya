package com.capstone.laya;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.capstone.laya.fragments.OnboardingFirst;

public class OnboardingActivity extends AppCompatActivity {

    private IntroPref introPref;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding2);


        introPref = new IntroPref(this);
        if (!introPref.isFirstTimeLaunch()){
            startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
            finish();
        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.OnboardingFragmentView, OnboardingFirst.class, null).commit();
        }

    }
}