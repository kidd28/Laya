package com.capstone.laya.Adapter;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capstone.laya.Dashboard;
import com.capstone.laya.Model.AudioModel;
import com.capstone.laya.R;

import java.io.IOException;
import java.util.ArrayList;

public class SelectedAudioAdapter  extends RecyclerView.Adapter<SelectedAudioAdapter.HolderAdapter>{

    Context context;
    ArrayList<AudioModel> audioModels;

    public SelectedAudioAdapter(Context context, ArrayList<AudioModel> audioModels) {
        this.context = context;
        this.audioModels = audioModels;
    }
    @NonNull
    @Override
    public SelectedAudioAdapter.HolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.aaclist, parent, false);

        return new SelectedAudioAdapter.HolderAdapter(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedAudioAdapter.HolderAdapter holder, int position) {
        AudioModel model = audioModels.get(position);
        String img = model.getImageLink();

        Glide.with(context).load(img).centerCrop().into(holder.img);
        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio(model.getFileLink());
            }
        });

    }

    @Override
    public int getItemCount() {
        return audioModels.size();
    }

    private void playAudio(String audioUrl) {
        // initializing media player
        MediaPlayer mediaPlayer = new MediaPlayer();

        // below line is use to set the audio stream type for our media player.
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            // below line is use to set our
            // url to our media player.
            mediaPlayer.setDataSource(audioUrl);

            // below line is use to prepare
            // and start our media player.
            mediaPlayer.prepare();
            mediaPlayer.start();

            // below line is use to display a toast message.
        } catch (IOException e) {
            // this line of code is use to handle error while playing our audio file.
        }
    }

    public class HolderAdapter extends RecyclerView.ViewHolder {
        ImageView img;

        public HolderAdapter(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
        }
    }
}
