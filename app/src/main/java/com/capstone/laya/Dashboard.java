package com.capstone.laya;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capstone.laya.Adapter.AudioAdapter;
import com.capstone.laya.Adapter.SelectedAudioAdapter;
import com.capstone.laya.Model.AudioModel;
import com.capstone.laya.fragments.CategoryFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Dashboard extends AppCompatActivity {
    FirebaseDatabase database;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;

    ImageView speak, clear, setting, rotate;

    StorageReference storageRef;
    private int currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    float actVolume, maxVolume, volume;
    ArrayList<AudioModel> audioModels;
    RecyclerView rv;
    SelectedAudioAdapter audioAdapter;

    SoundPool soundPool;
    int count = 0;
    boolean play;
    int soundId;
    boolean loaded;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(null);//!!!
        setSupportActionBar(toolbar); //!!!

        rv = findViewById(R.id.rv);
        clear = findViewById(R.id.clear);
        speak = findViewById(R.id.speak);
        setting = findViewById(R.id.setting);
        rotate = findViewById(R.id.rotate);
        rv = findViewById(R.id.rv);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        loaded = false;


        Glide.with(this).load(R.drawable.trash).into(clear);
        Glide.with(this).load(R.drawable.speaking).into(speak);
        play = true;
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

        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Dashboard.this, Settings.class));
////            finish();
            }
        });

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    currentOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                }
            }
        });

        download();
    }

    private void speak() {
        /**MediaPlayer mp = new MediaPlayer();
         mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
         try {
         mp.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/AudioAAC/" + audioModels.get(count).getId() + ".mp3");
         mp.prepare();
         mp.start();
         mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
        @Override public void onCompletion(MediaPlayer mp) {
        mp.stop();
        mp.reset();
        count++;
        if (count < audioModels.size()) {
        try {
        mp.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/AudioAAC/" + audioModels.get(count).getId() + ".mp3");
        mp.prepare();
        mp.start();
        } catch (IOException e) {
        throw new RuntimeException(e);
        }
        } else {
        count = 0;
        }
        }
        });
         } catch (Exception e) {
         e.printStackTrace();
         }**/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(audioModels.size())
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else {
            soundPool = new SoundPool(audioModels.size(), AudioManager.STREAM_MUSIC, 0);
        }
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        soundId = soundPool.load(Environment.getExternalStorageDirectory().getPath() + "/AudioAAC/" + audioModels.get(count).getId() + ".mp3", 1);


        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                Log.i("OnLoadCompleteListener", "Sound " + sampleId + " loaded.");
                loaded = true;
                soundPool.play(soundId, 1, 1, 1, 0, 1f);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        count++;
                        if (count < audioModels.size()) {
                            soundId = soundPool.load(Environment.getExternalStorageDirectory().getPath() + "/AudioAAC/" + audioModels.get(count).getId() + ".mp3", 1);
                        } else {
                            count = 0;
                        }
                    }
                }, 500);
            }
        });

        /**  MediaPlayer mp = new MediaPlayer();
         mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
         for (AudioModel audioModel : audioModels) {
         try {
         mp.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/AudioAAC/" + audioModel.getId() + ".mp3");
         mp.prepare();
         mp.start();
         } catch (Exception e) {
         e.printStackTrace();
         }
         mp.reset();
         }**/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
        soundPool = null;
    }


    public void additem(String id, String category) {
        System.out.println(id);
        System.out.println(category);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedAudio");
        reference.keepSynced(true);
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
        reference.keepSynced(true);
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


    private void download() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedAudio");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    AudioModel model = snap.getValue(AudioModel.class);
                    downloadFile(model.getFileLink(), model.getId());
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
                    AudioModel model = snap.getValue(AudioModel.class);
                    downloadFile(model.getFileLink(), model.getId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void downloadFile(String fileLink, String id) {
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileLink);

        File localFile = new File(Environment.getExternalStorageDirectory(), "/AudioAAC");
        if (!localFile.exists()) {
            localFile.mkdirs();
        } else {
            File audiofile = new File(Environment.getExternalStorageDirectory(), "/AudioAAC/" + id + ".mp3");
            if (!audiofile.exists()) {
                storageRef.getFile(audiofile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Download success, " + id);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            } else {
                System.out.println("audio exist");
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Storage permissions granted", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(this, "Storage permissions denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void audioPlayer(String path) {

        //set up MediaPlayer

    }

}