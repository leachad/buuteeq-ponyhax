/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */
package network_power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Created by eduard_prokhor on 5/20/15.
 */
public class PowerReceiver extends BroadcastReceiver {

    private static final String TAG = "BatteryLevelReceiver";

    public PowerReceiver() {

    }

    /**
     * This method handles any incoming broadcasts intended for power management. This can be used to
     * affect polling rates and the rate at which coordinates are pushed up to the web services.
     * @param context application context
     * @param intent the incoming intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive" + intent.getAction());

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        if (isCharging) Log.i("IsThePhoneBeingCharged?", "Heck Yeah");


        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if (usbCharge) Log.i("YOu are hooked up by a ", " @@@@ Damn usb @@@");

        if (acCharge) Log.i("You are hooked up by a ", " @@@ the wall");
    }
}