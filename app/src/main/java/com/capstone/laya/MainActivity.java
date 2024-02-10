package com.capstone.laya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    private static final int RC_SIGN_IN = 100;
    CardView Login;
    TextView welcome;
    TextView subtext;
    TextView btntext;
    FirebaseAuth mAuth;
    FirebaseUser user;
    GoogleSignInAccount account;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //initialize variables
        Login = findViewById(R.id.login);
        subtext = findViewById(R.id.subtext);
        welcome = findViewById(R.id.welcome);
        btntext = findViewById(R.id.btntext);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("Login"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner


        mAuth = FirebaseAuth.getInstance();// Initialize Firebase Auth
        user = FirebaseAuth.getInstance().getCurrentUser();


         language = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getString("Language", "English");

        if(language.equals("Filipino")){

            btntext.setText("Magsimula");
            welcome.setText("Mabuhay!");
            subtext.setText("Halina't mag-usap!");

        } else if (language.equals("English")) {
            btntext.setText("Get Started");
        }
        //Begin Sign in request
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Login button click listener
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //display google sign in interface
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // google sign in failed
                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                System.out.println(e);
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
                            // Sign in success, update UI with the signed-in user's information
                            if (isNew) {
                                //if new user
                                Intent intent = new Intent(MainActivity.this, Register.class);
                                intent.putExtra("email", account.getEmail());//send email value in next activity
                                intent.putExtra("uid", user1.getUid());//send uid value in next activity
                                intent.putExtra("name", account.getDisplayName().substring(0, 1).toUpperCase() + account.getDisplayName().substring(1).toLowerCase());//send name value in next activity
                                intent.putExtra("language", language);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            } else {
                                //if existing user
                                progressDialog.show(); // Display Progress Dialog
                                user = FirebaseAuth.getInstance().getCurrentUser();
                                Intent i = new Intent(MainActivity.this, Dashboard.class);
                                i.putExtra("Welcome", "Welcome!");
                                i.putExtra("Language", language);
                                startActivity(i);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Sign-in Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.reload();
            progressDialog.cancel();
            startActivity(new Intent(MainActivity.this, Dashboard.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        } else {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                        System.exit(0);
                    }
                }).create().show();
    }
}