/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.Date;
import java.util.List;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;

/**
 * Created by Huy Ngo
 * MapFragment is used to display the map into a fragment so that it is an option in NavigationDrawer.
 */
public class MyMap extends Fragment implements OnMapReadyCallback, UIUpdater {

    /**
     * GoogleMap Object
     */
    private GoogleMap mMap;

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
     * Testing purposes only.
     */
    private void setUpMap() {
        //Set up coordinates
        LatLng testLocation = new LatLng(47.244911, -122.438871);
        LatLng testLocation2 = new LatLng(47.244889, -122.436940);

        //Add the markers between two points
        mMap.addMarker(new MarkerOptions().position(testLocation).title("Location 1"));
        mMap.addMarker(new MarkerOptions().position(testLocation2).title("Location 2"));

        //Add the line between the two points
        PolylineOptions line = new PolylineOptions();
        line.add(testLocation, testLocation2);
        line.width(5);
        line.color(Color.YELLOW);

        mMap.addPolyline(line);


        //Camera movement
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(testLocation, 17));
    }

    /**
     * When the map is loaded and ready, it will call this!
     *
     * @param googleMap the map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        CoordinateStorageDatabaseHelper db = new CoordinateStorageDatabaseHelper(getActivity().getApplicationContext());
        List<Coordinate> coordinates = db.getAllCoordinates(getActivity().getApplicationContext());
        update(null, coordinates);
        //setUpMap();
    }

    /**
     * When there is an update in location, it will add the new location to the map.
     *
     * @param currentLocation current location
     * @param locations the list of all coordinates
     */
    public void update(Location currentLocation, List<Coordinate> locations) {
        if (locations.size() == 1) {
            LatLng location1 = addLocation(locations.get(0));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location1, 17));
        } else if (locations.size() > 1) {
            Coordinate previousLocation = null;
            for (Coordinate location : locations) {
                addLocation(location);
                if (previousLocation != null) {
                    addLine(previousLocation, location);
                }
                previousLocation = location;
            }
            LatLng lastLocation = new LatLng(previousLocation.getLatitude(), previousLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 17));
        }
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
        mMap.addMarker(new MarkerOptions().position(location1).title(date.toString()));
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

    }

    public void updateView() {
        //Currently, do nothing for now
    }


}
