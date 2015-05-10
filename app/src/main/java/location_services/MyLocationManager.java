/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package location_services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;

/**
 * Created by BrentYoung on 4/27/15.
 */
public class MyLocationManager {

    public static final String ACTION_LOCATION = "android.intent.action.LOCALE_CHANGED";

    private static final int DEFAULT_INTERVAL = 5000; //1 min

    private static final int DATA_ONLY_DISTANCE = 1000;
    private static final int CONNECTED_WIFI_DISTANCE = 10; //10 meters

    private static final int DATA_ONLY_RATE = 300000; //5 min 300000
    private static final int CONNECTED_WIFI_RATE = 120000; //2 min 120000

    private static MyLocationManager ourInstance;
    private static Context mAppContext;
    private static int minTime;
    private static int minDistance;
    //For Wifi checks
//    private static ConnectivityManager connManager = (ConnectivityManager) mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//    private static NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    private static WifiManager wifi;

    private LocationManager mLocationManager;

    private MyLocationManager(Context appContext) {
        mAppContext = appContext;
        wifi = (WifiManager) mAppContext.getSystemService(Context.WIFI_SERVICE);
        mLocationManager = (LocationManager) mAppContext.getSystemService(Context.LOCATION_SERVICE);
        minTime = DEFAULT_INTERVAL;
    }

    /**
     * Returns an instance of this singleton to the caller.
     *
     * @param context the application context
     * @return the single instance of this class
     */
    public static MyLocationManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new MyLocationManager(context.getApplicationContext());
        }

        return ourInstance;
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    public void startLocationUpdates(LocationListener listener) {
        String provider = LocationManager.GPS_PROVIDER;
        //set polling state using constants
        //WIFI based
//        if (wifi.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
//            minTime = CONNECTED_WIFI_RATE;
//            minDistance = CONNECTED_WIFI_DISTANCE;
//        } else {
//            minTime = DATA_ONLY_RATE;
//            minDistance = DATA_ONLY_DISTANCE;
//        }

        //start updates from location manager
        PendingIntent pi = getLocationPendingIntent(true);

        mLocationManager.requestLocationUpdates(provider, minTime, minDistance, listener);


    }

    public void stopLocationUpdates(LocationListener listener) {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(listener);
            pi.cancel();
        }
    }

    public boolean isTrackingLocation() {
        return getLocationPendingIntent(false) != null;
    }


}
