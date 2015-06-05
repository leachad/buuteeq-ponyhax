/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.location.Location;

import java.util.List;

import db.Coordinate;

/**
 * Created by BrentYoung on 5/5/15.
 * This interface calls for the implementing views to provide an update method that will allow for
 * dynamic changes during the apps use.
 */
public interface UIUpdater {

    void update(Location currentLocation, List<Coordinate> locations);

}
