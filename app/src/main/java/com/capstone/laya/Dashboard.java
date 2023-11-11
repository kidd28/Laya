package com.capstone.laya;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.capstone.laya.Adapter.AudioAdapter;
import com.capstone.laya.Adapter.SelectedAudioAdapter;
import com.capstone.laya.Model.AudioModel;
import com.capstone.laya.fragments.CategoryFragment;
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
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Dashboard extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;

    ImageView speak, clear;


    private int currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;


    ArrayList<AudioModel> audioModels;
    RecyclerView rv;
    SelectedAudioAdapter audioAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(null);//!!!
        setSupportActionBar(toolbar); //!!!

        rv = findViewById(R.id.rv);
        clear = findViewById(R.id.clear);
        speak = findViewById(R.id.speak);
        rv = findViewById(R.id.rv);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        Glide.with(this).load(R.drawable.trash).into(clear);
        Glide.with(this).load(R.drawable.speaking).into(speak);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(Dashboard.this, gso);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true).add(R.id.fragmentView, CategoryFragment.class, null).commit();
        }


        audioModels = new ArrayList<>();
        audioAdapter = new SelectedAudioAdapter(Dashboard.this, audioModels);
        rv.setAdapter(audioAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(layoutManager);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioModels.clear();
                audioAdapter.notifyDataSetChanged();
            }
        });

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak();
            }
        });
    }

    private void speak() {

        for(AudioModel audio: audioModels){
            audio.getFileLink();
            System.out.println(audio.getFileLink());
            playAudio(audio.getFileLink());
        }

    }

    public void additem(String id, String category) {
        System.out.println(id);
        System.out.println(category);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedAudio");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (snap.child("Id").getValue().equals(id)) {
                        AudioModel model = snap.getValue(AudioModel.class);
                        audioModels.add(model);
                        audioAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("AudioAddedByUser").child(user.getUid());
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (Objects.equals(snap.child("Id").getValue(), id)) {
                        AudioModel model = snap.getValue(AudioModel.class);
                        audioModels.add(model);
                        audioAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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
    private void playAudio(String audioUrl) {
        // initializing media player
        MediaPlayer mediaPlayer = new MediaPlayer();

        // below line is use to set the audio stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // below line is use to set our
            // url to our media player.
            mediaPlayer.setDataSource(audioUrl);

            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare();
            mediaPlayer.start();

            // below line is use to display a toast message.
        } catch (IOException e) {
            // this line of code is use to handle error while playing our audio file.
        }
    }

}