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

    private TextView mTotalDistanceView;
    private TextView mIntervalDistanceView;
    private TextView mDataPointsView;
    private TextView mLatitudeView;
    private TextView mLongitudeView;
    private TextView mSpeedView;
    private TextView mBearingView;
    private TextView mTimeView;


    public void update(Location currentLocation, List<Coordinate> locations) {



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
