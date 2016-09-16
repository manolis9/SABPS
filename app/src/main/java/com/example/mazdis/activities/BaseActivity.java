package com.example.mazdis.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.mazdis.sabps.infrastructure.SabpsApplication;

public class BaseActivity extends FragmentActivity {

    protected SabpsApplication application;
    @Override
    protected void onCreate(Bundle savedState){
        super.onCreate(savedState);

        application = (SabpsApplication) getApplication();
    }
}
