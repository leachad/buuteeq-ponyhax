package location_services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

/**
 * Created by leachad on 5/20/2015. Will contain
 * static calls to issue thread requests and set
 * different pertinent variables.
 */
public class GPSPlotter {

    private static final int DEFAULT_INTERVAL = 5;
    private static final int TIMESTAMP_MULTIPLIER = 1000;
    private static AlarmManager mAlarmManager = null;

    /**
     * If the start Service Intent method is called without a parameter
     * the DEFAULT_INTERVAL will be utilized.
     */
    public static void beginManagedLocationRequests(final Context context) {
        issuePendingIntent(DEFAULT_INTERVAL, context);
    }

    /**
     * User passes in a requested interval polling time in seconds as an
     * integer.
     *
     * @param requestedInterval is the polling interval as requested by the user.
     */
    public static void beginManagedLocationRequests(final int requestedInterval, final Context context) {
        issuePendingIntent(requestedInterval, context);

    }

    /**
     * Public method to end the managed Location Requests.
     */
    public static void endManagedLocationRequests(final Context theApplicationContext) {
        PendingIntent intent = PendingIntent.getService(theApplicationContext, 0, new Intent(theApplicationContext, GPSPlotterIntentService.class), 0);
        mAlarmManager = (AlarmManager) theApplicationContext.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.cancel(intent);
    }

    /**
     * Private method that will issue a pending Intent to run the service on a Background
     * thread.
     * @param theIntentInterval is the interval with which the AlarmManager will issue requests.
     */
    private static void issuePendingIntent(final int theIntentInterval, final Context theApplicationContext) {
        PendingIntent intent = PendingIntent.getService(theApplicationContext, 0, new Intent(theApplicationContext, GPSPlotterIntentService.class), 0);
        long startTime = SystemClock.elapsedRealtime();
        mAlarmManager = (AlarmManager) theApplicationContext.getSystemService(Context.ALARM_SERVICE);
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, startTime, theIntentInterval * TIMESTAMP_MULTIPLIER, intent);

    }


}
