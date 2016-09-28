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
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_profile);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.7), (int) (height*0.4));

        TextView headerView = (TextView) findViewById(R.id.header_textview);
        headerView.setPadding(0,70,0,70);

        titleTextView = (TextView) findViewById(R.id.title_textview);
        addressTextView = (TextView) findViewById(R.id.address_textview);
        TextView priceTextView = (TextView) findViewById(R.id.price_textview);

        titleTextView.setText(getIntent().getStringExtra("title"));
        addressTextView.setText(getIntent().getStringExtra("address"));
        String rawPrice = getIntent().getStringExtra("price");
        priceTextView.setText('$' + rawPrice);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();


    }

    public void startReservedMap(View view){

        String reservedAddress = addressTextView.getText().toString();
        String reservedTitle = titleTextView.getText().toString();

        createBooking();

        Intent intent = new Intent(this, ReservedMapsActivity.class);
        intent.putExtra("reservedAddress", reservedAddress);
        intent.putExtra("reservedTitle", reservedTitle);
        startActivity(intent);
        finish();

    }

    public void createBooking(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
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

        DatabaseReference booking_db = mDatabase.child("Booking Titles");

        booking_db.child(bookingTitle).setValue(bookingTitle);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("string_id", bookingTitle);
        editor.commit();

    }
}
