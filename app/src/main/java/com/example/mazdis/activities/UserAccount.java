package com.example.mazdis.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mazdis.sabps.R;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mazdis.sabps.infrastructure.User;

import java.util.Map;

public class UserAccount extends Menu {

    private static final String FIREBASE_USER_NAME = "name";
    private static final String FIREBASE_USER_EMAIL = "email";
    private static final String FIREBASE_USER_ADDRESS = "address";
    private static final String FIREBASE_USER_PHONE_NUMBER = "phone number";
    private static final String FIREBASE_USERS = "Users";

    private EditText nameField;
    private EditText emailField;
    private EditText addressField;
    private EditText phoneNumberField;

    private FirebaseAuth mAuth;
    private Firebase mRef;
    private DatabaseReference mDatabase;
    private MenuItem saveItem;
    private MenuItem editItem;
    private MenuItem changePassword;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_user_account);

        nameField = (EditText) findViewById(R.id.name_edittext);
        emailField = (EditText) findViewById(R.id.email_edittext);
        addressField = (EditText) findViewById(R.id.address_edittext);
        phoneNumberField = (EditText) findViewById(R.id.phoneNumber_edittext);

        mAuth = FirebaseAuth.getInstance();
        mRef = new Firebase("https://sabps-cd1b7.firebaseio.com");
        mDatabase = FirebaseDatabase.getInstance().getReference().child(FIREBASE_USERS);

        DbToEditTexts();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {

        getMenuInflater().inflate(R.menu.account_action_bar, menu);
        saveItem = menu.findItem(R.id.action_save);
        editItem = menu.findItem(R.id.action_edit);
        changePassword = menu.findItem(R.id.action_change_password);
        changePassword.setVisible(true);
        saveItem.setVisible(false);
        editItem.setVisible(true);


        return true;
    }

    /* If the edit button is tapped, the edittexts become enabled. The
    * user can then edit their info and tap on save to update the database
    * and disable the edittexts.
    */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_edit) {
            enableEditTexts(true);
            editItem.setVisible(false);
            saveItem.setVisible(true);
            saveItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            return true;
        }
        if (item.getItemId() == R.id.action_save) {
            saveInfo();
            saveItem.setVisible(false);
            saveItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            editItem.setVisible(true);
            Toast.makeText(this, "Saved Changes", Toast.LENGTH_LONG).show();
            return true;
        }
        if (item.getItemId() == R.id.action_change_password) {
            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction()
                    .addToBackStack(null);
            ChangePassword dialog = new ChangePassword();
            dialog.show(transaction, null);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* By tapping on LOGOUT, the user gets logged out and LoginActivity starts*/
    public void logout(View view) {

        mAuth.signOut();
        application.getAuth().getUser().setLoggedIn(false);
        startActivity(new Intent(this, LoginActivity.class));
    }


    /* Updates the current user's info on the database based on the info
     * in the edittexts and disables the edittexts.
      */
    public void saveInfo() {

        enableEditTexts(false);

        String name = nameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String address = addressField.getText().toString().trim();
        String phoneNumber = phoneNumberField.getText().toString().trim();

        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = mDatabase.child(user_id);

        current_user_db.child(FIREBASE_USER_NAME).setValue(name);
        current_user_db.child(FIREBASE_USER_EMAIL).setValue(email);
        current_user_db.child(FIREBASE_USER_ADDRESS).setValue(address);
        current_user_db.child(FIREBASE_USER_PHONE_NUMBER).setValue(phoneNumber);


    }

    /* If bool is set to true, the edittexts become enabled; if it is
    * set to false, the edittexts become disabled.
    */
    public void enableEditTexts(Boolean bool) {

        if (bool == true) {

            nameField.setEnabled(true);
            emailField.setEnabled(true);
            addressField.setEnabled(true);
            phoneNumberField.setEnabled(true);

        } else {
            nameField.setEnabled(false);
            emailField.setEnabled(false);
            addressField.setEnabled(false);
            phoneNumberField.setEnabled(false);
        }
    }

    /* Gets the current users personal info from the database and sets it to the corresponding
     * edittexts. It keeps checking the database for changes in the current users info and updates the
     * edittexts accordingly. It also disables the edittexts.
     */


    public void DbToEditTexts() {

        enableEditTexts(false);

        String user_id = mAuth.getCurrentUser().getUid();
        Firebase current_mRef = mRef.child(FIREBASE_USERS).child(user_id);

        current_mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, String> map = dataSnapshot.getValue(Map.class);

                nameField.setText(map.get(FIREBASE_USER_NAME));
                emailField.setText(map.get(FIREBASE_USER_EMAIL));
                addressField.setText(map.get(FIREBASE_USER_ADDRESS));
                phoneNumberField.setText(map.get(FIREBASE_USER_PHONE_NUMBER));

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
