package location_services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import db.LocalStorage;

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
            Log.w(LOGGING_KEY, "Got an Intent! Grabbing coordinate");
            grabLocation();

        }
    }


    /**
     * Private helper method to grab the current location using the GooglePlayServices
     * API.
     *
     */
    private void grabLocation() {
        //TODO Current sample context will be derived from the classes that Ed develops
        //further, this means that argument 1 of get LastKnownLocation will be a method call to the
        //classes that Ed Develops, unless he stores those in Localstorage as well
        Location location = LocalStorage.getLastKnowLocation(LocalStorage.SampleContext.GPS, getApplicationContext());
        Log.w(LOGGING_KEY, location.toString());

    }

}
