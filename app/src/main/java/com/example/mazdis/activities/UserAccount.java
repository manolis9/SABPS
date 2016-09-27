package com.example.mazdis.activities;

import android.content.Intent;
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

public class UserAccount extends Menu{

    private EditText nameField;
    private EditText usernameField;
    private EditText emailField;
    private EditText addressField;
    private EditText phoneNumberField;

    private FirebaseAuth mAuth;
    private Firebase mRef;
    private DatabaseReference mDatabase;
    private MenuItem saveItem;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_user_account);

        nameField = (EditText) findViewById(R.id.name_edittext);
        usernameField = (EditText) findViewById(R.id.username_edittext);
        emailField = (EditText) findViewById(R.id.email_edittext);
        addressField = (EditText) findViewById(R.id.address_edittext);
        phoneNumberField = (EditText) findViewById(R.id.phoneNumber_edittext);

        mAuth = FirebaseAuth.getInstance();
        mRef = new Firebase("https://sabps-cd1b7.firebaseio.com/Users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        DbToEditTexts();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu){

        getMenuInflater().inflate(R.menu.account_action_bar, menu);
        saveItem = menu.findItem(R.id.action_save);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.action_edit){
            editInfo();
            saveItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

            return true;
        }
        if(item.getItemId() == R.id.action_save){
            saveInfo();
            saveItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            Toast.makeText(this, "Saved Changes", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void logout(View view){

        mAuth.signOut();
        application.getAuth().getUser().setLoggedIn(false);
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void editInfo(){
        enableEditTexts(true);
    }

    public void saveInfo(){
        enableEditTexts(false);

        String name = nameField.getText().toString().trim();
        String username = usernameField.getText().toString().trim();
        String email = emailField.getText().toString().trim();
        String address = addressField.getText().toString().trim();
        String phoneNumber = phoneNumberField.getText().toString().trim();

        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = mDatabase.child(user_id);
        current_user_db.child("name").setValue(name);
        current_user_db.child("username").setValue(username);
        current_user_db.child("email").setValue(email);
        current_user_db.child("address").setValue(address);
        current_user_db.child("phone number").setValue(phoneNumber);

    }

    public void enableEditTexts(Boolean bool){

        if(bool == true){

            nameField.setEnabled(true);
            usernameField.setEnabled(true);
            emailField.setEnabled(true);
            addressField.setEnabled(true);
            phoneNumberField.setEnabled(true);

        } else {
            nameField.setEnabled(false);
            usernameField.setEnabled(false);
            emailField.setEnabled(false);
            addressField.setEnabled(false);
            phoneNumberField.setEnabled(false);
        }
    }

    public void DbToEditTexts(){

        enableEditTexts(false);

        String user_id = mAuth.getCurrentUser().getUid();
        Firebase current_mRef = mRef.child(user_id);

        current_mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, String> map = dataSnapshot.getValue(Map.class);

                nameField.setText(map.get("name"));
                usernameField.setText(map.get("username"));
                emailField.setText(map.get("email"));
                addressField.setText(map.get("address"));
                phoneNumberField.setText(map.get("phone number"));

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

}
