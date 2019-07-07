package com.example.android.geofenceappaveros.ui.maps;

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
import android.view.KeyEvent;
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
 * Main Activity to perform user action
 * Will ask user to get location permission
 * Set radius to create fence for user
 * Clear map that will allow user to draw new fence by giving radius
 * GoogleMap Callbacks required perform activites on map
 * View interface will implement all methods with desired action (part of MVP)
 */
public class MapsActivity extends AppCompatActivity implements View, OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    //map reference variable
    private GoogleMap mMap;
    //presenter interface reference variable assigned MainPresenter instance
    private Presenter presenter;
    //variable to store latitude and longitude
    private double latitude, longitude;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //GeofenceAppAverosPreference instance creation to pass to custome preference class for data manipulcation
        //Context is required to create the preference instance in app
        GeofenceAppAverosPreference geofenceAppAverosPreference =
                new GeofenceAppAverosPreference(this);

        //Presenter reference variable with MainPresenter instance
        //MainPresenter Constructor required Custom Preference class instance
        presenter = new MainPresenter(geofenceAppAverosPreference);

        //Internet Availibility checker
        //CheckInternet will return boolean
        if (!checkInternet()) {
            networkDialog();
        }
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

        //Checking user status if currently radius is set on map
        //if true than map will draw previous fence
        if (presenter.getBoolean(Constants.STATUS)) {
            // Add a marker in user selectedlocation and move the camera
            mMap.setOnMapLongClickListener(null);

            //Assigning latitude and longitude to latitude and longitude variables
            //latlng will be aquired when google map is ready
            latitude = Double.parseDouble(presenter.getString(Constants.LATITUDE));
            longitude = Double.parseDouble(presenter.getString(Constants.LONGITUDE));

            //Add marker to map require latlng
            LatLng position = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(position));
            //Camera position in map to land particular view which will be determined by position
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));

            //setRadius will store user defined fence in preference
            setRadius();

        } else {
            //toast method to show message to user
                toast(R.string.string_radius_guide);
                //enabling map long click which will draw radius on map if it has cleared previous radius
                mMap.setOnMapLongClickListener(this);
        }
    }

    /**
     * onMApLongClick will compute location from map
     * @param latLng will hold coordinates
     */
    @Override
    public void onMapLongClick(final LatLng latLng) {

        //Permission check for location and else dialogbox to get radius from user
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Override permsision method will be called on permission check
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            /**
             * drawRadius method will draw fence on map when user enter radius in numbers
             * radius will be calculated as meters
             * LatLng will be required to save in preference
             */
            drawRadius(latLng);
        }

    }

    /**
     * This method will draw fence on map in circle
     * @param latLng will be considered as center point
     */
    @Override
    public void drawRadius(final LatLng latLng) {

        //Drop Marker on user selected position
        mMap.addMarker(new MarkerOptions().position(latLng).title(""));
        //Move camera to the user selected option to give user select position view
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        /**
         * ALert dialog will popup when user press long on map
         * alertdialog with edittext ask user to insert radius
         * radius numbers will be calcualted as meters in map to draw fence
         * setInputType is a validated type to accept ony numbers from user
         * setPadding, setView and setCancelable are view methods to for edittext view
         * setPositiveButton will pass user entered numbers to preference
         * setNegativeButton user will remove dialog from map activity
         */
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.text_radius);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setRawInputType(Configuration.KEYBOARD_12KEY);
        input.setPadding(60, 30, 0, 50);
        alert.setView(input);
        alert.setCancelable(false);
        alert.setPositiveButton(R.string.text_set, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                //Validation (null check)
                if (TextUtils.isEmpty(input.getText().toString())) {
                    //clear map if user not inserted any value
                    mapClear_enableUserSelection();
                    toast(R.string.string_radius_set);
                } else {
                    /**
                     * on Set event following processes will execute
                     * removing onMapLongClick event to prevent user from selecting other location
                     * save user preference: Status, Latitude, longitude and radius
                     * logic to determined which OS version is runnning
                     */
                    mMap.setOnMapLongClickListener(null);

                    //status preference to determined whether user previoulsy selected location or not
                    presenter.putBoolean(Constants.STATUS, true);
                    //user selected location latitude and longitude to check distance in service
                    presenter.putString(Constants.LATITUDE, String.valueOf(latLng.latitude));
                    presenter.putString(Constants.LONGITUDE, String.valueOf(latLng.longitude));
                    //Radius preference to check whether user is in fence or not
                    presenter.putString(Constants.RADIUS, String.valueOf(input.getText().toString()));


                    //save user current radius in preference
                    setRadius();
                    //startServices will start background service to which will calculate user current location to deteremine if user user is inside fence or not
                    startServices();
                }

            }
        });
        alert.setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //Cancel event to clear mao and enable user to select location again
                mapClear_enableUserSelection();
            }
        });
        //show method will show alerdialog on map activity
        alert.show();
    }

    /**
     * On permission given this method will be called to get user current lccation from device
     * @param requestCode was set on 1
     * @param permissions Fine Permission to determine locaiton from available providers
     * @param grantResults checking if permission is given
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //toast will show on screen id permission is granted or not
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            toast(R.string.string_permission_granted);
        } else {
            toast(R.string.string_permission_notGranted);
        }
    }

    /**
     * option menu will be added on toolbar
     * @param menu is clear map, preference and radius option
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    /**
     * menu item event
     * @param item is Clear map in menu which clear map
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            //menu option to clear map and stop service
            case R.id.create_new:
                //clear map and enable user to draw radius again
                mapClear_enableUserSelection();
                //this will stop background service to get, compute and generate notifications
                stopServices();
                return true; //return true if case is matched
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Clear map and enable user selection in case of cancalaion of alterdialog and invalid input
    @Override
    public void mapClear_enableUserSelection() {
        //clear Marker and radius from map
        mMap.clear();
        //clear stored preferences
        presenter.clear();
        //enable long click on map to draw new radius
        mMap.setOnMapLongClickListener(MapsActivity.this);
    }

    /**
     * this method will store user radius in preference from the reference if MainPresenter
     * draw red colored circle on map for the user view
     */
    @Override
    public void setRadius() {

        //Latlng will be retrieved from preference with the reference of presenter and
        LatLng latLng = new LatLng(Double.parseDouble(presenter.getString(Constants.LATITUDE)),
                Double.parseDouble(presenter.getString(Constants.LONGITUDE)));

        //addCircle is a Google Map method which will draw radius from given center point by user
        mMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(Double.parseDouble(presenter.getString(Constants.RADIUS)))
                .strokeColor(Color.RED));
    }

    /**
     * this method will check internet availability of device to load google map
     * @return if internet available than will value will be true else false
     */
    @Override
    public boolean checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null) {
            return false; //return true if internet available
        } else {
            return true; //return false if internet not available
        }
    }

    /**
     * network dialog will be shown if internet is not available or connected
     */
    @Override
    public void networkDialog() {
        new AlertDialog.Builder(MapsActivity.this)
                .setTitle(getResources().getString(R.string.app_name))
                .setMessage(getResources().getString(R.string.internet_error))
                .setCancelable(false)
                .setPositiveButton("OK", null).show();
    }

    /**
     * This method have toast class instance to utilize it recurrsively without initiating new instance
     * @param resource are ids of stored string values in app/res/values/string.xml
     */
    @Override
    public void toast(int resource) {
        String content = getResources().getString(R.string.string_radius_guide);
        //make test method will create toast
        //show method will show toast on activity
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }

    /**
     * this method will start background service to computer and generate notification
     */
    @Override
    public void startServices() {

        /**
         * device API OS version will be determined
         * <a href="https://developer.android.com/training/run-background-service/create-service">Background Service</a>.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(MapsActivity.this,
                    new Intent(MapsActivity.this, GeofenceAppAverosLocationListener.class));
        } else {
            startService(new Intent(MapsActivity.this, GeofenceAppAverosLocationListener.class));
        }
    }

    /**
     * this method will stop background service and location listener
     */
    @Override
    public void stopServices() {
        stopService(new Intent(MapsActivity.this, GeofenceAppAverosLocationListener.class));
        //on service stop user current status will be false which will indicate that user has cleared the map marker radius and will not receive any notification
        presenter.putBoolean(Constants.STATUS, false);
    }

    /**
     * this method will prevent user to fall back to the splash screen on clicking back key
     * @param keyCode is required to determine which is pressed
     * @param event to perform action which is not allowing fallback to previous activity which is stored in stack
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:

                moveTaskToBack(true);

                return true;
        }
        return false;
    }

}
