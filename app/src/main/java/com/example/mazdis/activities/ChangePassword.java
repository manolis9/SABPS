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
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class ChangePassword extends DialogFragment implements View.OnClickListener {

    private EditText currentPassword;
    private EditText newPassword;
    private EditText confirmNewPassword;
    private FirebaseAuth mAuth;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.activity_change_password, null, false);

        currentPassword = (EditText) dialogView.findViewById(R.id.change_password_current_password);
        newPassword = (EditText) dialogView.findViewById(R.id.change_password_new_password);
        confirmNewPassword = (EditText) dialogView.findViewById(R.id.change_password_confirm_new_password);

        mAuth = FirebaseAuth.getInstance();

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

        mAuth.getCurrentUser().updatePassword(newPassword.getText().toString());
        Toast.makeText(getActivity(), "Password Updated", Toast.LENGTH_SHORT).show();
        dismiss();
    }
}
