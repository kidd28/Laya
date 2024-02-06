package com.capstone.laya;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.audiofx.Equalizer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Dashboard extends AppCompatActivity {
    FirebaseDatabase database;
    String language;
    private static final int STORAGE_PERMISSION_CODE = 23;
    private static final int STORAGE_PERMISSION_CODE11 = 24;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;

    ImageView speak, clear, setting, rotate;

    StorageReference storageRef;
    private int currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    ArrayList<AudioModel> audioModels;
    RecyclerView rv;
    SelectedAudioAdapter audioAdapter;

    int count = 0;
    boolean play;
    boolean loaded;

    ProgressDialog progressDialog;
    MediaPlayer mp;
    MediaPlayer mp2;
    TextView hellouser;

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
        setting = findViewById(R.id.setting);
        rotate = findViewById(R.id.rotate);
        rv = findViewById(R.id.rv);
        hellouser = findViewById(R.id.hellouser);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");


        String newlanguage = getIntent().getStringExtra("Language");



        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                language = ""+snapshot.child("Language").getValue();
                if(newlanguage != null){
                    if(!newlanguage.equals(language)){
                        setLanguage(newlanguage);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getUserName();

        loaded = false;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Initializing AAC, please wait..");

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
                Intent i = new Intent(Dashboard.this, Passcode.class);
                i.putExtra("Intent", "Settings");
                startActivity(i);
            }
        });

        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    currentOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;

                    Fragment fragment = new CategoryFragment(); // replace your custom fragment class
                    Bundle bundle = new Bundle();

                    FragmentTransaction fragmentTransaction = Dashboard.this.getSupportFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.fragmentView, fragment);
                    fragmentTransaction.commit();;

                } else if(currentOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    currentOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

                    Fragment fragment = new CategoryFragment(); // replace your custom fragment class
                    Bundle bundle = new Bundle();
                    FragmentTransaction fragmentTransaction = Dashboard.this.getSupportFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.fragmentView, fragment);
                    fragmentTransaction.commit();
                }
            }
        });

        if (checkStoragePermissions()) {
            download();
        } else {
            requestForStoragePermissions();
        }

    }
    public static String getFirstName(String fullName) {
        String surname;
        try{
             surname = fullName.substring(0, fullName.indexOf(' '));
            surname = surname.substring(0, 1).toUpperCase() + surname.substring(1).toLowerCase();
            return surname;
        }catch (Exception e){
            return fullName;
        }
    }
    public boolean checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //Android is 11 (R) or above
            return Environment.isExternalStorageManager();
        } else {
            //Below android 11
            int write = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);

            return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED;
        }
    }
    private void setLanguage(String language) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Language", language);
        reference.child(user.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(Dashboard.this, "Language changed successfully ",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void requestForStoragePermissions() {
        //Android is 11 (R) or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // If you have access to the external storage, do whatever you need
            if (Environment.isExternalStorageManager()){
                download();
            }else{
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        STORAGE_PERMISSION_CODE11
                );
            }
        } else {
            //Below android 11
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                    },
                    STORAGE_PERMISSION_CODE
            );
        }

    }

    private void speak() {
        mp = new MediaPlayer();
        mp2 = new MediaPlayer();
        count = 0;
        try {
            mp.reset();
            mp.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/AudioAAC/" + audioModels.get(count).getId() + ".mp3");
            mp.prepare();
            count++;
            mp2.reset();
            mp2.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/AudioAAC/" + audioModels.get(count).getId() + ".mp3");
            mp2.prepare();
            mp.setNextMediaPlayer(mp2);
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mp.stop();
                    count++;
                    if (mp2 != null) {
                        mp2.start();
                        if (count < audioModels.size()) {
                            try {
                                mp.reset();
                                mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mp.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/AudioAAC/" + audioModels.get(count).getId() + ".mp3");
                                mp.prepare();
                                mp2.setNextMediaPlayer(mp);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            mp.stop();
                            mp.reset();
                            mp.release();
                            mp = null;
                            count = 0;
                        }
                    }

                }
            });
            mp2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mp2.stop();
                    count++;
                    if (mp != null) {
                        mp.start();
                        if (count < audioModels.size()) {
                            try {
                                mp2.reset();
                                mp2.setAudioStreamType(AudioManager.STREAM_MUSIC);
                                mp2.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/AudioAAC/" + audioModels.get(count).getId() + ".mp3");
                                mp2.prepare();
                                mp.setNextMediaPlayer(mp2);

                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            mp2.reset();
                            mp2.release();
                            mp2 = null;
                            count = 0;
                        }
                    }

                }
            });

        } catch (Exception e) {

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void additem(String id, String category) {
        System.out.println(id);
        System.out.println(category);

        if(language.equals("Filipino")){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedAudio").child("Filipino");
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
        } else if (language.equals("English")) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedAudio").child("English");
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
        }

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("AudioAddedByUser").child(user.getUid());
        reference2.keepSynced(true);
        reference2.addValueEventListener(new ValueEventListener() {
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
        progressDialog.show();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedAudio").child("English");
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
        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("ProvidedAudio").child("Filipino");
        reference2.addValueEventListener(new ValueEventListener() {
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
        progressDialog.dismiss();
    }

    private void downloadFile(String fileLink, String id) {
        storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(fileLink);
        File localFile = new File(Environment.getExternalStorageDirectory(), "/AudioAAC");
        if (!localFile.exists()) {
            localFile.mkdirs();
        } else {
            progressDialog.show();
            File audiofile = new File(Environment.getExternalStorageDirectory(), "/AudioAAC/" + id + ".mp3");
            if (!audiofile.exists()) {
                storageRef.getFile(audiofile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        System.out.println("Download success, " + id);
                        download();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull FileDownloadTask.TaskSnapshot snapshot) {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });
            } else {
                progressDialog.dismiss();
                System.out.println("audio exist");
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (read && write) {
                    download();
                    Toast.makeText(Dashboard.this, "Storage Permissions Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Dashboard.this, "Storage Permissions Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }else if(requestCode == STORAGE_PERMISSION_CODE11) {
            if (grantResults.length > 0) {
                try {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(intent, 2296);
                    download();
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 2296);
                }
            }
        }

    }

    public void getUserName() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               String Name = ""+snapshot.child("Name").getValue();
               hellouser.setText("Hello "+ getFirstName(Name));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
 }


}