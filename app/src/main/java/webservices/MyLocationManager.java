/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package webservices;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by BrentYoung on 4/27/15.
 */
public class MyLocationManager {

    public static final String ACTION_LOCATION = "android.intent.action.LOCALE_CHANGED";

    private static final int DATA_ONLY_DISTANCE = 10;
    private static final int CONNECTED_WIFI_DISTANCE = 1;

    private static final int DATA_ONLY_RATE = 10;
    private static final int CONNECTED_WIFI_RATE = 20;

    private static MyLocationManager ourInstance;
    private static Context mAppContext;
    private LocationManager mLocationManager;

    private static int minTime;
    private static int minDistance;

    //For Wifi checks
    private static ConnectivityManager connManager = (ConnectivityManager) mAppContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    private static NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

    /**
     * Returns an instance of this singleton to the caller.
     * @param context the application context
     * @return the single instance of this class
     */
    public static MyLocationManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new MyLocationManager(context.getApplicationContext());
        }

        //set polling state using constants
        //WIFI based
        if (mWifi.isConnected()) {
            minTime = CONNECTED_WIFI_RATE;
            minDistance = CONNECTED_WIFI_DISTANCE;
        } else {
            minTime = DATA_ONLY_RATE;
            minDistance = DATA_ONLY_DISTANCE;
        }

        return ourInstance;
    }

    private MyLocationManager(Context appContext) {
        mAppContext = appContext;
        mLocationManager = (LocationManager)mAppContext.getSystemService(Context.LOCATION_SERVICE);
    }

    private PendingIntent getLocationPendingIntent(boolean shouldCreate) {
        Intent broadcast = new Intent(ACTION_LOCATION);
        int flags = shouldCreate ? 0 : PendingIntent.FLAG_NO_CREATE;
        return PendingIntent.getBroadcast(mAppContext, 0, broadcast, flags);
    }

    public void startLocationUpdates() {
        String provider = LocationManager.GPS_PROVIDER;

        //start updates from location manager
        PendingIntent pi = getLocationPendingIntent(true);
        mLocationManager.requestLocationUpdates(provider, minTime, minDistance, pi);


    }

    public void stopLocationUpdates() {
        PendingIntent pi = getLocationPendingIntent(false);
        if (pi != null) {
            mLocationManager.removeUpdates(pi);
            pi.cancel();
        }
    }

    public boolean isTrackingLocation() {
        return getLocationPendingIntent(false) != null;
    }


}
