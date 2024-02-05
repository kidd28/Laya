package com.capstone.laya.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.capstone.laya.Adapter.AudioAdapter;
import com.capstone.laya.Adapter.CategoriesAdapter;
import com.capstone.laya.Adapter.ParentAuidoAdapter;
import com.capstone.laya.Model.AudioModel;
import com.capstone.laya.Model.CategoriesModel;
import com.capstone.laya.ParentAccessAudio;
import com.capstone.laya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AudioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudioFragment extends Fragment {
    RecyclerView rv;
    TextView cat;
    ArrayList<AudioModel> audioModels;
    String category;
    FirebaseUser user;

    SearchView sv;
    RelativeLayout bg;

    String language;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AudioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AudioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AudioFragment newInstance(String param1, String param2) {
        AudioFragment fragment = new AudioFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_audio, container, false);
        rv = v.findViewById(R.id.rv);
        cat = v.findViewById(R.id.cat);
        sv = v.findViewById(R.id.sv);
        bg = v.findViewById(R.id.bg);

        audioModels = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

        rv.setLayoutManager(layoutManager);

        user = FirebaseAuth.getInstance().getCurrentUser();
        Bundle bundle = this.getArguments();

        if (bundle != null) {
            category = bundle.getString("Category");
            // System.out.println(category);
            cat.setText(category);
        }



        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query.trim())){
                    searchCat(query);
                }else{
                    loadAudio(language);
                    loadAudioAddedbyUser();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText.trim())){
                    searchCat(newText);
                }else{
                    loadAudio(language);
                    loadAudioAddedbyUser();
                }
                return false;
            }
        });

        loadBgColor();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                language = ""+snapshot.child("Language").getValue();
                loadAudio(language);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return v;
    }

    private void loadBgColor() {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryAddedbyUser").child(user.getUid()).child(category);
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        System.out.println(String.valueOf(snapshot.child("Color").getValue()));
                        bg.getBackground().setColorFilter(Color.parseColor(String.valueOf(snapshot.child("Color").getValue())), PorterDuff.Mode.SRC_OVER);
                    }catch(Exception ignored){}
                  }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("ProvidedCategory").child(category);
            reference1.keepSynced(true);
            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        System.out.println(String.valueOf(snapshot.child("Color").getValue()));
                        bg.getBackground().setColorFilter(Color.parseColor(String.valueOf(snapshot.child("Color").getValue())), PorterDuff.Mode.SRC_OVER);
                    }catch(Exception ignored){}}
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }



    private void loadAudioAddedbyUser() {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AudioAddedByUser").child(user.getUid());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if (Objects.equals(snap.child("Category").getValue(), category)) {
                        AudioModel model = snap.getValue(AudioModel.class);
                        audioModels.add(model);
                    }
                }
                AudioAdapter audioAdapter = new AudioAdapter(getActivity(), audioModels);
                rv.setAdapter(audioAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    private void loadAudio(String language) {

        if (language.equals("English")){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedAudio").child("English");
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (Objects.equals(snap.child("Category").getValue(), category)) {
                            AudioModel model = snap.getValue(AudioModel.class);
                            audioModels.add(model);
                        }
                    }
                    AudioAdapter audioAdapter = new AudioAdapter(getActivity(), audioModels);
                    rv.setAdapter(audioAdapter);
                    loadAudioAddedbyUser();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else if (language.equals("Filipino")) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedAudio").child("Filipino");
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (Objects.equals(snap.child("Category").getValue(), category)) {
                            AudioModel model = snap.getValue(AudioModel.class);
                            audioModels.add(model);
                        }
                    }
                    AudioAdapter audioAdapter = new AudioAdapter(getActivity(), audioModels);
                    rv.setAdapter(audioAdapter);
                    loadAudioAddedbyUser();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }
    private void searchCat(String query) {
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        audioModels = new ArrayList<>();
        audioModels.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AudioAddedByUser").child(user.getUid());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("Name").toString().toLowerCase().contains(query.toLowerCase())) {
                        AudioModel model = ds.getValue(AudioModel.class);
                        audioModels.add(model);
                    }
                }
                AudioAdapter audioAdapter = new AudioAdapter(getActivity(), audioModels);
                rv.setAdapter(audioAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        if (language.equals("English")) {
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("ProvidedAudio").child("English");
            reference.keepSynced(true);
            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("Name").toString().toLowerCase().contains(query.toLowerCase())) {
                            AudioModel model = ds.getValue(AudioModel.class);
                            audioModels.add(model);
                        }
                    }
                    AudioAdapter audioAdapter = new AudioAdapter(getActivity(), audioModels);
                    rv.setAdapter(audioAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } else if (language.equals("Filipino")) {
            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("ProvidedAudio").child("Filipino");
            reference.keepSynced(true);
            reference1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("Name").toString().toLowerCase().contains(query.toLowerCase())) {
                            AudioModel model = ds.getValue(AudioModel.class);
                            audioModels.add(model);
                        }
                    }
                    AudioAdapter audioAdapter = new AudioAdapter(getActivity(), audioModels);
                    rv.setAdapter(audioAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

    }
    @Override
    public void onDetach() {
        super.onDetach();
        Fragment fragment = new CategoryFragment(); // replace your custom fragment class
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = ((FragmentActivity) getContext()).getSupportFragmentManager().beginTransaction();
        System.out.println(category);
        bundle.putString("Category", category); // use as per your need
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragmentView, fragment);
        fragmentTransaction.commit();
    }
}