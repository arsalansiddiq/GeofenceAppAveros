package com.example.android.geofenceappaveros.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.android.geofenceappaveros.Constants;
import com.example.android.geofenceappaveros.R;
import com.example.android.geofenceappaveros.prefs.GeofenceAppAverosPreference;

public class GeofenceAppAverosLocationListener extends Service {

    private final String TAG = GeofenceAppAverosLocationListener.this.getClass().getName();

    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 0;
    private static final float LOCATION_DISTANCE = 0;
    NotificationCompat.Builder builder;
    NotificationCompat.Builder builderOreo;
    NotificationManager notificationManager;

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);

            GeofenceAppAverosPreference geofenceAppAverosPreference =
                    new GeofenceAppAverosPreference(GeofenceAppAverosLocationListener.this);

            Location initialLocation = new Location(LocationManager.NETWORK_PROVIDER);
            initialLocation.setLatitude(Double.parseDouble(
                    geofenceAppAverosPreference.getString(Constants.LATITUDE)));
            initialLocation.setLongitude(Double.parseDouble(
                    geofenceAppAverosPreference.getString(Constants.LONGITUDE)));

            double distance = initialLocation.distanceTo(location);
            double radius = Double.parseDouble(geofenceAppAverosPreference.getString(Constants.RADIUS));

            if (distance > radius) {
                sendNotification("You are out of fence!");
            } else {
                sendNotification("You are in fence!");
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.PASSIVE_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {

        Log.e(TAG, "onCreate");

        initializeLocationManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           makeStatusNotification("Service Running",this);
            final Notification n = builderOreo.setLocalOnly(true)
                    .setVisibility(Notification.VISIBILITY_PRIVATE)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .build();
            startForeground(3, n);
        }

        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[0]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }


        /*try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    LOCATION_INTERVAL,
                    LOCATION_DISTANCE,
                    mLocationListeners[1]
            );
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }*/
    }

    public void makeStatusNotification(String message, Context context) {

        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = Constants.WORK_MANAGER;
            String description = Constants.CHANNEL_DESCRIPTION;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel =
                    new NotificationChannel("GEOFENCEAPPAVEROS_NOTIFICATION", name, importance);
            channel.setDescription(description);

            // Add the channel
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create the notification
        builderOreo = new NotificationCompat.Builder(context, "GEOFENCEAPPAVEROS_NOTIFICATION")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("GeofenceAppAveros")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0]);

        // Show the notification
        NotificationManagerCompat.from(context).notify(1, builderOreo.build());
    }

    public void sendNotification(String notificationText) {


        Log.i("AlarmReceiver", "notified!");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            makeStatusNotification(notificationText,this);

        }

        builder = new NotificationCompat.Builder(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeStatusNotification(notificationText, this);
        } else {

            builder.setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("GeofenceAppAveros")
                    .setContentText(notificationText)
                    .setAutoCancel(true);
            notificationManager.notify(97, builder.build());

//        }
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: " + LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}