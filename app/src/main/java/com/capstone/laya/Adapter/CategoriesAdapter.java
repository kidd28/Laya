package com.capstone.laya.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capstone.laya.fragments.AudioFragment;
import com.capstone.laya.Model.CategoriesModel;
import com.capstone.laya.R;
import com.capstone.laya.fragments.CategoryFragment;

import java.util.ArrayList;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.HolderAdapter> {

    Context context;
    ArrayList<CategoriesModel> categoriesModels;

    public CategoriesAdapter(Context context, ArrayList<CategoriesModel> model){
        this.context = context;
        this.categoriesModels = model;
    }

    @NonNull
    @Override
    public CategoriesAdapter.HolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tiles, parent, false);
        return new HolderAdapter(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriesAdapter.HolderAdapter holder, int position) {
        CategoriesModel categoriesModel = categoriesModels.get(position);
        String category = categoriesModel.getCategory();
        String img = categoriesModel.getImageLink();

        holder.text.setText(category);
        Glide.with(context).load(img).centerCrop().into(holder.img);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new AudioFragment(); // replace your custom fragment class
                Bundle bundle = new Bundle();
                FragmentTransaction fragmentTransaction = ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                System.out.println(category);
                bundle.putString("Category",category); // use as per your need
                fragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.fragmentView,fragment);
                fragmentTransaction.commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return categoriesModels.size();
    }

    public class HolderAdapter extends RecyclerView.ViewHolder {
        ImageView img;
        TextView text;
        CardView card;
        public HolderAdapter(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            text = itemView.findViewById(R.id.name);
            card = itemView.findViewById(R.id.card);
        }
    }
}
