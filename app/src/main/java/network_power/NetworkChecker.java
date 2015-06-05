package network_power;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Huy on 6/3/2015.
 */
public class NetworkChecker {
    private static NetworkChecker instance = null;

    /**
     * Protected constructor for singleton design
     */
    protected NetworkChecker() {

    }

    /**
     * Method to check whether the device is on Wifi.
     *
     * @param context The current context of the application.
     * @return A boolean whether the device is connected to the Wifi or not.
     */
    public boolean isOnWifi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi != null)
            return mWifi.isConnected();
        else
            return false;
    }

    /**
     * Method that tells you whether the device is on mobile data.
     *
     * @param context The current application context.
     * @return A boolean whether the device is connected to mobile data or not.
     */
    public boolean isOnNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mNetwork != null)
            return mNetwork.isConnected();
        else
            return false;
    }


    /**
     * A method that tells you whether the device is connected to the internet.
     *
     * @param context The current application context.
     * @return A boolean. True, if the device is connected to the internet, otherwise false.
     */
    public boolean isOnInternet(Context context) {
        return isOnWifi(context) || isOnNetwork(context);
    }

    public static NetworkChecker getInstance() {
        if (instance == null) {
            instance = new NetworkChecker();
        }

        return instance;
    }
}
