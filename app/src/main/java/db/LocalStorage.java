package db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

/**
 * Created by leachad on 5/10/2015. Used to statically access and
 * publish shared preferences.
 */
public class LocalStorage {

    private static int TIMESTAMP_DIVISOR = 1000;

    public static void putUserId(String theUserID, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(User.USER_ID, theUserID).apply();
    }

    public static void putUserEmail(String theEmail, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(User.USER_EMAIL, theEmail).apply();
    }

    public static void putStartTime(long theStartTime, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(User.START_TIME, theStartTime).apply();
    }

    public static void putEndTime(long theEndTime, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putLong(User.END_TIME, theEndTime).apply();
    }

    public static void putUserAgreement(String theAgreement, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(User.USER_AGREEMENT, theAgreement).apply();
    }

    public static String getUserID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User.USER_ID, null);
    }

    public static String getUserIDCoordinateQuery(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User.USER_ID, User.ALL_USERS);
    }

    public static String getUserEmail(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User.USER_EMAIL, null);
    }

    public static long getStartTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(User.START_TIME, 0);
    }

    public static long getEndTime(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(User.END_TIME, 0);
    }

    public static long getEndTimeCurrentTimeBackup(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(User.END_TIME, Calendar.getInstance().getTimeInMillis() / TIMESTAMP_DIVISOR);
    }

    public static String getUserAgreement(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getString(User.USER_AGREEMENT, null);
    }

    public static void clearPrefs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().apply();
    }

}
