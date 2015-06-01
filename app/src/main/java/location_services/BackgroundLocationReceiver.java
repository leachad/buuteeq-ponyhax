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
    private CoordinateStorageDatabaseHelper mDbHelper;
    private SharedPreferences mLocalStorage;
    private boolean isAlive = true;

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


            mLocalStorage = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);


            Location location = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);

            Log.w(TAG, "App is in foreground - " + Boolean.toString(isAppInForeground(context)));
            //TODO do a not check here
            if (isAppInForeground(context)) {
                GPSPlotter.getInstance().addLocationToView(location);
            } else {
                Coordinate current = new Coordinate(location, intent.getStringExtra(User.USER_ID));
                Log.w(TAG, current.toString());
                mDbHelper.insertCoordinate(current);
            }

        } else {
            //TODO Something pertinent to a wake from boot operation
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


}
