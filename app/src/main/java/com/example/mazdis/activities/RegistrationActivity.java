package com.example.mazdis.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mazdis.sabps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends BaseActivity {

    private static final String EMAIL_FROM = "manolis.ioannides@mazdis.com";
    private static final String EMAIL_SUBJECT = "Registration Confirmation";
    private static final String EMAIL_BODY = "Welcome to Mazdis";
    private static final String FIREBASE_USER_NAME = "name";
    private static final String FIREBASE_USER_EMAIL = "email";
    private static final String FIREBASE_USERS = "Users";
    private static final String FIREBASE_EMAILS_TO_SEND = "Emails to Send";
    private static final String FIREBASE_EMAIL = "email";
    private static final String FIREBASE_USER_BOOKING_IN_PROGRESS = "booking in progress";

    private EditText nameField;
    private EditText emailField;
    private EditText passwordField;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mProgress = new ProgressDialog(this);

        nameField = (EditText) findViewById(R.id.name_registration_edittext);
        emailField = (EditText) findViewById(R.id.email_registration_edittext);
        passwordField = (EditText) findViewById(R.id.password_registration_edittext);

    }

    /* Once the user clicks Register, their login info gets saved in the database
    * and in Firebase->Auth and MapsActivity starts. If any of the registration fields are left empty,
    * a Toast saying "Fields are empty" appears.
    */
    public void startMap(View view){

        final String name = nameField.getText().toString().trim();
        final String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();

        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){

            Toast.makeText(this, "Fields are empty", Toast.LENGTH_LONG).show();

        } else{

            mProgress.setMessage("Registering...");
            mProgress.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if(task.isSuccessful()){

                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabase.child(FIREBASE_USERS).child(user_id);

                        Map<String, String> userInfo = new HashMap<>();
                        userInfo.put(FIREBASE_USER_NAME, name);
                        userInfo.put(FIREBASE_USER_EMAIL, email);
                        userInfo.put(FIREBASE_USER_BOOKING_IN_PROGRESS, "false");
                        current_user_db.setValue(userInfo);

                        createEmail();

                        mProgress.dismiss();
                        Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();

                        startActivity(new Intent(RegistrationActivity.this, MapsActivity.class));
                        finish();
                    }
                }
            });
        }
    }

    public void createEmail() {

        final DatabaseReference emails = mDatabase.child(FIREBASE_EMAILS_TO_SEND).child(FIREBASE_EMAIL);

        String userEmail = emailField.getText().toString();
        String name = nameField.getText().toString();

        Map<String, String> emailFields = new HashMap<>();
        emailFields.put("to", userEmail);
        emailFields.put("from", EMAIL_FROM);
        emailFields.put("subject", EMAIL_SUBJECT);
        emailFields.put("body", "Dear " + name + ", \n" + EMAIL_BODY);

        emails.setValue(emailFields);
    }

}

