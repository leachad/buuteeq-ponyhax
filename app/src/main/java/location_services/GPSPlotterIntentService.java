package location_services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

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

    /** Holds a reference to the GoogleAPIClient.*/
    private GoogleApiClient mApiClient = null;


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
        LocationManager manager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //Drop in conditional cases here for the GPS Provider, or possibly keep a reference to the LocationManager
        //in local Storage TODO
        Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.w(LOGGING_KEY, location.toString());

    }

    /**
     * Private helper method to determine if the GooglePlay Services connection
     * is valid, established and ready for use.
     * @return googlePlayIsConnected
     */
    private boolean googlePlayServicesConnected() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) == ConnectionResult.SUCCESS;
    }





}
