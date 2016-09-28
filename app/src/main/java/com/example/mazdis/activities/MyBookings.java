package com.example.mazdis.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mazdis.sabps.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyBookings extends Menu {


    private FirebaseAuth mAuth;
    private Firebase mRef;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        mAuth = FirebaseAuth.getInstance();
        mRef = new Firebase("https://sabps-cd1b7.firebaseio.com");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        createTextViews();
    }

    public void createTextViews() {
        final Firebase search_mRef = mRef.child("Booking Titles");

        search_mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                List<Object> bookings = new ArrayList<>(td.values());
                for(int i = bookings.size()-1; i >= 0; i--) {

                   createTextView(bookings.get(i).toString());
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void createTextView(String bookingTitle){

        String user_id = mAuth.getCurrentUser().getUid();
        Firebase current_mRef = mRef.child("Users").child(user_id).child("bookings").child(bookingTitle);

        current_mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                LinearLayout MainLinearLayout = (LinearLayout)findViewById(R.id.my_bookings_layout);

                LinearLayout innerLinearLayout = createLinearLayout();

                MainLinearLayout.addView(innerLinearLayout);

                Map<String, String> map = dataSnapshot.getValue(Map.class);

                TextView title = new TextView(MyBookings.this);
                title.setText(map.get("title"));
                title.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                innerLinearLayout.addView(title);

                TextView address = new TextView(MyBookings.this);
                address.setText(map.get("address"));
                address.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                innerLinearLayout.addView(address);

                TextView date = new TextView(MyBookings.this);
                date.setText(map.get("date"));
                date.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                innerLinearLayout.addView(date);

                TextView startTime = new TextView(MyBookings.this);
                startTime.setText(map.get("start time"));
                startTime.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                innerLinearLayout.addView(startTime);

                TextView endTime =  new TextView(MyBookings.this);
                endTime.setText(map.get("end time"));
                endTime.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                innerLinearLayout.addView(endTime);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public LinearLayout createLinearLayout(){

        LinearLayout layout = new LinearLayout(MyBookings.this);

        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams innerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        innerLayoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        innerLayoutParams.setMargins(100, 0, 100, 100);
        layout.setLayoutParams(innerLayoutParams);

        return layout;
    }
}

