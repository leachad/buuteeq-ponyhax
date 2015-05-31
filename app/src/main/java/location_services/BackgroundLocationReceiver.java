package location_services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import db.CoordinateStorageDatabaseHelper;

public class BackgroundLocationReceiver extends BroadcastReceiver {
    private static final String TAG = "BLocRec: ";
    private CoordinateStorageDatabaseHelper mDbHelper;

    public BackgroundLocationReceiver() {

    }
    public BackgroundLocationReceiver(Context theContext) {
        mDbHelper = new CoordinateStorageDatabaseHelper(theContext);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving

        Log.w(TAG, "BLR Received");
    }
}
