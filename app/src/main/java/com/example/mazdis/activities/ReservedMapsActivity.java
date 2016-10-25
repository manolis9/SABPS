package com.example.mazdis.activities;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;

import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mazdis.sabps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReservedMapsActivity extends Menu implements OnMapReadyCallback {

    private static final String FIREBASE_USERS = "Users";
    private static final String FIREBASE_USER_BOOKINGS = "bookings";
    private static final String TAG = "ReservedMapActivity";
    private GoogleMap mMap;
    private ProgressDialog mProgress;
    TextView countDownText;
    TextView addressText;
    TextView metersAwayText;
    Button btn;
    DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Handler timeHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.reservedMap);
        mapFragment.getMapAsync(this);

        mProgress = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        btn = (Button) findViewById(R.id.done_button);
        metersAwayText = (TextView) findViewById(R.id.meters_away_textview);
        countDownText = (TextView) findViewById(R.id.countDown_textView);

        timeHandler = new Handler();

        /* The menu should have a "Current Booking" button instead of a "Find Parking"
        * button so set the altMenuFlag to 1
        */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("altMenuFlag", 1);
        editor.commit();


        updateGUI();
        addressText = (TextView) findViewById(R.id.address_textview);
        addressText.setText(prefs.getString("moduleAddress", "no id"));

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1340);
        }

        placeMarker();

    }


    /* Given a context and an address, this method returns a LatLng object corresponding
     * to that address.
     * @Requires: the address has to be valid.
     */
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

    /* Receives from Firebase the SABPS module's address and title and
    * sets a marker at that address with that title.
    */
    public void placeMarker() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String reservedAddress = prefs.getString("moduleAddress", "no id");
        String reservedTitle = prefs.getString("moduleTitle", "no id");

        mProgress.setMessage("Loading Map...");
        mProgress.show();

        while (reservedAddress == null) {
            reservedAddress = prefs.getString("moduleAddress", "no id");
        }

        mProgress.dismiss();
        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
                .position(getLocationFromAddress(this, reservedAddress)).title(reservedTitle));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(getLocationFromAddress(this, reservedAddress)));
    }

    /* When the user taps "Done", confirmDone activity starts*/
    public void confirmDone(View view) {
        startActivity(new Intent(this, ConfirmDone.class));
    }


    private void updateGUI() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ReservedMapsActivity.this);
        int countdownDone = prefs.getInt("countdownDone", 0);
        Intent intent = new Intent(this, BroadcastService.class);
        if (!isMyServiceRunning(BroadcastService.class) && (countdownDone == 0)) {
            startService(intent);
        }
        if (intent.getExtras() != null) {
            long millisUntilFinished = intent.getLongExtra("countdown", 0);
            countDownText.setText(Long.toString(millisUntilFinished));
        } else Log.v("intent", "it's null");
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
