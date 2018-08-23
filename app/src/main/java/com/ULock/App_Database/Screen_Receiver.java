package com.ULock.App_Database;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

//Class to lock app open apps under ulock when power button is clicked
public class Screen_Receiver extends BroadcastReceiver {
    Screen_Receiver screen;
    Context context=null;
    private String screenOff;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        this.context=context;
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)||intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
//            Log.e("Screen on","screen on");
            screenOff = "on";

        }
        if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF))
        {
//            Log.e("Screen off","screen off");
            screenOff = "off";
        }

        Intent i = new Intent(context, LockService.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("screen", screenOff);
        context.startService(i);
    }
}