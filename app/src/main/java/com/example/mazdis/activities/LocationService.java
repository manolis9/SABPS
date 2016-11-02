package com.example.mazdis.activities;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class LocationService extends Service {

    private Handler locationHandler;
    double longitude;
    double latitude;

    @Override
    public void onCreate() {
        super.onCreate();

        locationHandler = new Handler();
//        Toast.makeText(LocationService.this, "LocationService Created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Toast.makeText(LocationService.this, "LocationService Started", Toast.LENGTH_SHORT).show();
        locationHandler.postDelayed(updateLocation, 1000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationHandler.removeCallbacksAndMessages(null);
//        Toast.makeText(LocationService.this, "LocationService Destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Runnable updateLocation = new Runnable() {
        @Override
        public void run() {
            LocationManager lm;
            Location location;
            locationHandler.postDelayed(updateLocation, 1000);
            if (Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission(LocationService.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(LocationService.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, locationListener);
            Log.v("this runs", "under lm.requestLocationUpdates");
        }
    };

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            LatLng currentLocation;
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            currentLocation = new LatLng(latitude, longitude);

            Intent i = new Intent("LOCATION_UPDATED");
            Bundle args = new Bundle();
            args.putParcelable("location update", currentLocation);
            i.putExtras(args);
            sendBroadcast(i);

            double lat = currentLocation.latitude;
            double lng = currentLocation.longitude;

            Log.v("current location", Double.toString(lat) + " " + Double.toString(lng));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
}
