/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.User;
import webservices.WebDriver;

import static android.content.SharedPreferences.*;

/**
 * MapFragment used to ease the transition between NavigationDrawer submenus
 */
public class MyMap extends Fragment implements OnMapReadyCallback, UIUpdater {

    private GoogleMap mMap;
    private int mapCoordinateSize;
    private Coordinate lastKnownLocation;

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
    }

    private void readFromDatabase() {
        SharedPreferences preferences = getActivity().getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        boolean loadDB = preferences.getBoolean("loadLocalDB", true);

        if (loadDB) {
            Log.e("MyMap", "MyMap is reading from database");
            SharedPreferences userPrefs = getActivity().getApplication().getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
            SharedPreferences prefs = getActivity().getApplication().getSharedPreferences(Coordinate.COORDINATE_PREFS, Context.MODE_PRIVATE);

            CoordinateStorageDatabaseHelper db = new CoordinateStorageDatabaseHelper(getActivity().getApplicationContext());
            List<Coordinate> coordinates = db.getAllCoordinates(userPrefs.getString(User.USER_ID, User.ALL_USERS));

            User theUser = new User();
            theUser.setID(userPrefs.getString(User.USER_ID, "-1"));
            try {
                List<Coordinate> theList = WebDriver.getLoggedCoordinates(userPrefs.getString(User.USER_ID, null), prefs.getLong(Coordinate.START_TIME, 0), prefs.getLong(Coordinate.END_TIME, Calendar.getInstance().getTimeInMillis()));
                if (theList != null) {
                    for (Coordinate c : theList) {
                        if (c != null) {
                            coordinates.add(c);
                        }
                    }
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            update(null, coordinates);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("loadLocalDB", false);
            editor.apply();
        }
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

    /**
     * When the map is loaded and ready, it will call this!
     *
     * @param googleMap the map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        readFromDatabase();
    }

    /**
     * This method is for adding individual points to the map.
     *
     * @param location the location
     * @return a LatLng object
     */
    private LatLng addLocation(Coordinate location) {
        LatLng location1 = new LatLng(location.getLatitude(), location.getLongitude());
        Date date = new Date(location.getTimeStamp() * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy h:mm:ss a", Locale.US);
        String dateStamp = dateFormat.format(date);
        mMap.addMarker(new MarkerOptions().position(location1).title(dateStamp));
        Log.e("MyMap", "Adding location " + location1.toString());
        return location1;
    }

    /**
     * This method is for adding the lines between two points.
     *
     * @param location1 location one
     * @param location2 location two
     */
    private void addLine(Coordinate location1, Coordinate location2) {
        LatLng testLocation = new LatLng(location1.getLatitude(), location1.getLongitude());
        LatLng testLocation2 = new LatLng(location2.getLatitude(), location2.getLongitude());

        PolylineOptions line = new PolylineOptions();
        line.add(testLocation, testLocation2);
        line.width(5);
        line.color(Color.YELLOW);

        mMap.addPolyline(line);

        lastKnownLocation = location2;

    }

    private void moveCameraToLastLocation() {
        if (lastKnownLocation != null) {
            LatLng lastLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 17));
        }
    }

    /**
     * @param currentLocation
     * @param locations
     */
    @Override
    public void update(Location currentLocation, List<Coordinate> locations) {
        if (locations.size() == 1) {
            addLocation(locations.get(0));
            lastKnownLocation = locations.get(0);
            Log.d("MyMap", "Location size is 1, adding one point");
        } else if (locations.size() > 1 && mapCoordinateSize == 0) {
            Coordinate previousLocation = null;
            for (Coordinate location : locations) {
                addLocation(location);
                if (previousLocation != null) {
                    addLine(previousLocation, location);
                }
                previousLocation = location;
            }
            Log.d("MyMap", "Initial load and location size is GREATER than one");
        } else if (mapCoordinateSize < locations.size()) {
            Coordinate current = locations.get(locations.size() - 1);
            addLocation(current);
            addLine(lastKnownLocation, current);
            Log.d("MyMap", "Adding additional coordinates");
        }

        moveCameraToLastLocation();
        mapCoordinateSize = locations.size();
        Log.e("MyMap", "location list size is " + locations.size());
    }
}
