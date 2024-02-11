package com.capstone.laya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

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

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(Account.this);
                View dialogview = inflater.inflate(R.layout.editname, null);
                final AlertDialog dialog = new AlertDialog.Builder(Account.this)
                        .setView(dialogview)
                        .setTitle("Input new AAC word")
                        .setPositiveButton("Save", null) //Set to null. We override the onclick
                        .setNegativeButton("Cancel", null)
                        .create();

                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button save = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        save.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                //What ever you want to do with the value
                                EditText YouEditTextValue = dialogview.findViewById(R.id.etName);
                                //OR
                                String name = YouEditTextValue.getText().toString();

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("Name", name);
                                reference.child(user.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                    }
                                });

                            }
                        });
                        startActivity(new Intent(Account.this, Settings.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();

                        Button cancel = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        cancel.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
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
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}