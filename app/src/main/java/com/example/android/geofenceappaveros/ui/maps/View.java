package com.example.android.geofenceappaveros.ui.maps;

import com.google.android.gms.maps.model.LatLng;

/**
 * Author: Arsalan Siddiq
 * View interface to declare all signatures required for View implementation
 */
public interface View {

    void mapClear_enableUserSelection();
    void setRadius();
    void drawRadius(LatLng latLng);
    boolean checkInternet();
    void networkDialog();
    void toast(int content);
    void startServices();
    void stopServices();

}
