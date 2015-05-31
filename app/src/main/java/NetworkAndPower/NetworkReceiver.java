package NetworkAndPower;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by eduard_prokhor on 5/20/15.
 */
public class NetworkReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkReceiver";
    private boolean wifiConnected;
    private boolean mobileConnected;

    public NetworkReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive" + intent.getAction().toString());
        boolean isConnected;

        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();

        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected) {
                Log.i(TAG, "@@The active connection is wifi.");
            } else if (mobileConnected) {
                Log.i(TAG, "@@The active connection is mobile.");
            }
        } else {
            Log.i(TAG, "@@No wireless or mobile connection.");
        }

//        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
//        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
//                status == BatteryManager.BATTERY_STATUS_FULL;
//
//        if(isCharging) Log.i("IsThePhoneBeingCharged?", "Heck Yeah");
//
//
//        int chargePlug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
//        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
//        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
//
//        if(usbCharge) Log.i("YOu are hooked up by a ", " @@@@ Damn usb @@@");
//
//        if(acCharge) Log.i("You are hooked up by a ", " @@@ the wall");
    }
}