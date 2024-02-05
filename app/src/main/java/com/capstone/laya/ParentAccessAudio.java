package com.capstone.laya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.capstone.laya.Adapter.AudioAdapter;
import com.capstone.laya.Adapter.ParentAuidoAdapter;
import com.capstone.laya.Model.AudioModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class ParentAccessAudio extends AppCompatActivity {
    ImageView back, add;
    TextView tv;
    RecyclerView rv;
    ParentAuidoAdapter adapter;
    ArrayList<AudioModel> audioModels;
    String category;
    FirebaseUser user;
    String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_access_audio);
    //    FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        back = findViewById(R.id.back);
        add = findViewById(R.id.add);
        tv = findViewById(R.id.categoryName);



        Glide.with(this).load(R.drawable.back).centerCrop().into(back);
        Glide.with(this).load(R.drawable.add).centerCrop().into(add);
        category = getIntent().getExtras().getString("Category");
        tv.setText(category);

        user = FirebaseAuth.getInstance().getCurrentUser();
        rv = findViewById(R.id.rv);

        audioModels = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);

        rv.setLayoutManager(layoutManager);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ParentAccessAudio.this, AddAudio.class);
                i.putExtra("Category", category);
                startActivity(i);
            }
        });
        loadAudio();
        loadAudioAddedbyUser();
    }
    private void loadAudioAddedbyUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AudioAddedByUser").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (Objects.equals(snap.child("Category").getValue(), category)) {
                        AudioModel model = snap.getValue(AudioModel.class);
                        audioModels.add(model);
                    }
                }
                adapter = new ParentAuidoAdapter(ParentAccessAudio.this, audioModels);
                rv.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadAudio() {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedAudio").child("English");
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (Objects.equals(snap.child("Category").getValue(), category)) {
                            AudioModel model = snap.getValue(AudioModel.class);
                            audioModels.add(model);
                        }
                    }
                    AudioAdapter audioAdapter = new AudioAdapter(ParentAccessAudio.this, audioModels);
                    rv.setAdapter(audioAdapter);
                    loadAudioAddedbyUser();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("ProvidedAudio").child("Filipino");
            reference1.keepSynced(true);
            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (Objects.equals(snap.child("Category").getValue(), category)) {
                            AudioModel model = snap.getValue(AudioModel.class);
                            audioModels.add(model);
                        }
                    }
                    AudioAdapter audioAdapter = new AudioAdapter(ParentAccessAudio.this, audioModels);
                    rv.setAdapter(audioAdapter);
                    loadAudioAddedbyUser();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ParentAccessAudio.this, ParentalAccess.class);
        i.putExtra("Category", category);
        startActivity(i);
        finish();
    }
}