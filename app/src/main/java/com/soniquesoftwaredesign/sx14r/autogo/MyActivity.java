package com.soniquesoftwaredesign.sx14r.autogo;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import java.text.DateFormat;
import java.util.Calendar;

//public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

public class MyActivity extends Activity {


    public int mId;
    private static final String TAG = "HomeActivity";
    /*
    public static Boolean initialConfig;
    public static Boolean isArmed;
    public static Boolean isStarted;
    public static Boolean isHVACOn;
    public static Boolean areLightsOn;
    public static Boolean isWindowUp;
    public static Boolean isVehicleDisabled;

    public static String lastCommand;
    public static String lastCommandStamp;
    public static String lastScreen;
    public static String userPin;
    public static String userName;
    public static String locationUpdateInterval;
    */

    public static String pinPanel_1 = "";
    public static String pinPanel_2 = "";
    public static String pinPanel_3 = "";
    public static String pinPanel_4 = "";
    public static Boolean SecondAttempt = false;
    public static String firstAttemptPin;



    public static int setTemp;

    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    public static final String PREFERENCES = "AutoGo Preferences";

    public View popupView;
    public PopupWindow popupWindow;

    public static Boolean backPressed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();

        //editor.putString("userName", "sonicxtacy02").apply();



        //get saved settings
        try {
            //initialConfig = prefs.getBoolean("initialConfig", true);
            Log.i(TAG, "initial config state is " + prefs.getBoolean("initialConfig", true));
        }catch (Exception e) {
            Log.e(TAG,e.getLocalizedMessage());
        }

       // initialConfig=true; //testing!

        if (prefs.getBoolean("initialConfig", true)){
            //need to setup initial bits here
            setContentView(R.layout.activity_my);
            try
            {
                DisplayPinPanel(findViewById(R.id.goToSecurityCmd));
                Log.i(TAG,"Display PIN panel for initial entry");
            }catch (Exception e) {
                Log.e(TAG,e.getLocalizedMessage());
            }
        }
        else {
            /*
            isStarted = prefs.getBoolean("isStarted", false);
            isArmed = prefs.getBoolean("isArmed", false);
            isHVACOn = prefs.getBoolean("isHVACOn", false);
            areLightsOn = prefs.getBoolean("areLightsOn", false);
            isWindowUp = prefs.getBoolean("isWindowUp", false);
            isVehicleDisabled = prefs.getBoolean("isVehicleDisabled", false);
            lastScreen = prefs.getString("lastScreen", "security");
            userPin = prefs.getString("userPin", null);
            userName = prefs.getString("userName", null);
            locationUpdateInterval = prefs.getString("locationUpdateInterval", null);

            lastCommand = prefs.getString("lastCommand", "null");
            lastCommandStamp = prefs.getString("lastCommandStamp", "null");

            setTemp = prefs.getInt("setTemp", 72);

            Log.i(TAG, "Settings loaded from file");
            */
            try{
                int icon = R.drawable.ic_launcher;
                long when = System.currentTimeMillis();
                Notification notification = new Notification(icon, getResources().getString(R.string.app_name), when);

                NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.ag_custom_notification);
                contentView.setImageViewResource(R.id.ag_notification_icon, R.drawable.ic_launcher);
                contentView.setTextViewText(R.id.ag_notification_title, getResources().getString(R.string.app_name));
                if (prefs.getBoolean("isStarted", true) == true) {
                    contentView.setImageViewResource(R.id.ag_notification_startStateImg, R.drawable.ic_start);
                } else {
                    contentView.setImageViewResource(R.id.ag_notification_startStateImg, R.drawable.ic_stop);
                }
                if (prefs.getBoolean("isArmed", true) == true) {
                    contentView.setTextViewText(R.id.text, getResources().getString(R.string.protection_status_message) + " " + getResources().getString(R.string.protection_state_active));
                    contentView.setImageViewResource(R.id.ag_notification_armStateImg, R.drawable.ic_armed);
                } else {
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
                Intent armStateIntent = new Intent(this, AutoGoArmStateNotification.class);
                PendingIntent pendingArmStateIntent = PendingIntent.getBroadcast(this, 0, armStateIntent, 0);
                //contentView.setOnClickPendingIntent(R.id.ag_notification_armStateBtn, pendingArmStateIntent);
                Intent startStateIntent = new Intent(this, AutoGoStartStateNotification.class);
                PendingIntent pendingStartStateIntent = PendingIntent.getBroadcast(this, 0, startStateIntent, 0);
                //contentView.setOnClickPendingIntent(R.id.ag_notification_startStateBtn, pendingStartStateIntent);
                mNotificationManager.notify(1, notification);
                Log.i(TAG,"Set notification indicators from previous state");
            }catch (Exception e) {
                Log.e(TAG,e.getLocalizedMessage());
            }

            try {
                if (prefs.getString("lastScreen", "security").equals("security")) {
                    setContentView(R.layout.ag_security);
                    goToSecurity();
                } else if (prefs.getString("lastScreen", "security").equals("controls")) {
                    goToControls();
                    setContentView(R.layout.ag_controls);
                } else if (prefs.getString("lastScreen", "security").equals("location")) {
                    goToLocation();
                    setContentView(R.layout.ag_location);
                } else if (prefs.getString("lastScreen", "security").equals("alerts")) {
                    goToAlerts();
                    setContentView(R.layout.ag_alerts);
                }
                else {
                    //all else fails, security is a good place to fall to
                    //editor.putString("lastScreen", "security").apply();
                    setContentView(R.layout.ag_security);
                    goToSecurity();
                }
                Log.i(TAG,"Set default activity as " + prefs.getString("lastScreen", "security") + " from prior state");
            }catch (Exception e) {
                Log.e(TAG,e.getLocalizedMessage());
            }
            startService();
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
                //prefs.getBoolean("isArmed" false) = false;
                //message = "arm state has been changed to " + isArmed.toString();
                //message = getResources().getString(R.string.action_unlock);

                editor.putBoolean("isArmed", false);
                final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.unlock);
                mp1.start();
                DisplayResponse(message);
                //add the message to intent to pass data to the intent
                //intent.putExtra(EXTRA_MESSAGE,message);
                //go!
                //startActivity(intent);
                UpdateNotifier();
                return true;
            case R.id.action_arm:
                //message = getResources().getString(R.string.action_lock);
                //isArmed = true;
                //message = "arm state has been changed to " + isArmed.toString();

