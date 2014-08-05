package com.soniquesoftwaredesign.sx14r.autogo;


//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;

//import android.media.MediaPlayer;
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

public class AutoGoControls extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ag_controls);
        // Show the Up button in the action bar.
        setupActionBar();

        //set default button states
        if(MyActivity.isStarted) {
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
        if(MyActivity.areLightsOn) {
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
        //set temp label from value
        //recall last command
        TextView tv = (TextView) findViewById(R.id.ag_controls_hvacTempSetting);
        tv.setText(MyActivity.setTemp + "°");

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
                MyActivity.isArmed = false;
                message = "arm state has been changed to " + MyActivity.isArmed.toString();
                //message = getResources().getString(R.string.action_unlock);

                MyActivity.editor.putBoolean("isArmed", false).apply();
                DisplayResponse(message);
                //add the message to intent to pass data to the intent
                //intent.putExtra(EXTRA_MESSAGE,message);
                //go!
                //startActivity(intent);
                UpdateNotifier();
                return true;
            case R.id.action_arm:
                //message = getResources().getString(R.string.action_lock);
                MyActivity.isArmed = true;
                message = "arm state has been changed to " + MyActivity.isArmed.toString();

                MyActivity.editor.putBoolean("isArmed", true).apply();
                DisplayResponse(message);
                //add the message to intent to pass data to the intent
                //intent.putExtra(EXTRA_MESSAGE,message);
                //go!
                //startActivity(intent);
                UpdateNotifier();
                return true;
            case R.id.action_start:
                //message = getResources().getString(R.string.action_start);
                MyActivity.isStarted = true;
                message = "engine running state has been changed to " + MyActivity.isStarted.toString();

                MyActivity.editor.putBoolean("isStarted", true).apply();
                DisplayResponse(message);
                //intent.putExtra(EXTRA_MESSAGE, message);
                //startActivity(intent);
                UpdateNotifier();
                return true;
            case R.id.action_stop:
                //message = getResources().getString(R.string.action_start);
                MyActivity.isStarted = false;
                message = "engine running state has been changed to " + MyActivity.isStarted.toString();

                MyActivity.editor.putBoolean("isStarted", false).apply();
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

        if(MyActivity.isArmed)
        {
            menu.findItem(R.id.action_arm).setVisible(false);
        }
        else
        {
            menu.findItem(R.id.action_unarm).setVisible(false);
        }
        if(MyActivity.isStarted)
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
        if (MyActivity.isStarted==false){
            MyActivity.isStarted=true;
        }

        final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.start);
        mp1.start();

        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_startVehicleBtn);
        btn.setEnabled(false);
        btn = (SAutoBgButton) findViewById(R.id.ag_controls_stopVehicleBtn);
        btn.setEnabled(true);
        MyActivity.editor.putBoolean("isStarted", true).apply();

        UpdateNotifier();
    }

    public void stopVehicle(View view){
        if(MyActivity.isStarted==true){
            MyActivity.isStarted=false;
        }
        final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.stop);
        mp1.start();
        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_stopVehicleBtn);
        btn.setEnabled(false);
        btn = (SAutoBgButton) findViewById(R.id.ag_controls_startVehicleBtn);
        btn.setEnabled(true);
        MyActivity.editor.putBoolean("isStarted", false).apply();

        UpdateNotifier();
    }

    public void hvacOn(View view) {
        MyActivity.isHVACOn=true;
        ImageView img = (ImageView) findViewById(R.id.ag_controls_hvacStateImg);
        img.setImageResource(R.drawable.ag_controls_power_on);
        //final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.lock);
        //mp1.start();
        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_hvacOnBtn);
        btn.setEnabled(false);
        btn = (SAutoBgButton) findViewById(R.id.ag_controls_hvacOffBtn);
        btn.setEnabled(true);
        MyActivity.editor.putBoolean("isHVACOn", true).apply();
    }

    public void hvacOff(View view){
        MyActivity.isHVACOn=false;
        ImageView img = (ImageView) findViewById(R.id.ag_controls_hvacStateImg);
        img.setImageResource(R.drawable.ag_controls_power_off);
        //final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.unlock);
        //mp1.start();
        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_hvacOnBtn);
        btn.setEnabled(true);
        btn = (SAutoBgButton) findViewById(R.id.ag_controls_hvacOffBtn);
        btn.setEnabled(false);
        MyActivity.editor.putBoolean("isHVACOn", false).apply();
    }

    public void lightsOff(View view) {
        MyActivity.areLightsOn=false;
        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_lightingOnBtn);
        btn.setEnabled(true);
        btn = (SAutoBgButton) findViewById(R.id.ag_controls_lightingOffBtn);
        btn.setEnabled(false);
        MyActivity.editor.putBoolean("areLightsOn", false).apply();
    }

    public void lightsOn(View view) {
        MyActivity.areLightsOn=true;
        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_lightingOnBtn);
        btn.setEnabled(false);
        btn = (SAutoBgButton) findViewById(R.id.ag_controls_lightingOffBtn);
        btn.setEnabled(true);
        MyActivity.editor.putBoolean("areLightsOn", true).apply();
    }

    public void tempUp(View view){
        if (MyActivity.setTemp < 80) {
            MyActivity.setTemp++;
            MyActivity.editor.putInt("setTemp", MyActivity.setTemp).apply();
            if (MyActivity.setTemp==80)
            {
                SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_climateUpBtn);
                btn.setEnabled(false);
            }
            if(MyActivity.setTemp>60)
            {
                SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_controls_climateDownBtn);
                btn.setEnabled(true);
            }
            //recall last command
            TextView tv = (TextView) findViewById(R.id.ag_controls_hvacTempSetting);
            tv.setText(MyActivity.setTemp + "°");
        }
    }

    public void tempDown(View view){
        if (MyActivity.setTemp > 60) {
            MyActivity.setTemp--;
            MyActivity.editor.putInt("setTemp", MyActivity.setTemp).apply();
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
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, getResources().getString(R.string.app_name), when);

        NotificationManager mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.ag_custom_notification);
        contentView.setImageViewResource(R.id.ag_notification_icon, R.drawable.ic_launcher);
        contentView.setTextViewText(R.id.ag_notification_title, getResources().getString(R.string.app_name));
        if(MyActivity.isStarted==true)
        {
            contentView.setImageViewResource(R.id.ag_notification_startStateImg, R.drawable.ic_start);
        }else
        {

            contentView.setImageViewResource(R.id.ag_notification_startStateImg, R.drawable.ic_stop);
        }
        if(MyActivity.isArmed==true)
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
    }

    public void goToSettings(View view) {
    }
}


