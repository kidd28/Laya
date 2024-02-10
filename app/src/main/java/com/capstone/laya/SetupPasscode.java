package com.capstone.laya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
    TextView error,done,setup,conPass,entPass,nxttv;
    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_passcode);


        user = FirebaseAuth.getInstance().getCurrentUser();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(SetupPasscode.this, gso);

        language = PreferenceManager.getDefaultSharedPreferences(SetupPasscode.this).getString("Language", "English");

        pinView1 = findViewById(R.id.firstPinView);
        pinView2 = findViewById(R.id.SecondPinView);
        error = findViewById(R.id.error);
        done = findViewById(R.id.done);
        setup = findViewById(R.id.setup);
        entPass = findViewById(R.id.entPass);
        conPass = findViewById(R.id.conPass);
        next = findViewById(R.id.next);
        nxttv = findViewById(R.id.nxttv);


        type = getIntent().getStringExtra("Type");
        if(type.equals("Update")|| type.equals("Reset")){
            done.setVisibility(View.GONE);
            if (language.equals("Filipino")) {
                setup.setText("Maglagay ng bagong Pin");
            } else {
                setup.setText("Enter new passcode");
            }

        }


        if (language.equals("Filipino")) {
            done.setText("Tayo'y malapit na, Magpatuloy!");
            setup.setText("Gumawa ng Passcode");
            entPass.setText("Maglagay ng Passcode");
            conPass.setText("Kumpirmahin ang Passcode");
            nxttv.setText("Magpatuloy");
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
                    if(type.equals("New")){
                    Intent intent = new Intent(SetupPasscode.this, SecurityQuestions.class);
                        intent.putExtra("set", "new");
                        intent.putExtra("language", language);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    SetupPasscode.this.finish();
                    }
                    if(type.equals("Update")|| type.equals("Reset")){
                        Intent intent = new Intent(SetupPasscode.this, Dashboard.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        SetupPasscode.this.finish();
                    }
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
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }
}