                editor.putBoolean("isArmed", true);
                final MediaPlayer mp4 = MediaPlayer.create(getBaseContext(), R.raw.lock);
                mp4.start();
                DisplayResponse(message);
                //add the message to intent to pass data to the intent
                //intent.putExtra(EXTRA_MESSAGE,message);
                //go!
                //startActivity(intent);
                UpdateNotifier();
                return true;
            case R.id.action_start:
                //message = getResources().getString(R.string.action_start);
                //isStarted = true;
                //message = "engine running state has been changed to " + isStarted.toString();

                editor.putBoolean("isStarted", true);
                final MediaPlayer mp3 = MediaPlayer.create(getBaseContext(), R.raw.start);
                mp3.start();
                DisplayResponse(message);
                //intent.putExtra(EXTRA_MESSAGE, message);
                //startActivity(intent);
                UpdateNotifier();
                return true;
            case R.id.action_stop:
                //message = getResources().getString(R.string.action_start);
                //isStarted = false;
               //message = "engine running state has been changed to " + isStarted.toString();

                editor.putBoolean("isStarted", false);
                final MediaPlayer mp2 = MediaPlayer.create(getBaseContext(), R.raw.stop);
                mp2.start();
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
        if(!prefs.getBoolean("initialConfig", true)) {
            if (prefs.getBoolean("isArmed", false)) {
                menu.findItem(R.id.action_arm).setVisible(false);
            } else {
                menu.findItem(R.id.action_unarm).setVisible(false);
            }
            if (prefs.getBoolean("isStarted", false)) {
                menu.findItem(R.id.action_start).setVisible(false);
            } else {
                menu.findItem(R.id.action_stop).setVisible(false);
            }
        }


        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    public void UpdateNotifier(){
        try
        {
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


            Intent armStateIntent = new Intent(this,AutoGoArmStateNotification.class);
            PendingIntent pendingArmStateIntent = PendingIntent.getBroadcast(this,0,armStateIntent,0);

            //contentView.setOnClickPendingIntent(R.id.ag_notification_armStateBtn, pendingArmStateIntent);

            Intent startStateIntent = new Intent(this,AutoGoStartStateNotification.class);
            PendingIntent pendingStartStateIntent = PendingIntent.getBroadcast(this,0,startStateIntent,0);

            //contentView.setOnClickPendingIntent(R.id.ag_notification_startStateBtn, pendingStartStateIntent);


            mNotificationManager.notify(1, notification);
            Log.i(TAG,"Notification indicator state updated");
        }catch (Exception e) {
            Log.e(TAG,e.getLocalizedMessage());
        }

    }
    public void SetupNotifier()
    {
        String ContentText = "";
        if(prefs.getBoolean("isArmed", false))
        {
            ContentText = getResources().getString(R.string.protection_status_message) + " " + getResources().getString(R.string.protection_state_active);
        }
        else
        {
            ContentText = getResources().getString(R.string.protection_status_message) + " " + getResources().getString(R.string.protection_state_inactive);
        }
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setContentText(ContentText);


        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MyActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MyActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());

