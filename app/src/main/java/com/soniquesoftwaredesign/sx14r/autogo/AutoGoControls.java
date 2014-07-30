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

        Intent intent = getIntent();

        String message = intent.getStringExtra(MyActivity.EXTRA_MESSAGE);

        TextView textView = new TextView(this);
        textView.setTextSize(40);

        textView.setText(message);

        setContentView(R.layout.ag_controls);
        // Show the Up button in the action bar.
        setupActionBar();

        //MyActivity.isStarted = GetSetting(getResources().getString(R.string.setting_start));
        //MyActivity.isArmed = GetSetting(getResources().getString(R.string.setting_arm));
        //MyActivity.isHVACOn = GetSetting(getResources().getString(R.string.setting_hvacOn));


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
            case R.id.action_disarm:
                MyActivity.isArmed = false;
                message = "arm state has been changed to " + MyActivity.isArmed.toString();
                //message = getResources().getString(R.string.action_unlock);

                SaveSetting(getResources().getString(R.string.setting_arm),false);
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

                SaveSetting(getResources().getString(R.string.setting_arm),true);
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

                SaveSetting(getResources().getString(R.string.setting_start),true);
                DisplayResponse(message);
                //intent.putExtra(EXTRA_MESSAGE, message);
                //startActivity(intent);
                UpdateNotifier();
                return true;
            case R.id.action_stop:
                //message = getResources().getString(R.string.action_start);
                MyActivity.isStarted = false;
                message = "engine running state has been changed to " + MyActivity.isStarted.toString();

                SaveSetting(getResources().getString(R.string.setting_start),false);
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
            menu.findItem(R.id.action_disarm).setVisible(false);
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
        SaveSetting(getResources().getString(R.string.setting_start), true);
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
        SaveSetting(getResources().getString(R.string.setting_start), false);
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
        SaveSetting(getResources().getString(R.string.setting_hvacOn), true);
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
        SaveSetting(getResources().getString(R.string.setting_hvacOn), false);
    }

    public void ag_securityActivity (View view)
    {
        Intent intent = new Intent(this, AutoGoSecurity.class);

        //EditText editText = (EditText) findViewById(R.id.edit_message);

        //String message = editText.getText().toString();

        //intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
    }

    public void ag_controlsActivity (View view)
    {
        Intent intent = new Intent(this, AutoGoControls.class);

        //EditText editText = (EditText) findViewById(R.id.edit_message);

        //String message = editText.getText().toString();

        //intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
    }

    public void ag_locationActivity (View view)
    {
        Intent intent = new Intent(this, AutoGoSecurity.class);

        //EditText editText = (EditText) findViewById(R.id.edit_message);

        //String message = editText.getText().toString();

        //intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
    }

    public void ag_alertsActivity (View view)
    {
        Intent intent = new Intent(this, AutoGoSecurity.class);

        //EditText editText = (EditText) findViewById(R.id.edit_message);

        //String message = editText.getText().toString();

        //intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
    }

    public void ag_settingsActivity (View view)
    {
        Intent intent = new Intent(this, AutoGoSecurity.class);

        //EditText editText = (EditText) findViewById(R.id.edit_message);

        //String message = editText.getText().toString();

        //intent.putExtra(EXTRA_MESSAGE, message);

        startActivity(intent);
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

    public Boolean GetSetting(String keyValue){
        //set a Default Value
        Boolean ResultValue = false;
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        ResultValue = sharedPref.getBoolean(keyValue, false);
        return ResultValue;
    }

    public void SaveSetting(String keyValue, Boolean savedValue){
        //write the last action to setting file
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(keyValue, savedValue);
        editor.commit();

        UpdateNotifier();
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

}


