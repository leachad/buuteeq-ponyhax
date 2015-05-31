/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.LocalStorage;
import location_services.GPSPlotter;

/**
 * Created by BrentYoung on 4/12/15.
 * <p/>
 * MyAccountFragment allows for flexibility of UI when navigating through submenus with
 * the navigation drawer.
 */
public class MyAccountFragment extends Fragment implements UIUpdater {

    private static final String UNIT = "M";
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    private TextView mTotalDistanceView;
    private TextView mIntervalDistanceView;
    private TextView mDataPointsView;
    private MyCoordinateAdapter mCoordinateAdapter;
    private ListView mPointListView;

    UIListUpdater mCallBackActivity;

    public void update(Location currentLocation, List<Coordinate> locations) {

        int scannedCoordinates = 0;
        long startTime = LocalStorage.getStartTime(getActivity());
        long endTime = LocalStorage.getEndTimeCurrentTimeBackup(getActivity());

        double distanceTraveled = 0.;
        double distanceTraveledInterval = 0.;

        Coordinate prev = null;

        for (Coordinate coordinate : locations) {

            if ((startTime == 0) || (coordinate.getTimeStamp() < endTime && coordinate.getTimeStamp() > startTime)) {

                if (prev != null) distanceTraveledInterval += calcDistance(prev, coordinate, UNIT);

                scannedCoordinates++;

            }

            if (prev != null) distanceTraveled += calcDistance(prev, coordinate, UNIT);

            prev = coordinate;
        }

        if (currentLocation != null) {
            updateListAdapter(locations);

        } else {
            if (!locations.isEmpty() && mCoordinateAdapter.getCount() == 0) {
                updateListAdapter(locations);
            }
        }

        NumberFormat num = NumberFormat.getNumberInstance();
        num.setMaximumFractionDigits(6);

        mTotalDistanceView.setText(getResources().getString(R.string.total_distance_string) + " " + num.format(distanceTraveled) + " miles");
        mIntervalDistanceView.setText(getResources().getString(R.string.total_distance_range_string) + " " + num.format(distanceTraveledInterval) + " miles");
        mDataPointsView.setText(getResources().getString(R.string.num_data_points) + " " + scannedCoordinates);


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
        mPointListView.setDivider(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha));
        mPointListView.setDividerHeight(5);


//        List<Coordinate> coordinates = pollCoordinates();
        mCoordinateAdapter = new MyCoordinateAdapter(new ArrayList<Coordinate>());
        mPointListView.setAdapter(mCoordinateAdapter);

//        try {
//            List<Coordinate> theList = WebDriver.getLoggedCoordinates(LocalStorage.getUserID(getActivity()),
//                    LocalStorage.getStartTime(getActivity()),
//                    LocalStorage.getEndTimeCurrentTimeBackup(getActivity()));
//
//            if (theList != null) {
//                for (Coordinate c : theList) {
//                    coordinates.add(c);
//                }
//            }
//        } catch (ExecutionException | InterruptedException e) {
//            e.printStackTrace();
//        }

        List<Coordinate> coordinates = mCallBackActivity.getList();

        update(null, coordinates);

    }

    /**
     * Private method used to update the contents of a List view everytime the user points are updated.
     */
    private void updateListAdapter(List<Coordinate> theUpdatedCoordinates) {
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
     *
     * @author leachad
     * @version 5.10.15
     */
    private class CoordinateComparator implements Comparator<Coordinate> {

        @Override
        public int compare(Coordinate theLeft, Coordinate theRight) {
            return Long.valueOf(theRight.getTimeStamp()).compareTo(theLeft.getTimeStamp());
        }
    }

    private class MyCoordinateAdapter extends ArrayAdapter<Coordinate> {

        public MyCoordinateAdapter(ArrayList<Coordinate> myCoordinates) {
            super(getActivity(), 0, myCoordinates);

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_coordinate_list_item, null);
            }

            String separator = "\t";

            Date date = new Date(getItem(position).getTimeStamp() * 1000);
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' at 'HH:mm");
            String dateFormatted = formatter.format(date);

            TextView timeView = (TextView) convertView.findViewById(R.id.time_list_text);
            timeView.setText(getResources().getString(R.string.time_stamp_string) + separator + dateFormatted);


            NumberFormat num = NumberFormat.getNumberInstance();
            num.setMaximumFractionDigits(6);

            String longitude = num.format(getItem(position).getLongitude());
            TextView longView = (TextView) convertView.findViewById(R.id.long_list_text);
            longView.setText(getResources().getString(R.string.longitude_string) + separator + longitude);
            Log.w("LON DISPLAY:", longitude);

            String latitude = num.format(getItem(position).getLatitude());
            TextView latView = (TextView) convertView.findViewById(R.id.lat_list_text);
            latView.setText(getResources().getString(R.string.latitude_string) + separator + latitude);
            Log.w("LAT DISPLAY:", latitude);

            String speed = num.format(getItem(position).getUserSpeed());
            TextView speedView = (TextView) convertView.findViewById(R.id.speed_list_text);
            speedView.setText(getResources().getString(R.string.speed_string) + separator + speed);
            Log.w("SPEED DISPLAY:", speed);

            String bearing = num.format(getItem(position).getHeading());
            TextView bearingView = (TextView) convertView.findViewById(R.id.head_list_text);
            bearingView.setText(getResources().getString(R.string.bearing_string) + separator + bearing);
            Log.w("BEARING DISPLAY:", bearing);

            return convertView;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        mCallBackActivity = (UIListUpdater) activity;
        super.onAttach(activity);
    }

    public interface UIListUpdater {

        List<Coordinate> getList();

        GPSPlotter getGPSPlotter();
    }

}
