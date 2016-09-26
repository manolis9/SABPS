package com.example.mazdis.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.view.View;

import com.example.mazdis.sabps.R;

public abstract class Menu extends BaseActivity{

    private ProgressDialog mProgress;

    @Override
    protected void onPostCreate(Bundle savedState){
        super.onPostCreate(savedState);
        mProgress = new ProgressDialog(this);

        findViewById(R.id.find_parking_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                mProgress.setMessage("Starting Map...");
                mProgress.show();
                startActivity(new Intent(Menu.this, MapsActivity.class));
                mProgress.dismiss();
                finish();
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

    }
}

