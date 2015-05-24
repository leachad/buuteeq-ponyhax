package location_services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import db.LocalStorage;

/**
 * Created by leachad on 5/20/2015. Will contain
 * static calls to issue thread requests and set
 * different pertinent variables.
 */
public class GPSPlotter {

    private static final int DEFAULT_INTERVAL = 5;
    private static final int TIMESTAMP_MULTIPLIER = 1000;
    private static AlarmManager mAlarmManager = null;
    private static int mIntentInterval = 0;


    /**
     * User passes in a requested interval polling time in seconds as an
     * integer.
     *
     * @param requestedInterval is the polling interval as requested by the user.
     */
    public static void beginManagedLocationRequests(final int requestedInterval, final Context context) {

        if (requestedInterval == 0) {
            issuePendingIntent(DEFAULT_INTERVAL, context);
        } else {
            issuePendingIntent(requestedInterval, context);
        }

    }

    /**
     * Public method to end the managed Location Requests.
     */
    public static void endManagedLocationRequests(final Context theApplicationContext) {
        killAlarmManager(theApplicationContext);
        Toast.makeText(theApplicationContext, "KILLED ALARM MANAGER", Toast.LENGTH_SHORT).show();
    }

    /**
     * Private method that will issue a pending Intent to run the service on a Background
     * thread.
     * @param theIntentInterval is the interval with which the AlarmManager will issue requests. This is determined outside
     *                          of this class by the Network and Power logic.
     */
    private static void issuePendingIntent(final int theIntentInterval, final Context theApplicationContext) {
        PendingIntent intent = PendingIntent.getService(theApplicationContext, 0, new Intent(theApplicationContext, GPSPlotterIntentService.class), 0);

        if (mAlarmManager == null) {
            initializeAlarmManager(theApplicationContext);
            fireAlarmManager(theIntentInterval, intent);
            mIntentInterval = theIntentInterval;

        } else if (mIntentInterval != theIntentInterval) {
            killAlarmManager(theApplicationContext);
            initializeAlarmManager(theApplicationContext);
            fireAlarmManager(theIntentInterval, intent);
            mIntentInterval = theIntentInterval;

        } else {
            fireAlarmManager(theIntentInterval, intent);
            mIntentInterval = theIntentInterval;
        }

    }

    private static void initializeAlarmManager(Context theApplicationContext) {
        mAlarmManager = (AlarmManager) theApplicationContext.getSystemService(Context.ALARM_SERVICE);
    }

    private static void fireAlarmManager(int theIntentInterval, PendingIntent theIntent) {
        long startTime = SystemClock.elapsedRealtime();
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, startTime, theIntentInterval * TIMESTAMP_MULTIPLIER, theIntent);
    }

    private static void killAlarmManager(Context theApplicationContext) {
        PendingIntent intent = PendingIntent.getService(theApplicationContext, 0, new Intent(theApplicationContext, GPSPlotterIntentService.class), 0);
        mAlarmManager = (AlarmManager) theApplicationContext.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.cancel(intent);
    }


    /**
     * This listener allows for several variations on the same basic Location Listener interface.
     * Calling code passes in a Static String identifying the provider.
     *
     * @author leachad
     * @version 5.20.15
     */
    public static class GPSPlotterListener implements LocationListener {

        /** Private field to hold a tag to the current Provider.*/
        public Location mLastLocation;
        public GPSPlotterListener(final String theProvider) {
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
