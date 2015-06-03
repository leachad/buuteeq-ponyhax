/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */
package location_services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * This IntentService is issued from on Reboot Action Received in the Background Location Receiver.
 * Using this queued intent, the device restarts Location updates if the user previously had
 * updates turned on before the device was shut down.
 *
 * @author leachad
 * @version 6.1.15
 */
public class BackgroundService extends IntentService {
    /**
     * Private static final String to represent a TAG for this class.
     */
    private static final String TAG = BackgroundService.class.getName();

    public BackgroundService() {
        super("BackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.w(TAG, "Intent is not null...");
            GPSPlotter plotter = GPSPlotter.getInstance(getApplicationContext());
            int counter = 0;

            while (!plotter.hasApiClientConnectivity()) {

                if (counter == 0) {
                    Log.w(TAG, "Plotter does not have api connectivity.");
                    counter++;
                }
            }

            Log.w(TAG, "Plotter is connected-" + Boolean.toString(plotter.hasApiClientConnectivity()));
            plotter.beginManagedLocationRequests(null);
        }
    }
}
