package com.example.mazdis.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.example.mazdis.sabps.R;

/* This class controls the buttons on the shared layout file "activity_menu" */
public abstract class Menu extends BaseActivity{

    @Override
    protected void onPostCreate(Bundle savedState){
        super.onPostCreate(savedState);

        final Button findParking = (Button) findViewById(R.id.find_parking_button);

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
                    startActivity(new Intent(Menu.this, MapsActivity.class));
                    finish();
                } else{
                    startActivity(new Intent(Menu.this, ReservedMapsActivity.class));
                    finish();
                }
            }
        });

        findViewById(R.id.my_account_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                startActivity(new Intent(Menu.this, UserAccount.class));
                finish();
            }
        });

        findViewById(R.id.settings_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                startActivity(new Intent(Menu.this, SettingsActivity.class));

            }
        });

        findViewById(R.id.contact_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                startActivity(new Intent(Menu.this, ContactUs.class));
                finish();

            }
        });

        findViewById(R.id.my_bookings_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                startActivity(new Intent(Menu.this, MyBookings.class));
                finish();

            }
        });

    }
}

