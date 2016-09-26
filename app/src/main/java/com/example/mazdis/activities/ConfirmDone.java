package com.example.mazdis.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import com.example.mazdis.sabps.R;

public class ConfirmDone extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_done);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.8), (int) (height*0.3));

        TextView headerView = (TextView) findViewById(R.id.header_textview);
        headerView.setPadding(0,50,0,50);

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

