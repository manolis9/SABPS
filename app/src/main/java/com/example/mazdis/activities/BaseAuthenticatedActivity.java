//package com.example.mazdis.activities;
//
//import android.content.Intent;
//import android.os.Bundle;
//
//import com.google.firebase.analytics.FirebaseAnalytics;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public abstract class BaseAuthenticatedActivity extends BaseActivity {
//
//    @Override
//    protected final void onCreate(Bundle savedState){
//        super.onCreate(savedState);
//
//
//
//        if(!application.getAuth().getUser().isLoggedIn()){
//            startActivity(new Intent(this, LoginActivity.class));
//            finish();
//            return;
//        }
//
//        onSabpsCreate(savedState);
//
//
//    }
//
//    protected abstract void onSabpsCreate(Bundle savedState);
//
//}
