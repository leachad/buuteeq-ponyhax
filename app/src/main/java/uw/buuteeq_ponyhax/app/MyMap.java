/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.User;
import webservices.WebDriver;

/**
 * MapFragment used to ease the transition between NavigationDrawer submenus
 */
public class MyMap extends Fragment implements OnMapReadyCallback, UIUpdater {

    private GoogleMap mMap;

    /**
     * @param currentLocation
     * @param locations
     */
    @Override
    public void update(Location currentLocation, List<Coordinate> locations) {

//        PolylineOptions line = new PolylineOptions();
//        line.width(5);
//        line.color(Color.YELLOW);
//        LatLng marker1 = new LatLng(47.244911, -122.438871);
//
//        //Splits this into two possible occurences, either the current location will be null because we have it in the list already anyways,
//        //or we wont be concerned with the list because we already have all of the points in the map.
//        if (currentLocation == null && locations != null) {
//
//            marker1 = new LatLng(locations.get(0).getLatitude(), locations.get(0).getLongitude());
//            for (int i = 1; i < locations.size(); i++) {
//                LatLng marker2 = new LatLng((locations.get(i).getLatitude()), locations.get(i).getLongitude());
//
//                line.add(marker1, marker2);
//
//                marker1 = marker2;
//
//            }
//
//        } else if (currentLocation != null) {
//            marker1 = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
//            LatLng marker2 = new LatLng(locations.get(locations.size() - 1).getLatitude(), locations.get(locations.size() - 1).getLongitude());
//            line.add(marker2, marker1);
//        }
//        mMap.addPolyline(line);
//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker1, 17));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.activity_my_map, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        SharedPreferences userPrefs = getActivity().getApplication().getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences prefs = getActivity().getApplication().getSharedPreferences(Coordinate.COORDINATE_PREFS, Context.MODE_PRIVATE);

        CoordinateStorageDatabaseHelper db = new CoordinateStorageDatabaseHelper(getActivity().getApplicationContext());
        List<Coordinate> coordinates = db.getAllCoordinates(userPrefs.getString(User.USER_ID, CoordinateStorageDatabaseHelper.ALL_USERS));

        try {
            List<Coordinate> theList = WebDriver.getLoggedCoordinates(userPrefs.getString(User.USER_ID, null), prefs.getLong(Coordinate.START_TIME, 0), prefs.getLong(Coordinate.END_TIME, Calendar.getInstance().getTimeInMillis()));
            if (theList != null) {
                for (Coordinate c : theList) {
                    coordinates.add(c);
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        update(null, coordinates);

    }

    /**
     * If the map doesn't exist, it will initialize the map in the subfragment.
     */
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

    }


//    /**
//     * Testing purposes only, but we can implement an actual setup later.
//     */
//    private void setUpMap() {
//        //Set up coordinates
//        LatLng testLocation = new LatLng(47.244911, -122.438871);
//        LatLng testLocation2 = new LatLng(47.244889, -122.436940);
//
//        //Add the markers between two points
//        mMap.addMarker(new MarkerOptions().position(testLocation).title("Location 1"));
//        mMap.addMarker(new MarkerOptions().position(testLocation2).title("Location 2"));
//
//        //Add the line between the two points
//        PolylineOptions line = new PolylineOptions();
//        line.add(testLocation, testLocation2);
//        line.width(5);
//        line.color(Color.YELLOW);
//
//        mMap.addPolyline(line);
//
//
//        //Camera movement
//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(testLocation, 17));
//    }

    /**
     * When the map is loaded and ready, it will call this!
     *
     * @param googleMap the map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        setUpMap();


    }
}
