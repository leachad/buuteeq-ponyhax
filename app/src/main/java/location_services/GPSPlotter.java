package location_services;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.LocalStorage;
import uw.buuteeq_ponyhax.app.MyAccount;

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
    private MyAccount mParentActivity;
    private int mIntentInterval;
    private boolean mRequestingForegroundUpdates;
    private boolean mRequestingBackgroundUpdates;


    public GPSPlotter(Context theContext, MyAccount theParentActivity) {
        initializeFields(theContext, theParentActivity);
        buildApiClient();
        mGoogleApiClient.connect(); //This connect is issued early to establish connection.
    }

    /**
     * Private method to initialize the fields of the GPS Plotter class.
     * @param theContext is the application context.
     */
    private void initializeFields(Context theContext, MyAccount theParentActivity) {
        mGoogleApiClient = null;
        mCurrentLocation = null;
        mDbHelper = new CoordinateStorageDatabaseHelper(theContext);
        mUserID = LocalStorage.getUserID(theContext);
        mContext = theContext;
        mParentActivity = theParentActivity;
        mIntentInterval = 0;
        mRequestingForegroundUpdates = false;
        mRequestingBackgroundUpdates = false;
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
     * Private helper method used to generate a PendingIntent for use when the User requests background service
     * within the FusedLocationApi until the Interval is changed.
     * @return pendingIntent
     */
    private PendingIntent buildPendingIntent() {
        return PendingIntent.getService(mContext, 1, new Intent(mContext, GPSService.class), 0);
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
    public void beginManagedLocationRequests(final int requestedInterval, ServiceType serviceType) {
        mIntentInterval = requestedInterval;

        if (googlePlayServicesInstalled() && serviceType.equals(ServiceType.FOREGROUND)) {
            mRequestingForegroundUpdates = true;
            startForegroundUpdates();
        } else if (googlePlayServicesInstalled() && serviceType.equals(ServiceType.BACKGROUND)) {
            mRequestingBackgroundUpdates = true;
            startBackgroundUpdates();
        } else {
            Log.w(TAG, "Google Play Services unavailable");
        }


    }

    /**
     * Public method to end the managed Location Requests.
     */
    public void endManagedLocationRequests() {

        //TODO Implement variation requests depending on whether
        // or not the service is background or foreground

        if (mGoogleApiClient.isConnected() && mRequestingForegroundUpdates) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, getLocationListener());
            mRequestingForegroundUpdates = false;
        } else if (mGoogleApiClient.isConnected() && mRequestingBackgroundUpdates) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, buildPendingIntent());
            mRequestingBackgroundUpdates = false;

        }
    }

    /**
     * Private method to start the Location Updates using the FusedLocation API in .the foreground.
     */
    private void startForegroundUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, buildLocationRequest(), getLocationListener());
    }

    /**
     * Private method to start the Location Updates using the FusedLocation API in the background.
     */
    private void startBackgroundUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, buildLocationRequest(), buildPendingIntent());
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
        Log.w(TAG, "Connected. Ready to Go!");
        if (mRequestingForegroundUpdates) {
            //Do something here
        } else if (mRequestingBackgroundUpdates) {
            //Construct a Pending intent...
            //TODO This logic should probably fired by another method rather than wait
            //for onConnected to trigger.
        }
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
        Log.w(TAG, "Location obtained is: " + location.toString());
        mCurrentLocation = location;
        mDbHelper.insertCoordinate(new Coordinate(mCurrentLocation, mUserID));
        mParentActivity.addCoordinateToList(new Coordinate(mCurrentLocation, mUserID));
        List<Coordinate> list = mParentActivity.getList();
        list.add(new Coordinate(mCurrentLocation, mUserID));
        mParentActivity.fragment.update(mCurrentLocation, list);

    }

    /**
     * Public class to create an IntentService that will run in a background thread.
     * @author leachad
     * @version 5.27.15
     *
     */
    public static class GPSService extends IntentService {

        /**
         * Creates an IntentService.  Invoked by your subclass's constructor.
         *
         *  Used to name the worker thread, important only for debugging.
         */
        public GPSService() {
            super(GPSService.class.getName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            Log.w("GPS SERVICE", "I heard an Intent!");
            Location current = intent.getParcelableExtra(FusedLocationProviderApi.KEY_LOCATION_CHANGED);
            if (current != null) {
                Log.w("GPS SERVICE" + "cur-Loc:", current.toString());
            }
        }
    }

    /**
     * Public Static Class to contain Enumerated Types useful for
     * articulating service preferences from the User's selected background processes.
     */
    public enum ServiceType {
        BACKGROUND, FOREGROUND;
    }

}
