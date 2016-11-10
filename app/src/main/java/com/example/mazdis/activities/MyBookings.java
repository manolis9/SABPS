package com.example.mazdis.activities;

import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

public class MyBookings extends Menu  {

    private static final String FIREBASE_USERS = "Users";
    private static final String FIREBASE_USER_BOOKINGS = "bookings";
    private static final String FIREBASE_USER_BOOKING_TITLES = "Booking Titles";
    private static final String FIREBASE_BOOKING_DATE = "date";
    private static final String FIREBASE_BOOKING_START_TIME = "start time";
    private static final String FIREBASE_BOOKING_END_TIME = "end time";
    private static final String FIREBASE_BOOKING_COST = "cost";
    private static final String FIREBASE_BOOKING_ADDRESS = "address";
    private static final String FIREBASE_BOOKING_TITLE = "title";


    private FirebaseAuth mAuth;
    private Firebase mRef;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        mAuth = FirebaseAuth.getInstance();
        mRef = new Firebase("https://sabps-cd1b7.firebaseio.com");
        mDatabase = FirebaseDatabase.getInstance().getReference().child(FIREBASE_USERS);

        createTextViews();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /* Creates a Linear Layout for every booking that exists in the database, and a textview for every one of
    * its fields.
    */
    public void createTextViews() {

        String user_id = mAuth.getCurrentUser().getUid();
        final Firebase search_mRef = mRef.child(FIREBASE_USERS).child(user_id).child(FIREBASE_USER_BOOKING_TITLES);

        search_mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Map<String, Object> td = (HashMap<String, Object>) dataSnapshot.getValue();

                if(td != null) {

                List<Object> bookings = new ArrayList<>(td.values()); //list of all booking titles


                    for (int i = bookings.size() - 1; i >= 0; i--) {

                        createTextView(bookings.get(i).toString());
                    }

                } else Toast.makeText(MyBookings.this, "No past bookings", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    /* Takes a booking title and creates a textview for every field of the booking the title corresponds to.
    *  It then adds the textviews to a new Linear Layout.
    *  @Requires: the booking title should be in the same form it appears in the database.
    */
    public void createTextView(String bookingTitle){



            String user_id = mAuth.getCurrentUser().getUid();
            Firebase current_mRef = mRef.child(FIREBASE_USERS).child(user_id).child(FIREBASE_USER_BOOKINGS).child(bookingTitle);

            current_mRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Map<String, String> map = dataSnapshot.getValue(Map.class);

                        try{
                            map.get(FIREBASE_BOOKING_TITLE);
                        } catch(Exception e){
                            return;
                        }

                        LinearLayout MainLinearLayout = (LinearLayout) findViewById(R.id.my_bookings_layout);

                        LinearLayout innerLinearLayout = createLinearLayout();

                        MainLinearLayout.addView(innerLinearLayout);


                        TextView title = new TextView(MyBookings.this);
                        title.setText(map.get(FIREBASE_BOOKING_TITLE));
                        title.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        innerLinearLayout.addView(title);

                        TextView address = new TextView(MyBookings.this);
                        address.setText(map.get(FIREBASE_BOOKING_ADDRESS));
                        address.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        innerLinearLayout.addView(address);

                        TextView date = new TextView(MyBookings.this);
                        date.setText(map.get(FIREBASE_BOOKING_DATE));
                        date.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        innerLinearLayout.addView(date);

                        TextView startTime = new TextView(MyBookings.this);
                        startTime.setText(map.get(FIREBASE_BOOKING_START_TIME));
                        startTime.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        innerLinearLayout.addView(startTime);

                        TextView endTime = new TextView(MyBookings.this);
                        endTime.setText(map.get(FIREBASE_BOOKING_END_TIME));
                        endTime.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        innerLinearLayout.addView(endTime);

                        TextView cost = new TextView(MyBookings.this);
                        cost.setText(map.get(FIREBASE_BOOKING_COST));
                        cost.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        innerLinearLayout.addView(cost);

                    }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

    }

    /* Creates a new linear layout and sets its parameters */
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

