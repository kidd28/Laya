package com.capstone.laya.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.capstone.laya.IntroPref;
import com.capstone.laya.MainActivity;
import com.capstone.laya.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnboardingThird#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnboardingThird extends Fragment {
    CardView next ;
    String newItem;
    TextView Title, SubTitle,btntext;
    String language;

    private  IntroPref introPref;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OnboardingThird() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OnboardingThird.
     */
    // TODO: Rename and change types and number of parameters
    public static OnboardingThird newInstance(String param1, String param2) {
        OnboardingThird fragment = new OnboardingThird();
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
        View v=  inflater.inflate(R.layout.fragment_onboarding_third, container, false);

        introPref = new IntroPref(getActivity());

        next = v.findViewById(R.id.next);
        Title = v.findViewById(R.id.Title);
        SubTitle = v.findViewById(R.id.SubTitle);
        btntext = v.findViewById(R.id.btntext);

        language = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Language", "English");

        if(language.equals("Filipino")){
            Title.setText("ACCESS NG MAGULANG");
            SubTitle.setText("Maglagay ng passcode para sa eksklusibong kontrol ng magulang lamang.");
            btntext.setText("Sunod");

        } else if (language.equals("English")) {
            Title.setText("PARENTAL ACCESS");
            SubTitle.setText("Set up a passcode for exclusive control and peace of mind.");
            btntext.setText("Next");
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                introPref.setIsFirstTimeLaunch(false);
                Intent intent = new Intent(getActivity(), MainActivity.class);

                startActivity(intent);
                getActivity().finish();

            }
        });
        return  v;
    }
}