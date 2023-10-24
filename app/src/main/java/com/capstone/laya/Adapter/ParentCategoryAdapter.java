package com.capstone.laya.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capstone.laya.Model.AudioModel;
import com.capstone.laya.Model.CategoriesModel;
import com.capstone.laya.ParentAccessAudio;
import com.capstone.laya.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class ParentCategoryAdapter  extends RecyclerView.Adapter<ParentCategoryAdapter.HolderAdapter> {
    Context context;
    ArrayList<CategoriesModel> categoriesModels;
    FirebaseUser user;


    public ParentCategoryAdapter(Context context, ArrayList<CategoriesModel> model){
        this.context = context;
        this.categoriesModels = model;
    }
    @NonNull
    @Override
    public ParentCategoryAdapter.HolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false);
        return new ParentCategoryAdapter.HolderAdapter(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentCategoryAdapter.HolderAdapter holder, int position) {
        CategoriesModel categoriesModel = categoriesModels.get(position);
        String category = categoriesModel.getCategory();
        String img = categoriesModel.getImageLink();

        holder.category.setText(category);
        Glide.with(context).load(R.drawable.go).centerCrop().into(holder.go);
        Glide.with(context).load(img).centerCrop().into(holder.img);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ParentAccessAudio.class);
                i.putExtra("Category", category);
                context.startActivity(i);
                ((Activity)context).finish();
            }
        });

        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu p = new PopupMenu(context, view);
                MenuInflater inflater = p.getMenuInflater();
                inflater.inflate(R.menu.poupup_menu, p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        int m = menuItem.getItemId();
                        switch (m){
                            case R.id.edit:
                                LayoutInflater inflater = LayoutInflater.from(context);
                                View dialogview = inflater.inflate(R.layout.dialog, null);
                                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                alert.setTitle("Category Name");
                                alert.setView(dialogview);
                                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        //What ever you want to do with the value
                                        EditText YouEditTextValue = dialogview.findViewById(R.id.etCategory);
                                        //OR
                                        String NewCategory = YouEditTextValue.getText().toString();
                                        updateCategoryName(NewCategory, category,img);

                                    }
                                });
                                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // what ever you want to do with No option.
                                    }
                                });
                                alert.show();
                                break;
                            case R.id.delete:

                                break;
                        }
                        return true;
                    }
                });
                p.show();
                return true;
            }
        });
    }

    private void updateCategoryName(String newCategory, String oldCategory, String img) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryAddedbyUser").child(user.getUid());
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Category", newCategory);
        hashMap.put("ImageLink", img );
        reference.child(newCategory).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                reference.child(oldCategory).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AudioAddedByUser").child(user.getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()){
                                    if (Objects.equals(snap.child("Category").getValue(), oldCategory)) {
                                        DatabaseReference ref1 = snap.getRef();
                                        HashMap<String, Object> hashMap = new HashMap<>();
                                        hashMap.put("Category", newCategory);
                                        ref1.updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, "Success", Toast.LENGTH_SHORT);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT);
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
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {


                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoriesModels.size();
    }

    public class HolderAdapter extends RecyclerView.ViewHolder {
        ImageView img, go;
        CardView card;
        TextView category;
        public HolderAdapter(@NonNull View itemView) {
            super(itemView);

            card = itemView.findViewById(R.id.card);
            img = itemView.findViewById(R.id.img);
            category = itemView.findViewById(R.id.name);
            go = itemView.findViewById(R.id.go);
        }


    }
}
