package com.example.mazdis.activities;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mazdis.sabps.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPassword extends DialogFragment implements View.OnClickListener {

    private static final String FIREBASE_EMAIL= "email";
    private static final String FIREBASE_RESET_PASSWORD_EMAIL = "Reset Password Email";

    private DatabaseReference mDatabase;
    private EditText emailText;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.activity_forgot_password, null, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        emailText = (EditText) dialogView.findViewById(R.id.user_email);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setPositiveButton("Send Email", null)
                .setTitle("Reset Password")
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);

        return dialog;
    }

    @Override
    public void onClick(View view) {

        String email = emailText.getText().toString();

        if(email != null){
            DatabaseReference mRef = mDatabase.child(FIREBASE_RESET_PASSWORD_EMAIL).child(FIREBASE_EMAIL);
            mRef.setValue(email);
            Toast.makeText(getActivity(), "Reset password email sent", Toast.LENGTH_SHORT).show();
            dismiss();
        } else {
            Toast.makeText(getActivity(), "Please enter your email", Toast.LENGTH_SHORT).show();
        }

    }
}

/*
When the user clicks "Send Email" in this dialogue's resource file, take the email and
send it to Firebase. Start this dialogue when pressing a "forgot password button" in LoginActivity. In the server code, get that email
and use it in ref.resetPassword(email) to send a link to the user's email.

 */