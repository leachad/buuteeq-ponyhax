package location_services;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderApi;

import java.util.List;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.User;

public class BackgroundLocationReceiver extends BroadcastReceiver {
    private static final String TAG = "BLocRec: ";
    private static final String ACTION = "background";
    private static final int CURRENT_INTERVAL = 60;
    //TODO Current interval needs to be pulled from prefs
    private CoordinateStorageDatabaseHelper mDbHelper;

    public BackgroundLocationReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        if (intent.getAction().matches(ACTION)) {
            Log.w(TAG, "BLR Received-background");
            if (mDbHelper == null) {
                mDbHelper = new CoordinateStorageDatabaseHelper(context);
            }

            Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
            Log.w(TAG, "App is in foreground - " + Boolean.toString(isAppInForeground(context)));

            if (isAppInForeground(context)) {
                GPSPlotter.getInstance().addLocationToView(location);
            } else {
                insertCoordinateToDatabase(location, intent.getStringExtra(User.USER_ID));
            }

        } else if (intent.getAction().matches(Intent.ACTION_BOOT_COMPLETED)) {
            Log.w(TAG, "REBOOT!!!");
            boolean isBackgroundServiceRunning;

            SharedPreferences preferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
            if (preferences.contains(User.REQUESTING_LOCATION)) {
                Log.w(TAG, "Asking shared prefs about background services");
                isBackgroundServiceRunning = preferences.getBoolean(User.REQUESTING_LOCATION, false);

                if (isBackgroundServiceRunning) {
                    Log.w(TAG, "Restarting Background Service on Boot!");
                    Location current = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
                    GPSPlotter gpsPlotter = new GPSPlotter(context, null);

                    while (!gpsPlotter.hasApiClientConnectivity()) {
                        Log.w(TAG, "Waiting for api connectivity");
                    }

                    gpsPlotter.beginManagedLocationRequests(CURRENT_INTERVAL, GPSPlotter.ServiceType.BACKGROUND);

                } else {
                    Log.w(TAG, "Background Service wasn't running...");
                }
            }

        }


    }

    /**
     * Private method to determine if the app is in the foreground or not.
     *
     * @param theApplicationContext is the context of the lifecycle.
     * @return isForeground
     */
    private boolean isAppInForeground(Context theApplicationContext) {
        ActivityManager manager = (ActivityManager) theApplicationContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity.getPackageName().equals(theApplicationContext.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Private helper method to add points to the Coordinate Storage Database Helper.
     * @param theLocation is the newly obtained Location
     *                    @param theUserID is used to construct the Coordinate Object.
     */
    private void insertCoordinateToDatabase(Location theLocation, String theUserID) {
        Coordinate current = new Coordinate(theLocation, theUserID);
        Log.w(TAG, current.toString());
        mDbHelper.insertCoordinate(current);
    }


}
