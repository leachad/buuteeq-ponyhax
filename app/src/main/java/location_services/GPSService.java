/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package location_services;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import db.Coordinate;
import db.User;

/**
 * Created by leachad on 5/8/2015. Similar to
 * the WebDriver class, this class will have static method calls to
 * run something every n seconds.
 */
public class GPSService extends IntentService {
    private static final String GPS_TAG = "GPSService";
    private static final int GPS_INTERVAL = 6000;

    public GPSService() {
        super(GPS_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences prefs = getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        Log.d(GPS_TAG, "Received a GPS Intent: " + intent);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Coordinate coordinate = new Coordinate(location.getLongitude(), location.getLatitude(),
                location.getTime(), location.getSpeed(), location.getBearing(), prefs.getString(User.USER_ID, null));
        Toast.makeText(getApplicationContext(), "Coordinate: " + coordinate.toString(), Toast.LENGTH_SHORT).show();

    }

    public static void setGPSServiceState(Context context, boolean isOn) {
        Intent intent = new Intent(context, GPSService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), GPS_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}
