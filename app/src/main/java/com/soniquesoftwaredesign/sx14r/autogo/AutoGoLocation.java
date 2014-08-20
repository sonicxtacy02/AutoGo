package com.soniquesoftwaredesign.sx14r.autogo;

//import java.text.SimpleDateFormat;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.LinkedList;
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
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
//import android.widget.Button;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.List;


public class AutoGoLocation extends Activity {
    private GoogleMap googleMap;
    GPSTracker deviceGPS;
    Location deviceLocation = new Location("Device Location");
    // latitude and longitude
    public String onBoardLastUpdated;
    public double onBoardLatitude=0; //= 37.533;  //testing coordinates
    public double onBoardLongitude=0; //= -77.467;
    public double onBoardSpeed=0;
    public double onBoardHeading=0;
    public double onBoardAltitude=0;
    public double onBoardSatcount =0;

    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    private static final String PREFERENCES = "AutoGo Preferences";

    public View popupView;
    public PopupWindow popupWindow;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ag_location);
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();

        try {
            //get last location from table
            getLastLocation();

            // Loading map
            initializeMap();

            //Toast.makeText(getApplicationContext(), MyActivity.prefs.getString("locationUpdateInterval", null),
                    //Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * function to load map. If map is not created it will create it for you
     * */


    private void getLastLocation(){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                editor = prefs.edit();
                try {
                    try {
                        try {
                            String getReceiverUrl = "http://soniquesoftwaredesign.com/AutoGo/loc/request_latest_location.php?";
                            // HttpClient
                            HttpClient httpClient = new DefaultHttpClient();

                            List<NameValuePair> params = new LinkedList<NameValuePair>();
                            params.add(new BasicNameValuePair("usr", prefs.getString("userName", null)));

                            String paramString = URLEncodedUtils.format(params, "utf-8");
                            getReceiverUrl += paramString;

                            HttpGet httpget = new HttpGet(getReceiverUrl);
                            ResponseHandler<String> responseHandler = new BasicResponseHandler();
                            String ServerResponseString = httpClient.execute(httpget, responseHandler);
                            Log.e("pass 1", "connection success ");

                            String[] separated = ServerResponseString.split("@");
                            onBoardLastUpdated = separated[0];
                            onBoardLatitude = Double.parseDouble(separated[1]);
                            onBoardLongitude = Double.parseDouble(separated[2]);
                            onBoardSpeed = Double.parseDouble(separated[3]);
                            onBoardHeading = Double.parseDouble(separated[4]);
                            onBoardAltitude = Double.parseDouble(separated[5]);
                            onBoardSatcount = Double.parseDouble(separated[6]);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    Log.e("Fail 1", e.toString());
                    Toast.makeText(getApplicationContext(), "Invalid IP Address",
                            Toast.LENGTH_LONG).show();
                }
            }





        });
        thread.start();
        try {
            thread.join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initializeMap() {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
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
        MarkerOptions marker = new MarkerOptions().position(new LatLng(onBoardLatitude, onBoardLongitude)).title(onBoardLastUpdated);


// adding marker
        googleMap.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(onBoardLatitude, onBoardLongitude)).zoom(16).build();

        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        GetDistanceDifference();

        //geocode
        GeoCodeAddress();

        ImageView iv = new ImageView(AutoGoLocation.this);
        iv = (ImageView) findViewById(R.id.ag_location_statusImg);

        if(onBoardLatitude == 0 && onBoardLongitude == 0)
        {
            //no clue
            iv.setImageResource(R.drawable.ag_location_na);
        }else {
            if (onBoardAltitude == 0 && onBoardHeading == 0 && onBoardSpeed == 0) {
                //car is parked, set image status
                iv.setImageResource(R.drawable.ag_location_parked);
            } else {
                iv.setImageResource(R.drawable.ag_location_moving);
            }
        }

        TextView tv = new TextView(AutoGoLocation.this);
        if(prefs.getString("speedType", "ms").equals("ms")) {
            tv=(TextView)findViewById(R.id.ag_location_speed);
            tv.setText(String.valueOf(Math.round(onBoardSpeed)) + " m/s");
        }else if (prefs.getString("speedType", "ms").equals("mph"))
        {
            tv=(TextView)findViewById(R.id.ag_location_speed);
            tv.setText(String.valueOf(Math.round(onBoardSpeed * 2.2369362920544) ) + " mph");
        }else if (prefs.getString("speedType", "ms").equals("kmh"))
        {
            tv=(TextView)findViewById(R.id.ag_location_speed);
            tv.setText(String.valueOf(Math.round(onBoardSpeed * 3.6) ) + " kmh");
        }

        tv=(TextView)findViewById(R.id.ag_location_altitude);
        tv.setText(String.valueOf(Math.round(onBoardAltitude)) + " ft");

        String direction;
        if(onBoardHeading>=0 && onBoardHeading>22.5) {
            direction = "N";
        }
        else if(onBoardHeading>=22.5 && onBoardHeading>67.5) {
            direction="NE";
        }
        else if(onBoardHeading>=67.5 && onBoardHeading>112.5)
        {
            direction="E";
        }
        else if(onBoardHeading>=112.5 && onBoardHeading>157.5)
        {
            direction="SE";
        }
        else if(onBoardHeading>=157.5 && onBoardHeading>202.5) {
            direction = "S";
        }
        else if(onBoardHeading>=202.5 && onBoardHeading>247.5) {
            direction="SW";
        }
        else if(onBoardHeading>=247.5 && onBoardHeading>292.5) {
            direction="W";
        }
        else if(onBoardHeading>=292.5 && onBoardHeading>337.5) {
            direction="NW";
        }
        else {
            direction="N";
        }

        tv=(TextView)findViewById(R.id.ag_location_heading);
        tv.setText(direction);


    }

    public void GeoCodeAddress(){
        Geocoder geocoder = new Geocoder(AutoGoLocation.this, Locale.ENGLISH);
        TextView myAddress = new TextView(AutoGoLocation.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(onBoardLatitude, onBoardLongitude, 1);

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
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
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
        vehicleLocation.setLatitude(onBoardLatitude);
        vehicleLocation.setLongitude(onBoardLongitude);


        if (vehicleLocation != null && deviceLocation !=null) {
            float distance = vehicleLocation.distanceTo(deviceLocation);

            //convert meters to miles
            if(prefs.getString("distanceType", "meters").equals("meters")) {
                DecimalFormat dec = new DecimalFormat("###.##");
                TextView distanceAway = new TextView(AutoGoLocation.this);
                distanceAway=(TextView)findViewById(R.id.ag_location_distanceAway);
                distanceAway.setText(dec.format(distance) + " Meters Away");
            }
            else if(prefs.getString("distanceType", "miles").equals("miles")) {

                DecimalFormat dec = new DecimalFormat("###.##");
                distance = ((float) (distance / 1609.344));
                TextView distanceAway = new TextView(AutoGoLocation.this);
                distanceAway = (TextView) findViewById(R.id.ag_location_distanceAway);
                distanceAway.setText(dec.format(distance) + " Miles Away");
            }
        }
    }

    public void RefreshMap(View v)
    {
        try {
            //get last location from table
            getLastLocation();

            // Loading map
            initializeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ShowMapFullScreen(View v)
    {
        Intent intent = new Intent(this, AutoGoLocationFull.class);
        startActivity(intent);
    }

    public void ShowMapOptions(View v)
    {

        LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.ag_map_options, null );
        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);

        //Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
        //btnDismiss.setOnClickListener(new Button.OnClickListener()
        //setContentView(R.layout.ag_pin_panel);
        popupWindow.showAtLocation(v, Gravity.NO_GRAVITY, 0, 390);
    }

    public void DismissMapOptions(View v)
    {
        popupWindow.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //initializeMap();
    }

    public void goToSecurity (View view)
    {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        try {
            MyActivity.editor.putString("lastScreen", "security").apply();

            Intent intent = new Intent(this, AutoGoSecurity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToLocation (View view)
    {
        /*
        try {
            MyActivity.editor.putString("lastScreen", "location");

            Intent intent = new Intent(this, AutoGoLocation.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    public void goToControls (View view)
    {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        try
        {
            MyActivity.editor.putString("lastScreen", "controls").apply();

            Intent intent = new Intent(this, AutoGoControls.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void goToAlerts(View view) {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        try {
            MyActivity.editor.putString("lastScreen", "alerts").apply();

            Intent intent = new Intent(this, AutoGoAlerts.class);
            intent.putExtra("fromNotify", false);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(this, AutoGoSettings.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if(MyActivity.backPressed)
        {
            System.exit(0);
        }
        else
        {
            //warning
            Toast.makeText(getApplicationContext(),
                    "Press Back button again to exit AutoGo", Toast.LENGTH_LONG)
                    .show();
            MyActivity.backPressed = true;
            BackButtonTimer counter = new BackButtonTimer(3000,1000);
            counter.start();
        }
    }

    public static class MainSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
    }






















}


