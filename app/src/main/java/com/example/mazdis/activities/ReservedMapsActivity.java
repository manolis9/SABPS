package com.example.mazdis.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import com.example.mazdis.sabps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class ReservedMapsActivity extends Menu implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserved_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mProgress = new ProgressDialog(this);

        /* The menu should have a "Current Booking" button instead of a "Find Parking"
        * button so set the altMenuFlag to 1
        */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("altMenuFlag", 1);
        editor.commit();

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

    /* Receives from SharedPreferences the SABPS module's address and title and
    * sets a marker at that address with that title.
    */
    public void placeMarker(){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String reservedAddress = prefs.getString("moduleAddress", "no id");
        String reservedTitle = prefs.getString("moduleTitle", "no id");

        mProgress.setMessage("Loading Map...");
        mProgress.show();

        while(reservedAddress == null){
           reservedAddress = prefs.getString("moduleAddress", "no id");
        }

        mProgress.dismiss();
        mMap.addMarker(new MarkerOptions().position(getLocationFromAddress(this, reservedAddress)).title(reservedTitle));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(getLocationFromAddress(this, reservedAddress)));
    }

    /* When the user taps "Done", confirmDone activity starts*/
    public void confirmDone(View view){
        startActivity(new Intent(this, ConfirmDone.class));
    }

}
