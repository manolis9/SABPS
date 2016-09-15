package com.example.mazdis.sabps;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;

public abstract class Menu extends FragmentActivity{
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

    }
}

