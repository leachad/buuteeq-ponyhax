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
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;


public class RangePickerFragment extends android.support.v4.app.Fragment implements UIUpdater {

    public static final String START_RANGE = "start";
    public static final String END_RANGE = "end";
    public static final int TARGET_CODE = 1;
    public TextView mStartDateDisplay;
    public TextView mEndDateDisplay;



    public void update(Location currentLocation, List<Coordinate> locations) {

    }

    public void updateView() {
        modifyDisplayFields();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_range_picker, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Button mRangeStartButton = (Button) getActivity().findViewById(R.id.rangeStartButton);
        Button mRangeEndButton = (Button) getActivity().findViewById(R.id.rangeEndButton);
        Button mQueryDateRangeButton = (Button) getActivity().findViewById(R.id.queryDateRangeButton);

        mStartDateDisplay = (TextView) getActivity().findViewById(R.id.startDateDisplay);
        mEndDateDisplay = (TextView) getActivity().findViewById(R.id.endDateDisplay);
        modifyDisplayFields();

        mRangeStartButton.setOnClickListener(new RangeDialogListener());
        mRangeEndButton.setOnClickListener(new RangeDialogListener());
        mQueryDateRangeButton.setOnClickListener(new QueryRangeListener());


        //TODO Gather and store coordinates based on Values returned from Calendar and Time Pickers

    }

    public void modifyDisplayFields() {
        SharedPreferences prefs = getActivity().getSharedPreferences(Coordinate.COORDINATE_PREFS, Context.MODE_PRIVATE);
        mStartDateDisplay.setText(new Date(prefs.getLong(Coordinate.START_TIME, 0)).toString());
        mEndDateDisplay.setText(new Date(prefs.getLong(Coordinate.END_TIME, 0)).toString());
    }

    private boolean selectedDatesOrdered() {
        SharedPreferences prefs = getActivity().getSharedPreferences(Coordinate.COORDINATE_PREFS, Context.MODE_PRIVATE);

        return prefs.getLong(Coordinate.START_TIME, 0) < prefs.getLong(Coordinate.END_TIME, 0);
    }

    /**
     * Private class to implement a RangeDialogListener that follows the android convention of placing a
     * date and time picker within a dialog fragment.
     *
     * @author leachad
     * @version 5.4.15
     */
    private class RangeDialogListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            RangeDialogFragment dialog = new RangeDialogFragment();
            if (v.getId() == R.id.rangeStartButton) {
                modifyDisplayFields();
                dialog.setTargetFragment(RangePickerFragment.this, -1);
                getActivity().getSupportFragmentManager().beginTransaction().add(dialog, START_RANGE).commit();
            } else {
                modifyDisplayFields();
                dialog.setTargetFragment(RangePickerFragment.this, -1);
                getActivity().getSupportFragmentManager().beginTransaction().add(dialog, END_RANGE).commit();
            }

        }
    }

    /**
     * Private class to implement a QueryRangeListener that verifies dates are within a correct range and will eventually
     * modify the displayed coordinates.
     *
     * @author leachad
     * @version 5.6.15
     */
    private class QueryRangeListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            SharedPreferences prefs = getActivity().getSharedPreferences(Coordinate.COORDINATE_PREFS, Context.MODE_PRIVATE);
            if (!selectedDatesOrdered()) {
                Toast.makeText(getActivity().getApplicationContext(),
                        "START: " + prefs.getLong(Coordinate.START_TIME, 0) + " END: " + prefs.getLong(Coordinate.END_TIME, 0),
                        Toast.LENGTH_SHORT).show();

            } else {

            }

        }
    }


}
