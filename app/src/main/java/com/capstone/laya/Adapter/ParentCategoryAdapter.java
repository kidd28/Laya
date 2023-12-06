package com.capstone.laya.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.util.Log;
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
import com.capstone.laya.EditCategory;
import com.capstone.laya.Model.AudioModel;
import com.capstone.laya.Model.CategoriesModel;
import com.capstone.laya.ParentAccessAudio;
import com.capstone.laya.ParentalAccess;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
                                Intent i = new Intent(context, EditCategory.class);
                                i.putExtra("CategoryName", category);
                                i.putExtra("ImageLink", img);
                                context.startActivity(i);
                                break;
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Do you want to delete this Category?");
                                builder.setTitle("Delete Category");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                                    deleteCategory(category, img);
                                });
                                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                                    dialog.cancel();
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
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

    private void deleteCategory(String category, String img) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(img);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryAddedbyUser").child(user.getUid()).child(category);
                reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AudioAddedByUser").child(user.getUid());
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap : snapshot.getChildren()){
                                    if (Objects.equals(snap.child("Category").getValue(), category)) {
                                        for(DataSnapshot snp :snap.getChildren()){
                                            DatabaseReference ref1 = snp.getRef();
                                            ref1.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    context.startActivity(new Intent(context, ParentalAccess.class));
                                                    ((Activity) context).finish();
                                                }
                                            });
                                        }
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Log.e("firebasestorage", "onFailure: did not delete file");
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
