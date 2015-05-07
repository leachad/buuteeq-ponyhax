/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Range;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

import db.Coordinate;
import db.User;

/**
 * Created by leachad on 5/4/2015.
 * A Custom class that has the Date and Time Picker utilities on one Dialog Box.
 */
public class RangeDialogFragment extends DialogFragment {

    private DatePicker mDatePicker;
    private TimePicker mTimePicker;


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        View curView = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment_range_picker, null);


        mDatePicker = (DatePicker) curView.findViewById(R.id.rangeDatePicker);
        mTimePicker = (TimePicker) curView.findViewById(R.id.rangeTimePicker);
        mDatePicker.setCalendarViewShown(false);

        dialogBuilder.setPositiveButton(R.string.confirmRange, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setPrefsFromBoundary();
                updateParentView();
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancelRange, null);


        /**
         * Modify the dialog range window based on which button was clicked.
         */
        if (this.getTag().matches(RangePickerFragment.START_RANGE)) {
            dialogBuilder.setTitle(R.string.startOfRange);
        } else {
            dialogBuilder.setTitle(R.string.endOfRange);
            //Set the current day and time to the current time in milliseconds and current day
            mDatePicker.init(calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR), null);
            mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR));
            mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
        }

        dialogBuilder.setView(curView);
        return dialogBuilder.create();
    }

    /**
     * Private helper method to return the combined long as a time stamp.
     *
     * @return calTimeLong
     */
    private long getCalAndTimeLong() {
        Calendar selected = Calendar.getInstance();
        selected.set(mDatePicker.getYear(), mDatePicker.getMonth(),
                mDatePicker.getDayOfMonth(), mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute());
        return selected.getTimeInMillis() % 1000;
    }

    private void updateParentView() {

    }

    /**
     * Private helper method to determine which dialog range fragment currently has focus
     * and to set the prefs based on that current coordinate.
     */
    private void setPrefsFromBoundary() {
        SharedPreferences prefs = getActivity().getApplicationContext()
                .getSharedPreferences(Coordinate.COORDINATE_PREFS, Context.MODE_PRIVATE);
        if (this.getTag().matches(RangePickerFragment.START_RANGE)) {
            prefs.edit().putString(Coordinate.COORDINATE_SOURCE, prefs.getString(User.USER_ID, null));
            prefs.edit().putLong(Coordinate.START_TIME, getCalAndTimeLong()).apply();

        } else {
            prefs.edit().putString(Coordinate.COORDINATE_SOURCE, prefs.getString(User.USER_ID, null));
            prefs.edit().putLong(Coordinate.END_TIME, getCalAndTimeLong()).apply();

        }

    }
}
