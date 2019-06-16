package com.example.android.geofenceappaveros.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.geofenceappaveros.Constants;

/**
 * Created by jellani on 6/16/2019.
 */

public class GeofenceAppAverosPreference {

    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreEditor;

    public GeofenceAppAverosPreference(Context context) {
        this.mContext = context;
        mSharedPreferences = context.getSharedPreferences(Constants.GEOFENCE_APP_AVEROS_PREFERENCES,context.MODE_PRIVATE);
        mSharedPreEditor = mSharedPreferences.edit();
    }

    public void putString(String key, String value) {
        mSharedPreEditor.putString(key, value);
        mSharedPreEditor.apply();
    }

    public void putLong(String key, Long value) {
        mSharedPreEditor.putLong(key, value);
        mSharedPreEditor.apply();
    }

    public void putBoolean(String key, boolean value) {
        mSharedPreEditor.putBoolean(key, value);
        mSharedPreEditor.apply();
    }

    public int getInt(String key) {
        return  mSharedPreferences.getInt(key, 0);
    }

    public String getString(String key) {
        return  mSharedPreferences.getString(key, null);
    }
    public Boolean getBoolean(String key) {
        return  mSharedPreferences.getBoolean(key, false);
    }

    public void clear() {
        mSharedPreEditor.clear().apply();
    }
}
