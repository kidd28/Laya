package com.capstone.laya;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capstone.laya.Adapter.ParentCategoryAdapter;
import com.capstone.laya.Model.CategoriesModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {
    FirebaseUser user;
    TextView logout;
    GoogleSignInClient mGoogleSignInClient;

    CardView parentalacess;
    TextView name, email, account, language, passcode, feedback, about,how,custtv;
    CircleImageView pfp;

    ImageView backwhite;
    String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        parentalacess = findViewById(R.id.parentalaccess);
        backwhite = findViewById(R.id.backwhite);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        pfp = findViewById(R.id.pfp);
        logout = findViewById(R.id.logout);
        account = findViewById(R.id.account);
        language = findViewById(R.id.language);
        passcode = findViewById(R.id.passcode);
        how = findViewById(R.id.how);
        feedback = findViewById(R.id.feedback);
        about = findViewById(R.id.about);
        custtv = findViewById(R.id.custtv);

        user = FirebaseAuth.getInstance().getCurrentUser();
        parentalacess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, ParentalAccess.class));
                finish();
            }
        });


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lang = "" + snapshot.child("Language").getValue();
                if(lang.equals("Filipino")){
                    account.setText("Account");
                    language.setText("Lengguwahe");
                    passcode.setText("I-set up ang passcode");
                    how.setText("Paano gamitin");
                    feedback.setText("Katugunan at Rekomendasyon");
                    logout.setText("Mag Sign out");
                    custtv.setText("I-customize ang AAC");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, Account.class));
                finish();
            }
        });
        passcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Settings.this, Passcode.class);
                i.putExtra("Intent","Passcode");
                startActivity(i);
                finish();
            }
        });
        backwhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.this, Dashboard.class));
                finish();
            }
        });

        language.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String option[] = {"English", "Filipino"};
                AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);
                builder.setTitle("Select Language");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                              setLanguage("English");
                                break;
                            case 1:
                                setLanguage("Filipino");
                                break;
                        }
                    }
                });
                builder.create().show();
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

    private void setLanguage(String language) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Language", language);
        reference.child(user.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Settings.this, "Language changed successfully ",Toast.LENGTH_SHORT).show();
            }
        });

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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Settings.this, Dashboard.class));
        finish();
    }
}