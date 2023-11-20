package com.capstone.laya.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capstone.laya.Dashboard;
import com.capstone.laya.Model.AudioModel;
import com.capstone.laya.R;

import java.io.IOException;
import java.util.ArrayList;

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.HolderAdapter> {

    Context context;
    ArrayList<AudioModel> audioModels;

    public AudioAdapter(Context context, ArrayList<AudioModel> audioModels) {
        this.context = context;
        this.audioModels = audioModels;

    }

    @NonNull
    @Override
    public AudioAdapter.HolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tiles, parent, false);

        return new HolderAdapter(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioAdapter.HolderAdapter holder, int position) {
        AudioModel model = audioModels.get(position);
        String name = model.getName();
        String img = model.getImageLink();

        holder.text.setText(name);
        Glide.with(context).load(img).centerCrop().into(holder.img);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioPlayer(Environment.getExternalStorageDirectory().getPath()+"/AudioAAC/"+model.getId()+".mp3");
                Dashboard dashboard = (Dashboard) context;
                dashboard.additem(model.getId(), model.getCategory());
            }
        });

    }

    @Override
    public int getItemCount() {
        return audioModels.size();
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

    public void audioPlayer(String path){
        //set up MediaPlayer
        MediaPlayer mp = new MediaPlayer();

        try {
            mp.setDataSource(path );
            mp.prepare();
            mp.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
