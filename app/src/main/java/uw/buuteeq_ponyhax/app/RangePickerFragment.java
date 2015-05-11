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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import db.Coordinate;
import db.LocalStorage;


public class RangePickerFragment extends android.support.v4.app.Fragment implements UIUpdater  {

//    private static final String ERROR_MESSAGE = "Select Time and Date must be in Contiguous Order";
//    private static final String DIALOG_PROMPT = "&#9660";
    private static final int TIMESTAMP_DIVISOR = 1000;
    private Calendar mCalendar = GregorianCalendar.getInstance();
    private Calendar mStartCalendar;
    private Calendar mEndCalendar;
    private TextView mStartDate;
    private TextView mStartTime;
    private TextView mEndDate;
    private TextView mEndTime;

    public static final String START_RANGE = "start";
    public static final String END_RANGE = "end";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mStartCalendar = GregorianCalendar.getInstance();
        mEndCalendar = GregorianCalendar.getInstance();
        return inflater.inflate(R.layout.fragment_range_picker, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
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


        updateAllFields();
    }

    private void closeKeyboard(Context context, IBinder windowToken) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(windowToken, 0);
    }

    private void updateAllFields() {
        mStartCalendar.setTime(new Date(LocalStorage.getStartTime(getActivity()) * TIMESTAMP_DIVISOR));
        mStartDate.setText(getDate(mStartCalendar.getTime()));
        mStartTime.setText(getTime(mStartCalendar.getTime()));

        mEndCalendar.setTime(new Date(LocalStorage.getEndTime(getActivity()) * TIMESTAMP_DIVISOR));
        mEndDate.setText(getDate(mEndCalendar.getTime()));
        mEndTime.setText(getTime(mEndCalendar.getTime()));

    }

    @Override
    public void update(Location currentLocation, List<Coordinate> locations) {

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
        DateFormat df = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
        return df.format(theNewDate);
    }

    private String getDate(final Date theNewDate) {
        DateFormat df = SimpleDateFormat.getDateInstance();
        return df.format(theNewDate);
    }

    private long getUnixTimeStamp(final String theBoundary) {
        if (theBoundary.matches(START_RANGE)) {
            return mStartCalendar.getTimeInMillis() / TIMESTAMP_DIVISOR;
        } else {
            return mEndCalendar.getTimeInMillis() / TIMESTAMP_DIVISOR;
        }
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
            if (myBoundary.matches(START_RANGE)) {
                mStartCalendar.set(year, monthOfYear, dayOfMonth, mStartCalendar.get(Calendar.HOUR_OF_DAY), mStartCalendar.get(Calendar.MINUTE), 0);
                mStartDate.setText(getDate(mStartCalendar.getTime()));
                LocalStorage.putStartTime(getUnixTimeStamp(START_RANGE), getActivity());
                Log.d("Start Date Set: ", Long.toString(getUnixTimeStamp(START_RANGE)));
            } else {
                mEndCalendar.set(year, monthOfYear, dayOfMonth, mEndCalendar.get(Calendar.HOUR_OF_DAY), mEndCalendar.get(Calendar.MINUTE), 0);
                mEndDate.setText(getDate(mEndCalendar.getTime()));
                LocalStorage.putEndTime(getUnixTimeStamp(END_RANGE), getActivity());
                Log.d("End Date Set: ", Long.toString(getUnixTimeStamp(END_RANGE)));
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
            if (myBoundary.matches(START_RANGE)) {
                mStartCalendar.set(mStartCalendar.get(Calendar.YEAR), mStartCalendar.get(Calendar.MONTH), mStartCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
                mStartTime.setText(getTime(mStartCalendar.getTime()));
                LocalStorage.putStartTime(getUnixTimeStamp(START_RANGE), getActivity());
                Log.d("Start Time Set: ", Long.toString(getUnixTimeStamp(START_RANGE)));
            } else {
                mEndCalendar.set(mEndCalendar.get(Calendar.YEAR), mEndCalendar.get(Calendar.MONTH), mEndCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
                mEndTime.setText(getTime(mEndCalendar.getTime()));
                LocalStorage.putEndTime(getUnixTimeStamp(END_RANGE), getActivity());
                Log.d("End Time Set: ", Long.toString(getUnixTimeStamp(END_RANGE)));
            }
        }
    }
}


