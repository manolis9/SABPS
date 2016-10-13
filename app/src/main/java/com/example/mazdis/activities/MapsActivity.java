package com.example.mazdis.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.example.mazdis.sabps.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends Menu implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mProgress = new ProgressDialog(this);

        /* The menu should have a "Find Parking" button instead of a "Current Booking"
        * button so set the altMenuFlag to 0
        */
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("altMenuFlag", 0);
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


//        mProgress.setMessage("Loading Map...");
//        mProgress.show();
//        while(list == null) {
//            list = modulesList();
//        }
//
//        mProgress.dismiss();
        placeMarkers();

        /* Once a marker is tapped, start ModuleProfile with the info of the
        *  SABPS module the marker corresponds to
        */
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                showModule(marker);
                return false;
            }
        });

    }

    /* Takes a list of SABPS modules and places a marker at the location of every SABPS module on the list
    *  @Requires: the list should not be null
    */
    public void placeMarkers(){

        ArrayList<Module> list = modulesList();

        for(int i = 0; i < list.size(); i++) {
            mMap.addMarker(new MarkerOptions().position(getLocationFromAddress(this, list.get(i).getAddress())).title(list.get(i).getTitle()));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(getLocationFromAddress(this, list.get(i).getAddress())));
        }
    }

    /* Takes a marker as an input and starts ModuleProfile Activity with the info
    *  of the SABPS module the marker corresponds to.
    */
    public void showModule(Marker marker){

        ArrayList<Module> list = modulesList();

        for(int i = 0; i < list.size(); i++){

            if(marker.getTitle().equals(list.get(i).getTitle())){

                Intent intent = new Intent(this, ModuleProfile.class);
                intent.putExtra("title", list.get(i).getTitle());
                intent.putExtra("address", list.get(i).getAddress());
                intent.putExtra("rate", String.valueOf(list.get(i).getRate()));
                startActivity(intent);
                break;
            }

        }

    }

    /* Returns a list of SABPS modules*/
    public ArrayList modulesList(){

        ArrayList<Module> list = new ArrayList<>();

        Module module1 = new Module("SABPS SUB", "6138 Student Union Blvd, Vancouver, BC V6T 1Z1", 1.00);
        list.add(module1);
        Module module2 = new Module("SABPS Athens", "Othonos 73, Kifisia, Greece 145 61", 1.00);
        list.add(module2);
        Module module3 = new Module("SABPS Kits Beach ", "1499 Arbutus St, Vancouver, BC V6J 5N2", 0.50);
        list.add(module3);
        Module module4 = new Module("SABPS Calgary", "324 8 Ave SW, Calgary, AB T2P 2Z2", 1.00);
        list.add(module4);
        Module module5 = new Module("SABPS Toronto", "220 Yonge St, Toronto, ON M5B 2H1", 2.00);
        list.add(module5);
        Module module6 = new Module("SABPS Waterfront", "200 Granville St, Vancouver, BC V6C 1S4", 2.00);
        list.add(module6);
        Module module7 = new Module("SABPS BRAZIL", "Brazil", 1.00);
        list.add(module7);
        Module module8 = new Module("SABPS NY", "NY", 3.00);
        list.add(module8);
        Module module9 = new Module("SABPS McDonald", "2827 W Broadway, Vancouver, BC V6K 2G6", 1.00);
        list.add(module9);
        Module module10 = new Module("SABPS Granville", "1465 W Broadway, Vancouver, BC V6H 3G6", 1.00);
        list.add(module10);
        Module module11 = new Module("SABPS Alma", "2565 Alma St, Vancouver, BC V6R 3R8", 1.00);
        list.add(module11);

        return list;
    }

    /* Given a context and an address, this method returns a LatLng object corresponding
    *  to that address.
    *  @Requires: the address has to be valid.
    */
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<android.location.Address> address;
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

}




