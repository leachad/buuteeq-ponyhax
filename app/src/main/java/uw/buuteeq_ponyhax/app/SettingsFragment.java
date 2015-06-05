/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import db.Coordinate;
import db.LocalStorage;
import webservices.JsonBuilder;
import webservices.WebDriver;


public class SettingsFragment extends android.support.v4.app.Fragment implements UIUpdater {


    /**
     * Static fields for maintaining
     * data integrity dependent of the actions.
     */
    public static final String START_RANGE = "start";
    public static final String END_RANGE = "end";
    public static final int TIMESTAMP_DIVISOR = 1000;
    public static final int SAMPLE_CONVERSION = 60;
    private static final String ERROR_MESSAGE = "Select Time and Date must be in Contiguous Order";
    /**
     * Forget password settings
     */
    private static final String RESET_PROMPT = "Your password can be reset with the link sent to: ";
    private static final String RESET_FAILED = "Unable to execute reset request. Please try again later.";
    private static final String FREQUENCY_KEY = "frequencyValue";
    private Button mResetPassword;
    private Button mUploadButton;
    private CheckBox mShowAllBox;
    private int numPointsCollected;
    /**
     * Callback fields
     */
    MyAccountFragment.UIListUpdater mCallBackActivity;
    /**
     * Backend calendars used for maintaining the correct date format.
     */
    private Calendar mCalendar = GregorianCalendar.getInstance();
    private Calendar mStartCalendar;
    private Calendar mEndCalendar;
    /**
     * Text view widgets that allow use within the scope of the class.
     */
    private TextView mStartDate;
    private TextView mStartTime;
    private TextView mEndDate;
    private TextView mEndTime;
    /**
     * Frequency fields
     */
    private TextView frequencyText;
    private SeekBar frequencyBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(FREQUENCY_KEY, frequencyBar.getProgress() + 1);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        mStartCalendar = GregorianCalendar.getInstance();
        mEndCalendar = GregorianCalendar.getInstance();

