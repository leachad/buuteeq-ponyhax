package NetworkAndPower;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by eduard_prokhor on 5/20/15.
 */
public class Receive extends BroadcastReceiver{

//    1) To check for connectivity, use the Android's CONNECTIVITY_SERVICE.
//    mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

//    2) Check if network is available or not.
//            NetworkInfo activeNetwork = mConnectivityManager.getActiveNetworkInfo();
//    boolean isConnected = activeNetwork != null &&
//            activeNetwork.isConnectedOrConnecting();
//    Log.i(TAG, "Network connectivity: " + Boolean.toString(isConnected));

//    Example at https://github.com/mmuppa/BatteryAndNetworkManagementExample (Links to an external site.)
//
//
//
//    Sources:
//
//    https://developer.android.com/reference/android/os/BatteryManager.html (Links to an external site.)  (Links to an external site.)
//
//    https://developer.android.com/training/monitoring-device-state/index.html (Links to an external site.)

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
