package com.example.mazdis.activities;

import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mazdis.sabps.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePassword extends DialogFragment implements View.OnClickListener {

    private static int PASSWORD_LENGTH = 7;
    private EditText currentPassword;
    private EditText newPassword;
    private EditText confirmNewPassword;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.activity_change_password, null, false);

        currentPassword = (EditText) dialogView.findViewById(R.id.change_password_current_password);
        newPassword = (EditText) dialogView.findViewById(R.id.change_password_new_password);
        confirmNewPassword = (EditText) dialogView.findViewById(R.id.change_password_confirm_new_password);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .setTitle("Change Password")
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);

        return dialog;
    }

    @Override
    public void onClick(View view) {

        String currPass = currentPassword.getText().toString();
        final String newPass = newPassword.getText().toString();
        String confirm = confirmNewPassword.getText().toString();

        final Toast passUpdated = Toast.makeText(getActivity(), "Password Updated", Toast.LENGTH_SHORT);
        final Toast passFailed = Toast.makeText(getActivity(), "Failed to update password", Toast.LENGTH_SHORT);
        final Toast authFailed = Toast.makeText(getActivity(), "Current Password is incorrect. Please try again", Toast.LENGTH_SHORT);

        if (newPass.equals(confirm) && newPass.length() >= PASSWORD_LENGTH) {

            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            String email = user.getEmail();

            AuthCredential credential = EmailAuthProvider
                    .getCredential(email, currPass);

            // Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                user.updatePassword(newPass)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    passUpdated.show();
                                                } else {
                                                    passFailed.show();
                                                }
                                            }
                                        });
                            } else {
                                authFailed.show();
                            }
                        }
                    });

            dismiss();
        } else {
            Toast.makeText(getActivity(), "Passwords must match and new password should be at least " + Integer.toString(PASSWORD_LENGTH) + " characters long", Toast.LENGTH_SHORT).show();
        }
    }
}
