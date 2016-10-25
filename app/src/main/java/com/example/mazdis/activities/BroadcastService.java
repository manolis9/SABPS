package com.example.mazdis.activities;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.mazdis.sabps.R;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class BroadcastService extends Service {


    private static final String FIREBASE_USERS = "Users";
    private static final String FIREBASE_USER_BOOKINGS = "bookings";
    private static final String FIREBASE_USER_BOOKING_TITLES = "Booking Titles";
    private static final String FIREBASE_USER_BOOKING_IN_PROGRESS = "booking in progress";
    private final static String TAG = "BroadcastService";
    private final int ONE_MINUTE = 1000;
    private final int THIRTY_MINUTES = 1000 * 30;
    private long remainingTimeMillis = THIRTY_MINUTES;

    public static final String COUNTDOWN_BR = "com.example.mazdis.activities.countdown_br";
    Intent bi = new Intent(COUNTDOWN_BR);

    private FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    private Firebase mRef;
    private Handler countdownHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        countdownHandler = new Handler();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mRef = new Firebase("https://sabps-cd1b7.firebaseio.com");

        Intent notificationIntent = new Intent(this, ReservedMapsActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.mazdis_logo)
                .setContentTitle("Mazdis")
                .setContentText(TimeUnit.MILLISECONDS.toMinutes(remainingTimeMillis) + " until booking")
                .setContentIntent(pendingIntent).build();

        startForeground(1337, notification);

        Toast.makeText(BroadcastService.this, "Service Created", Toast.LENGTH_SHORT).show();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(BroadcastService.this, "Service Started", Toast.LENGTH_SHORT).show();

        countdownHandler.postDelayed(countdown, ONE_MINUTE);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {

        Toast.makeText(BroadcastService.this, "Service Stopped", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }


    private Runnable countdown = new Runnable() {
        public void run() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(BroadcastService.this);
            SharedPreferences.Editor editor = prefs.edit();
            int countdownDone = prefs.getInt("countdownDone", 0);
            Intent i = new Intent("TIME_UPDATED");
            String user_id = mAuth.getCurrentUser().getUid();
            String bookingTitle = prefs.getString("bookingTitle", "no id");

            if ((remainingTimeMillis > 0) && (countdownDone == 0)) {

                countdownHandler.postDelayed(countdown, ONE_MINUTE);
                remainingTimeMillis = remainingTimeMillis - ONE_MINUTE;
                Log.v("remaining time", Long.toString(remainingTimeMillis));

                i.putExtra("remaining time", "Time Remaining: " + String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes(remainingTimeMillis),
                        TimeUnit.MILLISECONDS.toSeconds(remainingTimeMillis)
                ));
                sendBroadcast(i);

            } else if ((remainingTimeMillis == 0) && (countdownDone == 0)) {

                i.putExtra("remaining time", "Booking cancelled");
                DatabaseReference current_user_db = mDatabase.child(FIREBASE_USERS).child(user_id);
                current_user_db.child(FIREBASE_USER_BOOKING_IN_PROGRESS).setValue("false");

                Firebase bookingTitles_mRef = mRef.child(FIREBASE_USERS).child(user_id).child(FIREBASE_USER_BOOKING_TITLES).child(bookingTitle);
                bookingTitles_mRef.removeValue();

                Firebase bookings_mRef = mRef.child(FIREBASE_USERS).child(user_id).child(FIREBASE_USER_BOOKINGS).child(bookingTitle);
                bookings_mRef.removeValue();

                editor.putInt("altMenuFlag", 0);
                editor.putInt("countdownDone", 1);
                editor.commit();
//                startActivity(new Intent(BroadcastService.this, MapsActivity.class));

                stopSelf();

            } else {

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                Calendar c = Calendar.getInstance();
                String startTime = timeFormat.format(c.getTime());
                DatabaseReference current_user_bookings = mDatabase.child(FIREBASE_USERS).child(user_id).child(FIREBASE_USER_BOOKINGS).child(bookingTitle);
                current_user_bookings.child("start time").setValue(startTime);

                editor.putInt("countdownDone", 1);
                editor.commit();

                i.putExtra("remaining time", "");

                remainingTimeMillis = THIRTY_MINUTES;

                stopSelf();
            }
        }
    };
}


