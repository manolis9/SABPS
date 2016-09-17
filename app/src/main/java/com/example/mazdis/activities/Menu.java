package com.example.mazdis.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mazdis.sabps.R;

public abstract class Menu extends BaseAuthenticatedActivity{
    @Override
    protected void onPostCreate(Bundle savedState){
        super.onPostCreate(savedState);

        findViewById(R.id.find_parking_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                startActivity(new Intent(Menu.this, MapsActivity.class));
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

    }
}

