package com.capstone.laya;

import static android.app.PendingIntent.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.capstone.laya.Adapter.CategoriesAdapter;
import com.capstone.laya.Adapter.ParentCategoryAdapter;
import com.capstone.laya.Model.CategoriesModel;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ParentalAccess extends AppCompatActivity {
    ImageView back, add;
    RecyclerView rv;
    ParentCategoryAdapter adapter;
    ArrayList<CategoriesModel> categoriesModelArrayList;
    FirebaseUser user;
    String language;
    static boolean isInit = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parental_access);



        back = findViewById(R.id.back);
        add = findViewById(R.id.add);

        user = FirebaseAuth.getInstance().getCurrentUser();


        Glide.with(this).load(R.drawable.back).centerCrop().into(back);
        Glide.with(this).load(R.drawable.add).centerCrop().into(add);

        if(isInit) {
            System.out.println("reloaded");
            isInit = false;
            startActivity(new Intent(this, ParentalAccess.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
            Log.d("Restart", "asdasda");
        }

        rv = findViewById(R.id.rview);

        categoriesModelArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);

        rv.setLayoutManager(layoutManager);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentalAccess.this, AddCategory.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentalAccess.this, Settings.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });
        categoriesModelArrayList.clear();
        loadCategories();
        loadCategoriesAddedbyUser();
    }

    private void loadCategoriesAddedbyUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryAddedbyUser").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    CategoriesModel model = snap.getValue(CategoriesModel.class);
                    categoriesModelArrayList.add(model);
                }
                ParentCategoryAdapter categoriesAdapter = new ParentCategoryAdapter(ParentalAccess.this, categoriesModelArrayList);
                rv.setAdapter(categoriesAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadCategories() {

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedCategory").child("English");
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        CategoriesModel model = snap.getValue(CategoriesModel.class);
                        categoriesModelArrayList.add(model);
                    }
                    CategoriesAdapter categoriesAdapter = new CategoriesAdapter(ParentalAccess.this, categoriesModelArrayList);
                    rv.setAdapter(categoriesAdapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("ProvidedCategory").child("Filipino");
            reference.keepSynced(true);
            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        CategoriesModel model = snap.getValue(CategoriesModel.class);
                        categoriesModelArrayList.add(model);
                    }
                    CategoriesAdapter categoriesAdapter = new CategoriesAdapter(ParentalAccess.this, categoriesModelArrayList);
                    rv.setAdapter(categoriesAdapter);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ParentalAccess.this, Settings.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
