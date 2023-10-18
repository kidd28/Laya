package com.capstone.laya.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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



        audioModels = new ArrayList<>();
        GridLayoutManager layoutManager=new GridLayoutManager(getActivity(),2);

        rv.setLayoutManager(layoutManager);

        user = FirebaseAuth.getInstance().getCurrentUser();
        Bundle bundle = this.getArguments();

        if(bundle != null){
            category = bundle.getString("Category");
           // System.out.println(category);
            cat.setText(category);
        }

        loadAudio();
        loadAudioAddedbyUser();
        return v;
    }
    private void loadAudioAddedbyUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("AudioAddedByUser").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    if (Objects.equals(snap.child("Category").getValue(), category)) {
                        for (DataSnapshot s : snap.getChildren()) {
                            if(!s.getKey().equals("Category")&&!s.getKey().equals("ImageLink")){
                                AudioModel model = s.getValue(AudioModel.class);
                                audioModels.add(model);
                            }

                        }
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
    private void loadAudio() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedAudio");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    if (Objects.equals(snap.child("Category").getValue(), category)) {
                        for (DataSnapshot s : snap.getChildren()) {
                            if(!s.getKey().equals("Category")&&!s.getKey().equals("ImageLink")){
                                AudioModel model = s.getValue(AudioModel.class);
                                audioModels.add(model);
                            }

                        }
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