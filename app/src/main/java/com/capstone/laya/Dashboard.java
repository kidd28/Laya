package com.capstone.laya;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.capstone.laya.fragments.CategoryFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Dashboard extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;


    private int currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(null);//!!!
        setSupportActionBar(toolbar); //!!!


        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(Dashboard.this, gso);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.fragmentView, CategoryFragment.class, null).commit();
        }

    }


    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(Dashboard.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    //<<<
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.parental_access) {
            startActivity(new Intent(Dashboard.this, ParentalAccess.class));
            finish();
        }
        if (id == R.id.settings) {
            startActivity(new Intent(Dashboard.this, Settings.class));
            finish();
        }
        if (id == R.id.rotate) {
            if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                currentOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            }
        }
        return true;
    } ///>>>>


}