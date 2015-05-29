package NetworkAndPower;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.util.Log;

/**
 * Created by eduard_prokhor on 5/20/15.
 */
public class Receive extends BroadcastReceiver{

    private static final String TAG = "BatteryLevelReceiver";

    public Receive() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive" + intent.getAction().toString());

        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        if(isCharging) Log.i("IsThePhoneBeingCharged?", "Heck Yeah");


        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        if(usbCharge) Log.i("YOu are hooked up by a ", " @@@@ Damn usb @@@");

        if(acCharge) Log.i("You are hooked up by a ", " @@@ the wall");
    }
}