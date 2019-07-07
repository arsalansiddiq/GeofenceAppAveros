package com.example.android.geofenceappaveros;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.geofenceappaveros.prefs.GeofenceAppAverosPreference;
import com.example.android.geofenceappaveros.services.GeofenceAppAverosLocationListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    Context appContext = InstrumentationRegistry.getTargetContext();

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.

        assertEquals("com.example.android.geofenceappaveros", appContext.getPackageName());
    }

    @Test
    public void preferenceTest() {
        GeofenceAppAverosPreference geofenceAppAverosPreference =
                new GeofenceAppAverosPreference(appContext);

        geofenceAppAverosPreference.putString("test_key","test");

        assertEquals(geofenceAppAverosPreference.getString("test_key"), "test");
    }

    @Test
    public void testGeofenceAppNotification() {

        GeofenceAppAverosLocationListener geofenceAppAverosLocationListener =
                new GeofenceAppAverosLocationListener();

        geofenceAppAverosLocationListener.makeStatusNotification("test Notificaiton", appContext);
    }
}
