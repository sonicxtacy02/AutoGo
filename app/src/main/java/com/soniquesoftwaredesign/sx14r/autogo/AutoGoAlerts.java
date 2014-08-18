package com.soniquesoftwaredesign.sx14r.autogo;

//import java.text.SimpleDateFormat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import java.text.DateFormat;
import java.util.Calendar;

//import java.util.Date;
//import android.widget.Button;

public class AutoGoAlerts extends Activity {

    ListView list;
    String[] web = {"System OK","Bump Alert", "Low Battery Voltage", "Security Alert","Bump Alert", "Security Alert","Low Battery Voltage"} ;
    Integer[] imageId = {R.drawable.ag_alerts_ok,R.drawable.ag_alerts_bump,R.drawable.ag_alerts_battery,R.drawable.ag_alerts_theft,R.drawable.ag_alerts_bump,R.drawable.ag_alerts_theft,R.drawable.ag_alerts_battery};
    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    private static final String PREFERENCES = "AutoGo Preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ag_alerts);
        // Show the Up button in the action bar.
        setupActionBar();
        AutoGoAlertsCustomList adapter = new
                AutoGoAlertsCustomList(AutoGoAlerts.this, web, imageId);
        list=(ListView)findViewById(R.id.ag_alerts_alertList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(AutoGoAlerts.this, "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();
            }
        });

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
                //MyActivity.isArmed = false;
                message = "arm state has been changed to false";
                MyActivity.editor.putBoolean("isArmed", false).apply();

                UpdateNotifier();
                return true;
            case R.id.action_arm:
                //MyActivity.isArmed = true;
                message = "arm state has been changed to true";
                MyActivity.editor.putBoolean("isArmed", true).apply();

                UpdateNotifier();
                return true;
            case R.id.action_start:
                //MyActivity.isStarted = true;
                message = "engine running state has been changed to true";
                MyActivity.editor.putBoolean("isStarted", true).apply();

                UpdateNotifier();
                return true;
            case R.id.action_stop:
               // MyActivity.isStarted = false;
                message = "engine running state has been changed to false";
                MyActivity.editor.putBoolean("isStarted", false).apply();

                UpdateNotifier();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        super.onPrepareOptionsMenu(menu);
/*
        if(MyActivity.prefs.getBoolean("isArmed", false))
        {
            menu.findItem(R.id.action_arm).setVisible(false);
        }
        else
        {
            menu.findItem(R.id.action_unarm).setVisible(false);
        }
        if(MyActivity.prefs.getBoolean("isStarted", false))
        {
            menu.findItem(R.id.action_start).setVisible(false);
        }
        else
        {
            menu.findItem(R.id.action_stop).setVisible(false);
        }

*/

        return true;
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
        if(MyActivity.prefs.getBoolean("isStarted", false)==true)
        {
            contentView.setImageViewResource(R.id.ag_notification_startStateImg, R.drawable.ic_start);
        }else
        {

            contentView.setImageViewResource(R.id.ag_notification_startStateImg, R.drawable.ic_stop);
        }
        if(MyActivity.prefs.getBoolean("isArmed", false)==true)
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


        Intent armStateIntent = new Intent(this,AutoGoArmStateNotification.class);
        PendingIntent pendingArmStateIntent = PendingIntent.getBroadcast(this,0,armStateIntent,0);

        //contentView.setOnClickPendingIntent(R.id.ag_notification_armStateBtn, pendingArmStateIntent);

        Intent startStateIntent = new Intent(this,AutoGoStartStateNotification.class);
        PendingIntent pendingStartStateIntent = PendingIntent.getBroadcast(this,0,startStateIntent,0);

        //contentView.setOnClickPendingIntent(R.id.ag_notification_startStateBtn, pendingStartStateIntent);


        mNotificationManager.notify(1, notification);

    }

    public void DisplayResponse(String DisplayMessage)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(DisplayMessage);
        builder1.setCancelable(true);
        builder1.setNeutralButton("Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
        this.invalidateOptionsMenu();
    }

    public void goToSecurity (View view)
    {
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
        try {
            MyActivity.editor.putString("lastScreen", "location").apply();

            Intent intent = new Intent(this, AutoGoLocation.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void goToControls (View view)
    {
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
    }

    public void goToSettings(View view) {
        Intent intent = new Intent(this, AutoGoSettings.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {
        if(MyActivity.backPressed)
        {
            //System.exit(0);
            //finish();
            int pid = android.os.Process.myPid();
            android.os.Process.killProcess(pid);
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


