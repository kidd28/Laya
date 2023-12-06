package com.capstone.laya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class EditCategory extends AppCompatActivity {

    String ImageLink, CategoryName;
    Button uploadImg;
    EditText categoryName;
    ImageView image, upload, back;

    private final int PICK_IMAGE_REQUEST = 22;

    private Uri filePath;
    String[] storagePermission;


    FirebaseStorage storage;
    StorageReference storageReference;
    String categoryname;

    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);
        upload = findViewById(R.id.upload);
        uploadImg = findViewById(R.id.uploadImage);
        categoryName = findViewById(R.id.EditText);
        image = findViewById(R.id.CatgoryImage);
        back = findViewById(R.id.back);


        storage = FirebaseStorage.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        storageReference = storage.getReference().child("Category Added by user").child(user.getUid());

        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ImageLink = getIntent().getExtras().getString("ImageLink");
        CategoryName = getIntent().getExtras().getString("CategoryName");


        Glide.with(this).load(ImageLink).centerCrop().into(image);
        categoryName.setText(CategoryName);
        uploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectImage();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(CategoryName.equals(categoryName.getText().toString())){
                    Save();
                }else{
                    SaveNew();
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EditCategory.this, ParentalAccess.class));
                finish();
            }
        });

    }

    private void Save() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryAddedbyUser").child(user.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Category", CategoryName);
        hashMap.put("ImageLink", ImageLink);
        reference.child(CategoryName).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                startActivity(new Intent(EditCategory.this, ParentalAccess.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    private void SaveNew() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryAddedbyUser").child(user.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Category", categoryName.getText().toString());
        hashMap.put("ImageLink", ImageLink);
        reference.child(categoryName.getText().toString()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                reference.child(CategoryName).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AudioAddedByUser").child(user.getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()){
                                    if (Objects.equals(snap.child("Category").getValue(), CategoryName)) {
                                        DatabaseReference ref1 = snap.getRef();
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("Category", categoryName.getText().toString());
                                        ref1.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                startActivity(new Intent(EditCategory.this, ParentalAccess.class));
                                                finish();
                                                Toast.makeText(EditCategory.this, "Success", Toast.LENGTH_SHORT);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EditCategory.this, "Failed", Toast.LENGTH_SHORT);
                                            }
                                        });
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the Uri of data
            filePath = data.getData();
            try {
                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);
                uploadImage(categoryName.getText().toString());
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(String categoryName) {
        if (filePath != null) {
            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            // Defining the child of storageReference
            StorageReference ref = storageReference.child("Category_Images/" + UUID.randomUUID().toString());
            // adding listeners on upload
            // or failure of image
            ref.putFile(filePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!uriTask.isSuccessful()) ;
                                    if (uriTask.isSuccessful()) {
                                        String downloadUri = uriTask.getResult().toString();
                                        ImageLink = downloadUri;
                                    }
                                    progressDialog.dismiss();
                                    Toast.makeText(EditCategory.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Error, Image not uploaded
                            progressDialog.dismiss();
                            Toast.makeText(EditCategory.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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