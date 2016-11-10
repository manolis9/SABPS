package com.example.mazdis.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.example.mazdis.sabps.R;
import com.google.firebase.auth.FirebaseAuth;

/* This class controls the buttons on the shared layout file "activity_menu" */
public abstract class Menu extends BaseActivity{

    private FirebaseAuth mAuth;

    @Override
    protected void onPostCreate(Bundle savedState){
        super.onPostCreate(savedState);

        final Button findParking = (Button) findViewById(R.id.find_parking_button);
        mAuth = FirebaseAuth.getInstance();

        /* Check what the altMenuFlag is set to and set the button to say "Find Parking"
        * or "Current Booking" accordingly.
        */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Menu.this);
        int altMenuFlag = prefs.getInt("altMenuFlag", 0);
        if(altMenuFlag == 0){
            findParking.setText("Find Parking");
        } else findParking.setText("Current Booking");

        /*If the button's text is "Find Parking", start MapsActivity when clicked.
        * If it's "Current Booking", start ReservedMapsActivity when clicked
        */
        findParking.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(findParking.getText().equals("Find Parking")) {
                    Intent intent = new Intent(Menu.this, MapsActivity.class);
                  // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else{
                    Intent intent = new Intent(Menu.this, ReservedMapsActivity.class);
                   // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });

        findViewById(R.id.my_account_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(Menu.this, UserAccount.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(Menu.this, SettingsActivity.class);
               // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });

        findViewById(R.id.contact_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(Menu.this, ContactUs.class);
              //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

        findViewById(R.id.my_bookings_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(Menu.this, MyBookings.class);
              //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
        });

        findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                mAuth.signOut();
                application.getAuth().getUser().setLoggedIn(false);
                startActivity(new Intent(Menu.this, LoginActivity.class));

            }
        });


    }
}

