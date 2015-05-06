/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

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

/**
 * MapFragment used to ease the transition between NavigationDrawer submenus
 */
public class MyMap extends Fragment implements OnMapReadyCallback, UIUpdater {

    private GoogleMap mMap;

    public void update(Location currentLocation, List<Coordinate> locations) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);


        //start location updates when the activity first starts up
        //TODO Figure out why the Location Manager keeps throwing errors
        //MyLocationManager.getInstance(getActivity()).startLocationUpdates();

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
     * Testing purposes only, but we can implement an actual setup later.
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
        setUpMap();
    }
}
