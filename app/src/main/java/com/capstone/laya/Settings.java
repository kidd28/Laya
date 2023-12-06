package com.capstone.laya;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capstone.laya.Adapter.ParentCategoryAdapter;
import com.capstone.laya.Model.CategoriesModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {
    FirebaseUser user;
    Button logout;
    GoogleSignInClient mGoogleSignInClient;

    CardView parentalacess;
    TextView name, email;
    CircleImageView pfp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        parentalacess = findViewById(R.id.parentalaccess);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        pfp = findViewById(R.id.pfp);
        logout = findViewById(R.id.logout);
        parentalacess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, ParentalAccess.class));
                finish();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(Settings.this, gso);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Settings.this)
                        .setTitle("Log out")
                        .setMessage("Are you sure you want to exit?")
                        .setNegativeButton(android.R.string.no, null)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                revokeAccess();
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(Settings.this, MainActivity.class));
                                finish();
                            }
                        }).create().show();
            }
        });
        user = FirebaseAuth.getInstance().getCurrentUser();

        loadUserprofile();
    }

    private void loadUserprofile() {
        Glide.with(Settings.this).load(user.getPhotoUrl()).into(pfp);
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
    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(Settings.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }
}