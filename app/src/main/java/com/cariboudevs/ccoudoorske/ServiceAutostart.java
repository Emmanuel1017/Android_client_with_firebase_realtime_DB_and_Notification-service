package com.cariboudevs.ccoudoorske;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class ServiceAutostart extends BroadcastReceiver {
    Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            intent = new Intent(context, Notificationsservice.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
                Toast.makeText(context, " CCOUT Started a service ", Toast.LENGTH_LONG).show();
            } else {
                context.startService(intent);
                Toast.makeText(context, " CCOUT Started a service ", Toast.LENGTH_LONG).show();
            }
            Log.i("Autostart", "started");

        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }



}
