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
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

//public class MainActivity extends FragmentActivity implements ActionBar.TabListener {

public class MyActivity extends Activity {


    public int mId;
    public static Boolean isArmed;
    public static Boolean isStarted;
    public static Boolean isHVACOn;
    public static Boolean areLightsOn;

    public static String lastCommand;
    public static String lastCommandStamp;
    public static String lastScreen;

    public static int setTemp;

    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    private static final String PREFERENCES = "AutoGo Preferences";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();

        //get saved settings
        isStarted = prefs.getBoolean("isStarted", false);
        isArmed = prefs.getBoolean("isArmed", false);
        isHVACOn = prefs.getBoolean("isHVACOn", false);
        areLightsOn = prefs.getBoolean("areLightsOn", false);
        lastScreen = prefs.getString("lastScreen", "security");


        lastCommand = prefs.getString("lastCommand", "null");
        lastCommandStamp = prefs.getString("lastCommandStamp", "null");

        setTemp = prefs.getInt("setTemp", 72);

        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, getResources().getString(R.string.app_name), when);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.ag_custom_notification);
        contentView.setImageViewResource(R.id.ag_notification_icon, R.drawable.ic_launcher);
        contentView.setTextViewText(R.id.ag_notification_title, getResources().getString(R.string.app_name));
        if (isStarted == true) {
            contentView.setImageViewResource(R.id.ag_notification_startStateImg, R.drawable.ic_start);
        } else {
            contentView.setImageViewResource(R.id.ag_notification_startStateImg, R.drawable.ic_stop);
        }
        if (isArmed == true) {
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

        if (lastScreen.equals("security") ) {
            setContentView(R.layout.ag_security);
            goToSecurity();
        } else if (lastScreen.equals("controls")) {
            goToControls();
            setContentView(R.layout.ag_controls);
        } else if (lastScreen.equals("location")) {
            goToLocation();
            setContentView(R.layout.ag_location);
        }
        else {
            //all else fails, security is a good place to fall to
            editor.putString("lastScreen", "security").apply();
            setContentView(R.layout.ag_security);
            goToSecurity();
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
                isArmed = false;
                message = "arm state has been changed to " + isArmed.toString();
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
                isArmed = true;
                message = "arm state has been changed to " + isArmed.toString();

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
                isStarted = true;
                message = "engine running state has been changed to " + isStarted.toString();

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
                isStarted = false;
                message = "engine running state has been changed to " + isStarted.toString();

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

        if (isArmed) {
            menu.findItem(R.id.action_arm).setVisible(false);
        } else {
            menu.findItem(R.id.action_unarm).setVisible(false);
        }
        if (isStarted) {
            menu.findItem(R.id.action_start).setVisible(false);
        } else {
            menu.findItem(R.id.action_stop).setVisible(false);
        }


        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        lastScreen = GetStringSetting("lastScreen");
        if (lastScreen == null || lastScreen == "null") {
            setContentView(R.layout.ag_security);
        } else if (lastScreen == "security") {
            setContentView(R.layout.ag_security);
        } else if (lastScreen == "controls") {
            setContentView(R.layout.ag_controls);
        } else if (lastScreen == "location") {
            setContentView(R.layout.ag_location);
        }
        */
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
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, getResources().getString(R.string.app_name), when);

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.ag_custom_notification);
        contentView.setImageViewResource(R.id.ag_notification_icon, R.drawable.ic_launcher);
        contentView.setTextViewText(R.id.ag_notification_title, getResources().getString(R.string.app_name));
        if(isStarted==true)
        {
            contentView.setImageViewResource(R.id.ag_notification_startStateImg, R.drawable.ic_start);
        }else
        {

            contentView.setImageViewResource(R.id.ag_notification_startStateImg, R.drawable.ic_stop);
        }
        if(isArmed==true)
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
    public void SetupNotifier()
    {
        String ContentText = "";
        if(isArmed)
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
    }

    public void goToSettings() {
    }

}

