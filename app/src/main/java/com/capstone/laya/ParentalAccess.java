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
    ArrayList<CategoriesModel> categoriesModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parental_access);


        back = findViewById(R.id.back);
        add = findViewById(R.id.add);


        Glide.with(this).load(R.drawable.back).centerCrop().into(back);
        Glide.with(this).load(R.drawable.add).centerCrop().into(add);


        rv = findViewById(R.id.rv);

        categoriesModels = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);

        rv.setLayoutManager(layoutManager);

        loadCategories();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentalAccess.this, AddCategory.class));
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ParentalAccess.this, Settings.class));
                finish();
            }
        });
    }

    private void loadCategoriesAddedbyUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryAddedbyUser").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    CategoriesModel model = snap.getValue(CategoriesModel.class);
                    categoriesModels.add(model);
                }
                ParentCategoryAdapter categoriesAdapter = new ParentCategoryAdapter(ParentalAccess.this, categoriesModels);
                rv.setAdapter(categoriesAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadCategories() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedCategory");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    CategoriesModel model = snap.getValue(CategoriesModel.class);
                    categoriesModels.add(model);
                }
                ParentCategoryAdapter categoriesAdapter = new ParentCategoryAdapter(ParentalAccess.this, categoriesModels);
                rv.setAdapter(categoriesAdapter);
                loadCategoriesAddedbyUser();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ParentalAccess.this, Settings.class));
        finish();
    }
}
