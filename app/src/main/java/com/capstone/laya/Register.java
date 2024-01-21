package com.capstone.laya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class Register extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    EditText  name,dob;
    CardView register;
    TextView email;

    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;

    FirebaseAuth mAuth;
    FirebaseUser user;
    DatabaseReference reference;

    String Email, Name, Dob, Gender, uid;

    ImageView calendar;
    RadioButton rbMale, rbFemale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.EditText_email);
        name = findViewById(R.id.EditText_name);
        dob = findViewById(R.id.EditText_dob);
        calendar = findViewById(R.id.calendar);
        register = findViewById(R.id.Register);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);

        Email = getIntent().getStringExtra("email");
        Name = getIntent().getStringExtra("name").toUpperCase(Locale.ROOT);
        uid = getIntent().getStringExtra("uid");

        user = FirebaseAuth.getInstance().getCurrentUser();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Users");

        email.setText(Email);
        name.setText(Name);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(Register.this, gso);

        calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.capstone.laya.DatePicker datePicker;
                datePicker = new com.capstone.laya.DatePicker();
                datePicker.show(getSupportFragmentManager(), "DATE PICK");
            }
        });
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.capstone.laya.DatePicker datePicker;
                datePicker = new com.capstone.laya.DatePicker();
                datePicker.show(getSupportFragmentManager(), "DATE PICK");
            }
        });

        rbMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Gender = "Male";
            }
        });
        rbFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Gender = "Female";
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Dob != null && Name != null && !name.getText().toString().equals("") && Gender!= null){
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("Email", user.getEmail());
                    hashMap.put("Uid", uid);
                    hashMap.put("Name", name.getText().toString());
                    hashMap.put("DateOfBirth", Dob);
                    hashMap.put("Gender", Gender);
                    reference.child(user.getUid()).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            user.reload();
                            Intent intent = new Intent(Register.this, SetupPasscode.class);
                            intent.putExtra("Type", "New");
                            startActivity(intent);
                            Register.this.finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else {
                    Toast.makeText(Register.this, "Please complete the field above!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.MEDIUM).format(mCalendar.getTime());
        Dob = selectedDate;
        dob.setText(selectedDate);
    }

    private void revokeAccess() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(Register.this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(user.getUid());
        ref.removeValue();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Registration Cancelled", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        revokeAccess();
    }
}