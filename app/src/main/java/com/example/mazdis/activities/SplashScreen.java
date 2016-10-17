package com.example.mazdis.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.mazdis.sabps.R;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

/* While a Splash Screen is showing, this class checks if the user is
* already logged in or not. If they are, if a booking is in progress, ReservedMapsActivity starts,
* otherwise MapsActivity starts; if they are not logged in, LoginActivity starts.
*/
public class SplashScreen extends BaseActivity {

    private static int SPLASH_SCREEN_DELAY = 300;
    private Firebase mRef;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseAuth.AuthStateListener firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mRef = new Firebase("https://sabps-cd1b7.firebaseio.com/Users");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        firebaseAuth = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null) {

                    String user_id = mAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_ref = mDatabase.child("Users").child(user_id);

                    current_user_ref.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                        @Override
                        public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
                            Map<String, String> map = (Map) dataSnapshot.getValue();

                            String booking_in_progress = map.get("booking in progress");

                            if(booking_in_progress.equals("true")){
                                startActivity(new Intent(SplashScreen.this, ReservedMapsActivity.class));
                                finish();
                            } else {
                                startActivity(new Intent(SplashScreen.this, MapsActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {

                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

        };

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAuth.addAuthStateListener(firebaseAuth);
            }
        }, SPLASH_SCREEN_DELAY);
    }
}
