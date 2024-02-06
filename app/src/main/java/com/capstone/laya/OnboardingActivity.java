package com.capstone.laya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.capstone.laya.fragments.OnboardingFirst;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OnboardingActivity extends AppCompatActivity {

    private IntroPref introPref;
    FirebaseUser user;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding2);

        user = FirebaseAuth.getInstance().getCurrentUser();
        introPref = new IntroPref(this);
        if (!introPref.isFirstTimeLaunch()){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SecurityQuestions");
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.child(user.getUid()).exists()){
                        startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
                        finish();
                    }else {
                        startActivity(new Intent(OnboardingActivity.this, SecurityQuestions.class));
                        finish();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.OnboardingFragmentView, OnboardingFirst.class, null).commit();
        }

    }
}