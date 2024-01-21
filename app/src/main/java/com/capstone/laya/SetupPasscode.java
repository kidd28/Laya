package com.capstone.laya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class SetupPasscode extends AppCompatActivity {
    GoogleSignInClient mGoogleSignInClient;
    FirebaseUser user;
    PinView pinView1, pinView2;

    CardView next;
    String pin;
    String type;
    TextView error,done,setup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_passcode);
        user = FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(SetupPasscode.this, gso);


        pinView1 = findViewById(R.id.firstPinView);
        pinView2 = findViewById(R.id.SecondPinView);
        error = findViewById(R.id.error);
        done = findViewById(R.id.done);
        setup = findViewById(R.id.setup);
        next = findViewById(R.id.next);


        type = getIntent().getStringExtra("Type");
        if(type.equals("Update")){
            done.setVisibility(View.GONE);
            setup.setText("Enter new passcode");
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validatePin();
            }
        });

    }

    private void validatePin() {
        if (pinView1.getText().toString().isEmpty() || pinView1.getText().length() < 4) {
            error.setVisibility(View.VISIBLE);
            error.setText("Please Enter 4 digit pin");
        } else if (pinView2.getText().toString().isEmpty() || pinView2.getText().length() < 4) {
            error.setVisibility(View.VISIBLE);
            error.setText("Please Confirm the 4 digit pin");
        } else if (!pinView1.getText().toString().equals(pinView2.getText().toString())) {
            error.setVisibility(View.VISIBLE);
            error.setText("Confirmation passcode didn't match");
        } else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Pin", pinView2.getText().toString());
            reference.child(user.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Intent intent = new Intent(SetupPasscode.this, Dashboard.class);
                    startActivity(intent);
                    SetupPasscode.this.finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SetupPasscode.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }


    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(SetupPasscode.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (type.equals("New")) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(user.getUid());
            ref.removeValue();
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SetupPasscode.this, "Registration Cancelled", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            revokeAccess();
        }else if(type.equals("Update")){
            startActivity(new Intent(SetupPasscode.this, Dashboard.class));
            finish();
        }
    }
}