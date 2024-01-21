package com.capstone.laya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends AppCompatActivity {
    TextView name , email;
    FirebaseUser user;
    CircleImageView pfp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        pfp = findViewById(R.id.pfp);

        user = FirebaseAuth.getInstance().getCurrentUser();
        loadUserprofile();
    }

    private void loadUserprofile() {
        Glide.with(Account.this).load(user.getPhotoUrl()).into(pfp);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Name = ""+snapshot.child("Name").getValue();
                String Email = ""+snapshot.child("Email").getValue();
                name.setText(Name);
                email.setText(Email);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Account.this, Settings.class));
        finish();
    }
}