/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import db.Coordinate;


public class RangePickerFragment extends Fragment implements UIUpdater {

    private static final String START_RANGE = "start";
    private static final String END_RANGE = "end";
    private static final String ERROR_MESSAGE = "Select Time and Date must be in Contiguous Order";
    private static final String DIALOG_PROMPT = "&#9660";
    private static final String BUFFER = " ";
    private static final String DATE_BUFFER = " / ";
    private static final String TIME_BUFFER = " : ";
    private Calendar mCalendar = GregorianCalendar.getInstance();
    private TextView mStartDate;
    private TextView mStartTime;
    private TextView mEndDate;
    private TextView mEndTime;


    public void update(Location currentLocation, List<Coordinate> locations) {

    }

    public void updateView() {

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

        /** Save button that will store the new dates and display confirmation.*/
        Button mStoreChosenButton = (Button) getActivity().findViewById(R.id.storeChosenTimes);

        /** Instantiate the EditText Fields that will display and hold listeners for date dialogs.*/
        mStartDate = (TextView) getActivity().findViewById(R.id.startDateField);
        mStartTime = (TextView) getActivity().findViewById(R.id.startTimeField);
        mEndDate = (TextView) getActivity().findViewById(R.id.endDateField);
        mEndTime = (TextView) getActivity().findViewById(R.id.endTimeField);



        mStartDate.setOnClickListener(new DateDialogListener(new OnDateChosen(START_RANGE)));
        mStartTime.setOnClickListener(new TimeDialogListener(new OnTimeChosen(START_RANGE)));
        mEndDate.setOnClickListener(new DateDialogListener(new OnDateChosen(END_RANGE)));
        mEndTime.setOnClickListener(new TimeDialogListener(new OnTimeChosen(END_RANGE)));

        mStartDate.setOnTouchListener(new FieldSelectedListener());
        mStartTime.setOnTouchListener(new FieldSelectedListener());
        mEndDate.setOnTouchListener(new FieldSelectedListener());
        mEndTime.setOnTouchListener(new FieldSelectedListener());

        mStoreChosenButton.setOnClickListener(new QueryRangeListener());
        //TODO save both start and end time in prefs once the user elects to SAVE

        updateAllFields();
    }

    private void closeKeyboard(Context context, IBinder windowToken) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(windowToken, 0);
    }

    private void updateAllFields() {
        SharedPreferences prefs = getActivity().getSharedPreferences(Coordinate.COORDINATE_PREFS, Context.MODE_PRIVATE);
        mStartDate.setText(getDate(new Date(prefs.getLong(Coordinate.START_TIME, 0))));
        mStartTime.setText(getTime(new Date(prefs.getLong(Coordinate.START_TIME, 0))));

        mEndDate.setText(getDate(new Date(prefs.getLong(Coordinate.END_TIME, 0))));
        mEndTime.setText(getTime(new Date(prefs.getLong(Coordinate.END_TIME, 0))));
    }


    /**
     * Private class to implement a DateDialogListener that uses the standard DatePickerDialog
     * to find the user a selected Date.
     *
     * @author leachad
     * @version 5.8.15
     */
    private class DateDialogListener implements View.OnClickListener {
        private OnDateChosen myDateChosen;

        public DateDialogListener(final OnDateChosen theDateChosen) {
            myDateChosen = theDateChosen;
        }

        @Override
        public void onClick(View v) {
            closeKeyboard(getActivity(), v.getWindowToken());
            new DatePickerDialog(RangePickerFragment.this.getActivity(), myDateChosen,
                    mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH)).show();

        }
    }

    /**
     * Private class to implement an OnTimeSelectedListener
     *
     * @author leachad
     * @version 5.8.15
     */
    private class TimeDialogListener implements View.OnClickListener {
        private OnTimeChosen myTimeChosen;

        public TimeDialogListener(final OnTimeChosen theTimeChosen) {
            myTimeChosen = theTimeChosen;
        }
        @Override
        public void onClick(View v) {
            closeKeyboard(getActivity(), v.getWindowToken());
            new TimePickerDialog(RangePickerFragment.this.getActivity(), myTimeChosen,
                    mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true).show();
        }
    }

    /**
     * Private class to implement a TextSelectedListener
     *
     * @author leachad
     * @version 5.8.15
     */
    private class FieldSelectedListener implements View.OnTouchListener {


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            closeKeyboard(getActivity(), v.getWindowToken());
            return false;
        }
    }

    private String getTime(final Date theNewDate) {
        DateFormat df = SimpleDateFormat.getTimeInstance();
        return df.format(theNewDate);
    }

    private String getDate(final Date theNewDate) {
        DateFormat df = SimpleDateFormat.getDateInstance();
        return df.format(theNewDate);
    }

    private long getUnixTimeStamp(final String theBoundary) {
        long timeStamp = 0;
        if (theBoundary.matches(START_RANGE)) {

        } else {

        }
        return timeStamp;
    }

    private boolean selectedDatesOrdered() {
        return getUnixTimeStamp(START_RANGE) < getUnixTimeStamp(END_RANGE);
    }


    /**
     * Private class to implement an OnDateSelectedListener
     */
    private class OnDateChosen implements DatePickerDialog.OnDateSetListener {
        private String myBoundary;

        public OnDateChosen(final String theBoundary) {
            myBoundary = theBoundary;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar temp = Calendar.getInstance();
            temp.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
            if (myBoundary.matches(START_RANGE)) {
                mStartDate.setText(getDate(temp.getTime()));
            } else {
                mEndDate.setText(getDate(temp.getTime()));
            }
        }
    }

    /**
     * Private class to implement an OnDateSelectedListener
     */
    private class OnTimeChosen implements TimePickerDialog.OnTimeSetListener {
        private String myBoundary;

        public OnTimeChosen(final String theBoundary) {
            myBoundary = theBoundary;
        }
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar temp = Calendar.getInstance();
            temp.set(0, 0, 0, hourOfDay, minute, 0);

            if (myBoundary.matches(START_RANGE)) {
                mStartTime.setText(getTime(temp.getTime()));
            } else {
                mEndTime.setText(getTime(temp.getTime()));
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

            if (!selectedDatesOrdered()) {
                Toast.makeText(getActivity().getApplicationContext(), ERROR_MESSAGE, Toast.LENGTH_SHORT);
            } else {

            }
        }
    }
}

