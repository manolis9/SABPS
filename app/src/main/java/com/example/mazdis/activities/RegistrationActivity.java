package com.example.mazdis.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mazdis.sabps.R;

public class RegistrationActivity extends BaseAuthenticatedActivity {

    @Override
    protected void onSabpsCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_registration);

    }

    public void startMap(View view){

        Toast.makeText(this, "Registration Successful", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, MapsActivity.class));
        finish();
    }
}
