package com.example.mazdis.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mazdis.sabps.R;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

/* While a Splash Screen is showing, this class checks if the user is
* already logged in or not. If they are, MapsActivity starts; if not,
* LoginActivity starts.
*/
public class SplashScreen extends BaseActivity {

    private static int SPLASH_SCREEN_DELAY = 300;
    private Firebase mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mRef = new Firebase("https://sabps-cd1b7.firebaseio.com/Users");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                AuthData authData = mRef.getAuth();
                if (authData != null) {

                    Intent intent = new Intent(SplashScreen.this, MapsActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, SPLASH_SCREEN_DELAY);
    }
}
