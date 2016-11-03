package com.example.mazdis.activities;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mazdis.sabps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPassword extends DialogFragment implements View.OnClickListener {

    private static final String FIREBASE_EMAIL = "email";
    private static final String FIREBASE_RESET_PASSWORD_EMAIL = "Reset Password Email";

    private DatabaseReference mDatabase;
    private EditText emailText;
    FirebaseAuth mAuth;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.activity_forgot_password, null, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        emailText = (EditText) dialogView.findViewById(R.id.user_email);

        mAuth = FirebaseAuth.getInstance();

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Send Email", null)
                .setTitle("Reset Password")
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);

        return dialog;
    }

    @Override
    public void onClick(View view) {

        String email = emailText.getText().toString();
        final Toast emailSent =  Toast.makeText(getActivity(), "Password reset email sent", Toast.LENGTH_SHORT);
        final Toast emailFailed = Toast.makeText(getActivity(), "Could not find any account linked to the email address entered", Toast.LENGTH_SHORT);

        if (email != null) {

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        emailSent.show();
                    } else {
                        emailFailed.show();
                    }
                }
            });

            dismiss();

        } else {
            Toast.makeText(getActivity(), "Please enter your email", Toast.LENGTH_SHORT).show();
        }

    }
}

