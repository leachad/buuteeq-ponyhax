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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.LocalStorage;
import db.User;
import webservices.WebDriver;

/**
 * Created by BrentYoung on 4/12/15.
 * <p/>
 * MyAccountFragment allows for flexibility of UI when navigating through submenus with
 * the navigation drawer.
 */
public class MyAccountFragment extends Fragment implements UIUpdater {

    private static final String UNIT = "M";

    private TextView mTotalDistanceView;
    private TextView mIntervalDistanceView;
    private TextView mDataPointsView;
    private ArrayAdapter<Coordinate> mCoordinateAdapter;
    private ListView mPointListView;

    public void update(Location currentLocation, List<Coordinate> locations) {


        long startTime = LocalStorage.getStartTime(getActivity());
        long endTime = LocalStorage.getEndTimeCurrentTimeBackup(getActivity());

        double distanceTraveled = 0.;
        double distanceTraveledInterval = 0.;

        Coordinate prev = null;

        for (Coordinate coordinate : locations) {

            if ((startTime == 0) || (coordinate.getTimeStamp() < endTime && coordinate.getTimeStamp() > startTime)) {

                if (prev != null) distanceTraveledInterval += calcDistance(prev, coordinate, UNIT);

            }

            if (prev != null) distanceTraveled += calcDistance(prev, coordinate, UNIT);

            prev = coordinate;
        }

        if (currentLocation != null) {
            updateListAdapter(locations);

        } else {
            if (!locations.isEmpty()) {
                updateListAdapter(locations);
            }
        }

        mTotalDistanceView.setText(getResources().getString(R.string.total_distance_string) + " " + distanceTraveled + " miles");
        mIntervalDistanceView.setText(getResources().getString(R.string.total_distance_range_string) + " " + distanceTraveledInterval + " miles");
        mDataPointsView.setText(getResources().getString(R.string.num_data_points) + " " + locations.size());


    }

    private double calcDistance(Coordinate first, Coordinate second, String unit) {

        double lon1 = first.getLongitude();
        double lon2 = second.getLongitude();
        double lat1 = first.getLatitude();
        double lat2 = second.getLatitude();

        double theta = lon1 - lon2;

        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);

        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;

        if (unit.matches("K")) {
            dist = dist * 1.609344;

        } else if (unit.matches("N")) {
            dist = dist * 0.8684;

        }

        return (dist);

    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_my_account, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        mTotalDistanceView = (TextView) getActivity().findViewById(R.id.text_total_distance);
        mIntervalDistanceView = (TextView) getActivity().findViewById(R.id.text_total_distance_interval);
        mDataPointsView = (TextView) getActivity().findViewById(R.id.text_account_numDataPoints);
        mPointListView = (ListView) getActivity().findViewById(R.id.listViewMyAccount);

        List<Coordinate> coordinates = pollCoordinates();
        mCoordinateAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, new ArrayList<Coordinate>());
        mPointListView.setAdapter(mCoordinateAdapter);

        try {
            List<Coordinate> theList = WebDriver.getLoggedCoordinates(LocalStorage.getUserID(getActivity()),
                    LocalStorage.getStartTime(getActivity()),
                    LocalStorage.getEndTimeCurrentTimeBackup(getActivity()));

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
     * Private method used to update the contents of a List view everytime the user points are updated.
     */
    private void updateListAdapter(List<Coordinate> theUpdatedCoordinates)  {
        mCoordinateAdapter.addAll(theUpdatedCoordinates);
        mCoordinateAdapter.sort(new CoordinateComparator());
        mCoordinateAdapter.notifyDataSetChanged();
    }

    private List<Coordinate> pollCoordinates() {
        CoordinateStorageDatabaseHelper db = new CoordinateStorageDatabaseHelper(getActivity().getApplicationContext());
        return db.getAllCoordinates(LocalStorage.getUserID(getActivity()));
    }


    /**
     * Private class to implement a CoordinateComparator for correctly sorting the coordinates.
     * @author leachad
     * @version 5.10.15
     */
    private class CoordinateComparator implements Comparator<Coordinate> {

        @Override
        public int compare(Coordinate theLeft, Coordinate theRight) {
            return Long.valueOf(theRight.getTimeStamp()).compareTo(theLeft.getTimeStamp());
        }
    }
}
