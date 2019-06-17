package com.example.android.geofenceappaveros;

import com.example.android.geofenceappaveros.ui.MapsActivity;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Test;
import org.robolectric.Robolectric;

/**
 * Author: Arsalan Siddiq
 * Class: GeofenceAppAverosUnitTests
 */

//@RunWith(RobolectricTestRunner.class)
public class GeofenceAppAverosUnitTests {

    @Test
    public void splahActivityTest() {
        MapsActivity mapsActivity = Robolectric.setupActivity(MapsActivity.class);

        mapsActivity.onMapLongClick(new LatLng(24.932627, 67.05374));

    }
//    private Context context = ApplicationProvider.getApplicationContext();
//
//    @Test
//    public void preferenceTest() {
//        GeofenceAppAverosPreference geofenceAppAverosPreference =
//                new GeofenceAppAverosPreference(context);
//
////        geofenceAppAverosPreference.
//    }

    @Test
    public void testMapActivity() {
        MapsActivity mapsActivity = new MapsActivity();

        mapsActivity.onMapLongClick(new LatLng(24.932627, 67.05374));
    }
}
