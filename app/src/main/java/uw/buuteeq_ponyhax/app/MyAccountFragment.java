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
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import db.Coordinate;
import db.User;

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
        SharedPreferences prefs = getActivity().getSharedPreferences(Coordinate.COORDINATE_PREFS, Context.MODE_PRIVATE);

        long startTime = prefs.getLong(Coordinate.START_TIME, 0);
        long endTime = prefs.getLong(Coordinate.END_TIME, 0);

        double distanceTraveled = 0.;
        double distanceTraveledInterval = 0.;

        Coordinate prev = null;

        for (Coordinate coordinate: locations) {

            if (startTime == 0 && endTime == Calendar.getInstance().getTimeInMillis() || (coordinate.getTimeStamp() < endTime && coordinate.getTimeStamp() > startTime)) {

                if (prev != null) distanceTraveledInterval += calcDistance(prev, coordinate, UNIT);

            }

            if (prev != null) distanceTraveled += calcDistance(prev, coordinate, UNIT);

            prev = coordinate;
        }

        mLongitudeView.setText(R.string.longitude_string + " " + currentLocation.getLongitude());
        mLatitudeView.setText(R.string.latitude_string + " " + currentLocation.getLatitude());
        mSpeedView.setText(R.string.speed_string + " " + currentLocation.getSpeed());
        mBearingView.setText(R.string.bearing_string + " " + currentLocation.getBearing());
        mTimeView.setText(R.string.time_stamp_string + " " + currentLocation.getTime());

        mTotalDistanceView.setText(R.string.total_distance_string + " " + distanceTraveled);
        mIntervalDistanceView.setText(R.string.total_distance_range_string + " " + distanceTraveledInterval);
        mDataPointsView.setText(R.string.num_data_points + " " + locations.size());

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


    }
}
