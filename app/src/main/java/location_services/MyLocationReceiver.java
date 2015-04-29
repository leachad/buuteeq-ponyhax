/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package location_services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

/**
 * Created by BrentYoung on 4/27/15.
 * Edited by Andrew Leach on 4/27/15
 */
public class MyLocationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO make sure to start the service again in here, or else nothing will happen
    }

    protected void onLocationReceived(Context context, Location loc) {

    }

    protected void onProviderEnabledChanged(boolean enabled) {

    }
}
