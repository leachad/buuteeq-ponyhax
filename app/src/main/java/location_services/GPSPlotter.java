package location_services;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

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

    private GoogleApiClient mGoogleApiClient = null;
    private FusedLocationListener mLocationListener = null;
    private int mIntentInterval = 0;
    private boolean mRequestingLocationUpdates = false;
    private Location mCurrentLocation = null;
    private CoordinateStorageDatabaseHelper mDbHelper;

    public GPSPlotter(Context theContext) {
        buildApiClient();
        initializeGoogleApiClient(theContext);
        mGoogleApiClient.connect();
    }

    /**
     * Private method to build the Api Client for use with the LocationServices API.
     */
    private synchronized void buildApiClient() {
        Log.w(TAG, "Building Google Api Client...");
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
    public void beginManagedLocationRequests(final int requestedInterval, final Context context) {
        //Start the db Helper
        mDbHelper = new CoordinateStorageDatabaseHelper(context);
        mIntentInterval = requestedInterval;
        if (googlePlayServicesInstalled(context)) {
            Log.w(TAG, "Play Services Installed");
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }


    }

    private void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, buildLocationRequest(), getLocationListener());
    }

    private void initializeGoogleApiClient(Context theContext) {
        mGoogleApiClient = new GoogleApiClient.Builder(theContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }

    private boolean googlePlayServicesInstalled(Context context) {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (result != ConnectionResult.SUCCESS) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Public method to end the managed Location Requests.
     */
    public void endManagedLocationRequests(final Context theApplicationContext) {

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, getLocationListener());
            mRequestingLocationUpdates = false;
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
        Log.w(TAG, "In on Connection Failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.w(TAG, "Location changed");
        mCurrentLocation = location;
    }

    /**
     * Private class to implement a FusedLocation Listener.
     *
     * @author leachad
     * @version 5.26.15
     */
    private class FusedLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.w(TAG, location.toString());
            mCurrentLocation = location;
        }
    }


    /**
     * Private class to implement a ConnectionCallback Listener.
     *
     * @author leachad
     * @version 5.26.15
     */
    private class LocationCallbackListener implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(Bundle bundle) {
            Log.w(TAG, "In on Connected");
            if (mRequestingLocationUpdates) {
                mGoogleApiClient.connect();
                startLocationUpdates();
            }
        }

        @Override
        public void onConnectionSuspended(int i) {
            mGoogleApiClient.connect();
        }
    }

    /**
     * Private class to implement an OnConnectionFailedListener.
     *
     * @author leachad
     * @version 5.26.15
     */
    private class LocationFailedListener implements GoogleApiClient.OnConnectionFailedListener {

        /**
         * @param connectionResult
         */
        @Override
        public synchronized void onConnectionFailed(ConnectionResult connectionResult) {
            Log.i(TAG, "Connection to Api Client Failed -- " + connectionResult.getErrorCode());
        }
    }

}
