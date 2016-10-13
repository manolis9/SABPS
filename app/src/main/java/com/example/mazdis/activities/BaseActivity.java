package com.example.mazdis.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import com.mazdis.sabps.infrastructure.SabpsApplication;

public class BaseActivity extends AppCompatActivity {

    protected SabpsApplication application;
    @Override
    protected void onCreate(Bundle savedState){
        super.onCreate(savedState);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        application = (SabpsApplication) getApplication();


    }
}
