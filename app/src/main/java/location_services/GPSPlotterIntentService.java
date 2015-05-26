package location_services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
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
        Log.w(LOGGING_KEY, "Going to grab location");
        /**
         * TODO This method will contain different conditionals based on the values that the Network and
         * Power class obtains for use in sampling.
         */
        Location current = LocalStorage.getLastKnowLocation(LocalStorage.ProviderType.GPS, getApplicationContext());
        if (current == null) {
            Log.w(LOGGING_KEY, "Location was NULL");
        } else {
            Log.w(LOGGING_KEY, new Coordinate(current, LocalStorage.getUserID(getApplicationContext())).toString());
            Toast.makeText(getApplicationContext(), new Coordinate(current, LocalStorage.getUserID(getApplicationContext())).toString(), Toast.LENGTH_SHORT).show();
            new CoordinateStorageDatabaseHelper(getApplicationContext()).insertCoordinate(new Coordinate(current, LocalStorage.getUserID(getApplicationContext())));
        }
    }
}
