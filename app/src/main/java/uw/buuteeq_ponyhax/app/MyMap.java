/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.app.Activity;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.LocalStorage;
import db.User;
import webservices.WebDriver;

/**
 * MapFragment used to ease the transition between NavigationDrawer submenus
 */
public class MyMap extends Fragment implements OnMapReadyCallback, UIUpdater {

    private GoogleMap mMap;
    private int mapCoordinateSize;
    private Coordinate lastKnownLocation;

    private MyAccountFragment.UIListUpdater mCallBackActivity;

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
        boolean loadDB = LocalStorage.getDBFlag(getActivity());


//        if (loadDB) {
//            Log.w("MyMap", "MyMap is reading from database");
//            List<Coordinate> coordinates = new ArrayList<>();
//            try {
//                List<Coordinate> theList = WebDriver.getLoggedCoordinates(LocalStorage.getUserID(getActivity()),
//                        LocalStorage.getStartTime(getActivity()),
//                        LocalStorage.getEndTimeCurrentTimeBackup(getActivity()));
//
//                if (theList != null) {
//                    for (Coordinate c : theList) {
//                        coordinates.add(c);
//                    }
//                }
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            update(null, coordinates);
//            LocalStorage.putDBFlag(false, getActivity());
//
//        }

        List<Coordinate> coordinates = mCallBackActivity.getList();
        Log.w("CALLBACK LIST:mymap", coordinates.get(0).toString());
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
        Log.w("In addLoc;mymap-lat:", Double.toString(location.getLatitude()));
        Log.w("In addLoc;mymap-long:", Double.toString(location.getLongitude()));
        Date date = new Date(location.getTimeStamp() * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy h:mm:ss a", Locale.US);
        String dateStamp = dateFormat.format(date);
        mMap.addMarker(new MarkerOptions().position(location1).title(dateStamp));
        Log.w("MyMap", "Adding location " + location1.toString());
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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 25));
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
            Log.w("MyMap", "Location size is 1, adding one point");
        } else if (locations.size() > 1 && mapCoordinateSize == 0) {
            Coordinate previousLocation = null;
            for (Coordinate location : locations) {
                addLocation(location);
                if (previousLocation != null) {
                    addLine(previousLocation, location);
                }
                previousLocation = location;
            }
            Log.w("MyMap", "Initial load and location size is GREATER than one");
        } else if (mapCoordinateSize < locations.size()) {
            Coordinate current = locations.get(locations.size() - 1);
            addLocation(current);
            addLine(lastKnownLocation, current);
            Log.w("MyMap", "Adding additional coordinates");
        }

        moveCameraToLastLocation();
        mapCoordinateSize = locations.size();
        Log.w("MyMap", "location list size is " + locations.size());
    }

    @Override
    public void onAttach(Activity activity) {
        mCallBackActivity = (MyAccountFragment.UIListUpdater) activity;
        super.onAttach(activity);
    }
}
