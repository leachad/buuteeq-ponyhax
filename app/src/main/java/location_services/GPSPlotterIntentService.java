package location_services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

/**
 *
 * IntentService to issue a new Request for Location and to obtain the Location
 * update.
 * @author leachad
 * @version 5.20.15
 *
 */
public class GPSPlotterIntentService extends IntentService {

    /** Unique String identifier for Logging purposes.*/
    private final String LOGGING_KEY = GPSPlotterIntentService.this.getClass().getName();


    public GPSPlotterIntentService() {
        super("GPSPlotterIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.w(LOGGING_KEY, "Got an Intent!");

        }
    }


}
