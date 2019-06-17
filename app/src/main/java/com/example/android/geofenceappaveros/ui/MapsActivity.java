package com.example.android.geofenceappaveros.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.geofenceappaveros.Constants;
import com.example.android.geofenceappaveros.R;
import com.example.android.geofenceappaveros.prefs.GeofenceAppAverosPreference;
import com.example.android.geofenceappaveros.services.GeofenceAppAverosLocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    GeofenceAppAverosPreference geofenceAppAverosPreference;

    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofenceAppAverosPreference =
                new GeofenceAppAverosPreference(MapsActivity.this);
    }

    /**
     * Manipulates the map once avilable.
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

        if (geofenceAppAverosPreference.getBoolean(Constants.STATUS)) {
            // Add a marker in Sydney and move the camera
            mMap.setOnMapLongClickListener(null);
            latitude = Double.parseDouble(geofenceAppAverosPreference.getString(Constants.LATITUDE));
            longitude = Double.parseDouble(geofenceAppAverosPreference.getString(Constants.LONGITUDE));

            LatLng position = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(position));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

            setRadius();

        } else {
            Toast.makeText(this, "long press to mark your location!", Toast.LENGTH_SHORT).show();
            mMap.setOnMapLongClickListener(this);
        }
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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

                    mMap.setOnMapLongClickListener(null);

                    geofenceAppAverosPreference.putBoolean(Constants.STATUS, true);
                    geofenceAppAverosPreference.putString(Constants.LATITUDE, String.valueOf(latLng.latitude));
                    geofenceAppAverosPreference.putString(Constants.LONGITUDE, String.valueOf(latLng.longitude));
                    geofenceAppAverosPreference.putString(Constants.RADIUS, String.valueOf(input.getText().toString()));

                    setRadius();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        ContextCompat.startForegroundService(MapsActivity.this,
                                new Intent(MapsActivity.this, GeofenceAppAverosLocationListener.class));
                    } else {
                        startService(new Intent(MapsActivity.this, GeofenceAppAverosLocationListener.class));
                    }

                }
            });
            alert.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Put actions for CANCEL button here, or leave in blank
                    mMap.clear();
                    mMap.setOnMapLongClickListener(MapsActivity.this);
                }
            });
            alert.show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "granted!", Toast.LENGTH_SHORT).show();
            // permission was granted, yay! Do the
            // contacts-related task you need to do.
        } else {
            Toast.makeText(this, "not granted!", Toast.LENGTH_SHORT).show();
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
        }
    }

    private void setRadius() {

        LatLng latLng = new LatLng(Double.parseDouble(geofenceAppAverosPreference.getString(Constants.LATITUDE)),
                Double.parseDouble(geofenceAppAverosPreference.getString(Constants.LONGITUDE)));

        mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(Double.parseDouble(geofenceAppAverosPreference.getString(Constants.RADIUS)))
                .strokeColor(Color.RED));
    }
}
