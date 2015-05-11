package db;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by leachad on 5/10/2015. Used to statically access and
 * publish shared preferences.
 */
public class LocalStorage {


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

    public 

}
