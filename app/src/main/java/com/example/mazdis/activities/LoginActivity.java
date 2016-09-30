package com.example.mazdis.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mazdis.sabps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends BaseActivity {

    private EditText emailField;
    private EditText passwordField;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        emailField = (EditText) findViewById(R.id.email_login_edittext);
        passwordField = (EditText) findViewById(R.id.password_login_edittext);

        /* If user is logged in, start MapsActivity */
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (firebaseAuth.getCurrentUser() != null) {
                   // application.getAuth().getUser().setLoggedIn(true);
                    startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                    finish();
                }
            }
        };
    }

    /* If the user is already logged in send them directly to MapsActivity. If not,
    *  start this activity.
    */
    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    /* When the "Login" button is tapped, this method checks if the login info entered
     * by the user in the edittexts is correct.
     * If yes, the AuthStateListener gets notified and logs the user in. If not,
     * a Toast appears saying "Login problem". If the user has left a field empty,
     * a Toast appears saying "Fields are empty".
     */
    public void doLogin(View view) {

        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){

            Toast.makeText(LoginActivity.this, "Fields are empty", Toast.LENGTH_LONG).show();

        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login problem", Toast.LENGTH_LONG).show();

                    }
                }
            });
        }

    }

    /* If a new user taps on "Register", start RegistrationActivity. */
    public void startRegistration(View view){
        //application.getAuth().getUser().setLoggedIn(true);
        startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        finish();
    }

}
