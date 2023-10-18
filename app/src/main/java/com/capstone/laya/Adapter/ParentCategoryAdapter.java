package com.capstone.laya.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capstone.laya.Model.CategoriesModel;
import com.capstone.laya.ParentAccessAudio;
import com.capstone.laya.R;

import java.util.ArrayList;

public class ParentCategoryAdapter  extends RecyclerView.Adapter<ParentCategoryAdapter.HolderAdapter> {
    Context context;
    ArrayList<CategoriesModel> categoriesModels;

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