        if (savedInstanceState != null) {
            int frequencyValue = savedInstanceState.getInt(FREQUENCY_KEY);
            frequencyBar = (SeekBar) view.findViewById(R.id.gps_sampling_seek_bar);
            frequencyBar.setProgress(frequencyValue);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        numPointsCollected = mCallBackActivity.getNumLocallyStoredPoints();
        /** Instantiate the TextView Fields that will display and hold listeners
         *  for Date and Time Dialogs.
         *
         */
        mStartDate = (TextView) getActivity().findViewById(R.id.startDateField);
        mStartTime = (TextView) getActivity().findViewById(R.id.startTimeField);
        mEndDate = (TextView) getActivity().findViewById(R.id.endDateField);
        mEndTime = (TextView) getActivity().findViewById(R.id.endTimeField);

        /**
         * Sets the OnClickListeners with the respective DateDialog/DateChosen OR TimeDialog/TimeChosen
         * Listeners.
         */
        mStartDate.setOnClickListener(new DateDialogListener(new OnDateChosen(START_RANGE)));
        mStartTime.setOnClickListener(new TimeDialogListener(new OnTimeChosen(START_RANGE)));
        mEndDate.setOnClickListener(new DateDialogListener(new OnDateChosen(END_RANGE)));
        mEndTime.setOnClickListener(new TimeDialogListener(new OnTimeChosen(END_RANGE)));

        /**
         * Sets the OnTouchListeners for the same TextViews to suppress the display of the
         * soft keyboard.
         */
        mStartDate.setOnTouchListener(new FieldSelectedListener());
        mStartTime.setOnTouchListener(new FieldSelectedListener());
        mEndDate.setOnTouchListener(new FieldSelectedListener());
        mEndTime.setOnTouchListener(new FieldSelectedListener());

        //Setup reset password
        mResetPassword = (Button) getActivity().findViewById(R.id.resetPasswordSettings);
        mResetPassword.setOnClickListener(new ResetPasswordListener());

        //setup push updates to remote database
        mUploadButton = (Button) getActivity().findViewById(R.id.pushToRemoteButton);
        mUploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallBackActivity.pushUpdates();
                numPointsCollected = 0;
                setUploadButtonText();
            }
        });
        setUploadButtonText();

        //setup show all check box
        mShowAllBox = (CheckBox) getActivity().findViewById(R.id.showAllCheckbox);
        mShowAllBox.setChecked(LocalStorage.getStartTime(getActivity().getApplicationContext()) == 0);
        mShowAllBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LocalStorage.putStartTime(0, getActivity().getApplicationContext());
                    LocalStorage.putPreviousStartTime(mStartCalendar.getTimeInMillis() / TIMESTAMP_DIVISOR, getActivity().getApplicationContext());
                    LocalStorage.putEndTime(Calendar.getInstance().getTimeInMillis() / TIMESTAMP_DIVISOR, getActivity().getApplicationContext());
                    LocalStorage.putPreviousEndTime(mEndCalendar.getTimeInMillis() / TIMESTAMP_DIVISOR, getActivity().getApplicationContext());
                    Log.e("STORAGE OF TIME", "" + new Date(LocalStorage.getPreviousStartTime(getActivity().getApplicationContext()) * TIMESTAMP_DIVISOR).toString());
                } else {
                    LocalStorage.putStartTime(LocalStorage.getPreviousStartTime(getActivity().getApplicationContext()), getActivity().getApplicationContext());
                    LocalStorage.putEndTime(LocalStorage.getPreviousEndTime(getActivity().getApplicationContext()), getActivity().getApplicationContext());
                }
                updateAllFields();
            }
        });


        //Grab the current gps frequency value
        frequencyText = (TextView) getActivity().findViewById(R.id.gps_sampling_seconds);
        frequencyBar = (SeekBar) getActivity().findViewById(R.id.gps_sampling_seek_bar);

        int currentGPSInterval = mCallBackActivity.getGPSPlotter().getInterval() / 60;
        changeIntervalText(currentGPSInterval);
        frequencyBar.setProgress(currentGPSInterval);
        frequencyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int frequency;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frequency = progress + 1;
                changeIntervalText(frequency);
                Log.d("Progress Bar Test", "The seekbar value is at " + frequency);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mCallBackActivity.getGPSPlotter().changeRequestIntervals(frequency * SAMPLE_CONVERSION);
                LocalStorage.putSamplingRate(frequency * SAMPLE_CONVERSION, getActivity().getApplicationContext());
                Log.d("Progress Bar Test", "The GPSPlotter value is at " + mCallBackActivity.getGPSPlotter().getInterval());
            }
        });

        /**
         * Updates all fields from shared prefs using the static methods in the LocalStorage
         * class.
         */
        updateAllFields();
    }

    @Override
    public void onAttach(Activity activity) {
        mCallBackActivity = (MyAccountFragment.UIListUpdater) activity;
        super.onAttach(activity);
    }

    private void changeIntervalText(int inputSeconds) {
        if (inputSeconds > 1) {
            frequencyText.setText("Every " + inputSeconds + " minutes");
        } else {
            frequencyText.setText("Every " + inputSeconds + " minute");
        }
    }

    private void setUploadButtonText() {
        Log.d("SET TEXT METHOD", "SETTINGS");
        mUploadButton.setText(getResources().getString(R.string.push_points_prompt1) + " " + numPointsCollected + " " + getResources().getString(R.string.push_points_prompt2));
    }

    /**
     * Uses the InputMethodManager to suppress the display of the soft keyboard.
     *
     * @param context     is the Application Context
     * @param windowToken is the window where the keyboard should be suppressed
     */
    private void closeKeyboard(Context context, IBinder windowToken) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(windowToken, 0);
    }


    /**
     * Updates the view fields based on prefs stored in LocalStorage.
     */
    private void updateAllFields() {
        mStartCalendar.setTime(new Date(LocalStorage.getStartTime(getActivity()) * TIMESTAMP_DIVISOR));
        mStartDate.setText(getDate(mStartCalendar.getTime()));
        mStartTime.setText(getTime(mStartCalendar.getTime()));

        mEndCalendar.setTime(new Date(LocalStorage.getEndTime(getActivity()) * TIMESTAMP_DIVISOR));
        mEndDate.setText(getDate(mEndCalendar.getTime()));
        mEndTime.setText(getTime(mEndCalendar.getTime()));

    }


    /**
     * Returns a formatted time String based on the Date passed
     * as a parameter.
     *
     * @param theNewDate is the Date object with the date needed to display
     * @return a formatted time display
     */
    private String getTime(final Date theNewDate) {
        DateFormat df = SimpleDateFormat.getTimeInstance(DateFormat.SHORT);
        return df.format(theNewDate);
    }

    /**
     * Returns a formatted date String based on the Date passed
     * as a parameter.
     *
     * @param theNewDate is the Date object with the date needed to display
     * @return a formatted Date Display
     */
    private String getDate(final Date theNewDate) {
        DateFormat df = SimpleDateFormat.getDateInstance();
        return df.format(theNewDate);
    }

    /**
     * Returns a formatted Unix time stamp as a long based
     * on the date stored in the backing Calendars and divided
     * by 1000 to obtain the correct date.
     *
     * @param theBoundary is the START_RANGE or the END_RANGE
     * @return a unix time stamp as a long
     */
    private long getUnixTimeStamp(final String theBoundary) {
        if (theBoundary.matches(START_RANGE)) {
            Log.w("SettingsFragment", "Current Start Date " + mStartCalendar.getTime().toString());
            return mStartCalendar.getTimeInMillis() / TIMESTAMP_DIVISOR;
        } else {
            Log.w("SettingsFragment", "Current End Date " + mEndCalendar.getTime().toString());
            return mEndCalendar.getTimeInMillis() / TIMESTAMP_DIVISOR;
        }
    }

    /**
     * Method to determine if the selected Date/Combos are in order.
     *
     * @return datesAreOrdered
     */
    private boolean selectedDatesOrdered() {
        return getUnixTimeStamp(START_RANGE) < getUnixTimeStamp(END_RANGE);
    }

    /**
     * Do nothing method that mimics a callback for updating view.
     *
     * @param currentLocation is theCurrentLocation
     * @param locations       is the list of Locations
     */
    @Override
    public void update(Location currentLocation, List<Coordinate> locations) {
//        if (currentLocation != null) {
            Log.d("UPDATE METHOD SETTINGS", "UPDATER");
            numPointsCollected++;
            setUploadButtonText();
//        }
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
            new DatePickerDialog(SettingsFragment.this.getActivity(), myDateChosen,
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
            new TimePickerDialog(SettingsFragment.this.getActivity(), myTimeChosen,
                    mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), true).show();
        }
    }


    /**
     * Private class to implement an OnDateSetListener. Determines the behavior for Display after
     * the user selects a Date from the DatePicker widget in the Android Library.
     *
     * @author leachad
     * @version 5.7.15
     */
    private class OnDateChosen implements DatePickerDialog.OnDateSetListener {
        private String myBoundary;

        public OnDateChosen(final String theBoundary) {
            myBoundary = theBoundary;
        }

        /**
         * OnDateset updates the prefs, textview displays, and backside calendars using
         * the year month and day selected by the user.
         *
         * @param view        is the DatePicker widget
         * @param year        is the selected year
         * @param monthOfYear is the selected month
         * @param dayOfMonth  is the selected day
         */
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            int tempYear;
            int tempMonth;
            int tempDay;
            if (myBoundary.matches(START_RANGE)) {
                tempYear = mStartCalendar.get(Calendar.YEAR);
                tempMonth = mStartCalendar.get(Calendar.MONTH);
                tempDay = mStartCalendar.get(Calendar.DAY_OF_MONTH);
                mStartCalendar.set(year, monthOfYear, dayOfMonth, mStartCalendar.get(Calendar.HOUR_OF_DAY), mStartCalendar.get(Calendar.MINUTE), 0);
            } else {
                tempYear = mEndCalendar.get(Calendar.YEAR);
                tempMonth = mEndCalendar.get(Calendar.MONTH);
                tempDay = mEndCalendar.get(Calendar.DAY_OF_MONTH);
                mEndCalendar.set(year, monthOfYear, dayOfMonth, mEndCalendar.get(Calendar.HOUR_OF_DAY), mEndCalendar.get(Calendar.MINUTE), 0);
            }

            if (selectedDatesOrdered()) {
                if (myBoundary.matches(START_RANGE)) {
                    mStartDate.setText(getDate(mStartCalendar.getTime()));
                    LocalStorage.putStartTime(getUnixTimeStamp(START_RANGE), getActivity());
                    Log.w("Start Date Set: ", Long.toString(getUnixTimeStamp(START_RANGE)));
                } else {
                    mEndDate.setText(getDate(mEndCalendar.getTime()));
                    LocalStorage.putEndTime(getUnixTimeStamp(END_RANGE), getActivity());
                    Log.w("End Date Set: ", Long.toString(getUnixTimeStamp(END_RANGE)));
                }
            } else {
                Toast.makeText(getActivity(), ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                //Reset Calendar
                if (myBoundary.matches(START_RANGE)) {
                    mStartCalendar.set(tempYear, tempMonth, tempDay, mStartCalendar.get(Calendar.HOUR_OF_DAY), mStartCalendar.get(Calendar.MINUTE), 0);
                } else {
                    mEndCalendar.set(tempYear, tempMonth, tempDay, mEndCalendar.get(Calendar.HOUR_OF_DAY), mEndCalendar.get(Calendar.MINUTE), 0);
                }
            }
        }
    }

    /**
     * Private class to implement an OnTimeSetListener. Determines the behavior for Display after
     * the user selects a Time from the TimePicker widget in the Android Library.
     *
     * @author leachad
     * @version 5.7.15
     */
    private class OnTimeChosen implements TimePickerDialog.OnTimeSetListener {
        private String myBoundary;

        public OnTimeChosen(final String theBoundary) {
            myBoundary = theBoundary;
        }

        /**
         * OnTimeSet updates the prefs, textview displays, and backside calendars using
         * the hour and minute selected by the User.
         *
         * @param view      is the TimePicker widget.
         * @param hourOfDay is thehour selected
         * @param minute    is the minute selected
         */
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            int tempHour;
            int tempMinute;
            if (myBoundary.matches(START_RANGE)) {
                tempHour = mStartCalendar.get(Calendar.HOUR_OF_DAY);
                tempMinute = mStartCalendar.get(Calendar.MINUTE);
                mStartCalendar.set(mStartCalendar.get(Calendar.YEAR), mStartCalendar.get(Calendar.MONTH), mStartCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
            } else {
                tempHour = mEndCalendar.get(Calendar.HOUR_OF_DAY);
                tempMinute = mEndCalendar.get(Calendar.MINUTE);
                mEndCalendar.set(mEndCalendar.get(Calendar.YEAR), mEndCalendar.get(Calendar.MONTH), mEndCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
            }

            if (selectedDatesOrdered()) {
                if (myBoundary.matches(START_RANGE)) {
                    mStartTime.setText(getTime(mStartCalendar.getTime()));
                    LocalStorage.putStartTime(getUnixTimeStamp(START_RANGE), getActivity());
                    Log.w("Start Time Set: ", Long.toString(getUnixTimeStamp(START_RANGE)));
                } else {
                    mEndTime.setText(getTime(mEndCalendar.getTime()));
                    LocalStorage.putEndTime(getUnixTimeStamp(END_RANGE), getActivity());
                    Log.w("End Time Set: ", Long.toString(getUnixTimeStamp(END_RANGE)));
                }
            } else {
                Toast.makeText(getActivity(), ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                //Reset Calendar
                if (myBoundary.matches(START_RANGE)) {
                    mStartCalendar.set(mStartCalendar.get(Calendar.YEAR), mStartCalendar.get(Calendar.MONTH), mStartCalendar.get(Calendar.DAY_OF_MONTH), tempHour, tempMinute, 0);
                } else {
                    mEndCalendar.set(mEndCalendar.get(Calendar.YEAR), mEndCalendar.get(Calendar.MONTH), mEndCalendar.get(Calendar.DAY_OF_MONTH), tempHour, tempMinute, 0);
                }
            }
        }
    }


    /**
     * Private class to implement a TouchListener. Suppresses the soft keyboard if the user touches
     * the widget. Fail safe if the user fails to touch the painted area of the widget and instead
     * touches the text.
     *
     * @author leachad
     * @version 5.8.15
     */
    private class FieldSelectedListener implements View.OnTouchListener {


        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mShowAllBox.isChecked()) {
                mShowAllBox.setChecked(false);
            }
            closeKeyboard(getActivity(), v.getWindowToken());
            return false;
        }
    }

    /**
     * Private class to implement a ResetPasswordListener.
     *
     * @author leachad
     * @version 5.3.15
     */
    private class ResetPasswordListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            String email = LocalStorage.getUserEmail(getActivity());

            try {
                String result = WebDriver.resetPassword(email);

                if (result.matches(JsonBuilder.VAL_FAIL)) {
                    Toast.makeText(getActivity().getApplicationContext(), RESET_FAILED, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), RESET_PROMPT + email, Toast.LENGTH_SHORT).show();
                    LocalStorage.clearPrefs(getActivity());
                    getActivity().finish();
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }


        }
    }
}


