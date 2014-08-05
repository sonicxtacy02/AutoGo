package com.soniquesoftwaredesign.sx14r.autogo;

//import java.text.SimpleDateFormat;
        import java.text.DateFormat;
        import java.util.Calendar;
//import java.util.Date;

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
        import android.widget.Toast;

public class AutoGoSecurity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ag_security);
        // Show the Up button in the action bar.
        setupActionBar();



        ImageView img = (ImageView) findViewById(R.id.armStateImg);
        img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.armStateImg:
                        if(MyActivity.isArmed==false)
                        {
                            ArmVehicle(v);
                        }else if(MyActivity.isArmed==true)
                        {
                            UnarmVehicle(v);
                        }
                        break;
                }

            }

        });

        //set default button states
        if(MyActivity.isArmed) {
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_armBtn);
            btn.setEnabled(false);
            btn = (SAutoBgButton) findViewById(R.id.ag_security_unarmBtn);
            btn.setEnabled(true);

            img.setImageResource(R.drawable.armed);
        }
        else {
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_armBtn);
            btn.setEnabled(true);
            btn = (SAutoBgButton) findViewById(R.id.ag_security_unarmBtn);
            btn.setEnabled(false);

            img.setImageResource(R.drawable.unarmed);
        }

        //recall last command
        TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
        tv.setText(MyActivity.lastCommand + " " + "@ " + MyActivity.lastCommandStamp);
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
                message = "arm state has been changed to false";
                MyActivity.editor.putBoolean("isArmed", false).apply();

                UpdateNotifier();
                return true;
            case R.id.action_arm:
                MyActivity.isArmed = true;
                message = "arm state has been changed to true";
                MyActivity.editor.putBoolean("isArmed", true).apply();

                UpdateNotifier();
                return true;
            case R.id.action_start:
                MyActivity.isStarted = true;
                message = "engine running state has been changed to true";
                MyActivity.editor.putBoolean("isStarted", true).apply();

                UpdateNotifier();
                return true;
            case R.id.action_stop:
                MyActivity.isStarted = false;
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




    public void ArmVehicle(View view) {
        ImageView img = (ImageView) findViewById(R.id.armStateImg);
        img.setImageResource(R.drawable.armed);
        MyActivity.isArmed=true;
        MyActivity.lastCommand = "ARM";
        final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.lock);
        mp1.start();
        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_armBtn);
        btn.setEnabled(false);
        btn = (SAutoBgButton) findViewById(R.id.ag_security_unarmBtn);
        btn.setEnabled(true);
        MyActivity.editor.putBoolean("isArmed", true).apply();

        MyActivity.lastCommandStamp = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
        tv.setText(MyActivity.lastCommand + " " + "@ " + MyActivity.lastCommandStamp);
        MyActivity.editor.putString("lastCommand", MyActivity.lastCommand).apply();

        UpdateNotifier();
    }

    public void UnarmVehicle(View view){
        ImageView img = (ImageView) findViewById(R.id.armStateImg);
        img.setImageResource(R.drawable.unarmed);
        MyActivity.isArmed=false;
        MyActivity.lastCommand = "UNARM";
        final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.unlock);
        mp1.start();
        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_unarmBtn);
        btn.setEnabled(false);
        btn = (SAutoBgButton) findViewById(R.id.ag_security_armBtn);
        btn.setEnabled(true);
        MyActivity.editor.putBoolean("isArmed", false).apply();

        MyActivity.lastCommandStamp = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
        tv.setText(MyActivity.lastCommand + " " + "@ " + MyActivity.lastCommandStamp);
        MyActivity.editor.putString("lastCommand", MyActivity.lastCommand).apply();
        MyActivity.editor.putString("lastCommandStamp", MyActivity.lastCommandStamp).apply();

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
        /*
        try {
            MyActivity.editor.putString("lastScreen", "security");

            Intent intent = new Intent(this, AutoGoSecurity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
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
    }



}


