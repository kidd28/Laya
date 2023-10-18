package com.capstone.laya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;

public class Register extends AppCompatActivity {
     EditText email;
    EditText name;
    EditText dob;
    EditText gender;
    Button register;

    FirebaseDatabase database;
    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference reference;

    String Email, Name, Dob, Gender,uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.EditText_email);
        name = findViewById(R.id.EditText_name);
        dob = findViewById(R.id.EditText_dob);
        gender = findViewById(R.id.EditText_Gender);
        register = findViewById(R.id.Register);

        Email = getIntent().getStringExtra("email");
        Name = getIntent().getStringExtra("name").toUpperCase(Locale.ROOT);
        uid = getIntent().getStringExtra("uid");

        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        email.setText(Email);
        name.setText(Name);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dob = dob.getText().toString().trim();
                Gender = dob.getText().toString().trim();
                user = FirebaseAuth.getInstance().getCurrentUser();
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("Email",Email );
                hashMap.put("Uid", uid);
                hashMap.put("Name", Name);
                hashMap.put("DateOfBirth", Dob);
                hashMap.put("Gender", Gender);
                reference.child(user.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        user.reload();
                        Toast.makeText(Register.this, "Registration complete!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Register.this, SelectLanguage.class);
                        startActivity(intent);
                        Register.this.finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



    }
}