package com.example.mazdis.activities;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mazdis.sabps.R;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleProfile extends BaseActivity {

    private static final String EMAIL_FROM = "manolis.ioannides@mazdis.com";
    private static final String EMAIL_SUBJECT = "Booking Confirmation";
    private static final String EMAIL_BODY = "You made a booking at the following address:\n";
    private static final String FIREBASE_USER_NAME = "name";
    private static final String FIREBASE_USER_EMAIL = "email";
    private static final String FIREBASE_USERS = "Users";
    private static final String FIREBASE_EMAILS_TO_SEND = "Emails to Send";
    private static final String FIREBASE_EMAIL = "email";
    private static final String FIREBASE_USER_BOOKING_IN_PROGRESS = "booking in progress";
    private static final String FIREBASE_USER_BOOKINGS = "bookings";
    private static final String FIREBASE_USER_BOOKING_TITLES = "Booking Titles";
    private static final String FIREBASE_BOOKING_DATE = "date";
    private static final String FIREBASE_BOOKING_START_TIME = "start time";
    private static final String FIREBASE_BOOKING_ADDRESS = "address";
    private static final String FIREBASE_BOOKING_TITLE = "title";
    private static final String FIREBASE_EMAIL_FROM = "from";
    private static final String FIREBASE_EMAIL_TO = "to";
    private static final String FIREBASE_EMAIL_SUBJECT = "subject";
    private static final String FIREBASE_EMAIL_BODY = "body";

    private TextView titleTextView;
    private TextView addressTextView;
    private TextView rateTextView;
    private TextView distanceTextView;
    private String parsedDistance;
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

        getWindow().setLayout((int) (width * 0.7), (int) (height * 0.4));

        TextView headerView = (TextView) findViewById(R.id.header_textview);
        headerView.setPadding(0, 70, 0, 70);

        titleTextView = (TextView) findViewById(R.id.title_textview);
        addressTextView = (TextView) findViewById(R.id.address_textview);
        rateTextView = (TextView) findViewById(R.id.rate_textview);
        distanceTextView = (TextView) findViewById(R.id.distance_textview_ModuleProfile);

        /* Set the textviews to contain the info received from MapsActivity */
        titleTextView.setText(getIntent().getStringExtra("title"));
        addressTextView.setText(getIntent().getStringExtra("address"));
        String rate = getIntent().getStringExtra("rate");
        String dollarRate = '$' + rate;
        rateTextView.setText(dollarRate);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        startLocationService();
        registerReceiver(locationUpdated, new IntentFilter("LOCATION_UPDATED"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(locationUpdated);
    }

    private BroadcastReceiver locationUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            LatLng crtLocation;
            Location currentLocation = new Location("currentLocation");

            crtLocation = intent.getParcelableExtra(("location update"));

            Log.v("received lat", Double.toString(crtLocation.latitude));

            currentLocation.setLatitude(crtLocation.latitude);
            currentLocation.setLongitude(crtLocation.longitude);

            Location markerLocation = new Location("markerLocation");

            LatLng mkrLoc = getLocationFromAddress(ModuleProfile.this, addressTextView.getText().toString());

            markerLocation.setLatitude(mkrLoc.latitude);
            markerLocation.setLongitude(mkrLoc.longitude);

                // float distance = currentLocation.distanceTo(markerLocation) / 1000;
                String distance = getBicyclingDistance(currentLocation,markerLocation);
                if(distance != null) {
                    String distanceAway = distance + " away";
                    distanceTextView.setText(distanceAway);
                }

        }
    };


    /* Once the user taps on "Reserve", a booking and a booking title
    * are added to the database and ReservedMapsActivity starts*/
    public void startReservedMap(View view) {

        createBooking();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("countdownDone", 0);
        editor.commit();

        Intent intent = new Intent(this, ReservedMapsActivity.class);
        startActivity(intent);
        finish();

    }

    /* Creates a new booking in the database under the current user. The
    * booking includes the current date and time and the SABPS module title
    * and address. The method also creates a new booking title under the current user's
    * "Booking Titles". The bookings info and the booking title are added to Shared Preferences.
    * Also it updates the email field which the server will see and email the user
    */
    public void createBooking() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Calendar c = Calendar.getInstance();
        String startTime = timeFormat.format(c.getTime());
        String date = dateFormat.format(c.getTime());

        String bookingTitle = (date + " " + startTime + " " + titleTextView.getText().toString());

        String user_id = mAuth.getCurrentUser().getUid();

        //Bookings field in database
        DatabaseReference current_user_bookings = mDatabase.child(FIREBASE_USERS).child(user_id).child(FIREBASE_USER_BOOKINGS).child(bookingTitle);

        Map<String, String> booking = new HashMap<>();
        booking.put(FIREBASE_BOOKING_DATE, date);
        booking.put(FIREBASE_BOOKING_START_TIME, startTime);
        booking.put(FIREBASE_BOOKING_ADDRESS, addressTextView.getText().toString());
        booking.put(FIREBASE_BOOKING_TITLE, titleTextView.getText().toString());
        current_user_bookings.setValue(booking);

        //Booking Titles field in database
        DatabaseReference booking_db = mDatabase.child(FIREBASE_USERS).child(user_id).child(FIREBASE_USER_BOOKING_TITLES);
        booking_db.child(bookingTitle).setValue(bookingTitle);

        createEmail();

        DatabaseReference current_user_db = mDatabase.child(FIREBASE_USERS).child(user_id);
        current_user_db.child(FIREBASE_USER_BOOKING_IN_PROGRESS).setValue("true");

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("bookingTitle", bookingTitle);
        editor.putString("bookingStartTime", startTime);
        editor.putString("moduleAddress", addressTextView.getText().toString());
        editor.putString("moduleTitle", titleTextView.getText().toString());
        editor.putString("moduleRate", rateTextView.getText().toString());

        editor.commit();

    }

    public void createEmail() {

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
                        + addressTextView.getText().toString());

                emails.setValue(emailFields);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng latlng = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            android.location.Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            latlng = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return latlng;
    }

    public String getBicyclingDistance(final Location start, final Location end) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL url = new URL("http://maps.googleapis.com/maps/api/directions/json?origin=" + start.getLatitude() + "," + start.getLongitude() + "&destination=" + end.getLatitude() + "," + end.getLongitude() + "&sensor=false&units=metric&mode=bicycling");
                    final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    String response;
                    response = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray array = jsonObject.getJSONArray("routes");
                    JSONObject routes = array.getJSONObject(0);
                    JSONArray legs = routes.getJSONArray("legs");
                    JSONObject steps = legs.getJSONObject(0);
                    JSONObject distance = steps.getJSONObject("distance");
                    parsedDistance = distance.getString("text");

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return parsedDistance;
    }

    private void startLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        if(!isMyServiceRunning(LocationService.class)) {
            startService(intent);
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
