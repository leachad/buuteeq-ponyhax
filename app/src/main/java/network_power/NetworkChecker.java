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
    public NetworkChecker() {

    }

    public boolean isOnWifi(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWifi != null)
            return mWifi.isConnected();
        else
            return false;
    }

    public boolean isOnNetwork(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetwork = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mNetwork != null)
            return mNetwork.isConnected();
        else
            return false;
    }


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
