package com.cariboudevs.ccoudoorske;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class Restarter extends BroadcastReceiver {
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
    try{

            Log.i("Broadcast Listened", "Service tried to stop");
            //Toast.makeText(context, "Service restarted " + Notificationsservice.class.getSimpleName(), Toast.LENGTH_SHORT).show();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(new Intent(context, Notificationsservice.class));
            } else {
                context.startService(new Intent(context, Notificationsservice.class));
            }


    } catch (Exception ex) {
        Log.i("",ex.getMessage());
        Log.i("Broadcast Listened", ex.getMessage());

       // Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
    }
}

}