package com.capstone.laya.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
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
    SearchView sv;
    String language;
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

        View v = inflater.inflate(R.layout.fragment_category, container, false);
        // Inflate the layout for this fragment
        rv = v.findViewById(R.id.rv);
        categoriesModels = new ArrayList<>();

        sv = v.findViewById(R.id.sv);
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);

        user = FirebaseAuth.getInstance().getCurrentUser();
        rv.setLayoutManager(layoutManager);



        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query.trim())){
                    searchCat(query);
                }else{
                    loadCategories(language);
                    loadCategoriesAddedbyUser();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText.trim())){
                    searchCat(newText);
                }else{
                    loadCategories(language);
                    loadCategoriesAddedbyUser();
                }
                return false;
            }
        });


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                language = ""+snapshot.child("Language").getValue();
                loadCategories(language);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        loadCategoriesAddedbyUser();
        return v;
    }

    private void loadCategoriesAddedbyUser() {
        categoriesModels.clear();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryAddedbyUser").child(user.getUid());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
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
    private void loadCategories(String language) {
        categoriesModels.clear();
        if(language.equals("English")){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedCategory").child("English");
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
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
        } else if (language.equals("Filipino")) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ProvidedCategory").child("Filipino");
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot snap : snapshot.getChildren()) {
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

    }
    private void searchCat(String query) {
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        categoriesModels = new ArrayList<>();
        categoriesModels.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("CategoryAddedbyUser").child(user.getUid());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("Category").toString().toLowerCase().contains(query.toLowerCase())) {
                        CategoriesModel model = ds.getValue(CategoriesModel.class);
                        categoriesModels.add(model);
                    }
                }
                CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getActivity(), categoriesModels);
                rv.setAdapter(categoriesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("ProvidedCategory");
        reference.keepSynced(true);
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("Category").toString().toLowerCase().contains(query.toLowerCase())) {
                        CategoriesModel model = ds.getValue(CategoriesModel.class);
                        categoriesModels.add(model);
                    }
                }
                CategoriesAdapter categoriesAdapter = new CategoriesAdapter(getActivity(), categoriesModels);
                rv.setAdapter(categoriesAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}