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
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
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
    private TextView mLatitudeView;
    private TextView mLongitudeView;
    private TextView mSpeedView;
    private TextView mBearingView;
    private TextView mTimeView;


    public void update(Location currentLocation, List<Coordinate> locations) {
        SharedPreferences prefs = getActivity().getApplication().getSharedPreferences(Coordinate.COORDINATE_PREFS, Context.MODE_PRIVATE);

        long startTime = prefs.getLong(Coordinate.START_TIME, 0);
        long endTime = prefs.getLong(Coordinate.END_TIME, Calendar.getInstance().getTimeInMillis());

        double distanceTraveled = 0.;
        double distanceTraveledInterval = 0.;

        Coordinate prev = null;

        for (Coordinate coordinate: locations) {

            if ((startTime == 0 && endTime == Calendar.getInstance().getTimeInMillis()) || (coordinate.getTimeStamp() < endTime && coordinate.getTimeStamp() > startTime)) {

                if (prev != null) distanceTraveledInterval += calcDistance(prev, coordinate, UNIT);

            }

            if (prev != null) distanceTraveled += calcDistance(prev, coordinate, UNIT);

            prev = coordinate;
        }

        if (currentLocation != null) {
            mLongitudeView.setText(getResources().getString(R.string.longitude_string) + " " + currentLocation.getLongitude());
            mLatitudeView.setText(getResources().getString(R.string.latitude_string) + " " + currentLocation.getLatitude());
            mSpeedView.setText(getResources().getString(R.string.speed_string) + " " + currentLocation.getSpeed());
            mBearingView.setText(getResources().getString(R.string.bearing_string) + " " + currentLocation.getBearing());
            mTimeView.setText(getResources().getString(R.string.time_stamp_string) + " " + currentLocation.getTime());


        } else {
            if (!locations.isEmpty()) {
                Coordinate last = locations.get(0);
                mLongitudeView.setText(getResources().getString(R.string.longitude_string) + " " + last.getLongitude());
                mLatitudeView.setText(getResources().getString(R.string.latitude_string) + " " + last.getLatitude());
                mSpeedView.setText(getResources().getString(R.string.speed_string) + " " + last.getUserSpeed());
                mBearingView.setText(getResources().getString(R.string.bearing_string) + " " + last.getHeading());
                mTimeView.setText(getResources().getString(R.string.time_stamp_string) + " " + last.getTimeStamp());
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

        if (unit == "K") {

            dist = dist * 1.609344;

        } else if (unit == "N") {

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
        mLatitudeView = (TextView) getActivity().findViewById(R.id.text_latitude);
        mLongitudeView = (TextView) getActivity().findViewById(R.id.text_longitude);
        mSpeedView = (TextView) getActivity().findViewById(R.id.text_speed);
        mBearingView = (TextView) getActivity().findViewById(R.id.text_bearing);
        mTimeView = (TextView) getActivity().findViewById(R.id.text_time);

        SharedPreferences userPrefs = getActivity().getApplication().getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        SharedPreferences prefs = getActivity().getApplication().getSharedPreferences(Coordinate.COORDINATE_PREFS, Context.MODE_PRIVATE);

        CoordinateStorageDatabaseHelper db = new CoordinateStorageDatabaseHelper(getActivity().getApplicationContext());
        List<Coordinate> coordinates = db.getAllCoordinates(userPrefs.getString(User.USER_ID, CoordinateStorageDatabaseHelper.ALL_USERS));

        Button listButton = (Button) getActivity().findViewById(R.id.displayPointsButton);
        listButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new OverviewListFragment()).commit();
            }
        });

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
}
