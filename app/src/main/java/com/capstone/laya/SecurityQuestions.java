package com.capstone.laya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.capstone.laya.Model.AudioModel;
import com.capstone.laya.Model.SecurityQuestionModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class SecurityQuestions extends AppCompatActivity {
    FirebaseUser user;
    GoogleSignInClient mGoogleSignInClient;
    ArrayList<SecurityQuestionModel> SecurityQuestionModel;
    String[] Engquestions1 = {"In what city were you born?", "What is the name of your favorite pet?", "What is your mother's maiden name?"};

    String[] Engquestions2 = {"What high school did you attend?", "What was the name of your elementary school?", "What was the name of your childhood bestfriend?"};
    String[] Engquestions3 = {"What was your favorite food as a child?", "Where did you meet your spouse?", "What year was your father born?"};

    String[] Filquestions1 = {"Saang lungsod ka ipinanganak?", "Ano ang pangalan ng iyong paboritong alagang hayop?", "Ano ang apelyido ng iyong ina bago siya ikasal?"};
    String[] Filquestions2 = {"Saang mataas na paaralan (High School) ka nag-aral?", "Ano ang pangalan ng iyong paaralan noong elementarya ka?", "Ano ang pangalan ng iyong  matalik na kaibigan noong bata ka?"};
    String[] Filquestions3 = {"Ano ang paborito mong pagkain noong bata ka pa?", "Saan mo nakilala ang iyong asawa?", "Anong taon ipinanganak ang iyong ama?"};
    Spinner q1, q2, q3;

    TextView qtv1,qtv2,qtv3;
    EditText ans1, ans2, ans3;

    Button save;

    String set,language;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_questions);


        q1 = findViewById(R.id.question1);
        q2 = findViewById(R.id.question2);
        q3 = findViewById(R.id.question3);
        ans1 = findViewById(R.id.answer1);
        ans2 = findViewById(R.id.answer2);
        ans3 = findViewById(R.id.answer3);
        qtv1 = findViewById(R.id.q1);
        qtv2 = findViewById(R.id.q2);
        qtv3 = findViewById(R.id.q3);
        save = findViewById(R.id.save);

        set = getIntent().getStringExtra("set");
        language = getIntent().getStringExtra("language");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(SecurityQuestions.this, gso);

        if(language.equals("Filipino")){
            save.setText("Magpatuloy");
            qtv1.setText("Unang tanong: ");
            qtv2.setText("Pangalawang tanong: ");
            qtv3.setText("Pangatlong tanong:");
            ans1.setHint("Sagot");
            ans2.setHint("Sagot");
            ans3.setHint("Sagot");
            ArrayAdapter ad1 = new ArrayAdapter(this, R.layout.custom_selected_question, Filquestions1);
            ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ArrayAdapter ad2 = new ArrayAdapter(this, R.layout.custom_selected_question, Filquestions2);
            ad2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            ArrayAdapter ad3 = new ArrayAdapter(this, R.layout.custom_selected_question, Filquestions3);
            ad3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            q1.setAdapter(ad1);
            q2.setAdapter(ad2);
            q3.setAdapter(ad3);

        }else{
            ArrayAdapter ad1 = new ArrayAdapter(this, R.layout.custom_selected_question, Engquestions1);
            ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            ArrayAdapter ad2 = new ArrayAdapter(this, R.layout.custom_selected_question, Engquestions2);
            ad2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            ArrayAdapter ad3 = new ArrayAdapter(this, R.layout.custom_selected_question, Engquestions3);
            ad3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            q1.setAdapter(ad1);
            q2.setAdapter(ad2);
            q3.setAdapter(ad3);
        }


        user = FirebaseAuth.getInstance().getCurrentUser();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = String.valueOf(System.currentTimeMillis());
                String id1 = String.valueOf(System.currentTimeMillis()+1);
                String id2 = String.valueOf(System.currentTimeMillis()+2);
                if (validateAnswer(ans1.getText().toString(),ans2.getText().toString(),ans3.getText().toString())) {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SecurityQuestions");
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("Question", q1.getSelectedItem().toString());
                    hashMap.put("Answer", ans1.getText().toString());
                    hashMap.put("Id",id);
                    reference.child(user.getUid()).child(id).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            HashMap<String, String> hashMap1 = new HashMap<>();
                            hashMap1.put("Question", q2.getSelectedItem().toString());
                            hashMap1.put("Answer", ans2.getText().toString());
                            hashMap1.put("Id", id1);
                            reference.child(user.getUid()).child(id1).setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    HashMap<String, String> hashMap2 = new HashMap<>();
                                    hashMap2.put("Question", q3.getSelectedItem().toString());
                                    hashMap2.put("Answer", ans3.getText().toString());
                                    hashMap2.put("Id", id2);
                                    reference.child(user.getUid()).child(id2).setValue(hashMap2).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Intent intent = new Intent(SecurityQuestions.this, Dashboard.class);
                                            startActivity(intent);
                                            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                            SecurityQuestions.this.finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                } else {
                    Toast.makeText(SecurityQuestions.this, "Please answer all Three questions", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validateAnswer(String ans1, String ans2, String ans3) {
        if (ans1.equals("") || ans2.equals("") || ans3.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(SecurityQuestions.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(set.equals("old")){

        }else{
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(user.getUid());
        ref.removeValue();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SecurityQuestions.this, "Registration Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        revokeAccess();}
    }
}