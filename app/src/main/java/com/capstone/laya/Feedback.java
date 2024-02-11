package com.capstone.laya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Feedback extends AppCompatActivity {

    FirebaseUser user;
    EditText edText;
    CardView send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        edText = findViewById(R.id.feedback);
        send = findViewById(R.id.send);
        user= FirebaseAuth.getInstance().getCurrentUser();

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Feedback");
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("Feedback", edText.getText().toString());
                reference.child(user.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Feedback.this, "Feedback sent successfully",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Feedback.this, Settings.class));
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(Feedback.this, Settings.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}