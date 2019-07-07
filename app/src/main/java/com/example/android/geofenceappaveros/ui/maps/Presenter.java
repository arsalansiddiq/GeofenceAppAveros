package com.example.android.geofenceappaveros.ui.maps;

/**
 * Author: Arsalan Siddiq
 * Presenter interface to declare all signatures required for presenter implementation
 */
public interface Presenter {

    void putBoolean(String key, boolean value);
    void putString(String key, String value);
    String getString(String key);
    boolean getBoolean(String key);
    void clear();

}
