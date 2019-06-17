package com.example.android.geofenceappaveros.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.geofenceappaveros.R;
import com.example.android.geofenceappaveros.data.Constants;
import com.example.android.geofenceappaveros.prefs.GeofenceAppAverosPreference;
import com.example.android.geofenceappaveros.services.GeofenceAppAverosLocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Author: Arsalan Siddiq
 * Activity: MapsActivity
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    //map reference variable
    private GoogleMap mMap;
    //preference reference variable
    private GeofenceAppAverosPreference geofenceAppAverosPreference;

    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Internet Availibility checker
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.app_name))
                    .setMessage(getResources().getString(R.string.internet_error))
                    .setPositiveButton("OK", null).show();
        }

        //preference instance
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
        //map instance
        mMap = googleMap;

        if (geofenceAppAverosPreference.getBoolean(Constants.STATUS)) {
            // Add a marker in user selectedlocation and move the camera
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

        //Permission check for location and else dialogbox to get radius from user
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            mMap.addMarker(new MarkerOptions().position(latLng).title(""));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle(R.string.text_radius);
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setRawInputType(Configuration.KEYBOARD_12KEY);
            input.setPadding(60, 30, 0, 50);
            alert.setView(input);
            alert.setPositiveButton(R.string.text_set, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    if (TextUtils.isEmpty(input.getText().toString())) {
                        mapClear_enableUserSelection();
                        Toast.makeText(MapsActivity.this, "please enter radius", Toast.LENGTH_SHORT).show();
                    } else {
                        /**
                         * on Set event following processes will execute
                         * removing onMapLongClick event to prevent user from selecting other location
                         * save user preference: Status, Latitude, longitude and radius
                         * logic to determined which OS version is runnning
                         */
                        mMap.setOnMapLongClickListener(null);

                        //status preference to determined whether user previoulsy selected location or not
                        geofenceAppAverosPreference.putBoolean(Constants.STATUS, true);
                        //user selected location latitude and longitude to check distance in service
                        geofenceAppAverosPreference.putString(Constants.LATITUDE, String.valueOf(latLng.latitude));
                        geofenceAppAverosPreference.putString(Constants.LONGITUDE, String.valueOf(latLng.longitude));
                        //Radius preference to check whether user is in fence or not
                        geofenceAppAverosPreference.putString(Constants.RADIUS, String.valueOf(input.getText().toString()));

                        //optional feature added circle with given radius
                        setRadius();

                        //logic to determine API levels
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            ContextCompat.startForegroundService(MapsActivity.this,
                                    new Intent(MapsActivity.this, GeofenceAppAverosLocationListener.class));
                        } else {
                            startService(new Intent(MapsActivity.this, GeofenceAppAverosLocationListener.class));
                        }
                    }

                }
            });
            alert.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Cancel event to clear mao and enable user to select location again
                    mapClear_enableUserSelection();
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
        } else {
            Toast.makeText(this, "not granted!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //menu option to clear map and stop service
            case R.id.create_new:
                mapClear_enableUserSelection();
                stopService(new Intent(MapsActivity.this, GeofenceAppAverosLocationListener.class));
                geofenceAppAverosPreference.putBoolean(Constants.STATUS , false);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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

    //Clear map and enable user selection in case of cancalaion of alterdialog and invalid input
    private void mapClear_enableUserSelection() {
        mMap.clear();
        mMap.setOnMapLongClickListener(MapsActivity.this);
    }
}
