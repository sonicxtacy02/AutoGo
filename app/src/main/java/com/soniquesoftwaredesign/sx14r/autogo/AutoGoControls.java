package com.soniquesoftwaredesign.sx14r.autogo;


//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;

//import android.media.MediaPlayer;
        import android.graphics.Color;
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
        import android.widget.ImageView;
        import android.widget.RemoteViews;
        import android.widget.Toast;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.ImageView;
        import android.widget.TextView;

        import android.annotation.TargetApi;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.os.Build;

        import java.text.DateFormat;
        import java.util.Calendar;

public class AutoGoControls extends Activity {

    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    private static final String PREFERENCES = "AutoGo Preferences";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ag_controls);

        // Show the Up button in the action bar.
        setupActionBar();

       SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();


        //set default button states
        MyActivity.setTemp = prefs.getInt("setTemp", 72);
        if(prefs.getBoolean("isStarted",false)) {
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_stopVehicleBtn);
            btn.setEnabled(true);
            btn = (SAutoBgButton) findViewById(R.id.ag_controls_startVehicleBtn);
            btn.setEnabled(false);
        }
        else {
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_stopVehicleBtn);
            btn.setEnabled(false);
            btn = (SAutoBgButton) findViewById(R.id.ag_controls_startVehicleBtn);
            btn.setEnabled(true);
        }
        if(prefs.getBoolean("areLightsOn",false)) {
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_lightingOffBtn);
            btn.setEnabled(true);
            btn = (SAutoBgButton) findViewById(R.id.ag_controls_lightingOnBtn);
            btn.setEnabled(false);
        }
        else {
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_lightingOffBtn);
            btn.setEnabled(false);
            btn = (SAutoBgButton) findViewById(R.id.ag_controls_lightingOnBtn);
            btn.setEnabled(true);
        }
        if (prefs.getInt("setTemp",72)==80)
        {
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_climateUpBtn);
            btn.setEnabled(false);
            btn = (SAutoBgButton) findViewById(R.id.ag_controls_climateDownBtn);
            btn.setEnabled(true);
        }else if(prefs.getInt("setTemp",72)==60)
        {
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_climateUpBtn);
            btn.setEnabled(true);
            btn = (SAutoBgButton) findViewById(R.id.ag_controls_climateDownBtn);
            btn.setEnabled(false);
        }
        if (prefs.getBoolean("isHVACOn", false)) {
            ImageView myHvacOff = (ImageView) findViewById(R.id.ag_controls_hvacOffImg);
            myHvacOff.setVisibility(View.INVISIBLE);
            TextView myTempLbl = (TextView) findViewById(R.id.ag_controls_hvacTempSetting);
            myTempLbl.setTextColor(Color.BLACK);
        }else
        {
            ImageView myHvacOff = (ImageView) findViewById(R.id.ag_controls_hvacOffImg);
            myHvacOff.setVisibility(View.VISIBLE);
            TextView myTempLbl = (TextView) findViewById(R.id.ag_controls_hvacTempSetting);
            myTempLbl.setTextColor(Color.DKGRAY);
        }

        //set temp label from value
        //recall last command
        TextView tv = (TextView) findViewById(R.id.ag_controls_hvacTempSetting);
        tv.setText(prefs.getInt("setTemp",72) + "°");

        //set the temp image value
        SetTempImage(prefs.getInt("setTemp", 72));
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        //create new intent that defines a new activity
        //Intent intent = new Intent(this, DisplayMessageActivity.class);
        String message = "";
        switch (item.getItemId()) {
            case R.id.action_unarm:
                //editor.putBoolean = false;
                //message = "arm state has been changed to " + MyActivity.isArmed.toString();
                //message = getResources().getString(R.string.action_unlock);

                editor.putBoolean("isArmed", false).apply();
                DisplayResponse(message);
                //add the message to intent to pass data to the intent
                //intent.putExtra(EXTRA_MESSAGE,message);
                //go!
                //startActivity(intent);
                UpdateNotifier();
                return true;
            case R.id.action_arm:
                //message = getResources().getString(R.string.action_lock);
                //MyActivity.isArmed = true;
                //message = "arm state has been changed to " + MyActivity.isArmed.toString();

                editor.putBoolean("isArmed", true).apply();
                DisplayResponse(message);
                //add the message to intent to pass data to the intent
                //intent.putExtra(EXTRA_MESSAGE,message);
                //go!
                //startActivity(intent);
                UpdateNotifier();
                return true;
            case R.id.action_start:
                //message = getResources().getString(R.string.action_start);
                //MyActivity.isStarted = true;
                //message = "engine running state has been changed to " + MyActivity.isStarted.toString();

                editor.putBoolean("isStarted", true).apply();
                DisplayResponse(message);
                //intent.putExtra(EXTRA_MESSAGE, message);
                //startActivity(intent);
                UpdateNotifier();
                return true;
            case R.id.action_stop:
                //message = getResources().getString(R.string.action_start);
                //MyActivity.isStarted = false;
                //message = "engine running state has been changed to " + MyActivity.isStarted.toString();

                editor.putBoolean("isStarted", false).apply();
                DisplayResponse(message);
                UpdateNotifier();
                //intent.putExtra(EXTRA_MESSAGE, message);
                //startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();

        if(prefs.getBoolean("isArmed",false))
        {
            menu.findItem(R.id.action_arm).setVisible(false);
        }
        else
        {
            menu.findItem(R.id.action_unarm).setVisible(false);
        }
        if(prefs.getBoolean("isStarted", false))
        {
            menu.findItem(R.id.action_start).setVisible(false);
        }
        else
        {
            menu.findItem(R.id.action_stop).setVisible(false);
        }
        return true;
    }



    public void startVehicle(View view) {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        if (prefs.getBoolean("isStarted", false)==false){
            //MyActivity.isStarted=true;


            final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.start);
            mp1.start();

            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_startVehicleBtn);
            btn.setEnabled(false);
            btn = (SAutoBgButton) findViewById(R.id.ag_controls_stopVehicleBtn);
            btn.setEnabled(true);
            editor.putBoolean("isStarted", true).apply();

            //MyActivity.lastCommand = "STARTVEHICLE";
            //MyActivity.lastCommandStamp = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            //TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
            //tv.setText(MyActivity.lastCommand + " " + "@ " + MyActivity.lastCommandStamp);
            editor.putString("lastCommand", "STARTVEHICLE").apply();
            editor.putString("lastCommandStamp", DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();

            UpdateNotifier();
        }
    }

    public void stopVehicle(View view){
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        if(prefs.getBoolean("isStarted",false)==true) {
            //MyActivity.isStarted=false;

            final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.stop);
            mp1.start();
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_stopVehicleBtn);
            btn.setEnabled(false);
            btn = (SAutoBgButton) findViewById(R.id.ag_controls_startVehicleBtn);
            btn.setEnabled(true);
            editor.putBoolean("isStarted", false).apply();

            //MyActivity.lastCommand = "STOPVEHICLE";
            //MyActivity.lastCommandStamp = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            //TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
            //tv.setText(MyActivity.lastCommand + " " + "@ " + MyActivity.lastCommandStamp);
            editor.putString("lastCommand", "STOPVEHICLE").apply();
            editor.putString("lastCommandStamp", DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();

            UpdateNotifier();
        }
    }

    public void hvacOn(View view) {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        if(prefs.getBoolean("isStarted", false)) {
            //MyActivity.isHVACOn = true;
            ImageView img = (ImageView) findViewById(R.id.ag_controls_hvacStateImg);
            img.setImageResource(R.drawable.ag_controls_power_on);
            //final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.lock);
            //mp1.start();
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_hvacOnBtn);
            btn.setEnabled(false);
            btn = (SAutoBgButton) findViewById(R.id.ag_controls_hvacOffBtn);
            btn.setEnabled(true);
            editor.putBoolean("isHVACOn", true).apply();

            ImageView myHvacOff = (ImageView) findViewById(R.id.ag_controls_hvacOffImg);
            myHvacOff.setVisibility(View.INVISIBLE);
            TextView myTempLbl = (TextView) findViewById(R.id.ag_controls_hvacTempSetting);
            myTempLbl.setTextColor(Color.BLACK);

            //MyActivity.lastCommand = "HVACON";
            //MyActivity.lastCommandStamp = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            //TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
            //tv.setText(MyActivity.lastCommand + " " + "@ " + MyActivity.lastCommandStamp);
            editor.putString("lastCommand", "HVACON").apply();
            editor.putString("lastCommandStamp", DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();
        }
        else
        {
            //engine must be started to control HVAC
            VehicleStartedWarningDialog();
        }
    }

    public void hvacOff(View view){
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        if(prefs.getBoolean("isStarted",false)) {
            //MyActivity.isHVACOn=false;
            ImageView img = (ImageView) findViewById(R.id.ag_controls_hvacStateImg);
            img.setImageResource(R.drawable.ag_controls_power_off);
            //final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.unlock);
            //mp1.start();
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_hvacOnBtn);
            btn.setEnabled(true);
            btn = (SAutoBgButton) findViewById(R.id.ag_controls_hvacOffBtn);
            btn.setEnabled(false);
            editor.putBoolean("isHVACOn", false).apply();

            ImageView myHvacOff = (ImageView) findViewById(R.id.ag_controls_hvacOffImg);
            myHvacOff.setVisibility(View.VISIBLE);
            TextView myTempLbl = (TextView) findViewById(R.id.ag_controls_hvacTempSetting);
            myTempLbl.setTextColor(Color.DKGRAY);

            //MyActivity.lastCommand = "HVACOFF";
            //MyActivity.lastCommandStamp = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            //TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
            //tv.setText(MyActivity.lastCommand + " " + "@ " + MyActivity.lastCommandStamp);
            editor.putString("lastCommand", "HVACOFF").apply();
            editor.putString("lastCommandStamp", DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();
        }
        else
        {
            //engine must be started to control HVAC
            VehicleStartedWarningDialog();
        }
    }

    public void VehicleStartedWarningDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.ag_controls_hvacControlLbl))
                .setMessage(getString(R.string.ag_controls_vehicleStartedWarningDialog))
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startVehicle(getCurrentFocus());
                        hvacOn(getCurrentFocus());
                    }
                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    public void HVACOnWarningDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.ag_controls_hvacControlLbl))
                .setMessage(getString(R.string.ag_controls_hvacOnWarningDialog))
                .setPositiveButton(getString(R.string.dialog_yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        startVehicle(getCurrentFocus());
                        hvacOn(getCurrentFocus());
                    }
                }).setNegativeButton(getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Do nothing.
            }
        }).show();
    }

    public void lightsOff(View view) {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        //MyActivity.areLightsOn=false;
        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_lightingOnBtn);
        btn.setEnabled(true);
        btn = (SAutoBgButton) findViewById(R.id.ag_controls_lightingOffBtn);
        btn.setEnabled(false);
        editor.putBoolean("areLightsOn", false).apply();

        //MyActivity.lastCommand = "LIGHTSOFF";
        //MyActivity.lastCommandStamp = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        editor.putString("lastCommand", "LIGHTSOFF").apply();
        editor.putString("lastCommandStamp", DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();
    }

    public void lightsOn(View view) {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        //MyActivity.areLightsOn=true;
        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_lightingOnBtn);
        btn.setEnabled(false);
        btn = (SAutoBgButton) findViewById(R.id.ag_controls_lightingOffBtn);
        btn.setEnabled(true);
        editor.putBoolean("areLightsOn", true).apply();

        //MyActivity.lastCommand = "LIGHTSON";
        //MyActivity.lastCommandStamp = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        editor.putString("lastCommand", "LIGHTSON").apply();
        editor.putString("lastCommandStamp", DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();
    }

    public void tempUp(View view){
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        if (prefs.getBoolean("isStarted", false)) {
            //cars gotta be on
            if (prefs.getBoolean("isHVACOn", false)) {
                //hvac gotta be on
                if (MyActivity.setTemp < 80) {
                    MyActivity.setTemp++;
                    editor.putInt("setTemp", MyActivity.setTemp).apply();
                    if (MyActivity.setTemp == 80) {
                        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_climateUpBtn);
                        btn.setEnabled(false);
                    }
                    if (MyActivity.setTemp > 60) {
                        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_climateDownBtn);
                        btn.setEnabled(true);
                    }
                    //recall last command
                    TextView tv = (TextView) findViewById(R.id.ag_controls_hvacTempSetting);
                    tv.setText(MyActivity.setTemp + "°");

                    SetTempImage(MyActivity.setTemp);

                    //MyActivity.lastCommand = "TEMPUP";
                    //MyActivity.lastCommandStamp = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
                    //tv = (TextView) findViewById(R.id.lastCommandAndStamp);
                    //tv.setText(MyActivity.lastCommand + " " + "@ " + MyActivity.lastCommandStamp);
                    editor.putString("lastCommand", "TEMPUP").apply();
                    editor.putString("lastCommandStamp", DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();

                }
            } else
            {
                //HVAC not on
                HVACOnWarningDialog();
            }

        } else
        {
            //cars not on
            VehicleStartedWarningDialog();
        }
    }

    public void tempDown(View view){
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        if (prefs.getBoolean("isStarted", false)) {
            //cars gotta be on
            if (prefs.getBoolean("isHVACOn", false)) {
                //hvac gotta be on
                if (MyActivity.setTemp > 60) {
                    MyActivity.setTemp--;
                    editor.putInt("setTemp", MyActivity.setTemp).apply();
                    if (MyActivity.setTemp==60)
                    {
                        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_climateDownBtn);
                        btn.setEnabled(false);
                    }
                    if(MyActivity.setTemp<80)
                    {
                        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_climateUpBtn);
                        btn.setEnabled(true);
                    }
                    //recall last command
                    TextView tv = (TextView) findViewById(R.id.ag_controls_hvacTempSetting);
                    tv.setText(MyActivity.setTemp + "°");

                    SetTempImage(MyActivity.setTemp);

                    //MyActivity.lastCommand = ;
                    //MyActivity.lastCommandStamp = ;

                    editor.putString("lastCommand", "TEMPDOWN").apply();
                    editor.putString("lastCommandStamp", DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();
                }
            } else
            {
                //HVAC not on
                HVACOnWarningDialog();
            }

        } else
        {
            //cars not on
            VehicleStartedWarningDialog();
        }
    }

    public void SetTempImage(int temp){
        ImageView iv = (ImageView) findViewById(R.id.ag_controls_hvacDialImg);
        //fix image
        if(temp==60){
            iv.setImageResource(R.drawable.ag_controls_1);
        }else if(temp==61) {
            iv.setImageResource(R.drawable.ag_controls_2);
        }else if(temp==62) {
            iv.setImageResource(R.drawable.ag_controls_3);
        }else if(temp > 62 && temp < 64) {
            iv.setImageResource(R.drawable.ag_controls_4);
        }else if(temp >= 64 && temp < 66) {
            iv.setImageResource(R.drawable.ag_controls_5);
        }else if(temp >= 66 && temp < 68) {
            iv.setImageResource(R.drawable.ag_controls_6);
        }else if(temp >= 68 && temp < 70) {
            iv.setImageResource(R.drawable.ag_controls_7);
        }else if(temp >= 70 && temp < 72) {
            iv.setImageResource(R.drawable.ag_controls_8);
        }else if(temp >= 72 && temp < 74) {
            iv.setImageResource(R.drawable.ag_controls_9);
        }else if(temp >= 74 && temp < 76) {
            iv.setImageResource(R.drawable.ag_controls_10);
        }else if(temp == 76) {
            iv.setImageResource(R.drawable.ag_controls_11);
        }else if(temp == 77) {
            iv.setImageResource(R.drawable.ag_controls_12);
        }else if(temp == 78) {
            iv.setImageResource(R.drawable.ag_controls_13);
        }else if(temp == 79) {
            iv.setImageResource(R.drawable.ag_controls_14);
        }else if(temp >= 80) {
            iv.setImageResource(R.drawable.ag_controls_15);
        }
    }


    public void DisplayResponse(String DisplayMessage) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(DisplayMessage);
        builder1.setCancelable(true);
        builder1.setNeutralButton("Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                }
        );

        AlertDialog alert11 = builder1.create();
        alert11.show();
        this.invalidateOptionsMenu();
    }


    public void UpdateNotifier(){
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, getResources().getString(R.string.app_name), when);

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.ag_custom_notification);
        contentView.setImageViewResource(R.id.ag_notification_icon, R.drawable.ic_launcher);
        contentView.setTextViewText(R.id.ag_notification_title, getResources().getString(R.string.app_name));
        if(prefs.getBoolean("isStarted", false)==true)
        {
            contentView.setImageViewResource(R.id.ag_notification_startStateImg, R.drawable.ic_start);
        }else
        {

            contentView.setImageViewResource(R.id.ag_notification_startStateImg, R.drawable.ic_stop);
        }
        if(prefs.getBoolean("isArmed", false)==true)
        {
            contentView.setTextViewText(R.id.text, getResources().getString(R.string.protection_status_message) + " " +  getResources().getString(R.string.protection_state_active) );
            contentView.setImageViewResource(R.id.ag_notification_armStateImg, R.drawable.ic_armed);
        }else
        {
            contentView.setTextViewText(R.id.text, getResources().getString(R.string.protection_status_message) + " " + getResources().getString(R.string.protection_state_inactive));
            contentView.setImageViewResource(R.id.ag_notification_armStateImg, R.drawable.ic_unarmed);
        }
        notification.contentView = contentView;

        Intent notificationIntent = new Intent(this, MyActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notification.contentIntent = contentIntent;

        notification.flags |= Notification.FLAG_NO_CLEAR; //Do not clear the notification
        //notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        //notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
        //notification.defaults |= Notification.DEFAULT_SOUND; // Sound


        Intent lockStateIntent = new Intent(this,AutoGoArmStateNotification.class);
        PendingIntent pendingLockStateIntent = PendingIntent.getBroadcast(this,0,lockStateIntent,0);

        //contentView.setOnClickPendingIntent(R.id.ag_notification_armStateBtn, pendingLockStateIntent);

        Intent startStateIntent = new Intent(this,AutoGoStartStateNotification.class);
        PendingIntent pendingStartStateIntent = PendingIntent.getBroadcast(this,0,startStateIntent,0);

        //contentView.setOnClickPendingIntent(R.id.ag_notification_startStateBtn, pendingStartStateIntent);


        mNotificationManager.notify(1, notification);

    }

    public void goToSecurity (View view)
    {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        try {
            editor.putString("lastScreen", "security").apply();

            Intent intent = new Intent(this, AutoGoSecurity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToLocation (View view)
    {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        try {

            editor.putString("lastScreen", "location").apply();

            Intent intent = new Intent(this, AutoGoLocation.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToControls (View view)
    {
        /*
    }
        Intent intent = new Intent(this, AutoGoControls.class);

        //EditText editText = (EditText) findViewById(R.id.edit_message);

        //String message = editText.getText().toString();

        //intent.putExtra(EXTRA_MESSAGE, message);
        SaveStringSetting("lastScreen", "controls");
        startActivity(intent);
        */
    }


    public void goToAlerts(View view) {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        try {
            editor.putString("lastScreen", "alerts").apply();

            Intent intent = new Intent(this, AutoGoAlerts.class);
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


}


