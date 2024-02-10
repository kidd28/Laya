package com.capstone.laya;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static android.util.Log.e;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

import pereira.agnaldo.audiorecorder.AudioRecorderView;

public class EditAudio extends AppCompatActivity {
    private static final int REQUEST_PICK_AUDIO = 2;
    private final int PICK_IMAGE_REQUEST = 22;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private static final int REQUEST_AUDIO_PERMISSION = 102;
    private static final int PERMISSION_REQUEST_CODE = 1;
    Button uploadAudio, uploadImage;
    ImageView AudioImage, upload, back;
    TextView audioname;

    EditText name;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseUser user;
    Uri audioUri;
    String fname;
    private Uri imagefilePath;

    String Name, Category, FilePath, FileName, FileLink, ImageLink,UserUID;

    boolean upImg, upAudio;
    private TextToSpeechHelper textToSpeechHelper;
    AudioRecorderView recordView;
    String[] permissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_audio);


        uploadAudio = findViewById(R.id.uploadAudio);
        uploadImage = findViewById(R.id.uploadImage);
        name = findViewById(R.id.Name);
        upload = findViewById(R.id.upload);
        AudioImage = findViewById(R.id.Image);
        audioname = findViewById(R.id.audioname);
        back = findViewById(R.id.back);
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        storageReference = storage.getReference().child("Audio Added by User").child(user.getUid());

        Name = getIntent().getExtras().getString("Name");
        Category = getIntent().getExtras().getString("Category");
        FilePath = getIntent().getExtras().getString("FilePath");
        FileName = getIntent().getExtras().getString("FileName");
        FileLink = getIntent().getExtras().getString("FileLink");
        ImageLink = getIntent().getExtras().getString("ImageLink");
        UserUID = getIntent().getExtras().getString("UserUID");


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EditAudio.this, ParentAccessAudio.class);
                i.putExtra("Category", Category);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        if (SDK_INT>=33){
            permissions = new String[]{
                    READ_MEDIA_VIDEO,
                    READ_MEDIA_IMAGES,
                    READ_MEDIA_AUDIO,
                    CAMERA,RECORD_AUDIO
            };
        }else {
            permissions = new String[]{
                    READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,RECORD_AUDIO
            };
        }
        textToSpeechHelper = new TextToSpeechHelper(EditAudio.this, "Edit");

        audioname.setText(FileName);
        name.setText(Name);

        upAudio = false;
        upImg = false;

        Glide.with(this).load(ImageLink).centerCrop().into(AudioImage);

        uploadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String option[] = {"Select Audio", "Record Audio", "Text To Speech"};
                AlertDialog.Builder builder = new AlertDialog.Builder(EditAudio.this);
                builder.setTitle("Choose Action");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                if (ContextCompat.checkSelfPermission(EditAudio.this, READ_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(EditAudio.this,WRITE_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(EditAudio.this,
                                            permissions,
                                            REQUEST_STORAGE_PERMISSION);
                                } else {
                                    pickAudio();
                                }
                                break;

                            case 1:
                                if (ContextCompat.checkSelfPermission(EditAudio.this, RECORD_AUDIO)
                                        != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(EditAudio.this,READ_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(EditAudio.this, WRITE_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(EditAudio.this, new String[]{RECORD_AUDIO},
                                            REQUEST_AUDIO_PERMISSION);
                                } else {
                                    showRecorder();

                                }
                                break;
                            case 2:
                                File myDirectory = new File(Environment.getExternalStorageDirectory(), "/AudioAAC");
                                if (!myDirectory.exists()) {
                                    checkPermissionForDir();
                                    if (checkPermissionForDir()) {
                                        myDirectory.mkdirs();

                                    } else {
                                        requestPermissionforDR();
                                    }
                                }
                                showSelectLanguageDialog();
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().equals("") || name.getText().toString().equals(null)){
                    Toast.makeText(EditAudio.this, "Please enter AAC name", Toast.LENGTH_SHORT).show();
                }else{
                    SaveNew();
                }
            }
        });
    }
    private void showSelectLanguageDialog() {
        String option[] = {"Filipino", "English"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditAudio.this);
        builder.setTitle("Select Language");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case 0:
                        showTTSDialog("Filipino");
                        break;
                    case 1:
                        showTTSDialog("English");
                        break;
                }
            }
        });
        builder.create().show();

    }
    private void SaveNew() {
        String id = String.valueOf(System.currentTimeMillis());
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AudioAddedByUser").child(user.getUid());
        reference.child(Name).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Name", name.getText().toString());
                hashMap.put("Category", Category);
                hashMap.put("FilePath", FilePath);
                hashMap.put("FileName", FileName);
                hashMap.put("FileLink", FileLink);
                hashMap.put("ImageLink", ImageLink);
                hashMap.put("UserUID", UserUID);
                hashMap.put("Id", id);
                reference.child(name.getText().toString()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent i = new Intent(EditAudio.this, ParentAccessAudio.class);
                        i.putExtra("Category", Category);

                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                });
            }
        });

    }


    private void SelectImage() {
        // Defining Implicit Intent to mobile gallery
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Please select file",Toast.LENGTH_SHORT).show();
                    pickAudio();
                } else {
                    Toast.makeText(this, "Storage permissions denied",Toast.LENGTH_SHORT).show();
                }
                break;

            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        Toast.makeText(this, "Please select file",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Storage permissions denied",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case REQUEST_AUDIO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Audio permission granted", Toast.LENGTH_SHORT).show();
                    showRecorder();
                } else {
                    Toast.makeText(this, "Audio permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void pickAudio() {
        Intent pickAudioIntent = new Intent();
        pickAudioIntent.setType("audio/*");
        pickAudioIntent.setAction(Intent.ACTION_PICK);
        startActivityForResult(pickAudioIntent, REQUEST_PICK_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICK_AUDIO:
                if (RESULT_OK == resultCode) {
                    if (data != null) {
                        audioUri = data.getData();
                        try {
                            //call the getPath uri with context and uri
                            //To get path from uri
                            String path = getPath(this, audioUri);
                            File file = new File(path);
                            fname = file.getName();
                            fname = fname.replaceAll("\\..*", "");
                            audioname.setText(fname + ".mp3");
                            FileName = fname;
                            uploadAudio(audioUri, fname);
                        } catch (Exception e) {
                            e("Err", e.toString() + "");
                        }
                    }
                }
                break;
            case PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    // Get the Uri of data
                    imagefilePath = data.getData();
                    try {
                        // Setting image on image view using Bitmap
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagefilePath);
                        AudioImage.setImageBitmap(bitmap);
                        uploadImage(Category);
                    } catch (IOException e) {
                        // Log the exception
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void uploadAudio(Uri audioUri, String filename) {
        String AudioId = String.valueOf(System.currentTimeMillis());
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Audio...");
        progressDialog.show();
        String filePathAndName = Category + "/" + filename + "_" + AudioId;
        StorageReference ref = storageReference.child(filePathAndName);
        ref.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                if (uriTask.isSuccessful()) {
                    String downloadUri = uriTask.getResult().toString();
                    FileLink = downloadUri;
                    progressDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            // Progress Listener for loading
            // percentage on the dialog box
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int) progress + "%");
            }
        });
    }

    public String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    private void uploadImage(String categoryName) {
        if (imagefilePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Image...");
            progressDialog.show();
            // Defining the child of storageReference
            StorageReference ref = storageReference.child(categoryName + "_" + "Audio_Images/" + UUID.randomUUID().toString());
            // adding listeners on upload
            // or failure of image
            ref.putFile(imagefilePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Image uploaded successfully
                            // Dismiss dialog
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful()) ;
                            if (uriTask.isSuccessful()) {
                                String ImagedownloadUri = uriTask.getResult().toString();
                                ImageLink = ImagedownloadUri;
                                progressDialog.dismiss();
                                Toast.makeText(EditAudio.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(EditAudio.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        // Progress Listener for loading
                        // percentage on the dialog box
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void showTTSDialog(String language1) {
        LayoutInflater inflater = LayoutInflater.from(EditAudio.this);
        View dialogview = inflater.inflate(R.layout.ttsdialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(EditAudio.this)
                .setView(dialogview)
                .setTitle("Input new AAC word")
                .setPositiveButton("Save", null) //Set to null. We override the onclick
                .setNegativeButton("Cancel", null)
                .setNeutralButton("Play", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button save = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                save.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        //What ever you want to do with the value
                        EditText YouEditTextValue = dialogview.findViewById(R.id.etTTS);
                        //OR
                        String TTS = YouEditTextValue.getText().toString();

                        textToSpeechHelper.startConvert(TTS, TTS+".mp3","Save",language1);
                        dialog.dismiss();
                    }
                });
                Button cancel = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                cancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                Button play = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                play.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        EditText YouEditTextValue = dialogview.findViewById(R.id.etTTS);
                        //OR
                        String TTS = YouEditTextValue.getText().toString();
                        textToSpeechHelper.startConvert(TTS, TTS+".mp3","Play",language1);
                    }
                });
            }
        });
        dialog.show();
    }
    public void uploadAduioFromTTS(Uri audioUri, String filename){
        String AudioId = String.valueOf(System.currentTimeMillis());
        audioname.setText(filename + ".mp3");
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Audio...");
        progressDialog.show();
        String filePathAndName = Category + "/" + filename + "_" + AudioId;
        StorageReference ref = storageReference.child(filePathAndName);
        ref.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                if (uriTask.isSuccessful()) {
                    String downloadUri = uriTask.getResult().toString();
                    FileName = filename;
                    audioname.setText(filename);
                    Toast.makeText(EditAudio.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    FileLink = downloadUri;
                    progressDialog.dismiss();
                }
            }
        });
    }

    private void showRecorder(){
        LayoutInflater factory = LayoutInflater.from(EditAudio.this);
        final View audiorecorder = factory.inflate(R.layout.audiorecorder, null);
        final AlertDialog AudioRecorderDialog = new AlertDialog.Builder(EditAudio.this).create();
        AudioRecorderDialog.setView(audiorecorder);
        Button save= audiorecorder.findViewById(R.id.upload);
        EditText filename = audiorecorder.findViewById(R.id.filename);
        recordView = audiorecorder.findViewById(R.id.recordView);

        recordView.setOnFinishRecord(new AudioRecorderView.OnFinishRecordListener() {
            @Override
            public void onFinishRecordListener(@NotNull File file) {
                save.setVisibility(View.VISIBLE);
                filename.setVisibility(View.VISIBLE);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String fname = file.getName();
                        Uri recordedUri = Uri.fromFile(file);
                        System.out.println(fname);
                        if(!filename.getText().toString().equals("")){
                            FileName = filename.getText().toString()+".mp3";
                            audioname.setText(FileName);
                            uploadAudio(recordedUri, FileName);
                            AudioRecorderDialog.dismiss();
                        }else{
                            Toast.makeText(EditAudio.this, "Please enter file name",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        recordView.setOnDelete(new AudioRecorderView.OnDeleteListener() {
            @Override
            public void onDelete() {
                save.setVisibility(View.GONE);
                filename.setVisibility(View.GONE);

            }
        });
        AudioRecorderDialog.show();
    }
    private void requestPermissionforDR() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(EditAudio.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermissionForDir() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int result = ContextCompat.checkSelfPermission(EditAudio.this, READ_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(EditAudio.this, WRITE_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
        }
    }
}