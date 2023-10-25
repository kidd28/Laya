package com.capstone.laya;

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
import android.os.Bundle;
import android.provider.MediaStore;
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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.UUID;

public class EditAudio extends AppCompatActivity {
    private static final int REQUEST_PICK_AUDIO = 2;
    private final int PICK_IMAGE_REQUEST = 22;
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int STORAGE_PERMISSION_CODE = 101;
    Button uploadAudio, uploadImage, upload;
    ImageView AudioImage;
    TextView audioname;

    EditText name;
    FirebaseStorage storage;
    StorageReference storageReference;

    FirebaseUser user;
    Uri audioUri;
    String fname;
    private Uri imagefilePath;

    String Name,Category,FilePath,FileName,FileLink,ImageLink;

    boolean upImg,upAudio;
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
        user = FirebaseAuth.getInstance().getCurrentUser();
        storage = FirebaseStorage.getInstance();

        storageReference = storage.getReference().child("Audio Added by User").child(user.getUid());

        Name = getIntent().getExtras().getString("Name");
        Category = getIntent().getExtras().getString("Category");
        FilePath = getIntent().getExtras().getString("FilePath");
        FileName = getIntent().getExtras().getString("FileName");
        FileLink = getIntent().getExtras().getString("FileLink");
        ImageLink = getIntent().getExtras().getString("ImageLink");

        audioname.setText(FileName);
        name.setText(Name);

        upAudio = false;
        upImg = false;

        Glide.with(this).load(ImageLink).centerCrop().into(AudioImage);

        uploadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String option[] = {"Select Audio", "Record Audio"};
                AlertDialog.Builder builder = new AlertDialog.Builder(EditAudio.this);
                builder.setTitle("Choose Action");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
                                break;
                            case 1:
                                Toast.makeText(EditAudio.this, "Wala pa to", Toast.LENGTH_SHORT).show();
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
                if(name.getText().toString().equals(Name)){
                    Save();
                }else {
                    SaveNew();
                }
            }
        });
    }

    private void SaveNew() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AudioAddedByUser").child(user.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Name",name.getText().toString());
        hashMap.put("Category", Category);
        hashMap.put("FilePath", FilePath);
        hashMap.put("FileName", FileName);
        hashMap.put("FileLink", FileLink);
        hashMap.put("ImageLink", ImageLink);
        reference.child(name.getText().toString()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                reference.child(Name).removeValue();
                startActivity(new Intent(EditAudio.this, ParentAccessAudio.class));
                finish();
            }
        });
    }

    private void Save() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AudioAddedByUser").child(user.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Name",Name);
        hashMap.put("Category", Category);
        hashMap.put("FilePath", FilePath);
        hashMap.put("FileName", FileName);
        hashMap.put("FileLink", FileLink);
        hashMap.put("ImageLink", ImageLink);
        reference.child(Name).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                startActivity(new Intent(EditAudio.this, ParentAccessAudio.class));
                finish();
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
    public void checkPermission(String permission, int requestCode)
    {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(EditAudio.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(EditAudio.this, new String[] { permission }, requestCode);
        }
        else {
            pickAudio();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(EditAudio.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
                pickAudio();
            }
            else {
                Toast.makeText(EditAudio.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void pickAudio() {
        Intent pickAudioIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickAudioIntent, REQUEST_PICK_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICK_AUDIO:
                if (RESULT_OK == resultCode) {
                    if(data!=null){
                        audioUri = data.getData();
                        try{
                            //call the getPath uri with context and uri
                            //To get path from uri
                            String path = getPath(this, audioUri);
                            File file = new File(path);
                            fname = file.getName();
                            fname = fname.replaceAll("\\..*", "");
                            audioname.setText(fname+".mp3");
                            FileName = fname;
                            uploadAudio(audioUri, fname);
                        }catch(Exception e){
                            e("Err", e.toString()+"");
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
    public void uploadAudio(Uri audioUri, String filename){
        String AudioId = String.valueOf(System.currentTimeMillis());
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading Audio...");
        progressDialog.show();
        String filePathAndName = Category+"/"+filename+"_"+AudioId;
        StorageReference ref = storageReference.child(filePathAndName);
        ref.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful()) ;
                if(uriTask.isSuccessful()) {
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
        }).addOnProgressListener(
                new OnProgressListener<UploadTask.TaskSnapshot>() {
                    // Progress Listener for loading
                    // percentage on the dialog box
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress
                                = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded " + (int) progress + "%");
                    }
                });
    }
    public  String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
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
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
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
            StorageReference ref = storageReference.child(categoryName+"_"+"Audio_Images/" + UUID.randomUUID().toString());
            // adding listeners on upload
            // or failure of image
            ref.putFile(imagefilePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful());
                                    if(uriTask.isSuccessful()){
                                        String ImagedownloadUri = uriTask.getResult().toString();
                                        ImageLink =ImagedownloadUri;
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
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {
                                // Progress Listener for loading
                                // percentage on the dialog box
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                }
                            });
        }
    }
}