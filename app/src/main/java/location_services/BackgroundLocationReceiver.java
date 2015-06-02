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

import com.google.android.gms.location.FusedLocationProviderApi;

import java.util.List;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.User;

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
    private static final String ACTION = "background";
    private CoordinateStorageDatabaseHelper mDbHelper;

    public BackgroundLocationReceiver() {
        //Default, no-arg constructor
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().matches(ACTION)) {
            Log.w(TAG, "BLR Received-background");

            if (mDbHelper == null) {
                mDbHelper = new CoordinateStorageDatabaseHelper(context);
            }

            Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
            Log.w(TAG, "App is in foreground - " + Boolean.toString(isAppInForeground(context)));

            if (isAppInForeground(context)) {
                GPSPlotter.getInstance(context).updateParentActivity();
                GPSPlotter.getInstance(context).addLocationToView(location);

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
                    ComponentName componentName = new ComponentName(context.getPackageName(), BackgroundService.class.getName());
                    ComponentName service = context.startService(new Intent().setComponent(componentName));

                    if (service == null) {
                        Log.w(TAG, "Could not start service");
                    }

                } else {
                    Log.w(TAG, "Background Service wasn't running...");
                }
            }

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
     * @param theUserID   is used to construct the Coordinate Object.
     */
    private void insertCoordinateToDatabase(Location theLocation, String theUserID) {
        Coordinate current = new Coordinate(theLocation, theUserID);
        Log.w(TAG, current.toString());
        mDbHelper.insertCoordinate(current);
    }


}
