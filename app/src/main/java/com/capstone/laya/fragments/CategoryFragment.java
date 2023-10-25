package com.capstone.laya.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.capstone.laya.Adapter.CategoriesAdapter;
import com.capstone.laya.Adapter.ParentCategoryAdapter;
import com.capstone.laya.Model.CategoriesModel;
import com.capstone.laya.ParentalAccess;
import com.capstone.laya.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoryFragment extends Fragment {


    RecyclerView rv;
    CategoriesAdapter adapter;
    FirebaseUser user;
    ArrayList<CategoriesModel> categoriesModels;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CategoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoryFragment newInstance(String param1, String param2) {
        CategoryFragment fragment = new CategoryFragment();
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

        View v= inflater.inflate(R.layout.fragment_category, container, false);
        // Inflate the layout for this fragment
        rv = v.findViewById(R.id.rv);

        categoriesModels = new ArrayList<>();
        GridLayoutManager layoutManager=new GridLayoutManager(getActivity(),2);

        user = FirebaseAuth.getInstance().getCurrentUser();
        rv.setLayoutManager(layoutManager);
        loadCategories();

        return v;
    }
    private void loadCategoriesAddedbyUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryAddedbyUser").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    CategoriesModel model = snap.getValue(CategoriesModel.class);
                    categoriesModels.add(model);
                }
                CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getActivity(), categoriesModels);
                rv.setAdapter(categoriesAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void loadCategories() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedAudio");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()){
                    CategoriesModel model = snap.getValue(CategoriesModel.class);
                    categoriesModels.add(model);
                }
                CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getActivity(), categoriesModels);
                rv.setAdapter(categoriesAdapter);
                loadCategoriesAddedbyUser();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}