        UpdateNotifier();
    }


    public void DisplayPinPanel(final View view){

        MyActivity.pinPanel_1.equals("");
        MyActivity.pinPanel_2.equals("");
        MyActivity.pinPanel_3.equals("");
        MyActivity.pinPanel_4.equals("");

        LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.ag_create_pin_panel, null );
        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        //popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);

        popupView.post(new Runnable() {
            public void run() {
                try {
                    popupWindow.showAtLocation(popupView, Gravity.TOP, 0, 200);
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });


    }

    public void PinPanel_0(View view)
    {

        if(MyActivity.pinPanel_1.equals(""))
        {
            //all 4 pin chars are blank, enter the value
            MyActivity.pinPanel_1= "0";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
            tv.setText("*");
        }else if (MyActivity.pinPanel_2.equals(""))
        {
            MyActivity.pinPanel_2= "0";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_3.equals(""))
        {
            MyActivity.pinPanel_3= "0";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_4.equals(""))
        {
            MyActivity.pinPanel_4= "0";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
            tv.setText("*");
        }
        else
        {
            //all four are entered do nothing
        }
    }

    public void PinPanel_1(View view)
    {
        if(MyActivity.pinPanel_1.equals(""))
        {
            //all 4 pin chars are blank, enter the value
            MyActivity.pinPanel_1= "1";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
            tv.setText("*");
        }else if (MyActivity.pinPanel_2.equals(""))
        {
            MyActivity.pinPanel_2= "1";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_3.equals(""))
        {
            MyActivity.pinPanel_3= "1";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_4.equals(""))
        {
            MyActivity.pinPanel_4= "1";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
            tv.setText("*");
        }
        else
        {
            //all four are entered do nothing
        }
    }

    public void PinPanel_2(View view)
    {
        if(MyActivity.pinPanel_1.equals(""))
        {
            //all 4 pin chars are blank, enter the value
            MyActivity.pinPanel_1= "2";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
            tv.setText("*");
        }else if (MyActivity.pinPanel_2.equals(""))
        {
            MyActivity.pinPanel_2= "2";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_3.equals(""))
        {
            MyActivity.pinPanel_3= "2";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_4.equals(""))
        {
            MyActivity.pinPanel_4= "2";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
            tv.setText("*");
        }
        else
        {
            //all four are entered do nothing
        }
    }

    public void PinPanel_3(View view)
    {
        if(MyActivity.pinPanel_1.equals(""))
        {
            //all 4 pin chars are blank, enter the value
            MyActivity.pinPanel_1= "3";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
            tv.setText("*");
        }else if (MyActivity.pinPanel_2.equals(""))
        {
            MyActivity.pinPanel_2= "3";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_3.equals(""))
        {
            MyActivity.pinPanel_3= "3";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_4.equals(""))
        {
            MyActivity.pinPanel_4= "3";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
            tv.setText("*");
        }
        else
        {
            //all four are entered do nothing
        }
    }

    public void PinPanel_4(View view)
    {
        if(MyActivity.pinPanel_1.equals(""))
        {
            //all 4 pin chars are blank, enter the value
            MyActivity.pinPanel_1= "4";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
            tv.setText("*");
        }else if (MyActivity.pinPanel_2.equals(""))
        {
            MyActivity.pinPanel_2= "4";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_3.equals(""))
        {
            MyActivity.pinPanel_3= "4";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_4.equals(""))
        {
            MyActivity.pinPanel_4= "4";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
            tv.setText("*");
        }
        else
        {
            //all four are entered do nothing
        }
    }

    public void PinPanel_5(View view)
    {
        if(MyActivity.pinPanel_1.equals(""))
        {
            //all 4 pin chars are blank, enter the value
            MyActivity.pinPanel_1= "5";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
            tv.setText("*");
        }else if (MyActivity.pinPanel_2.equals(""))
        {
            MyActivity.pinPanel_2= "5";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_3.equals(""))
        {
            MyActivity.pinPanel_3= "5";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_4.equals(""))
        {
            MyActivity.pinPanel_4= "5";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
            tv.setText("*");
        }
        else
        {
            //all four are entered do nothing
        }
    }

    public void PinPanel_6(View view)
    {
        if(MyActivity.pinPanel_1.equals(""))
        {
            //all 4 pin chars are blank, enter the value
            MyActivity.pinPanel_1= "6";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
            tv.setText("*");
        }else if (MyActivity.pinPanel_2.equals(""))
        {
            MyActivity.pinPanel_2= "6";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_3.equals(""))
        {
            MyActivity.pinPanel_3= "6";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_4.equals(""))
        {
            MyActivity.pinPanel_4= "6";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
            tv.setText("*");
        }
        else
        {
            //all four are entered do nothing
        }
    }

    public void PinPanel_7(View view)
    {
        if(MyActivity.pinPanel_1.equals(""))
        {
            //all 4 pin chars are blank, enter the value
            MyActivity.pinPanel_1= "7";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
            tv.setText("*");
        }else if (MyActivity.pinPanel_2.equals(""))
        {
            MyActivity.pinPanel_2= "7";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_3.equals(""))
        {
            MyActivity.pinPanel_3= "7";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_4.equals(""))
        {
            MyActivity.pinPanel_4= "7";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
            tv.setText("*");
        }
        else
        {
            //all four are entered do nothing
        }
    }

    public void PinPanel_8(View view)
    {
        if(MyActivity.pinPanel_1.equals(""))
        {
            //all 4 pin chars are blank, enter the value
            MyActivity.pinPanel_1= "8";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
            tv.setText("*");
        }else if (MyActivity.pinPanel_2.equals(""))
        {
            MyActivity.pinPanel_2= "8";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_3.equals(""))
        {
            MyActivity.pinPanel_3= "8";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_4.equals(""))
        {
            MyActivity.pinPanel_4= "8";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
            tv.setText("*");
        }
        else
        {
            //all four are entered do nothing
        }
    }

    public void PinPanel_9(View view)
    {
        if(MyActivity.pinPanel_1.equals(""))
        {
            //all 4 pin chars are blank, enter the value
            MyActivity.pinPanel_1= "9";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
            tv.setText("*");
        }else if (MyActivity.pinPanel_2.equals(""))
        {
            MyActivity.pinPanel_2= "9";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_3.equals(""))
        {
            MyActivity.pinPanel_3= "9";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
            tv.setText("*");
        }
        else if (MyActivity.pinPanel_4.equals(""))
        {
            MyActivity.pinPanel_4= "9";
            TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
            tv.setText("*");
        }
        else
        {
            //all four are entered do nothing
        }
    }

    public void PinPanel_Clr(View view)
    {
        MyActivity.pinPanel_1 = "";
        MyActivity.pinPanel_2 = "";
        MyActivity.pinPanel_3 = "";
        MyActivity.pinPanel_4 = "";
        TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
        tv.setText("");
        tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
        tv.setText("");
        tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
        tv.setText("");
        tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
        tv.setText("");

    }

    public void PinPanel_Enter(View view)
    {

        if (SecondAttempt) {
            if (!MyActivity.pinPanel_1.equals("") && !MyActivity.pinPanel_2.equals("") && !MyActivity.pinPanel_3.equals("") && !MyActivity.pinPanel_4.equals("")) {
                String tryPin = MyActivity.pinPanel_1 + MyActivity.pinPanel_2 + MyActivity.pinPanel_3 + MyActivity.pinPanel_4;
                if(tryPin.equals(firstAttemptPin)) {

                    //save and restart
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.ag_pin_confirmed), Toast.LENGTH_LONG)
                            .show();
                    editor.putString("userPin", tryPin).apply();
                    editor.putBoolean("initialConfig", false).apply();

                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
                else
                {
                    //PINs dont match, start over
                    Toast.makeText(getApplicationContext(),
                            "PIN doesn't match entry.  Please re-enter", Toast.LENGTH_LONG)
                            .show();

                    MyActivity.pinPanel_1 = "";
                    MyActivity.pinPanel_2 = "";
                    MyActivity.pinPanel_3 = "";
                    MyActivity.pinPanel_4 = "";
                    TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
                    tv.setText("");
                    tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
                    tv.setText("");
                    tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
                    tv.setText("");
                    tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
                    tv.setText("");
                    tv = (TextView) popupView.findViewById(R.id.ag_createPin_message);
                    tv.setText(getString(R.string.ag_createpin_enterPinMessage));
                    SecondAttempt = false;
                }
            }
            else {
                //not enough characters in pin
                Toast.makeText(getApplicationContext(),
                        "Invalid number of characters.  Please re-enter.", Toast.LENGTH_LONG)
                        .show();

                MyActivity.pinPanel_1 = "";
                MyActivity.pinPanel_2 = "";
                MyActivity.pinPanel_3 = "";
                MyActivity.pinPanel_4 = "";
                TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
                tv.setText("");
                tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
                tv.setText("");
                tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
                tv.setText("");
                tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
                tv.setText("");
                tv = (TextView) popupView.findViewById(R.id.ag_createPin_message);
                tv.setText(getString(R.string.ag_createpin_enterPinMessage));
                SecondAttempt = false;
            }
        }else
        {
            //first PIN entry attempt
            if (!MyActivity.pinPanel_1.equals("") && !MyActivity.pinPanel_2.equals("") && !MyActivity.pinPanel_3.equals("") && !MyActivity.pinPanel_4.equals("")) {
                firstAttemptPin = MyActivity.pinPanel_1 + MyActivity.pinPanel_2 + MyActivity.pinPanel_3 + MyActivity.pinPanel_4;
                MyActivity.pinPanel_1 = "";
                MyActivity.pinPanel_2 = "";
                MyActivity.pinPanel_3 = "";
                MyActivity.pinPanel_4 = "";
                TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
                tv.setText("");
                tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
                tv.setText("");
                tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
                tv.setText("");
                tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
                tv.setText("");

                tv = (TextView) popupView.findViewById(R.id.ag_createPin_message);
                tv.setText(getString(R.string.ag_createPin_confirmPin));
                SecondAttempt = true;

            } else {
                //not enough characters in pin
                Toast.makeText(getApplicationContext(),
                        "Invalid number of characters.  Please re-enter.", Toast.LENGTH_LONG)
                        .show();

                MyActivity.pinPanel_1 = "";
                MyActivity.pinPanel_2 = "";
                MyActivity.pinPanel_3 = "";
                MyActivity.pinPanel_4 = "";
                TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
                tv.setText("");
                tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
                tv.setText("");
                tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
                tv.setText("");
                tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
                tv.setText("");
            }
        }

    }

    public void PinPanel_Close(View view)
    {
        MyActivity.pinPanel_1 = "";
        MyActivity.pinPanel_2 = "";
        MyActivity.pinPanel_3 = "";
        MyActivity.pinPanel_4 = "";
        TextView tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin1);
        tv.setText("");
        tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin2);
        tv.setText("");
        tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin3);
        tv.setText("");
        tv = (TextView) popupView.findViewById(R.id.ag_pinPanel_pin4);
        tv.setText("");
        popupWindow.dismiss();
    }





    public void goToSecurity ()
    {
        editor.putString("lastScreen", "security").apply();

        Intent intent = new Intent(this, AutoGoSecurity.class);
        startActivity(intent);
    }

    public void goToLocation ()
    {
        editor.putString("lastScreen", "location").apply();

        Intent intent = new Intent(this, AutoGoLocation.class);
        startActivity(intent);
    }

    public void goToControls ()
    {
        editor.putString("lastScreen", "controls").apply();

        Intent intent = new Intent(this, AutoGoControls.class);
        startActivity(intent);
    }


    public void goToAlerts() {
        try {
            editor.putString("lastScreen", "alerts").apply();

            Intent intent = new Intent(this, AutoGoAlerts.class);
            intent.putExtra("fromNotify", false);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToSettings() {
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
            backPressed = true;
            BackButtonTimer counter = new BackButtonTimer(3000,1000);
            counter.start();
        }
    }

    public void startService() {
        startService(new Intent(this, AutoGoAlertNotify.class));
    }

    public void stopService() {
        stopService(new Intent(this, AutoGoAlertNotify.class));
    }

}


