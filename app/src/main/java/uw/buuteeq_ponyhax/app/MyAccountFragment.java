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

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
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
    UIListUpdater mCallBackActivity;
    private TextView mTotalDistanceView;
    private TextView mIntervalDistanceView;
    private TextView mDataPointsView;
    private MyCoordinateAdapter mCoordinateAdapter;
    private ListView mPointListView;

    /**
     * This method handles the on the fly updates that are required to keep this UI current and responsive.
     * Changes include updating the list and metrics to match the date interval selected in settings should it be anything
     * other than all points.
     * @param currentLocation the most recent location that has come through the location manager
     * @param locations the list of all locations for this user, this can be parsed, by date in this case
     */
    public void update(Location currentLocation, List<Coordinate> locations) {
        ArrayList<Coordinate> localList = new ArrayList<>();
        int scannedCoordinates = 0;
        long startTime = LocalStorage.getStartTime(getActivity());
        long endTime = LocalStorage.getEndTimeCurrentTimeBackup(getActivity());

        double distanceTraveled = 0.;
        double distanceTraveledInterval = 0.;

        Coordinate prev = null;

        for (Coordinate coordinate : locations) {

            if ((startTime == 0) || (coordinate.getTimeStamp() < endTime && coordinate.getTimeStamp() > startTime)) {

                if (prev != null) distanceTraveledInterval += formatNumber(calcDistance(prev, coordinate, UNIT));
                localList.add(coordinate);
                scannedCoordinates++;

            }

            if (prev != null) distanceTraveled += formatNumber(calcDistance(prev, coordinate, UNIT));

            prev = coordinate;
        }

        updateListAdapter(localList);

        final DecimalFormat dFormatter = new DecimalFormat("###,###.######");
        String text_distanceTraveled = dFormatter.format(distanceTraveled);
        String text_distanceTraveledInterval = dFormatter.format(distanceTraveledInterval);

        mTotalDistanceView.setText(getResources().getString(R.string.total_distance_string) + " " + text_distanceTraveled + " miles");
        mIntervalDistanceView.setText(getResources().getString(R.string.total_distance_range_string) + " " + text_distanceTraveledInterval + " miles");
        mDataPointsView.setText(getResources().getString(R.string.num_data_points) + " " + scannedCoordinates);


    }

    /**
     * Helper method to format/trim the number in x decimal places.
     * @param number The number you want to format.
     * @return The number with it's value stripped passed the desired decimal places.
     */
    private double formatNumber(double number) {
        final double decimalPlaces = 1000000.0;
        return ((int) (number * decimalPlaces)) / decimalPlaces;
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


        mCoordinateAdapter = new MyCoordinateAdapter(new ArrayList<Coordinate>());
        mPointListView.setAdapter(mCoordinateAdapter);

        List<Coordinate> coordinates = mCallBackActivity.getList();

        update(null, coordinates);

    }

    /**
     * Private method used to update the contents of a List view every time the user points are updated.
     */
    private void updateListAdapter(ArrayList<Coordinate> theUpdatedCoordinates) {
        mCoordinateAdapter = new MyCoordinateAdapter(theUpdatedCoordinates);
        mCoordinateAdapter.sort(new CoordinateComparator());
        mPointListView.setAdapter(mCoordinateAdapter);
    }

    private List<Coordinate> pollCoordinates() {
        CoordinateStorageDatabaseHelper db = new CoordinateStorageDatabaseHelper(getActivity().getApplicationContext());
        return db.getAllCoordinates(LocalStorage.getUserID(getActivity()));
    }

    @Override
    public void onAttach(Activity activity) {
        mCallBackActivity = (UIListUpdater) activity;
        super.onAttach(activity);
    }

    /**
     * This interface callback defines behavior for MyAccountActivity, setting the precedent for updates
     * for all fragments contained in the activity, allowing fragments access to the list contained in the activity,
     * and the total number of points contained in the instace of coordinate database helper in the actiity.
     */
    public interface UIListUpdater {

        List<Coordinate> getList();

        GPSPlotter getGPSPlotter();

        void pushUpdates();

        int getNumLocallyStoredPoints();
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

        /**
         * This method defines a custom list view object that displays in the Overview fragment of this application.
         * @param position the current position iterated to in the adapters list
         * @param convertView the view being built
         * @param parent the calling view
         * @return a custom view object representing a coordinate point
         */
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


            DecimalFormat num = new DecimalFormat();
            num.setMaximumFractionDigits(6);
            num.setRoundingMode(RoundingMode.HALF_UP);


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

}
