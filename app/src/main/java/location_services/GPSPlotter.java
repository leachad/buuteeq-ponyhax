/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */
package location_services;

import android.app.AlarmManager;
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

    public static final String UPLOAD_ACTION = "upload";
    public static final String BACKGROUND_ACTION = "background";
    private static final String TAG = "GPSPlotter: ";
    private static final int DEFAULT_INTENT_INTERVAL = 60;
    private static final int ALARM_REGISTER_BUFFER = 60000;
    /**
     * Static fields used in both Background and Foreground Location Updates.
     */
    private static GPSPlotter gpsPlotterInstance;
    private ServiceType mCurrentServiceType;
    private GoogleApiClient mGoogleApiClient;
    private MyAccount mAccount;
    private static Location mCurrentLocation;
    private static CoordinateStorageDatabaseHelper mDbHelper;
    private static AlarmManager mAlarmManager;
    private static String mUserID;
    private static Context mContext;
    private int mIntentInterval;


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
        mCurrentServiceType = ServiceType.BACKGROUND;
    }


    /**
     * Private method to initialize an instance of the GPS Plotter class.
     */
    private void initializeInstance() {
        gpsPlotterInstance = this;

    }

    /***********************************GOOGLE API CLIENT METHODS*********************************/

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

    /***********************************UPLOAD PROCESSES AND INTENTS********************************/

    /**
     * Private method to create a pending intent for issuing alarm manager requests.
     *
     * @param theIntent is the original intent.
     * @return thePendingIntent
     */
    private PendingIntent buildUploadPendingIntent(Intent theIntent) {
        return PendingIntent.getBroadcast(mContext, 0, theIntent, 0);
    }

    /**
     * Private method to create an intent for issuing alarm manager requests.
     *
     * @return theIntent
     */
    private Intent buildUploadIntent() {
        Intent theIntent = new Intent(mContext, BackgroundLocationReceiver.class);
        theIntent.setAction(UPLOAD_ACTION);
        return theIntent;
    }

    /**
     * Private method to register an instance of an AlarmManager that will issue uploads to the
     * WebService intermittently. Default duration is one hour. Akarm manager waits one minute
     * from the current time before issuing the request to the background services for firing
     * points to the database.
     */
    private void registerAlarmManager() {
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                (System.currentTimeMillis() + ALARM_REGISTER_BUFFER),
                AlarmManager.INTERVAL_HOUR, buildUploadPendingIntent(buildUploadIntent()));
    }

    /**
     * Private method used to cancel the alarm manager in the case that a background point service
     * or a foreground point service are disabled.
     */
    private void unregisterAlarmManager() {
        if (mAlarmManager != null)
            mAlarmManager.cancel(buildUploadPendingIntent(buildUploadIntent()));
    }

    /*****************************************LOCATION SERVICE REQUESTS****************************/

    /**
     * User passes in a requested interval polling time in seconds as an
     * integer.
     *
     * @param theAccount is a reference to the parent activity used for updating views.
     */
    public void beginManagedLocationRequests(MyAccount theAccount) {
        if (mAccount == null)
            mAccount = theAccount;

        startBackgroundUpdates();

    }

    /**
     * Public method to end the managed Location Requests.
     */
    public void endManagedLocationRequests() {
            endBackgroundUpdates();

    }

    /**
     * This method handles the switch in polling rates by stopping and then starting once more the
     * background udpates, which in turn sets the interval in another method in the call stack.
     * @param theInterval the desired interval polling rate
     */
    public void changeRequestIntervals(int theInterval) {
        mIntentInterval = theInterval;
        if (LocalStorage.getRequestingBackgroundStatus(mContext)) {
            endBackgroundUpdates();
            startBackgroundUpdates();
        }



    }

    /**
     * Private helper method to build an Intent that will be couple with a pending intent uses
     * for issuing background Location requests.
     *
     * @return theIntent
     */
    private Intent buildBackgroundRequestIntent() {
        Intent intent = new Intent(mContext, BackgroundLocationReceiver.class);
        intent.setAction(BACKGROUND_ACTION);
        intent.putExtra(User.USER_ID, mUserID);
        return intent;
    }

    /**
     * Private helper method used to generate a PendingIntent for use when the User requests background service
     * within the FusedLocationApi until the Interval is changed.
     *
     * @return pendingIntent
     */
    private PendingIntent buildRequestPendingIntent(Intent theIntent) {
        Log.w(TAG, "building pending intent");
        return PendingIntent.getBroadcast(mContext, 0, theIntent, 0);
    }



    /**
     * Private method to start the Location Updates using the FusedLocation API in the background.
     */
    private void startBackgroundUpdates() {
        Log.w(TAG, "Starting background updates");
        if (googlePlayServicesInstalled()) {
            LocalStorage.putBackgroundRequestStatus(true, mContext);
            LocalStorage.putLocationRequestStatus(true, mContext);
            registerAlarmManager();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, buildLocationRequest(), buildRequestPendingIntent(buildBackgroundRequestIntent()));
        }
    }


    /**
     * Private method to end background updates.
     */
    private void endBackgroundUpdates() {
        Log.w(TAG, "Ending background updates");
        LocalStorage.putBackgroundRequestStatus(false, mContext);
        LocalStorage.putLocationRequestStatus(false, mContext);
        unregisterAlarmManager();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, buildRequestPendingIntent(buildBackgroundRequestIntent()));
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
     * Private method used to add the coordinates to the display (map and overview).
     * DOES NOT HANDLE BACKGROUND STORAGE...DB Helper, etc.
     *
     * @param location is the current obtained location.
     */
    public void addLocationToView(Location location) {
        Coordinate currentCoordinate = null;
        if (location != null) {
            Log.w(TAG, "Location obtained is: " + location.toString());
            mCurrentLocation = location;
            currentCoordinate = new Coordinate(mCurrentLocation, mUserID);
            mAccount.addCoordinateToList(currentCoordinate);
            List<Coordinate> list = mAccount.getList();
            mAccount.fragment.update(mCurrentLocation, list);
        }

        if (mCurrentServiceType.equals(ServiceType.FOREGROUND))
            mDbHelper.insertCoordinate(currentCoordinate);
    }


    /******************************GETTERS FOR VARIOUS INTEGRAL DATA CHECKS************************/


    /**
     * Public method to return the Service Type currently being used to the calling class.
     *
     * @return mServiceType
     */
    public ServiceType getServiceType() {
        return mCurrentServiceType;
    }

    /**
     * Public method to return the current interval within the GPSPlotter class.
     *
     * @return mIntentInterval
     */
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
     * Public method to determine if location updates are currently running.
     *
     * @return isRunningUpdates
     */
    public boolean isRunningLocationUpdates() {
        return LocalStorage.getLocationRequestStatus(mContext);
    }

    /**
     * Public method to determine if location updates are currently running in the
     * background.
     *
     * @return isRunningBackgroundUpdates
     */
    public boolean isRunningBackgroundLocationUpdates() {
        return LocalStorage.getRequestingBackgroundStatus(mContext);
    }

    /**
     * Public method to update the Parent View if it is currently null.
     */
    public void updateParentActivity() {
        mAccount = MyAccount.getInstance();
    }

    /**
     * *****************************OVERRIDDEN METHODS FROM INTERFACES IMPLEMENTED***************
     */

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
