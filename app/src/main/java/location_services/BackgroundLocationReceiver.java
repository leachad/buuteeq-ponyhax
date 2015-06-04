/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */
package location_services;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderApi;

import java.util.List;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.User;
import network_power.NetworkChecker;

/**
 * BackgroundLocationReceiver has two main responsibilities. The first responsibility is to
 * receive any background actions issued by the GPSPlotter class. The second responsibility is
 * to listen for System BOOT_COMPLETED actions and generate GPSPlotter services again based on the
 * users preferences before the device was turned off.
 *
 * @author leachad
 * @version 5.30.15
 */

public class BackgroundLocationReceiver extends BroadcastReceiver {
    private static final String TAG = "BLocRec: ";
    private static final String UPLOAD_ERROR_MESSAGE = "Background Service to Upload Coordinates Failed.";
    private CoordinateStorageDatabaseHelper mDbHelper;

    public BackgroundLocationReceiver() {
        //Default, no-arg constructor
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mDbHelper == null) {
            mDbHelper = new CoordinateStorageDatabaseHelper(context);
        }

        if (intent.getAction().matches(GPSPlotter.BACKGROUND_ACTION)) {
            Log.w(TAG, "BLR Received-background");
            Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
            storeLocation(location, context, intent.getStringExtra(User.USER_ID));

        } else if (intent.getAction().matches(Intent.ACTION_BOOT_COMPLETED)) {
            Log.w(TAG, "REBOOT!!!");
            initializeBackgroundServices(context);

        } else if (intent.getAction().matches(GPSPlotter.UPLOAD_ACTION) && verifyConnectivity(context)) {
            Log.w(TAG, "Push to Database. Connected!");
            pushToDatabase(intent.getStringExtra(User.USER_ID));

        } else if (intent.getAction().matches(GPSPlotter.UPLOAD_ACTION) && !verifyConnectivity(context)) {
            Log.w(TAG, "Push to Database. Not Connected!");
            Toast.makeText(context, UPLOAD_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * Private method to determine if the app is in the foreground or not.
     * <p/>
     * NOTE: Because we wanted to implement features for the user including a preference to sample
     * in the foreground or the background, we had to know when the app was in the background or the
     * foreground. Since Android does not have a system service to provide such a value, the below
     * method uses a Deprecated Method call on the Activity manager class. We realize this violates
     * convention and coding principles, but wanted to explicitly state we are aware of this violation.
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
     *
     * @param theLocation is the newly obtained Location
     *                    @param userID is the current UUID
     */
    private void insertCoordinateToDatabase(Location theLocation, String userID) {
        Coordinate current = new Coordinate(theLocation, userID);
        Log.w(TAG, current.toString());
        mDbHelper.insertCoordinate(current);
    }

    /**
     * Private helper method to determine if connectivity exists in the lifecycle of the app.
     * @return isConnected
     *
     */
    private boolean verifyConnectivity(Context theContext) {
        return NetworkChecker.getInstance().isOnInternet(theContext);
    }

    /**
     * Private method used to push the coordinates in the local sqlite database to the
     * WebService database using Async Tasks.
     * @param userID is the UUID
     */
    private void pushToDatabase(String userID) {
        mDbHelper.publishCoordinateBatch(userID);
    }

    /**
     * Private method used to initialize the background services
     * and make sure that everything is running correctly.
     * @param theContext is the current application context.
     */
    private void initializeBackgroundServices(Context theContext) {
        boolean isBackgroundServiceRunning;


        SharedPreferences preferences = theContext.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        if (preferences.contains(User.REQUESTING_BACKGROUND_STATUS)) {
            Log.w(TAG, "Asking shared prefs about background services");
            isBackgroundServiceRunning = preferences.getBoolean(User.REQUESTING_BACKGROUND_STATUS, false);

            if (isBackgroundServiceRunning) {
                Log.w(TAG, "Restarting Background Service on Boot!");
                ComponentName componentName = new ComponentName(theContext.getPackageName(), BackgroundService.class.getName());
                ComponentName service = theContext.startService(new Intent().setComponent(componentName));
                if (service == null) {
                    Log.w(TAG, "Could not start service");
                }

            } else {
                Log.w(TAG, "Background Service wasn't running...");
            }
        }
    }

    /**
     * Private method called upon to store the location in the appropriate structure.
     * If in foreground, the coordinate is stored in the database and in the View using
     * a reference to the GPSPlotter instance. Else, the coordinate is only stored in the Local
     * Database, because there is no view in the foreground of the application lifecycle.
     * @param theLocation is the location obtained from the Parcelable Extra.
     *                    @param theContext is the application context.
     *                                      @param userID is the UUID of current SUer
     */
    private void storeLocation(Location theLocation, Context theContext, String userID) {
        Log.w(TAG, "App is in foreground - " + Boolean.toString(isAppInForeground(theContext)));

        if (isAppInForeground(theContext)) {
            GPSPlotter.getInstance(theContext).updateParentActivity();
            GPSPlotter.getInstance(theContext).addLocationToView(theLocation);
            insertCoordinateToDatabase(theLocation, userID);

        } else {
            insertCoordinateToDatabase(theLocation, userID);
        }
    }


}
