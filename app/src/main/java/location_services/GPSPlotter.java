package location_services;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.LocalStorage;

/**
 * Created by leachad on 5/20/2015. Will contain
 * static calls to issue thread requests and set
 * different pertinent variables.
 */
public class GPSPlotter implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private final String TAG = "GPSPlotter: ";
    private final int TIMESTAMP_MULTIPLIER = 1000;

    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private CoordinateStorageDatabaseHelper mDbHelper;
    private String mUserID;
    private Context mContext;
    private int mIntentInterval;
    private boolean mRequestingLocationUpdates;


    public GPSPlotter(Context theContext) {
        initializeFields(theContext);
        buildApiClient();

        mGoogleApiClient.connect();
    }

    /**
     * Private method to initialize the fields of the GPS Plotter class.
     * @param theContext is the application context.
     */
    private void initializeFields(Context theContext) {
        mGoogleApiClient = null;
        mCurrentLocation = null;
        mDbHelper = new CoordinateStorageDatabaseHelper(theContext);
        mUserID = LocalStorage.getUserID(theContext);
        mContext = theContext;
        mIntentInterval = 0;
        mRequestingLocationUpdates = false;
    }


    /**
     * Private method to build the Api Client for use with the LocationServices API.
     */
    private synchronized void buildApiClient() {
        Log.w(TAG, "Building Google Api Client...");
        initializeGoogleApiClient();
    }

    /**
     * Private helper method used to generate a LocationRequest which will be used to handle all location updates
     * within the FusedLocationApi until the Interval is changed.
     * @return locationRequest
     */
    private LocationRequest buildLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(mIntentInterval * TIMESTAMP_MULTIPLIER);
        locationRequest.setFastestInterval((mIntentInterval / 2) * TIMESTAMP_MULTIPLIER);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    /**
     * Private helper method to return the current location listener to a FusedLocationservices Api
     * call and build it if it does not exists.
     * @return theCurrentLocationListener using the android.gms.location Listener API.
     */
    private LocationListener getLocationListener() {
        return this;
    }

    /**
     * User passes in a requested interval polling time in seconds as an
     * integer.
     *
     * @param requestedInterval is the polling interval as requested by the user.
     */
    public void beginManagedLocationRequests(final int requestedInterval) {
        mIntentInterval = requestedInterval;

        if (googlePlayServicesInstalled()) {
            Log.w(TAG, "Play Services Installed");
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }


    }

    /**
     * Public method to end the managed Location Requests.
     */
    public void endManagedLocationRequests() {

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, getLocationListener());
            mRequestingLocationUpdates = false;
        } else {
            mGoogleApiClient.connect();
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, getLocationListener());
            mRequestingLocationUpdates = false;
        }
    }

    /**
     * Private method to start the Location Updates using the FusedLocation API.
     */
    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, buildLocationRequest(), getLocationListener());
    }

    /**
     * Private helper method to initialize the Google Api Client with the
     * LocationServices Api and Build it for use.
     */
    private void initializeGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    /**
     * Private helper method to determine whether or not GooglePlayServices
     * are installed on the local system.
     * @return services are installed.
     */
    private boolean googlePlayServicesInstalled() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (result != ConnectionResult.SUCCESS) {
            return false;
        } else {
            return true;
        }
    }



    @Override
    public void onConnected(Bundle bundle) {
        Log.w(TAG, "In on Connectd");
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "In on Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(TAG, "Connection Failed!");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.w(TAG, "Location obtained is: " + location.toString());
        mCurrentLocation = location;
        mDbHelper.insertCoordinate(new Coordinate(location, mUserID));
    }

}
