package com.example.mazdis.sabps;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ConfirmDone extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_done);

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

