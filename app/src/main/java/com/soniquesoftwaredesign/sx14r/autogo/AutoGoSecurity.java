package com.soniquesoftwaredesign.sx14r.autogo;

//import java.text.SimpleDateFormat;
        import java.text.DateFormat;
        import java.util.Calendar;
//import java.util.Date;

        import android.graphics.drawable.GradientDrawable;
        import android.media.MediaPlayer;
        import android.os.Bundle;
        import android.app.Activity;
        import android.app.AlertDialog;
        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.view.Gravity;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.ViewGroup;
        import android.view.ViewGroup.LayoutParams;
//import android.widget.Button;
        import android.view.WindowManager;
        import android.widget.Button;
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
        import android.widget.Toast;

public class AutoGoSecurity extends Activity {
    public View popupView;
    public PopupWindow popupWindow;

    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    public static final String PREFERENCES = "AutoGo Preferences";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ag_security);
        // Show the Up button in the action bar.
        setupActionBar();
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();

        //SetImageArmState();

        ImageView img = (ImageView) findViewById(R.id.armStateImg);
       /* img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId())
                {
                    case R.id.armStateImg:
                        if(prefs.getBoolean("isArmed", false)==false)
                        {
                            ArmVehicle(v);
                        }else if(prefs.getBoolean("isArmed", false)==true)
                        {
                            UnarmVehicle(v);
                        }
                        break;
                }

            }

        });
        */

        //set default button states
        if(prefs.getBoolean("isArmed", false)) {
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_armBtn);
            btn.setEnabled(false);
            btn = (SAutoBgButton) findViewById(R.id.ag_security_unarmBtn);
            btn.setEnabled(true);
            img = (ImageView) findViewById(R.id.armStateImg);
            img.setImageResource(R.drawable.armed);


            img.setImageResource(R.drawable.armed);
        }
        else {
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_armBtn);
            btn.setEnabled(true);
            btn = (SAutoBgButton) findViewById(R.id.ag_security_unarmBtn);
            btn.setEnabled(false);
            img = (ImageView) findViewById(R.id.armStateImg);
            img.setImageResource(R.drawable.unarmed);
        }
        if(prefs.getBoolean("isWindowUp", true)){
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_windowsupbtn);
            btn.setEnabled(false);
            btn = (SAutoBgButton) findViewById(R.id.ag_security_windowsdownbtn);
            btn.setEnabled(true);
        }else
        {
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_windowsupbtn);
            btn.setEnabled(true);
            btn = (SAutoBgButton) findViewById(R.id.ag_security_windowsdownbtn);
            btn.setEnabled(false);
        }
        if(prefs.getBoolean("isVehicleDisabled", false)){
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_restorebtn);
            btn.setEnabled(true);
            btn = (SAutoBgButton) findViewById(R.id.ag_security_shutdownbtn);
            btn.setEnabled(true);
        }else
        {
            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_shutdownbtn);
            btn.setEnabled(true);
            btn = (SAutoBgButton) findViewById(R.id.ag_security_restorebtn);
            btn.setEnabled(false);
        }

        //recall last command
        TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
        tv.setText(prefs.getString("lastCommand", "NULL") + " " + "@ " + prefs.getString("lastCommandStamp", "NULL"));
    }


    public void SetImageArmState(View v) {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        if(prefs.getBoolean("isArmed", false)==false)
        {
            ArmVehicle(v);
        }else if(prefs.getBoolean("isArmed", false)==true)
        {
            UnarmVehicle(v);
        }
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
                editor.putBoolean("isArmed", false).apply();

                UpdateNotifier();
                return true;
            case R.id.action_arm:
                //MyActivity.isArmed = true;
                message = "arm state has been changed to true";
                editor.putBoolean("isArmed", true).apply();

                UpdateNotifier();
                return true;
            case R.id.action_start:
                //MyActivity.isStarted = true;
                message = "engine running state has been changed to true";
                editor.putBoolean("isStarted", true).apply();

                UpdateNotifier();
                return true;
            case R.id.action_stop:
                //MyActivity.isStarted = false;
                message = "engine running state has been changed to false";
                editor.putBoolean("isStarted", false).apply();

                UpdateNotifier();
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
        if(prefs.getBoolean("isArmed", false))
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




    public void ArmVehicle(View v) {
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        ImageView img = (ImageView) findViewById(R.id.armStateImg);
        img.setImageResource(R.drawable.armed);
        editor.putBoolean("isArmed", true).apply();
        editor.putString("lastCommand","ARM").apply();
        editor.putString("lastCommandStamp",DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) ).apply();
        final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.lock);
        mp1.start();
        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_armBtn);
        btn.setEnabled(false);
        btn = (SAutoBgButton) findViewById(R.id.ag_security_unarmBtn);
        btn.setEnabled(true);
        editor.putBoolean("isArmed", true).apply();

        //MyActivity.lastCommandStamp = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
        tv.setText(prefs.getString("lastCommand", "NULL") + " " + "@ " + prefs.getString("lastCommandStamp", "NULL"));
        //editor.putString("lastCommand", MyActivity.lastCommand).apply();

        UpdateNotifier();
    }

    public void UnarmVehicle(View v){
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        ImageView img = (ImageView) findViewById(R.id.armStateImg);
        img.setImageResource(R.drawable.unarmed);
        editor.putBoolean("isArmed", false).apply();
        editor.putString("lastCommand","UNARM").apply();
        editor.putString("lastCommandStamp", DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();
        final MediaPlayer mp1 = MediaPlayer.create(getBaseContext(), R.raw.unlock);
        mp1.start();
        SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_unarmBtn);
        btn.setEnabled(false);
        btn = (SAutoBgButton) findViewById(R.id.ag_security_armBtn);
        btn.setEnabled(true);
        editor.putBoolean("isArmed", false).apply();

        TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
        tv.setText(prefs.getString("lastCommand", "NULL") + " " + "@ " + prefs.getString("lastCommandStamp", "NULL"));

        UpdateNotifier();
    }

    public void WindowsUp(View view){
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        if(prefs.getBoolean("isWindowUp", true)==false) {
            editor.putBoolean("isWindowUp",true).apply();
            editor.putString("lastCommand","WINDOWUP").apply();
            editor.putString("lastCommandStamp", DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();
            //MyActivity.lastCommandStamp = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
            tv.setText(prefs.getString("lastCommand", "NULL") + " " + "@ " + prefs.getString("lastCommandStamp", "NULL"));
            //editor.putString("lastCommand", MyActivity.lastCommand).apply();
            editor.putBoolean("isWindowUp", true).apply();

            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_windowsupbtn);
            btn.setEnabled(false);
            btn = (SAutoBgButton) findViewById(R.id.ag_security_windowsdownbtn);
            btn.setEnabled(true);
        }
    }

    public void WindowsDown(View view){
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        if(prefs.getBoolean("isWindowUp", true)) {
            editor.putBoolean("isWindowUp",false).apply();
            editor.putString("lastCommand","WINDOWDOWN").apply();
            editor.putString("lastCommandStamp",DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();
            TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
            tv.setText(prefs.getString("lastCommand", "NULL") + " " + "@ " + prefs.getString("lastCommandStamp", "NULL"));
            //editor.putString("lastCommand", MyActivity.lastCommand).apply();
            //editor.putString("lastCommandStamp", MyActivity.lastCommandStamp).apply();
            editor.putBoolean("isWindowUp", false).apply();

            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_windowsupbtn);
            btn.setEnabled(true);
            btn = (SAutoBgButton) findViewById(R.id.ag_security_windowsdownbtn);
            btn.setEnabled(false);
        }
    }

    public void DisplaySecurityPanel(View view){

        MyActivity.pinPanel_1.equals("");
        MyActivity.pinPanel_2.equals("");
        MyActivity.pinPanel_3.equals("");
        MyActivity.pinPanel_4.equals("");

        LayoutInflater layoutInflater
                = (LayoutInflater)getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = layoutInflater.inflate(R.layout.ag_pin_panel, null );
        popupWindow = new PopupWindow(
                popupView,
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);

        //Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
        //btnDismiss.setOnClickListener(new Button.OnClickListener()
        //setContentView(R.layout.ag_pin_panel);
        popupWindow.showAtLocation(view, Gravity.TOP, 0,200);

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
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        if(!MyActivity.pinPanel_1.equals("") && !MyActivity.pinPanel_2.equals("") && !MyActivity.pinPanel_3.equals("")&& !MyActivity.pinPanel_4.equals("")){
            String tryPin = MyActivity.pinPanel_1 + MyActivity.pinPanel_2 + MyActivity.pinPanel_3 + MyActivity.pinPanel_4;
            if (tryPin.equals(prefs.getString("userPin", "0000"))){
                Toast.makeText(getApplicationContext(),
                        "Code entered successfully.  Action started.", Toast.LENGTH_LONG)
                        .show();
                ShutdownVehicle(view);
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

                SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_shutdownbtn);
                btn.setEnabled(false);
                btn = (SAutoBgButton) findViewById(R.id.ag_security_restorebtn);
                btn.setEnabled(true);

                editor.putBoolean("isVehicleDisabled", true).apply();
                editor.putString("lastCommand","SHUTDOWN").apply();
                editor.putString("lastCommandStamp",DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();
                tv = (TextView) findViewById(R.id.lastCommandAndStamp);
                tv.setText(prefs.getString("lastCommand", "NULL") + " " + "@ " + prefs.getString("lastCommandStamp", "NULL"));
                //editor.putString("lastCommand", MyActivity.lastCommand).apply();
                //editor.putString("lastCommandStamp", MyActivity.lastCommandStamp).apply();
                //editor.putBoolean("isVehicleDisabled", true).apply();
            }
            else
            {
                //wrong pin
                Toast.makeText(getApplicationContext(),
                        "Invalid PIN entry.  Please re-enter.", Toast.LENGTH_LONG)
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
        else
        {
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

    public void ShutdownVehicle(View view){
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        if(prefs.getBoolean("isVehicleDisabled", false)==false) {
            editor.putBoolean("isVehicleDisabled", true).apply();
            editor.putString("lastCommand", "SHUTDOWN").apply();
            editor.putString("lastCommandStamp",DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();
            TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
            tv.setText(prefs.getString("lastCommand", "NULL") + " " + "@ " + prefs.getString("lastCommandStamp", "NULL"));
            //editor.putString("lastCommand", MyActivity.lastCommand).apply();
            //editor.putString("lastCommandStamp", MyActivity.lastCommandStamp).apply();
            //editor.putBoolean("isVehicleDisabled", true).apply();

            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_shutdownbtn);
            btn.setEnabled(false);
            btn = (SAutoBgButton) findViewById(R.id.ag_security_restorebtn);
            btn.setEnabled(true);
        }
    }

    public void RestoreVehicle(View view){
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        if(prefs.getBoolean("isVehicleDisabled", false)==true) {
            editor.putBoolean("isVehicleDisabled",false).apply();
            editor.putString("lastCommand","RESTORE").apply();
            editor.putString("lastCommandStamp",DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())).apply();
            TextView tv = (TextView) findViewById(R.id.lastCommandAndStamp);
            tv.setText(prefs.getString("lastCommand", "NULL") + " " + "@ " + prefs.getString("lastCommandStamp", "NULL"));
            //editor.putString("lastCommand", MyActivity.lastCommand).apply();
            //editor.putString("lastCommandStamp", MyActivity.lastCommandStamp).apply();
            //editor.putBoolean("isVehicleDisabled", false).apply();

            SAutoBgButton btn = (SAutoBgButton) findViewById(R.id.ag_security_shutdownbtn);
            btn.setEnabled(true);
            btn = (SAutoBgButton) findViewById(R.id.ag_security_restorebtn);
            btn.setEnabled(false);
        }
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
            editor.putString("lastScreen", "security");

            Intent intent = new Intent(this, AutoGoSecurity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
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
        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
        editor = prefs.edit();
        try
        {
            editor.putString("lastScreen", "controls").apply();

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
            editor.putString("lastScreen", "alerts").apply();

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


