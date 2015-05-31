package location_services;

import android.location.Location;

/**
 * Created by leachad on 5/31/2015. Emulates callbacks
 * to a foreground class that writes points out to the Map
 * visible to the user.
 */
public interface BackgroundMapUpdate {

    void updatePointsOnMap(Location currentLocation);
}
