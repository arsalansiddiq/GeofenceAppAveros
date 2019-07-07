package com.example.android.geofenceappaveros.ui.maps;

import com.example.android.geofenceappaveros.prefs.GeofenceAppAverosPreference;

/**
 * Author: Arsalan Siddiq
 * Class MainPresenter will implement presenter methods which will ne invoked from Activity
 */
public class MainPresenter implements Presenter{

    //preference: Reference variable of Custom Preference Class for data Manipulation
    private GeofenceAppAverosPreference preference;

    /**
     * MainPresenter Constructore Required instance of app preference
     * @param sharedPreferences
     */
    public MainPresenter(GeofenceAppAverosPreference sharedPreferences) {
        //Assinging GeofenceAppAveros Preference instance
        preference = sharedPreferences;
    }

    /**
     * putBoolean Reference method to preference class to put boolean values
     * @param key
     * @param value
     */
    @Override
    public void putBoolean(String key, boolean value) {
        preference.putBoolean(key, value);
    }

    /**
     * putString Reference method to preference class to put string values
     * @param key
     * @param value
     */
    @Override
    public void putString(String key, String value) {
        preference.putString(key, value);
    }

    /**
     * getString Reference method to preference class to retrieve String values
     * @param key
     * return: String values
     */
    @Override
    public String getString(String key) {
       return preference.getString(key);
    }

    /**
     * getBoolean Reference method to preference class to retrieve boolean values
     * @param key
     * return: String values
     */
    @Override
    public boolean getBoolean(String key) {
        return preference.getBoolean(key);
    }

    /**
     * clear Reference method to preference class to clear preference
     */
    @Override
    public void clear() {
        preference.clear();
    }
}
