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
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ModuleProfile extends BaseActivity {

    private TextView titleTextView;
    private TextView addressTextView;
    private TextView rateTextView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_profile);

         /* This activity will take part of the screen. Tapping out of the activity window closes
           the activity and continues running the previous one.
        */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.7), (int) (height*0.4));

        TextView headerView = (TextView) findViewById(R.id.header_textview);
        headerView.setPadding(0,70,0,70);

        titleTextView = (TextView) findViewById(R.id.title_textview);
        addressTextView = (TextView) findViewById(R.id.address_textview);
        rateTextView = (TextView) findViewById(R.id.rate_textview);

        /* Set the textviews to contain the info received from MapsActivity */
        titleTextView.setText(getIntent().getStringExtra("title"));
        addressTextView.setText(getIntent().getStringExtra("address"));
        String rate = getIntent().getStringExtra("rate");
        rateTextView.setText('$' + rate);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


    }

    /* Once the user taps on "Reserve", a booking and a booking title
    * are added to the database and ReservedMapsActivity starts*/
    public void startReservedMap(View view){

        createBooking();
        Intent intent = new Intent(this, ReservedMapsActivity.class);
        startActivity(intent);
        finish();

    }

    /* Creates a new booking in the database under the current user. The
    * booking includes the current date and time and the SABPS module title
    * and address. The method also creates a new booking title under the current user's
    * "Booking Titles". The bookings info and the booking title are added to Shared Preferences.
    */
    public void createBooking(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Calendar c = Calendar.getInstance();
        String startTime = timeFormat.format(c.getTime());
        String date = dateFormat.format(c.getTime());

        String bookingTitle = (date + " " + startTime + " " + titleTextView.getText().toString());

        String user_id = mAuth.getCurrentUser().getUid();
        DatabaseReference current_user_db = mDatabase.child("Users").child(user_id).child("bookings").child(bookingTitle);

        current_user_db.child("date").setValue(date);
        current_user_db.child("start time").setValue(startTime);
        current_user_db.child("title").setValue(titleTextView.getText().toString());
        current_user_db.child("address").setValue(addressTextView.getText().toString());
        current_user_db.child("reservation confirmation email sent").setValue(false);

        DatabaseReference booking_db = mDatabase.child("Users").child(user_id).child("Booking Titles");

        booking_db.child(bookingTitle).setValue(bookingTitle);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("bookingTitle", bookingTitle);
        editor.putString("bookingStartTime", startTime);
        editor.putString("moduleAddress", addressTextView.getText().toString());
        editor.putString("moduleTitle", titleTextView.getText().toString());
        editor.putString("moduleRate", rateTextView.getText().toString());

        editor.commit();

    }
}
