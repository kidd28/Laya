package com.capstone.laya.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capstone.laya.Model.AudioModel;
import com.capstone.laya.R;

import java.util.ArrayList;

public class ParentAuidoAdapter  extends RecyclerView.Adapter<ParentAuidoAdapter.HolderAdapter>{
    Context context;
    ArrayList<AudioModel> audioModels;

    public ParentAuidoAdapter(Context context, ArrayList<AudioModel> audioModels){
        this.context = context;
        this.audioModels = audioModels;
    }
    @NonNull
    @Override
    public ParentAuidoAdapter.HolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list, parent, false);
        return new ParentAuidoAdapter.HolderAdapter(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ParentAuidoAdapter.HolderAdapter holder, int position) {
        AudioModel model = audioModels.get(position);
        String name= model.getName();
        String img = model.getImageLink();

        holder.category.setText(name);
        holder.go.setVisibility(View.GONE);
        Glide.with(context).load(img).centerCrop().into(holder.img);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {

        return audioModels.size();
    }

    public class HolderAdapter extends RecyclerView.ViewHolder{
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
