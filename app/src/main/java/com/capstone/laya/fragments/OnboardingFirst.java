package com.capstone.laya.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.laya.Adapter.CustomSpinnerAdapter;
import com.capstone.laya.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnboardingFirst#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnboardingFirst extends Fragment {

    Spinner spinner;
    TextView language,btntext;
    CardView next ;
    String newItem;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OnboardingFirst() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnboardingFirst.
     */
    // TODO: Rename and change types and number of parameters
    public static OnboardingFirst newInstance(String param1, String param2) {
        OnboardingFirst fragment = new OnboardingFirst();
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
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_onboarding_first, container, false);

        spinner = v.findViewById(R.id.spinner);
        language = v.findViewById(R.id.language);
        next = v.findViewById(R.id.next);
        btntext = v.findViewById(R.id.btntext);
        final List<String> states = Arrays.asList("English", "Filipino");


        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(requireActivity().getApplicationContext(), states);
        adapter.setDropDownViewResource(R.layout.my_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                newItem = states.get(i);
                if(newItem.equals("Filipino")){
                    language.setText("Mangyaring pumili ng wika");
                    btntext.setText("Magpatuloy");
                } else if (newItem.equals("English")) {
                    language.setText("Please select Language");
                    btntext.setText("Next");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("Language", newItem).apply();
                Fragment fragment = new OnboardingSecond(); // replace your custom fragment class
                Bundle bundle = new Bundle();
                FragmentTransaction fragmentTransaction = ((FragmentActivity) view.getContext()).getSupportFragmentManager().beginTransaction();
                bundle.putString("Language", newItem); // use as per your need
                fragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.OnboardingFragmentView, fragment);
                fragmentTransaction.commit();
            }
        });

        return v;
    }
}