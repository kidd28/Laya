package com.capstone.laya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


public class Passcode extends AppCompatActivity {
    FirebaseUser user;
    PinView pinView1;

    CardView confirm;
    String pin, language;

    TextView error, forgotpin, entPass,confirmtv;

    String nextintent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        pinView1 = findViewById(R.id.firstPinView);
        error = findViewById(R.id.error);
        confirm = findViewById(R.id.next);
        confirmtv = findViewById(R.id.confirmtv);
        forgotpin = findViewById(R.id.forgotpin);
        entPass = findViewById(R.id.entPass);
        user = FirebaseAuth.getInstance().getCurrentUser();

        nextintent = getIntent().getStringExtra("Intent");
        forgotpin.setPaintFlags(forgotpin.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        pinView1.setText("");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pin = snapshot.child("Pin").getValue().toString();
                language = snapshot.child("Language").getValue().toString();

                if (language.equals("Filipino")) {
                    entPass.setText("Maglagay ng passcode");
                    forgotpin.setText(getResources().getString(R.string.forgot_PinFil));

                    confirmtv.setText("Kumpirmahin");
                } else {
                    entPass.setText("Enter passcode");
                    forgotpin.setText(getResources().getString(R.string.forgot_Pin));
                    confirmtv.setText("Confirm");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePin(pin);
            }
        });

        forgotpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Passcode.this, ForgotPin.class));
            }
        });

    }

    private void validatePin(String pin) {
        if (pinView1.getText().toString().equals("") || pinView1.getText().toString().length() < 4) {

            if (language.equals("Filipino")) {
                error.setVisibility(View.VISIBLE);
                error.setText("Mangyaring maglagay ng 4-digit na passcode");
            } else {
                error.setVisibility(View.VISIBLE);
                error.setText("Please Enter 4 digit passcode");
            }

        } else if (!pinView1.getText().toString().equals(pin)) {
            if (language.equals("Filipino")) {
                error.setVisibility(View.VISIBLE);
                error.setText("Maling passcode, subukan muli!");
                pinView1.setText("");
            } else {
                error.setVisibility(View.VISIBLE);
                error.setText("Incorrect passcode, please try again!");
                pinView1.setText("");
            }
        } else {
            if (nextintent.equals("Settings")) {
                startActivity(new Intent(Passcode.this, Settings.class));
                finish();
            } else if (nextintent.equals("Passcode")) {
                Intent i = new Intent(Passcode.this, SetupPasscode.class);
                i.putExtra("Type", "Update");
                startActivity(i);
                finish();
            }

        }
    }
}