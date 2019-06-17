package com.example.android.geofenceappaveros.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.geofenceappaveros.data.Constants;

/**
 * Author: Arsalan Siddiq
 * Class: Preference
 */

public class GeofenceAppAverosPreference {

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreEditor;

    public GeofenceAppAverosPreference(Context context) {
        mSharedPreferences = context.getSharedPreferences(Constants.GEOFENCE_APP_AVEROS_PREFERENCES,context.MODE_PRIVATE);
        mSharedPreEditor = mSharedPreferences.edit();
    }

    //generic string method to store strings
    public void putString(String key, String value) {
        mSharedPreEditor.putString(key, value);
        mSharedPreEditor.apply();
    }

    //generic boolean method to store strings
    public void putBoolean(String key, boolean value) {
        mSharedPreEditor.putBoolean(key, value);
        mSharedPreEditor.apply();
    }

    //generic string method to retrieve strings
    public String getString(String key) {
        return  mSharedPreferences.getString(key, null);
    }
    //generic boolean method to retrieve strings
    public Boolean getBoolean(String key) {
        return  mSharedPreferences.getBoolean(key, false);
    }

    public void clear() {
        mSharedPreEditor.clear().apply();
    }
}
