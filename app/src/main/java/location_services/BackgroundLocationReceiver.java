package location_services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderApi;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.LocalStorage;
import db.User;

public class BackgroundLocationReceiver extends BroadcastReceiver {
    private static final String TAG = "BLocRec: ";
    private static final String ACTION = "background";
    private CoordinateStorageDatabaseHelper mDbHelper;
    private BackgroundMapUpdate mapUpdate;

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


            if (!LocalStorage.getDestroyedState(context)) {
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

}
