package com.example.mazdis.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mazdis.sabps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ConfirmDone extends BaseActivity {

    private static final String EMAIL_FROM = "manolis.ioannides@mazdis.com";
    private static final String EMAIL_SUBJECT = "Booking Completed";
    private static final String EMAIL_BODY = "You completed your booking at the following address:\n";

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

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

    }

    /* Completes booking and starts MapsActivity */
    public void startMap(View view) {
        completeBooking();
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

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Calendar c = Calendar.getInstance();
        final String endTime = timeFormat.format(c.getTime());

        final String user_id = mAuth.getCurrentUser().getUid();
        final DatabaseReference booking_db = mDatabase.child("Users").child(user_id).child("Booking in Progress");

        booking_db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = dataSnapshot.getValue(Map.class);

                String bookingTitle = map.get("booking title");
                String startTime = map.get("start time");
                String rate = map.get("rate");
                String cost = calculateCost(startTime, endTime, rate);

                DatabaseReference booking_titles_db = mDatabase.child("Users").child(user_id).child("bookings").child(bookingTitle);
                booking_titles_db.child("end time").setValue(endTime);
                booking_titles_db.child("cost").setValue(cost);

                final String address = map.get("address");
                createEmail(address);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String bookingTitle = prefs.getString("bookingTitle", "no id");
//        String startTime = prefs.getString("bookingStartTime", "no id");
//        String rate = prefs.getString("moduleRate", "no id");
//
//        String cost = calculateCost(startTime, endTime, rate);
//
//        DatabaseReference booking_titles_db = mDatabase.child("Users").child(user_id).child("bookings").child(bookingTitle);
//
//        booking_titles_db.child("end time").setValue(endTime);
//        booking_titles_db.child("cost").setValue(cost);

        booking_db.removeValue();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("altMenuFlag", 0);
        editor.commit();
    }

    /* Given the booking start time, end time and and rate, this method calculates the cost for a booking.
   `*  @Requires: Times should be in HH:mm format.
    *  @Returns: The booking's cost rounded to two decimal places, as a String
    */
    public String calculateCost(String startTime, String endTime, String rate) {

        String sT = startTime.replace(":", ".");
        String eT = endTime.replace(":", ".");
        String r;
        if (rate.contains("$")) {
            r = rate.replace("$", "");
        } else r = rate;

        double doubleStart = Double.parseDouble(sT);
        double doubleEnd = Double.parseDouble(eT);
        double doubleRate = Double.parseDouble(r);

        double cost = (doubleEnd - doubleStart) * doubleRate;
        Log.v("cost", Double.toString(cost));

        double roundOff = Math.round(cost * 100.0) / 100.0;
        Log.v("costRounded", Double.toString(roundOff));

        String costDollars = "$".concat(Double.toString(roundOff));

        return costDollars;
    }

    public void createEmail(final String address) {

        final DatabaseReference emails = mDatabase.child("Emails to Send").child("email");
//
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        final String moduleAddress = prefs.getString("moduleAddress", "no id");

        String user_id = mAuth.getCurrentUser().getUid();
        final DatabaseReference current_user_db = mDatabase.child("Users").child(user_id);

        current_user_db.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map) dataSnapshot.getValue();
                String userEmail = map.get("email");

                Map<String, String> emailFields = new HashMap<>();
                emailFields.put("to", userEmail);
                emailFields.put("from", EMAIL_FROM);
                emailFields.put("subject", EMAIL_SUBJECT);
                emailFields.put("body", EMAIL_BODY
                        + address);

                emails.setValue(emailFields);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}

