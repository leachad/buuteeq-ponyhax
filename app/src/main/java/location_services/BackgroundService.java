package location_services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
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
            plotter.beginManagedLocationRequests(60, GPSPlotter.ServiceType.BACKGROUND, null);
        }
    }
}
