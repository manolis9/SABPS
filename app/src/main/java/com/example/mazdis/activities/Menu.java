package com.example.mazdis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mazdis.sabps.R;

public abstract class Menu extends BaseActivity{

    @Override
    protected void onPostCreate(Bundle savedState){
        super.onPostCreate(savedState);

        final Button findParking = (Button) findViewById(R.id.find_parking_button);

        findParking.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                if(findParking.getText().toString().equals("Find Parking")) {
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

