package com.mazdis.sabps.infrastructure;

import android.app.Application;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;


public class SabpsApplication extends Application {

    private Auth auth;

    @Override
    public void onCreate(){
        super.onCreate();
        auth = new Auth(this);
        Firebase.setAndroidContext(this);
    }
    public  Auth getAuth(){
        return  auth;
    }
}
