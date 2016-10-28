package com.example.mazdis.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mazdis.sabps.R;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConfirmDone extends BaseActivity {

    private static final String EMAIL_FROM = "manolis.ioannides@mazdis.com";
    private static final String EMAIL_SUBJECT = "Booking Completed";
    private static final String EMAIL_BODY = "You completed your booking at the following address:\n";
    private static final String FIREBASE_USER_NAME = "name";
    private static final String FIREBASE_USER_EMAIL = "email";
    private static final String FIREBASE_USERS = "Users";
    private static final String FIREBASE_EMAILS_TO_SEND = "Emails to Send";
    private static final String FIREBASE_EMAIL = "email";
    private static final String FIREBASE_USER_BOOKING_IN_PROGRESS = "booking in progress";
    private static final String FIREBASE_USER_BOOKINGS = "bookings";
    private static final String FIREBASE_USER_BOOKING_TITLES = "Booking Titles";
    private static final String FIREBASE_BOOKING_END_TIME = "end time";
    private static final String FIREBASE_BOOKING_COST = "cost";
    private static final String FIREBASE_EMAIL_FROM = "from";
    private static final String FIREBASE_EMAIL_TO = "to";
    private static final String FIREBASE_EMAIL_SUBJECT = "subject";
    private static final String FIREBASE_EMAIL_BODY = "body";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_done);

        /* This activity will take part of the screen. Tapping out of the activity window closes
           the activity and continues running the previous one.
        */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.3));

        TextView headerView = (TextView) findViewById(R.id.header_textview);
        headerView.setPadding(0, 50, 0, 50);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRef = new Firebase("https://sabps-cd1b7.firebaseio.com");

    }

    /* Completes booking and starts MapsActivity */
    public void startMap(View view) {
        completeBooking();
        stopLocationService();
        startActivity(new Intent(this, MapsActivity.class));
        finish();
    }

    /* Starts ReservedMapsActivity */
    public void backToReservedMap(View view) {
        startActivity((new Intent(this, ReservedMapsActivity.class)));
        finish();
    }

    /* Completes a booking by adding the booking's end time and final cost to the database,
    *  under the correct booking. Also, after this method is called, the Menu will have a
    *  Find Parking button instead of Current Booking button.
    */
    public void completeBooking() {

        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = mDatabase.child(FIREBASE_USERS).child(user_id);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        int bikeParked = prefs.getInt("countdownDone", 0);

        if (bikeParked == 1) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Calendar c = Calendar.getInstance();
            String endTime = timeFormat.format(c.getTime());

            String bookingTitle = prefs.getString("bookingTitle", "no id");
            String startTime = prefs.getString("bookingStartTime", "no id");
            String rate = prefs.getString("moduleRate", "no id");
            final String address = prefs.getString("moduleAddress", "no id");

            String cost = calculateCost(startTime, endTime, rate);
            Log.v("cost", cost);

            DatabaseReference booking_titles_db = mDatabase.child(FIREBASE_USERS).child(user_id).child(FIREBASE_USER_BOOKINGS).child(bookingTitle);

            booking_titles_db.child(FIREBASE_BOOKING_END_TIME).setValue(endTime);
            booking_titles_db.child(FIREBASE_BOOKING_COST).setValue(cost);

            createEmail(address);

            current_user_db.child(FIREBASE_USER_BOOKING_IN_PROGRESS).setValue("false");

            editor.putInt("altMenuFlag", 0);

            editor.commit();

        } else {

            String bookingTitle = prefs.getString("bookingTitle", "no id");

            Firebase bookingTitles_mRef = mRef.child(FIREBASE_USERS).child(user_id).child(FIREBASE_USER_BOOKING_TITLES).child(bookingTitle);
            bookingTitles_mRef.removeValue();

            Firebase bookings_mRef = mRef.child(FIREBASE_USERS).child(user_id).child(FIREBASE_USER_BOOKINGS).child(bookingTitle);
            bookings_mRef.removeValue();

            current_user_db.child(FIREBASE_USER_BOOKING_IN_PROGRESS).setValue("false");

            editor.putInt("altMenuFlag", 0);
            editor.putInt("countdownDone", 1);
            editor.commit();
        }
    }

    /* Given the booking start time, end time and and rate, this method calculates the cost for a booking.
   `*  @Requires: Times should be in HH:mm format.
    *  @Returns: The booking's cost rounded to two decimal places, as a String
    */
    public String calculateCost(String startTime, String endTime, String rate) {

        double hours = 0;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
            Date sT = simpleDateFormat.parse(startTime);
            Date eT = simpleDateFormat.parse(endTime);
            long difference = eT.getTime() - sT.getTime();
            double days = (double) (difference / (1000 * 60 * 60 * 24));
            hours = ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            hours = (hours < 0 ? -hours : hours);

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        Log.v("hours", Double.toString(hours));
        String r;
        if (rate.contains("$")) {
            r = rate.replace("$", "");
        } else r = rate;
        double doubleRate = Double.parseDouble(r);

        Log.v("rate", Double.toString(doubleRate));
        double cost;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ConfirmDone.this);
        int bikeParked = prefs.getInt("countdownDone", 0);

        if (bikeParked == 1) { //if parkBike is pressed
            cost = hours * doubleRate;
        } else {
            cost = 0.0;
        }

        double roundOff = Math.round(cost * 1000.0) / 1000.0;

        String costDollars = "$".concat(Double.toString((roundOff)));

        return costDollars;

    }

    public void createEmail(final String address) {

        final DatabaseReference emails = mDatabase.child(FIREBASE_EMAILS_TO_SEND).child(FIREBASE_EMAIL);

        String user_id = mAuth.getCurrentUser().getUid();
        final DatabaseReference current_user_db = mDatabase.child(FIREBASE_USERS).child(user_id);

        current_user_db.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                String userEmail = map.get(FIREBASE_USER_EMAIL);
                String name = map.get(FIREBASE_USER_NAME);

                Map<String, String> emailFields = new HashMap<>();
                emailFields.put(FIREBASE_EMAIL_TO, userEmail);
                emailFields.put(FIREBASE_EMAIL_FROM, EMAIL_FROM);
                emailFields.put(FIREBASE_EMAIL_SUBJECT, EMAIL_SUBJECT);
                emailFields.put(FIREBASE_EMAIL_BODY, "Dear " + name + ", " + EMAIL_BODY
                        + address);

                emails.setValue(emailFields);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void stopLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        if (isMyServiceRunning(LocationService.class)) {
            stopService(intent);
        }
    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}

