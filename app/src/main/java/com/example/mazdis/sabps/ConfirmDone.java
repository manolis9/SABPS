package com.example.mazdis.sabps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

public class ConfirmDone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_done);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.8), (int) (height*0.4));

    }

    public void startMap(View view){
        startActivity(new Intent(this, MapsActivity.class));
        finish();
    }

    public void backToReservedMap(View view){
        startActivity((new Intent(this, ReservedMapsActivity.class)));
        finish();
    }

}

