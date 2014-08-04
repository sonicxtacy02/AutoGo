package com.soniquesoftwaredesign.sx14r.autogo;

//import java.text.SimpleDateFormat;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Locale;
//import java.util.Date;
import android.location.Criteria;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
//import android.widget.Button;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.location.Address;
import android.location.Geocoder;
import java.util.List;


public class AutoGoLocation extends Activity {
    private GoogleMap googleMap;
    GPSTracker deviceGPS;
    Location deviceLocation = new Location("Device Location");
    // latitude and longitude
    double latitude = 37.533;
    double longitude = -77.467;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.ag_location);


        try {
            // Loading map
            initializeMap();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * function to load map. If map is not created it will create it for you
     * */
    private void initializeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        //hide zoom control
        googleMap.getUiSettings().setZoomControlsEnabled(false);



// create marker
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Hello Maps ");

// adding marker
        googleMap.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(latitude, longitude)).zoom(16).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        GetDistanceDifference();

        //geocode
        GeoCodeAddress();


    }

    public void GeoCodeAddress(){
        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        TextView myAddress = new TextView(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if(addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("Nearby Address: ");
                for(int i=0; i<returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }



                myAddress=(TextView)findViewById(R.id.ag_location_nearAddress);
                myAddress.setText(strReturnedAddress.toString());

            }
            else{
                myAddress.setText("No Address returned!");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            myAddress.setText("Cannot get Address!");
        }
    }

    public void GetDistanceDifference()
    {
        //get distance difference based on sent location
        // create class object
        deviceGPS = new GPSTracker(AutoGoLocation.this);

        // check if GPS enabled
        if(deviceGPS.canGetLocation()){

            double deviceLatitude = deviceGPS.getLatitude();
            double deviceLongitude = deviceGPS.getLongitude();
            deviceLocation.setLatitude(deviceLatitude);
            deviceLocation.setLongitude(deviceLongitude);

            // \n is for new line
            //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        }else{
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            deviceGPS.showSettingsAlert();
        }

        Location vehicleLocation = new Location("Vehicle Location");
        vehicleLocation.setLatitude(latitude);
        vehicleLocation.setLongitude(longitude);


        if (vehicleLocation != null && deviceLocation !=null) {
            float distance = vehicleLocation.distanceTo(deviceLocation);

            //convert meters to miles
            DecimalFormat dec = new DecimalFormat("###.##");
            distance = ((float) (distance /1609.344));
            TextView distanceAway = new TextView(this);
            distanceAway=(TextView)findViewById(R.id.ag_location_distanceAway);
            distanceAway.setText(dec.format(distance) + " Miles Away");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeMap();
    }

    public void goToSecurity (View view)
    {
        try {
            Intent intent = new Intent(this, AutoGoSecurity.class);

            //EditText editText = (EditText) findViewById(R.id.edit_message);

            //String message = editText.getText().toString();

            //intent.putExtra(EXTRA_MESSAGE, message);
            SaveStringSetting("lastScreen", "security");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToLocation (View view)
    {
        /**
        try {
            Intent intent = new Intent(this, AutoGoLocation.class);

            //EditText editText = (EditText) findViewById(R.id.edit_message);

            //String message = editText.getText().toString();

            //intent.putExtra(EXTRA_MESSAGE, message);
            SaveStringSetting("lastScreen", "location");
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
         **/
    }

    public void goToControls (View view)
    {
        Intent intent = new Intent(this, AutoGoControls.class);

        //EditText editText = (EditText) findViewById(R.id.edit_message);

        //String message = editText.getText().toString();

        //intent.putExtra(EXTRA_MESSAGE, message);
        SaveStringSetting("lastScreen", "controls");
        startActivity(intent);
    }


    public void goToAlerts(View view) {
    }

    public void goToSettings(View view) {
    }

    public void SaveStringSetting(String keyValue, String savedValue){
        //write the last action to setting file
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(keyValue, savedValue);
        editor.commit();
    }























}


