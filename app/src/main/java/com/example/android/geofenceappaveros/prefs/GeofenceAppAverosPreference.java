package com.example.android.geofenceappaveros.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.geofenceappaveros.data.Constants;

/**
 * Author: Arsalan Siddiq
 * Class: Preference for data manipulation in App
 */

public class GeofenceAppAverosPreference {

    /*
    *SharedPreference to save user settings in app database
    *This reference variable will be using to perform CRUD in Preference
     */
    private SharedPreferences mSharedPreferences;

    /**
     *GeofenceAppAverosPreference Constructor to get instance of class
     * @param context is required to intialize SharedPreference
     */
    public GeofenceAppAverosPreference(Context context) {
        //Assigning Sharepreference instance to reference varaible mSharedPreferences
        mSharedPreferences = context.getSharedPreferences(Constants.GEOFENCE_APP_AVEROS_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * PutString method will store String values identified by a key to SharedPreference
     * @param key
     * @param value
     */
    public void putString(String key, String value) {
        /**
         * edit() is required to perform CRUD in SharedPreferences
         * PutString Actual Sharedpreference interface signature
         * apply() will apply changes into Sharedpreferences
         */
        mSharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * PutBoolean method will store Boolean values identified by a key to SharedPreference
     * @param key
     * @param value
     */
    public void putBoolean(String key, boolean value) {
        /**
         * edit() is required to perform CRUD in SharedPreferences
         * PutBoolean Actual Sharedpreference interface signature
         * apply() will apply changes into Sharedpreferences
         */
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    /**
     * getString custom method to retrieve data from sharedpreference
     * @param key
     * @return String value of particular key from sharedpreference
     */
    public String getString(String key) {

        //getString Actual Sharedpreference interface signature which will get String values of a key
        return  mSharedPreferences.getString(key, null);
    }

    /**
     * getBoolean custom method to retrieve data from sharedpreference
     * @param key
     * @return Boolean of particular key from sharedpreference
     */
    public Boolean getBoolean(String key) {

        //getboolean Actual Sharedpreference interface signature which will retrieve boolean value of a key
        return  mSharedPreferences.getBoolean(key, false);
    }

    /**
     * clear() custom method to clear stored sharedpreference in app
     */
    public void clear() {
        /**
         * edit() is required to perform CRUD in SharedPreferences
         * clear() Actual Sharedpreference interface signature which wiped out all stored sharedpreferences
         * apply() will apply changes into Sharedpreferences
         */
        mSharedPreferences.edit().clear().apply();
    }
}
