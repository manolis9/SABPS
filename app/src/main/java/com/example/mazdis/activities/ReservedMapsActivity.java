package com.example.mazdis.activities;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;

import android.location.Location;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mazdis.sabps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReservedMapsActivity extends Menu implements OnMapReadyCallback {

    private static final String FIREBASE_USERS = "Users";
    private static final String FIREBASE_USER_BOOKINGS = "bookings";
    public String parsedDistance;
    private GoogleMap mMap;
    private ProgressDialog mProgress;
    TextView countDownText;
    TextView addressText;
    TextView distanceText;
    Button doneButton;
    Button parkBikeButton;
    DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Location markerLocation;
    Location currentLocation = new Location("currentLocation");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.reservedMap);
        mapFragment.getMapAsync(this);

        markerLocation = new Location("markerLocation");

        mProgress = new ProgressDialog(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        registerReceiver(timeUpdated, new IntentFilter("TIME_UPDATED"));
        registerReceiver(locationUpdated, new IntentFilter("LOCATION_UPDATED"));

        doneButton = (Button) findViewById(R.id.done_button);
        parkBikeButton = (Button) findViewById(R.id.park_button);
        distanceText = (TextView) findViewById(R.id.distance_textview_ReservedMaps);
        countDownText = (TextView) findViewById(R.id.countDown_textView);

        /* The menu should have a "Current Booking" button instead of a "Find Parking"
        * button so set the altMenuFlag to 1
        */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("altMenuFlag", 1);
        editor.commit();

        addressText = (TextView) findViewById(R.id.address_textview);
        addressText.setText(prefs.getString("moduleAddress", "no id"));

        startLocationService();
        startBroadcastService();

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(timeUpdated);
        unregisterReceiver(locationUpdated);
    }

    private BroadcastReceiver timeUpdated = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            countDownText.setText(intent.getExtras().getString("remaining time"));

            if (intent.getExtras().getString("button") != null) {
                doneButton.setText(intent.getExtras().getString("button"));
            }

        }
    };

    private BroadcastReceiver locationUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            LatLng crtLocation;


            crtLocation = intent.getParcelableExtra(("location update"));

            Log.v("received lat", Double.toString(crtLocation.latitude));

            currentLocation.setLatitude(crtLocation.latitude);
            currentLocation.setLongitude(crtLocation.longitude);


            String distance = getBicyclingDistance(currentLocation,markerLocation);
            if((distance != null) && (markerLocation != null)) {
                distanceText.setText(distance + " away");
            }

        }
    };


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
        MarkerOptions markerOptions = new MarkerOptions();
        mMap.addMarker(markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.custom_marker))
                .position(getLocationFromAddress(this, reservedAddress)).title(reservedTitle));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(getLocationFromAddress(this, reservedAddress)));

        LatLng mrkLoc;
        mrkLoc = markerOptions.getPosition();

        markerLocation.setLatitude(mrkLoc.latitude);
        markerLocation.setLongitude(mrkLoc.longitude);
    }

    /* When the user taps "Done", confirmDone activity starts*/
    public void confirmDone(View view) {
        startActivity(new Intent(this, ConfirmDone.class));
    }

    public void parkBike(View v) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ReservedMapsActivity.this);
        SharedPreferences.Editor editor = prefs.edit();
        String user_id = mAuth.getCurrentUser().getUid();
        String bookingTitle = prefs.getString("bookingTitle", "no id");
        editor.putInt("countdownDone", 1);
        editor.commit();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Calendar c = Calendar.getInstance();
        String startTime = timeFormat.format(c.getTime());
        DatabaseReference current_user_bookings = mDatabase.child(FIREBASE_USERS).child(user_id).child(FIREBASE_USER_BOOKINGS).child(bookingTitle);
        current_user_bookings.child("start time").setValue(startTime);

        editor.putString("bookingStartTime", startTime);
        editor.commit();

        parkBikeButton.setVisibility(View.GONE);
    }


    private void startBroadcastService() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ReservedMapsActivity.this);
        int countdownDone = prefs.getInt("countdownDone", 0);
        Intent intent = new Intent(this, BroadcastService.class);
        if (!isMyServiceRunning(BroadcastService.class) && (countdownDone == 0)) {
            startService(intent);
        }
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

                    JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
                    final String encodedString = overviewPolylines.getString("points");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<LatLng> list = decodePoly(encodedString);
                            PolylineOptions options = new PolylineOptions().width(15).color(Color.BLUE).geodesic(true);
                            for (int z = 0; z < list.size(); z++) {
                                LatLng point = list.get(z);
                                options.add(point);
                            }
                            mMap.addPolyline(options);
                        }
                    });

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

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

}
