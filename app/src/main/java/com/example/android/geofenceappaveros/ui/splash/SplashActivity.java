package com.example.android.geofenceappaveros.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.android.geofenceappaveros.R;
import com.example.android.geofenceappaveros.ui.maps.MapsActivity;

/**
 * Author: Arsalan Siddiq
 * Activity: This activity will hold user for 2 seconds and is extended with base activity
 */
public class SplashActivity extends AppCompatActivity implements View {

    private final int holdTime = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //init Function to hold Splash Screen for 2 seconds
        init();
    }

     public void init() {
        final Handler handler = new Handler();
        try {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Launching Map Activity
                    startActivity(new Intent(SplashActivity.this, MapsActivity.class));
                }
            }, holdTime);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
