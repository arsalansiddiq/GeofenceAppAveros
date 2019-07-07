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

import com.example.android.geofenceappaveros.R;
import com.example.android.geofenceappaveros.data.Constants;
import com.example.android.geofenceappaveros.prefs.GeofenceAppAverosPreference;

/**
 * Author: Arsalan Siddiq
 * Service: GeofenceAppAverosLocationListener is a background service extended with Base Service
 * <a href="https://developer.android.com/guide/components/services>Service</a>.
 * <a href="https://developer.android.com/training/run-background-service/create-service">Background Service</a>.
 */
public class GeofenceAppAverosLocationListener extends Service {

    //Class name tag to filter logs with activity name
    private final String TAG = GeofenceAppAverosLocationListener.this.getClass().getName();

    /**
     * location manager reference variable
     * <a href="https://developer.android.com/reference/android/location/LocationManager">Location Manager</a>.
     */
    private LocationManager mLocationManager = null;

    //interval between lcoation fetch
    private static final int LOCATION_INTERVAL = 1000 * 60 * 5;
    //distance between location fetch
    private static final float LOCATION_DISTANCE = 500;
    // Preference reference varaible to store user preference in app
    private GeofenceAppAverosPreference preference;
    /**
     * Notificaiton builder is required to build custom notification
     * <a href="https://developer.android.com/reference/android/app/Notification.Builder">NotificationBuilder</a>.
     */
    NotificationCompat.Builder builder;
    /**
     * Notificaiton builder is required to build custom notification
     * Notification builder for Oreo which is required
     */
    NotificationCompat.Builder builderOreo;
    /**
     * Notification manager to notify user
     * <a href="https://developer.android.com/reference/android/app/NotificationManager">Notification Manager</a>.
     */
    NotificationManager notificationManager;

    //Location Listener class will be assign to location manager with interval and distance between each location computation
    //this class will update location after each interval and distance
    private class LocationListener implements android.location.LocationListener {
        //Location reference variable to assirgn location instance by Locationlistener implemented methods
        Location mLastLocation;

        /**
         * Constructor of LcoationListener class will allow to create instance
         * @param provider is a medium from which this listener will and location updates
         */
        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        //Default constructor is required for manifest service registration
        public LocationListener() {
        }

        /**
         * this method will be called after every interval location update
         * @param location will be passed after location updated method called this will hold current instance of loaction
         */
        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + location);
            Log.e(TAG, "interval: " + LOCATION_INTERVAL);
            mLastLocation.set(location);

            //preference custom class instance creation with current context to store user preference in app
            preference =
                    new GeofenceAppAverosPreference(GeofenceAppAverosLocationListener.this);

            //location instance to get comparable coordinates with initial user selected location coords
            Location initialLocation = new Location(LocationManager.PASSIVE_PROVIDER);

            //condition to check if user status is true, if user is enable to receive updates of current position
            if (preference.getBoolean(Constants.STATUS)) {
                //update user preference with updated coordinates
                initialLocation.setLatitude(Double.parseDouble(
                        preference.getString(Constants.LATITUDE)));
                initialLocation.setLongitude(Double.parseDouble(
                        preference.getString(Constants.LONGITUDE)));

                //distanceTo method will get actual distance b/w two coords
                double distance = initialLocation.distanceTo(location);

                //getting user entered radius
                double radius = Double.parseDouble(preference.getString(Constants.RADIUS));

                //logic to check weather user is in radius or not
                if (distance > radius) {
                    //this method will notify user about current position which is out of user set radius
                    sendNotification("You are out of fence!");
                } else {
                    //this method will notify user about current position which is in of user set radius
                    sendNotification("You have entered in fence!");
                }
            } else {
                //if user is not intented to receive position updates than ondestroy will stop serivce and disable location listener which will prevent from battery drain
                onDestroy();
            }
        }

        /**
         * this will be called when location provider is disabled
         * @param provider location provider name
         */
        @Override
        public void onProviderDisabled(String provider) {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        /**
         * this will be called when location provider is enabled
         * @param provider location provider name
         */
        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        /**
         * this will be called when location provider is changed
         * @param provider location provider name
         */
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

        // Create the Notification, but only on API 26+ because
        //this notification is required for background service, althoug we have other options too
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           makeStatusNotification("Service Running",this);
            final Notification notification = builderOreo.setLocalOnly(true)
                    .setVisibility(Notification.VISIBILITY_PRIVATE)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .build();
            startForeground(3, notification);
        }

        try {
            /**
             *Location Manager request for location updates to system
             * PASSIVE PROVIDER: This provider can be used to passively receive location updates when other applications or services request them without actually requesting the locations yourself.
             * LOCATION_INTERVAL: Time interval to request for location updates
             * LOCATION_DISTANCE: Distance between to request location updates
             * mLocationListeners: To listen on location change
             */
            mLocationManager.requestLocationUpdates(
                            LocationManager.PASSIVE_PROVIDER,
                            LOCATION_INTERVAL,
                            LOCATION_DISTANCE,
                            mLocationListeners[0]
                    );

        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "passive provider does not exist, " + ex.getMessage());
        }
    }

    //According to Android new rules Notification about running services is required
    //Notification required for Android API greater than or equal to Oreo
    public void makeStatusNotification(String message, Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Creating the NotificationChannel, but only for API 26+ because
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

    //Notification to inform about fence breach and enterance
    public void sendNotification(String notificationText) {

        Log.i("AlarmReceiver", "notified!");

        builder = new NotificationCompat.Builder(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //notificaiton to notify user about crossing or entering in the fence
        //logic to support 26+ and below API levels
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            makeStatusNotification(notificationText, this);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle("GeofenceAppAveros")
                    .setContentText(notificationText)
                    .setAutoCancel(true);
            notificationManager.notify(97, builder.build());
        }
    }

    //Default Destroy execute when service stops
    @Override
    public void onDestroy() {
            Log.e(TAG, "onDestroy");
        super.onDestroy();

        //Service Stop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);//true will remove notification
            stopSelf();
        } else {
            stopSelf();
        }

        //Remove Location Listener prevent battery drain after service stop
        mLocationManager.removeUpdates(mLocationListeners[0]);
    }

    //Creating LocationManager Singleton instance with System Location Service
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager - LOCATION_INTERVAL: " + LOCATION_INTERVAL + " LOCATION_DISTANCE: " + LOCATION_DISTANCE);
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}