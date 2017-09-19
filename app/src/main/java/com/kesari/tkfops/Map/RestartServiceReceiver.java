package com.kesari.tkfops.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kesari.tkfops.network.IOUtils;

/**
 * Created by kesari on 19/09/17.
 */

public class RestartServiceReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!IOUtils.isServiceRunning(LocationServiceNew.class, context)) {
            // LOCATION SERVICE
            context.startService(new Intent(context, LocationServiceNew.class));
            Log.e("SERVICE STARTED", "Location service is already running");
        }
    }

}