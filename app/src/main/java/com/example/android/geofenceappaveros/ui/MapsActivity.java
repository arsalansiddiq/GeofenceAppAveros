package com.example.android.geofenceappaveros.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.widget.EditText;

import com.example.android.geofenceappaveros.Constants;
import com.example.android.geofenceappaveros.R;
import com.example.android.geofenceappaveros.prefs.GeofenceAppAverosPreference;
import com.example.android.geofenceappaveros.services.LocationTrackingService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.setOnMapLongClickListener(this);
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
            intent.setData(uri);
            this.startActivity(intent);
//            Toast.makeText(MapsActivity.this, "Please Turn on your location!", Toast.LENGTH_SHORT).show();
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng).title(""));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.text_radius);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setRawInputType(Configuration.KEYBOARD_12KEY);
            alert.setView(input);
            alert.setPositiveButton(R.string.text_set, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    mMap.addCircle(new CircleOptions()
                            .center(latLng)
                            .radius(Double.parseDouble(input.getText().toString()))
                            .strokeColor(Color.RED)
                            .fillColor(Color.BLUE));

                    GeofenceAppAverosPreference geofenceAppAverosPreference =
                            new GeofenceAppAverosPreference(MapsActivity.this);

                    geofenceAppAverosPreference.putString(Constants.LATITUDE, String.valueOf(latLng.latitude));
                    geofenceAppAverosPreference.putString(Constants.LONGITUDE, String.valueOf(latLng.longitude));
                    geofenceAppAverosPreference.putString(Constants.RADIUS, String.valueOf(input.getText().toString()));

                    startService(new Intent(MapsActivity.this, LocationTrackingService.class));

                }
            });
            alert.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Put actions for CANCEL button here, or leave in blank
                }
            });
            alert.show();
        }

    }
}
