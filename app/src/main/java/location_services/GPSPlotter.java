package location_services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;

import android.location.LocationListener;
import android.util.Log;

/**
 * Created by leachad on 5/20/2015. Will contain
 * static calls to issue thread requests and set
 * different pertinent variables.
 */
public class GPSPlotter {

    private static final int DEFAULT_INTERVAL = 5;
    private static final int TIMESTAMP_MULTIPLIER = 1000;


    private static AlarmManager mAlarmManager = null;
    private static GPSPlotterListener[] networkListeners = null;
    private static LocationManager mLocationManager = null;
    //TODO Once network services are up and running, the call will hopefully be
    //something more like this:
    //private static GPSPlotterListener[] networkListeners = NetworkServices.getNetworkVariations();
    //Returns an int or even better an array of listeners, or at the least an array of strings that
    //Can be passed to create a series of listeners.

    /**
     * If the start Service Intent method is called without a parameter
     * the DEFAULT_INTERVAL will be utilized.
     */
    public static void beginManagedLocationRequests(final Context context) {
        issuePendingIntent(DEFAULT_INTERVAL, context);
    }

    /**
     * User passes in a requested interval polling time in seconds as an
     * integer.
     *
     * @param requestedInterval is the polling interval as requested by the user.
     */
    public static void beginManagedLocationRequests(final int requestedInterval, final Context context) {
        issuePendingIntent(requestedInterval, context);

    }

    /**
     * Public method to end the managed Location Requests.
     */
    public static void endManagedLocationRequests(final Context theApplicationContext) {
        PendingIntent intent = PendingIntent.getService(theApplicationContext, 0, new Intent(theApplicationContext, GPSPlotterIntentService.class), 0);
        mAlarmManager = (AlarmManager) theApplicationContext.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.cancel(intent);
    }

    /**
     * Private method that will issue a pending Intent to run the service on a Background
     * thread.
     * @param theIntentInterval is the interval with which the AlarmManager will issue requests.
     */
    private static void issuePendingIntent(final int theIntentInterval, final Context theApplicationContext) {

        PendingIntent intent = PendingIntent.getService(theApplicationContext, 0, new Intent(theApplicationContext, GPSPlotterIntentService.class), 0);
        long startTime = SystemClock.elapsedRealtime();
        mAlarmManager = (AlarmManager) theApplicationContext.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, startTime, theIntentInterval * TIMESTAMP_MULTIPLIER, intent);

    }

    /**
     * Helper method used to initialize the location manager for "grabbing" locations
     * using either the network or the gps of the device.
     */
    private static void initializeLocationManager(final Context theApplicationContext) {
        mLocationManager = (LocationManager) theApplicationContext.getSystemService(Context.LOCATION_SERVICE);
    }


    /**
     * Private class to issue a PendingIntent that will query the correct GPSPlotterListener
     * depending upon the LocationServices that are availble.
     *
     * @author leachad
     * @version 5.20.15
     */
    public class CoordinateIntent extends IntentService {

        /**
         * Creates an IntentService.  Invoked by your subclass's constructor.
         *
         * @param name Used to name the worker thread, important only for debugging.
         */
        public CoordinateIntent(String name) {
            super(name);
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, (float) 0, networkListeners[0]);
            Log.w("INNER PRIVATE INTENT:", intent.toString());
            Log.w("INNER PRIVATE INTENT:", networkListeners[0].mLastLocation.toString());

        }

    }

    /**
     * This listener allows for several variations on the same basic Location Listener interface.
     * Calling code passes in a Static String identifying the provider.
     *
     * @author leachad
     * @version 5.20.15
     */
    private static class GPSPlotterListener implements LocationListener {

        /** Private field to hold a tag to the current Provider.*/
        public Location mLastLocation;
        private GPSPlotterListener(final String theProvider) {
            Log.e(GPSPlotter.class.getName(), "LocationListener " + theProvider);
            mLastLocation = new Location(theProvider);
        }
        @Override
        public void onLocationChanged(Location location) {
            Log.e(GPSPlotter.class.getName(), "onLocationChanged: " + location);
            mLastLocation.set(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(GPSPlotter.class.getName(), "onStatusChanged: " + provider + ", " + status);

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(GPSPlotter.class.getName(), "onProviderEnabled: " + provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e(GPSPlotter.class.getName(), "onProviderDisabled: " + provider);
        }
    }


}
