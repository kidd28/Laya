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
import android.widget.Toast;

import com.capstone.laya.Model.AudioModel;
import com.capstone.laya.Model.SecurityQuestionModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ForgotPin extends AppCompatActivity {

    FirebaseUser user;
    Spinner questionSpinner;
    List<String> questionlist;
    Button submit;
    String Question;
    EditText answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pin);
        questionSpinner = findViewById(R.id.question);
        submit = findViewById(R.id.save);
        answer = findViewById(R.id.answer);
        user = FirebaseAuth.getInstance().getCurrentUser();

        questionlist = new ArrayList<>();
        loadQuestions();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!answer.getText().toString().equals("")) {
                    validateAnswer(Question, answer.getText().toString());
                }else{
                    Toast.makeText(ForgotPin.this, "Please type your answer", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void validateAnswer(String question, String answer) {
        questionlist.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SecurityQuestions").child(user.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    if(snap.child("Question").getValue().equals(question)){
                        if(snap.child("Answer").getValue().toString().equals(answer)){
                            Intent i = new Intent(ForgotPin.this, SetupPasscode.class);
                            i.putExtra("Type", "Reset");
                            startActivity(i);
                            finish();
                        }else{
                            Toast.makeText(ForgotPin.this, "Incorrect answer, please try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadQuestions() {
        questionlist.clear();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("SecurityQuestions").child(user.getUid());
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                            SecurityQuestionModel model = snap.getValue(SecurityQuestionModel.class);
                            questionlist.add(model.getQuestion().toString());
                }
                ArrayAdapter ad1 = new ArrayAdapter(ForgotPin.this, R.layout.custom_selected_question, questionlist);
                ad1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                questionSpinner.setAdapter(ad1);
                Question = questionSpinner.getSelectedItem().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}