package db;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by leachad on 5/10/2015. Used to statically access and
 * publish shared preferences. Consolidates SharedPreferences behavior
 * in the LocalStorage class to aid with troubleshooting class.
 *
 */
public class LocalStorage {


    /**
     * Public static method to put the UserID into the prefs.
     * @param theUserID is the UserID as passed from the calling code.
     * @param context is the application context within the lifecycle.
     */
    public static void putUserId(String theUserID, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(User.USER_ID, theUserID).apply();
    }

    /**
     * Public static method to put the UserEmail in the prefs.
     * @param theEmail is theUserEmail as passed from the calling code.
     * @param context is the application context within the lifecycle.
     */
    public static void putUserEmail(String theEmail, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(User.USER_EMAIL, theEmail).apply();
    }

    /**
     * Public static method to put the StartTime into the prefs.
     * @param theStartTime is the StartTime as set by the calling code for the date range of points.
     * @param context is the application context within the lifecycle.
     */
    public static void putStartTime(long theStartTime, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(User.START_TIME, theStartTime).apply();
    }

    /**
     * Public static method to put the EndTime into the prefs.
     * @param theEndTime is the EndTime as set by the calling code for the date range of points.
     * @param context is the application context within the lifecycle.
     */
    public static void putEndTime(long theEndTime, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(User.END_TIME, theEndTime).apply();
    }

    /**
     * Public static method to put the User agreement into the prefs.
     * @param theAgreement is the User agreement obtained from the WebServices.
     * @param context is the application context within the lifecycle.
     */
    public static void putUserAgreement(String theAgreement, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(User.USER_AGREEMENT, theAgreement).apply();
    }

    /**
     * Public static method to set the DB status flag in the prefs.
     * @param theFlag is the boolean passed from the calling code.
     * @param context is the application context within the lifecycle.
     */
    public static void putDBFlag(boolean theFlag, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(User.DB_FLAG, theFlag).apply();
    }

    /**
     * Public static method to put the flag into the prefs based on the Users existence within the
     * prefs.
     * @param theUserExists is the boolean passed from the calling code.
     * @param context is the application context within the lifecycle.
     */
    public static void putUserFlag(boolean theUserExists, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(User.USER_EXISTS, theUserExists).apply();
    }

    /**
     * Public static method to get the User ID from the prefs. Returns null if does not exist.
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
     * @param context is the application context within the lifecycle.
     * @return theUserID or a static call to ALL_USERS tags
     */
    public static String getUserIDCoordinateQuery(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User.USER_ID, User.ALL_USERS);
    }

    /**
     * Public static method to return the userEmail from the prefs.
     * @param context is the application context within the lifecycle.
     * @return theUserEmail
     */
    public static String getUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User.USER_EMAIL, null);
    }

    /**
     * Public static method to return the start time from the prefs, If does not exist, returns 0.
     * @param context is the application context within the lifecycle.
     * @return theStartTime stored in the prefs and 0 otherwise
     */
    public static long getStartTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(User.START_TIME, 0);
    }

    /**
     * Public static method to get the end time and returns 0 otherwise.
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
     * @param context is the application context within the lifecycle.
     * @return theUserAgreement from prefs
     */
    public static String getUserAgreement(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User.USER_AGREEMENT, null);
    }

    /**
     * Public static to get the state of the Database for display purposes.
     * @param context is the application context within the lifecycle.
     * @return theDatabaseState
     */
    public static boolean getDBFlag(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(User.DB_FLAG, true);
    }

    /**
     * Public static method to return whether or not the user exists within the prefs.
     * @param context is the application context within the lifecycle.
     * @return theUserExists
     */
    public static boolean getUserExists(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(User.USER_EXISTS, false);
    }

    /**
     * Public static method to clear the SharedPrefs for the running instance of the application.
     * @param context is the application context.
     */
    public static void clearPrefs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

}
