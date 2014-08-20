package com.soniquesoftwaredesign.sx14r.autogo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by SX14R on 8/19/2014.
 */
public class AutoGoAlertNotify extends Service {

    private static Timer timer = new Timer();
    public static SharedPreferences prefs;
    public static SharedPreferences.Editor editor;
    private static final String PREFERENCES = "AutoGo Preferences";
    public static String onBoardLastUpdated;
    public static Integer onBoardIsArmed;
    public static Integer onBoardIsStarted;
    public static Integer onBoardAreWindowsOpen;
    public static String alertLastUpdated;
    public static String alertType;
    public static NotificationManager mNotificationManager;
    public static Integer NOTIFICATION_ID;

    public static final String ALERT_TYPE = "alertType";

    public AutoGoAlertNotify() {
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        startService();
        //Toast.makeText(this, "Service was Created", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // Perform your long running operations here.
        //Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    public void startService()
    {
        timer.scheduleAtFixedRate(new mainTask(), 0, 60000);
    }

    private class mainTask extends TimerTask
    {
        public void run() {

            if(GetAlertCount() > 0)
            {
                GetAlert();
                AlertNotify();
            }

            //toastHandler.sendEmptyMessage(0);
        }

        public Integer GetAlertCount()
        {
            Integer TheCount = 0;

            try {
                try {
                    try {
                        //get vehicle state to check for consistency
                        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                        editor = prefs.edit();


                        //count the alerts available
                        String getAlertCountUrl = "http://soniquesoftwaredesign.com/AutoGo/alert/request_alert_count.php?";

                        // HttpClient
                        HttpClient httpClient = new DefaultHttpClient();

                        List<NameValuePair> params = new LinkedList<NameValuePair>();
                        params.add(new BasicNameValuePair("usr", prefs.getString("userName", null)));

                        String paramString = URLEncodedUtils.format(params, "utf-8");
                        getAlertCountUrl += paramString;

                        HttpGet httpget = new HttpGet(getAlertCountUrl);
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        String ServerResponseString = httpClient.execute(httpget, responseHandler);
                        Log.e("pass 1", "connection success ");


                        TheCount = Integer.parseInt(ServerResponseString);

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


            return TheCount;
        }

        public void GetAlert()
        {
            try {
                try {
                    try {
                        //get vehicle state to check for consistency
                        SharedPreferences prefs = getSharedPreferences(PREFERENCES, MODE_PRIVATE);
                        editor = prefs.edit();


                        //count the alerts available
                        String getAlertCountUrl = "http://soniquesoftwaredesign.com/AutoGo/alert/request_vehicle_alert.php?";

                        // HttpClient
                        HttpClient httpClient = new DefaultHttpClient();

                        List<NameValuePair> params = new LinkedList<NameValuePair>();
                        params.add(new BasicNameValuePair("usr", prefs.getString("userName", null)));

                        String paramString = URLEncodedUtils.format(params, "utf-8");
                        getAlertCountUrl += paramString;

                        HttpGet httpget = new HttpGet(getAlertCountUrl);
                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        String ServerResponseString = httpClient.execute(httpget, responseHandler);
                        Log.e("pass 1", "connection success ");
                        String[] separated = ServerResponseString.split("@");
                        alertLastUpdated = separated[0];
                        alertType = separated[1];

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


    }

    public void AlertNotify()
    {
        NOTIFICATION_ID = 1;
        String ns = Context.NOTIFICATION_SERVICE;
        mNotificationManager = (NotificationManager) getSystemService(ns);
        int icon=0;
        String notifyString  = getString(R.string.ag_alert_notify_unknown);

        if (alertType.toLowerCase().equals("bump")) {
            icon = R.drawable.ag_alerts_bump;
            notifyString = getString(R.string.ag_alert_notify_bump);

            long when = System.currentTimeMillis();
            Notification notification = new Notification(icon, notifyString + " @ " + alertLastUpdated, when);

            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.ag_alert_notify);
            contentView.setImageViewResource(R.id.notification_image, R.drawable.ag_alerts_bump);
            contentView.setTextViewText(R.id.notification_title, "AutoGo Alert!");
            contentView.setTextViewText(R.id.notification_text, getString(R.string.ag_alert_notify_bump) + " @ " + alertLastUpdated );
            notification.contentView = contentView;

            Intent notificationIntent = new Intent(this, AutoGoAlerts.class);
            notificationIntent.putExtra("fromNotify", true);
            //notificationIntent.putExtra("TypeAndDate", new String[] {alertType, alertLastUpdated});
            notificationIntent.putExtra(ALERT_TYPE, alertType.toString());
            notificationIntent.putExtra("alertLastUpdated", alertLastUpdated);

            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            notification.contentIntent = contentIntent;

            notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
            notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration
            notification.defaults |= Notification.DEFAULT_SOUND; // Sound



            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }
    }
}
