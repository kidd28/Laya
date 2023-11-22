package com.capstone.laya.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.capstone.laya.EditAudio;
import com.capstone.laya.Model.AudioModel;
import com.capstone.laya.ParentalAccess;
import com.capstone.laya.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class ParentAuidoAdapter extends RecyclerView.Adapter<ParentAuidoAdapter.HolderAdapter> {
    Context context;
    ArrayList<AudioModel> audioModels;
    FirebaseUser user;

    public ParentAuidoAdapter(Context context, ArrayList<AudioModel> audioModels) {
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
        String name = model.getName();
        String img = model.getImageLink();
        holder.category.setText(name);
        holder.go.setVisibility(View.GONE);
        Glide.with(context).load(img).centerCrop().into(holder.img);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio(model.getFileLink());
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
                        switch (m) {
                            case R.id.edit:
                                Intent intent = new Intent(context, EditAudio.class);
                                intent.putExtra("Name", model.getName());
                                intent.putExtra("Category", model.getCategory());
                                intent.putExtra("FilePath", model.getFilePath());
                                intent.putExtra("FileName", model.getFileName());
                                intent.putExtra("FileLink", model.getFileLink());
                                intent.putExtra("ImageLink", model.getImageLink());
                                context.startActivity(intent);
                                ((Activity) context).finish();
                                break;
                            case R.id.delete:
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Do you want to delete this Category?");
                                builder.setTitle("Delete Category");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                                    deleteAudio(model.getFileLink(), model.getImageLink(), model.getName());
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

    private void deleteAudio(String fileLink, String imageLink, String Name) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(fileLink);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                StorageReference image = FirebaseStorage.getInstance().getReferenceFromUrl(imageLink);
                image.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AudioAddedByUser").child(user.getUid()).child(Name);
                        reference.removeValue();
                        context.startActivity(new Intent(context, ParentalAccess.class));
                        ((Activity) context).finish();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return audioModels.size();
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
}
