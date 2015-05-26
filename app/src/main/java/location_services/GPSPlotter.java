package location_services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import db.CoordinateStorageDatabaseHelper;
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
    private static GoogleApiClient mGoogleApiClient = null;
    private static int mIntentInterval = 0;
    private static Location mCurrentLocation = null;
    private static CoordinateStorageDatabaseHelper mDbHelper;


    /**
     * Private method used to display the current location to the User.
     */
    private static void displayCurrentLocation() {
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation == null) {
            Log.w("GPSPlotter: ", "bogus coordinate");
        } else {
            Log.w("GPSPlotter: ", mLastLocation.toString());
        }
    }

    /**
     * User passes in a requested interval polling time in seconds as an
     * integer.
     *
     * @param requestedInterval is the polling interval as requested by the user.
     */
    public static void beginManagedLocationRequests(final LocalStorage.ProviderType theProvider, final int requestedInterval, final Context context) {
        //Start the db Helper
        mDbHelper = new CoordinateStorageDatabaseHelper(context);

        if (googlePlayServicesInstalled(context) && mGoogleApiClient == null) {
            Log.w("GPSPlotter: ", "Play Services Installed");
            initializeGoogleApiClient(context);
        }

        issuePendingIntent(requestedInterval, context);


    }

    private static void initializeGoogleApiClient(Context theContext) {
        mGoogleApiClient = new GoogleApiClient.Builder(theContext)
                .addConnectionCallbacks(new LocationCallbackListener())
                .addOnConnectionFailedListener(new LocationFailedListener())
                .addApi(LocationServices.API).build();
    }

    private static boolean googlePlayServicesInstalled(Context context) {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (result != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(result)) {
                Log.w("GOOGLEPLAY:", "error connecting");
            } else {
                Log.w("GOOGLEPLAY:", "device not supported");
            }
            return false;
        }
        return true;
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
     *
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
     * Private class to implement a ConnectionCallback Listener.
     *
     * @author leachad
     * @version 5.26.15
     */
    private static class LocationCallbackListener implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(Bundle bundle) {
            displayCurrentLocation();
        }

        @Override
        public void onConnectionSuspended(int i) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Private class to implement an OnConnectionFailedListener.
     *
     * @author leachad
     * @version 5.26.15
     */
    private static class LocationFailedListener implements GoogleApiClient.OnConnectionFailedListener {

        /**
         * @param connectionResult
         */
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.i("GPSPlotter: ", "Connection to Api Client Failed -- " + connectionResult.getErrorCode());
        }
    }


    /**
     * IntentService to issue a new Request for Location and to obtain the Location
     * update.
     *
     * @author leachad
     * @version 5.20.15
     */
    public static class GPSPlotterIntentService extends IntentService {

        /**
         * Unique String identifier for Logging purposes.
         */
        private final String LOGGING_KEY = GPSPlotterIntentService.this.getClass().getName();

        public GPSPlotterIntentService() {
            super("GPSPlotterIntentService");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            if (intent != null) {
                Log.w(LOGGING_KEY, "Got an Intent! Grabbing coordinate");
                grabLocation();

            }
        }


        /**
         * Private helper method to grab the current location using the GooglePlayServices
         * API.
         */
        private void grabLocation() {
            Log.w(LOGGING_KEY, "Going to grab location");
            /**
             * TODO This method will contain different conditionals based on the values that the Network and
             * Power class obtains for use in sampling.
             */


            displayCurrentLocation();

        }
    }


}
