/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;


import android.location.Location;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import db.Coordinate;


public class RangePickerFragment extends Fragment implements UIUpdater {

    public static final String START_RANGE = "start";
    public static final String END_RANGE = "end";


    public void update(Location currentLocation, List<Coordinate> locations) {}

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

        mRangeStartButton.setOnClickListener(new RangeDialogListener());
        mRangeEndButton.setOnClickListener(new RangeDialogListener());
        //TODO Gather and store coordinates based on Values returned from Calendar and Time Pickers

    }

    /**
     * Private class to implement a RangeDialogListener that follows the android convention of placing a
     * date and time picker within a dialog fragment.
     * @author leachad
     * @version 5.4.15
     */
    private class RangeDialogListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.rangeStartButton)
                getActivity().getFragmentManager().beginTransaction().add(new RangeDialogFragment(), START_RANGE).commit();
            else
                getActivity().getFragmentManager().beginTransaction().add(new RangeDialogFragment(), END_RANGE).commit();
        }
    }

}
