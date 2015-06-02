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
import location_services.GPSPlotter;
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
    private Button resetPassword;
    private Button uploadButton;
    private CheckBox backgroundCheckbox;
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
        mStartCalendar = GregorianCalendar.getInstance();
        mEndCalendar = GregorianCalendar.getInstance();

        if (savedInstanceState != null) {
            int frequencyValue = savedInstanceState.getInt(FREQUENCY_KEY);
            frequencyBar.setProgress(frequencyValue);
        }
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

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
        resetPassword = (Button) getActivity().findViewById(R.id.resetPasswordSettings);
        resetPassword.setOnClickListener(new ResetPasswordListener());

        //setup push updates to remote database
        uploadButton = (Button) getActivity().findViewById(R.id.pushToRemoteButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCallBackActivity.pushUpdates();
                setUploadButtonText();
            }
        });
        setUploadButtonText();

        //setup background updates checkbox -- still some stuff to do here
        backgroundCheckbox = (CheckBox) getActivity().findViewById(R.id.foregroundCheckbox);
        backgroundCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCallBackActivity.getGPSPlotter().modifyServiceType(GPSPlotter.ServiceType.BACKGROUND);
                } else {
                    mCallBackActivity.getGPSPlotter().modifyServiceType(GPSPlotter.ServiceType.FOREGROUND);
                }
            }
        });
        backgroundCheckbox.setChecked(LocalStorage.getLocationRequestStatus(getActivity().getApplicationContext()));

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
                mCallBackActivity.getGPSPlotter().changeRequestIntervals(frequency * SAMPLE_CONVERSION, GPSPlotter.ServiceType.FOREGROUND);
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
        uploadButton.setText(getResources().getString(R.string.push_points_prompt1) + " " + mCallBackActivity.getNumLocallyStoredPoints() + " " + getResources().getString(R.string.push_points_prompt2));
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
            return mStartCalendar.getTimeInMillis() / TIMESTAMP_DIVISOR;
        } else {
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
        if (currentLocation != null) {
            setUploadButtonText();
        }
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
            if (selectedDatesOrdered()) {
                if (myBoundary.matches(START_RANGE)) {
                    mStartCalendar.set(year, monthOfYear, dayOfMonth, mStartCalendar.get(Calendar.HOUR_OF_DAY), mStartCalendar.get(Calendar.MINUTE), 0);
                    mStartDate.setText(getDate(mStartCalendar.getTime()));
                    LocalStorage.putStartTime(getUnixTimeStamp(START_RANGE), getActivity());
                    Log.w("Start Date Set: ", Long.toString(getUnixTimeStamp(START_RANGE)));
                } else {
                    mEndCalendar.set(year, monthOfYear, dayOfMonth, mEndCalendar.get(Calendar.HOUR_OF_DAY), mEndCalendar.get(Calendar.MINUTE), 0);
                    mEndDate.setText(getDate(mEndCalendar.getTime()));
                    LocalStorage.putEndTime(getUnixTimeStamp(END_RANGE), getActivity());
                    Log.w("End Date Set: ", Long.toString(getUnixTimeStamp(END_RANGE)));
                }
            } else {
                Toast.makeText(getActivity(), ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
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
            if (selectedDatesOrdered()) {
                if (myBoundary.matches(START_RANGE)) {
                    mStartCalendar.set(mStartCalendar.get(Calendar.YEAR), mStartCalendar.get(Calendar.MONTH), mStartCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
                    mStartTime.setText(getTime(mStartCalendar.getTime()));
                    LocalStorage.putStartTime(getUnixTimeStamp(START_RANGE), getActivity());
                    Log.w("Start Time Set: ", Long.toString(getUnixTimeStamp(START_RANGE)));
                } else {
                    mEndCalendar.set(mEndCalendar.get(Calendar.YEAR), mEndCalendar.get(Calendar.MONTH), mEndCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute, 0);
                    mEndTime.setText(getTime(mEndCalendar.getTime()));
                    LocalStorage.putEndTime(getUnixTimeStamp(END_RANGE), getActivity());
                    Log.w("End Time Set: ", Long.toString(getUnixTimeStamp(END_RANGE)));
                }
            } else {
                Toast.makeText(getActivity(), ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
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


