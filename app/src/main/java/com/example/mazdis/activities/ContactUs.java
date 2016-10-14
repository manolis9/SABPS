package com.example.mazdis.activities;

import android.os.Bundle;

import com.example.mazdis.sabps.R;

public class ContactUs extends Menu {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
