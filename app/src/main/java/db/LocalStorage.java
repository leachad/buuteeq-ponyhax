/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package db;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;

import java.util.Calendar;

/**
 * Created by leachad on 5/10/2015. Used to statically access and
 * publish shared preferences. Consolidates SharedPreferences behavior
 * in the LocalStorage class to aid with troubleshooting class.
 */
public class LocalStorage {

    /**
     * Default variables for the LocationRequests.
     */
    public static final int DEFAULT_INTERVAL = 60;
    public static final int DEFAULT_MIN_DISTANCE = 0;
    public static final int TIMESTAMP_MULTIPLIER = 1000;

    /**
     * Private field to hold a reference to the LocationManager and its applicable data types
     * shared by the Lifecycle of the application.
     */
    private static LocationManager mLocationManager = null;
    private static ProviderType mProvider = null;
    private static Location mCurrentLocation = null;

    /**
     * Public static method to put the UserID into the prefs.
     *
     * @param theUserID is the UserID as passed from the calling code.
     * @param context   is the application context within the lifecycle.
     */
    public static void putUserId(String theUserID, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(User.USER_ID, theUserID).apply();
    }

    /**
     * Public static method to put the UserEmail in the prefs.
     *
     * @param theEmail is theUserEmail as passed from the calling code.
     * @param context  is the application context within the lifecycle.
     */
    public static void putUserEmail(String theEmail, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(User.USER_EMAIL, theEmail).apply();
    }

    /**
     * Public static method to put the StartTime into the prefs.
     *
     * @param theStartTime is the StartTime as set by the calling code for the date range of points.
     * @param context      is the application context within the lifecycle.
     */
    public static void putStartTime(long theStartTime, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(User.START_TIME, theStartTime).apply();
    }

    /**
     * Public static method to put the EndTime into the prefs.
     *
     * @param theEndTime is the EndTime as set by the calling code for the date range of points.
     * @param context    is the application context within the lifecycle.
     */
    public static void putEndTime(long theEndTime, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(User.END_TIME, theEndTime).apply();
    }

    /**
     * Public static method to put the User agreement into the prefs.
     *
     * @param theAgreement is the User agreement obtained from the WebServices.
     * @param context      is the application context within the lifecycle.
     */
    public static void putUserAgreement(String theAgreement, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(User.USER_AGREEMENT, theAgreement).apply();
    }

    /**
     * Public static method to set the DB status flag in the prefs.
     *
     * @param theFlag is the boolean passed from the calling code.
     * @param context is the application context within the lifecycle.
     */
    public static void putDBFlag(boolean theFlag, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(User.DB_FLAG, theFlag).apply();
    }

    /**
     * Public static method to set the Location Request status flag in the prefs.
     *
     * @param theFlag is the boolean passed from the calling code.
     * @param context is the application context within the lifecycle.
     */
    public static void putLocationRequestStatus(boolean theFlag, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(User.REQUESTING_LOCATION, theFlag).apply();
    }


    /**
     * Public static method to set the sampling rate selected by the user (Between 10 and 300 seconds)
     *
     * @param theSamplingRate is the user selected sampling rate.
     * @param context         is the application context within the lifecycle.
     */
    public static void putSamplingRate(int theSamplingRate, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(User.SAMPLE_RATE, theSamplingRate).apply();
    }

    /**
     * Public static method to set the upload rate selected by the user.
     *
     * @param theUploadRate is the user selected UploadRate enum.
     * @param context       is the application context within the lifecycle.
     */
    public static void putUploadRate(UploadRate theUploadRate, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(User.UPLOAD_RATE, theUploadRate.rateIndex).apply();
    }


    /**
     * Public static method to get the User ID from the prefs. Returns null if does not exist.
     *
     * @param context is the application context within the lifecycle.
     * @return theUserID or null
     */
    public static String getUserID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User.USER_ID, null);
    }

    /**
     * Public static method to getUserID for Coordinate Query. User ID returned if exists,otherwise,
     * returns the ALL_USER tag to return all coordinates when used for the query.
     *
     * @param context is the application context within the lifecycle.
     * @return theUserID or a static call to ALL_USERS tags
     */
    public static String getUserIDCoordinateQuery(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User.USER_ID, User.ALL_USERS);
    }

    /**
     * Public static method to return the userEmail from the prefs.
     *
     * @param context is the application context within the lifecycle.
     * @return theUserEmail
     */
    public static String getUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User.USER_EMAIL, null);
    }

    /**
     * Public static method to return the start time from the prefs, If does not exist, returns 0.
     *
     * @param context is the application context within the lifecycle.
     * @return theStartTime stored in the prefs and 0 otherwise
     */
    public static long getStartTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(User.START_TIME, 0);
    }

    /**
     * Public static method to get the end time and returns 0 otherwise.
     *
     * @param context is the application context within the lifecycle.
     * @return theEndTime
     */
    public static long getEndTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(User.END_TIME, 0);
    }

    /**
     * Public static method to return the endtime from prefs and the current date as a long if the pref
     * does not exists
     *
     * @param context is the application context within the lifecycle.
     * @return theEndTime as the current date as a long
     */
    public static long getEndTimeCurrentTimeBackup(Context context) {
        int TIMESTAMP_DIVISOR = 1000;
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(User.END_TIME, Calendar.getInstance().getTimeInMillis() / TIMESTAMP_DIVISOR);
    }

    /**
     * Public static method to return the User agreement as a String
     *
     * @param context is the application context within the lifecycle.
     * @return theUserAgreement from prefs
     */
    public static String getUserAgreement(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User.USER_AGREEMENT, null);
    }

    /**
     * Public static to get the state of the Database for display purposes.
     *
     * @param context is the application context within the lifecycle.
     * @return theDatabaseState
     */
    public static boolean getDBFlag(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(User.DB_FLAG, true);
    }

    /**
     * Public static method to get the flag determining if background requests
     * are being asked for on application startup.
     *
     * @param context is the application context within the lifecycle.
     * @return theRequestingLocationState
     */
    public static boolean getLocationRequestStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(User.REQUESTING_LOCATION, false);
    }


    /**
     * Public static method to return the sampling rate stored in the prefs.
     *
     * @param context is the application context within the lifecycle.
     * @return theSamplingRate
     */
    public static int getSelectedSampleRate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(User.SAMPLE_RATE, UploadRate.ONCE_DAILY.rateIndex);
    }

    /**
     * Public static method to return the sampling rate stored in the prefs.
     *
     * @param context is the application context within the lifecycle.
     * @return theSamplingRate
     */
    public static int getSelectedUploadRate(Context context) {
        int DEFAULT_RATE = 10;
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(User.SAMPLE_RATE, DEFAULT_RATE);
    }

    /**
     * Public static method to clear the SharedPrefs for the running instance of the application.
     *
     * @param context is the application context.
     */
    public static void clearPrefs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }


    public enum UploadRate {
        EVERY_HOUR(0), ONCE_DAILY(1), TWICE_DAILY(2), MANUAL(3);

        public int rateIndex;

        UploadRate(int index) {
            rateIndex = index;
        }


    }

    public enum ProviderType {
        GPS(LocationManager.GPS_PROVIDER), PASSIVE(LocationManager.PASSIVE_PROVIDER), NETWORK(LocationManager.NETWORK_PROVIDER);

        public String mProviderType;

        ProviderType(String provider) {
            mProviderType = provider;
        }
    }
}
