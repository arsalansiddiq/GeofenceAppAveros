package com.example.android.geofenceappaveros;


import com.example.android.geofenceappaveros.services.GeofenceAppAverosLocationListener;
import com.example.android.geofenceappaveros.ui.MapsActivity;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.junit.runner.RunWith;

public class GeofenceAppAverosUnitTests {

    @Test
    public void splahActivityTest() {
//        MapsActivity mapsActivity = Robolectric.setupActivity(MapsActivity.class);
//        mapsActivity.onMapLongClick(new LatLng(24.932627, 67.05374));
    }

    @Test
    public void preferenceTest() {
        GeofenceAppAverosLocationListener geofenceAppAverosLocationListener =
                new GeofenceAppAverosLocationListener();

    }

    @Test
    public void testMapActivity() {
        MapsActivity mapsActivity = new MapsActivity();

        mapsActivity.onMapLongClick(new LatLng(24.932627, 67.05374));
    }
}
