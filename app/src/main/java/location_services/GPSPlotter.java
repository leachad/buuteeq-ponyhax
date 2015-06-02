/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */
package location_services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.LocalStorage;
import db.User;
import uw.buuteeq_ponyhax.app.MyAccount;

/**
 * Created by leachad on 5/20/2015. Will contain
 * static calls to issue thread requests and set
 * different pertinent variables.
 */
public class GPSPlotter implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "GPSPlotter: ";
    private static final int DEFAULT_INTENT_INTERVAL = 60;
    /**
     * Static fields used in both Background and Foreground Location Updates.
     */
    private static GPSPlotter gpsPlotterInstance;
    private ServiceType mServiceType;
    private GoogleApiClient mGoogleApiClient;
    private MyAccount mAccount;
    private static Location mCurrentLocation;
    private static CoordinateStorageDatabaseHelper mDbHelper;
    private static String mUserID;
    private static Context mContext;
    private int mIntentInterval;
    private boolean startTracking;


    private GPSPlotter(Context theContext) {
        initializeInstance();
        initializeFields(theContext);
        buildApiClient();
        connectClient();
    }

    /**
     * Returns an instance of the GPS Plotter.
     */
    public static GPSPlotter getInstance(Context theContext) {

        if (gpsPlotterInstance == null)
            return new GPSPlotter(theContext);
        else
            return gpsPlotterInstance;

    }

    /**
     * Private method to start the Location Updates using the FusedLocation API in .the foreground.
     */
    private void startForegroundUpdates() {
        Log.w(TAG, "Starting foreground updates");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, buildLocationRequest(), getLocationListener());
        startTracking = true;
    }

    /**
     * Private method to start the Location Updates using the FusedLocation API in the background.
     */
    private void startBackgroundUpdates() {
        Log.w(TAG, "Starting background updates");
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, buildLocationRequest(), buildPendingIntent());
        startTracking = true;
    }

    /**
     * Private method to end foreground updates.
     */
    private void endForegroundUpdates() {
        Log.w(TAG, "Ending foreground updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, getLocationListener());
        startTracking = false;
    }

    /**
     * Private method to end background updates.
     */
    private void endBackgroundUpdates() {
        Log.w(TAG, "Ending background updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, buildPendingIntent());
        startTracking = false;
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
     *
     * @return services are installed.
     */
    private boolean googlePlayServicesInstalled() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        return result == ConnectionResult.SUCCESS;
    }

    /**
     * Private method to initialize an instance of the GPS Plotter class.
     */
    private void initializeInstance() {
        gpsPlotterInstance = this;

    }

    /**
     * Private method to initialize the fields of the GPS Plotter class.
     *
     * @param theContext is the application context.
     */
    private void initializeFields(Context theContext) {
        mGoogleApiClient = null;
        mCurrentLocation = null;
        mDbHelper = new CoordinateStorageDatabaseHelper(theContext);
        mUserID = LocalStorage.getUserID(theContext);
        mContext = theContext;
        mAccount = null;
        mIntentInterval = DEFAULT_INTENT_INTERVAL;
    }

    /**
     * Private method to build the Api Client for use with the LocationServices API.
     */
    private synchronized void buildApiClient() {
        Log.w(TAG, "Building Google Api Client...");
        initializeGoogleApiClient();
    }

    /**
     * Private method used to connect the ApiClient to the Api hosted by Google for
     * Accessing Locations.
     */
    private void connectClient() {
        mGoogleApiClient.connect();
    }

    /**
     * Private helper method used to generate a LocationRequest which will be used to handle all location updates
     * within the FusedLocationApi until the Interval is changed.
     *
     * @return locationRequest
     */
    private LocationRequest buildLocationRequest() {
        int dateConversion = 1000;
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(mIntentInterval * dateConversion);
        locationRequest.setFastestInterval((mIntentInterval / 2) * dateConversion);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Log.w(TAG, "Building location request");
        return locationRequest;
    }

    /**
     * Private method used to set the current service type and location requests
     * appropriately. Evaluates the parameter again the current service type.
     *
     * @param theServiceType is the currently requested service type.
     */
    public void modifyServiceType(ServiceType theServiceType) {
        if (!theServiceType.equals(mServiceType) && isRunningLocationUpdates()) {
            mServiceType = theServiceType;
            beginManagedLocationRequests(mIntentInterval, theServiceType, MyAccount.getInstance());
        } else {
            mServiceType = theServiceType;
        }
    }

    /**
     * Private method used to add the coordinates to the display (map and overview).
     *
     * @param location is the current obtained location.
     */
    public void addLocationToView(Location location) {
        if (location != null) {
            Log.w(TAG, "Location obtained is: " + location.toString());
            mCurrentLocation = location;
            Coordinate coord = new Coordinate(mCurrentLocation, mUserID);
            mDbHelper.insertCoordinate(coord);
            mAccount.addCoordinateToList(coord);
            List<Coordinate> list = mAccount.getList();
//        list.add(new Coordinate(mCurrentLocation, mUserID));
            mAccount.fragment.update(mCurrentLocation, list);
        }
    }

    /**
     * Private helper method used to generate a PendingIntent for use when the User requests background service
     * within the FusedLocationApi until the Interval is changed.
     *
     * @return pendingIntent
     */
    private PendingIntent buildPendingIntent() {
        Log.w(TAG, "building pending intent");
        Intent intent = new Intent(mContext, BackgroundLocationReceiver.class);
        intent.setAction("background");
        intent.putExtra(User.USER_ID, mUserID);
        return PendingIntent.getBroadcast(mContext, 0, intent, 0);
    }

    /**
     * Private helper method to return the current location listener to a FusedLocationservices Api
     * call and build it if it does not exists.
     *
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
    public void beginManagedLocationRequests(final int requestedInterval,
                                             ServiceType serviceType, MyAccount theAccount) {
        mIntentInterval = requestedInterval;

        if (mAccount == null)
            mAccount = theAccount;

        if (googlePlayServicesInstalled() && serviceType.equals(ServiceType.FOREGROUND)) {
            startForegroundUpdates();
        } else if (googlePlayServicesInstalled() && serviceType.equals(ServiceType.BACKGROUND)) {
            LocalStorage.putLocationRequestStatus(true, mContext);
            startBackgroundUpdates();

        }


    }

    /**
     * Public method to end the managed Location Requests.
     */
    public void endManagedLocationRequests(final int requestedInterval, ServiceType serviceType) {
        mIntentInterval = requestedInterval;
        if (serviceType.equals(ServiceType.FOREGROUND)) {
            endForegroundUpdates();
        } else if (serviceType.equals(ServiceType.BACKGROUND)) {
            endBackgroundUpdates();
            LocalStorage.putLocationRequestStatus(false, mContext);

        }
    }

    public void changeRequestIntervals(int theInterval, ServiceType theServiceType) {
        mIntentInterval = theInterval;
        if (theServiceType.equals(ServiceType.FOREGROUND)) {
            endForegroundUpdates();
            if (startTracking) {
                startForegroundUpdates();
            }
        } else if (theServiceType.equals(ServiceType.BACKGROUND)) {
            endForegroundUpdates();
            if (startTracking) {
                startBackgroundUpdates();
            }
        }
    }

    public int getInterval() {
        return mIntentInterval;
    }

    /**
     * Public method to determine if the google api client is indeed connected.
     *
     * @return isConnected
     */
    public boolean hasApiClientConnectivity() {
        return mGoogleApiClient.isConnected();
    }

    /**
     * Public method to determine if location updates are currently running in the background.
     *
     * @return isRunningUpdates
     */
    public boolean isRunningLocationUpdates() {
        return LocalStorage.getLocationRequestStatus(mContext);
    }

    /**
     * Public method to update the Parent View if it is currently null.
     */
    public void updateParentActivity() {
        mAccount = MyAccount.getInstance();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.w(TAG, "Connected. Ready to Go!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.w(TAG, "Connection Failed!");
    }

    @Override
    public void onLocationChanged(Location location) {
        addLocationToView(location);

    }

    /**
     * Public Static Class to contain Enumerated Types useful for
     * articulating service preferences from the User's selected background processes.
     */
    public enum ServiceType {
        BACKGROUND, FOREGROUND
    }


}
