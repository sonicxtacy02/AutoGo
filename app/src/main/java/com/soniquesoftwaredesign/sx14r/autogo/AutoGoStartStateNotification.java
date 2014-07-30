package com.soniquesoftwaredesign.sx14r.autogo;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.media.MediaPlayer;
        import android.widget.Toast;

public class AutoGoStartStateNotification extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){

        Toast.makeText(context, "this is my Start Toast message!!! =)", Toast.LENGTH_LONG).show();
        final MediaPlayer mp1 = MediaPlayer.create(context, R.raw.start);
        mp1.start();
    }



}
