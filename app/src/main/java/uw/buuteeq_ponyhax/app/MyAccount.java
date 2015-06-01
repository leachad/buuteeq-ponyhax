/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.LocalStorage;
import location_services.GPSPlotter;
import webservices.WebDriver;

public class MyAccount extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, MyAccountFragment.UIListUpdater {

    public static final String TAG = "Basic Network Demo";
    public static final String API_ERROR = "Try again soon. Api client currently disconnected.";
    private static final int DEFAULT_INTERVAL = 60;
    private static final int PUBLISH_INTERVAL = 5;
    // Whether there is a Wi-Fi connection.
    public static boolean wifiConnected = false;
    // Whether there is a mobile connection.
    public static boolean mobileConnected = false;
    private static MyAccount mAccountActivity;
    public int publishCounter = 0;
    public UIUpdater fragment;
    protected List<Coordinate> coordinates;
    private boolean wifizInTheHouse = false;
    private int mSelectedSampleRate = DEFAULT_INTERVAL;
    private GPSPlotter.ServiceType mServiceType = GPSPlotter.ServiceType.BACKGROUND;
    private GPSPlotter myGPSPlotter;
    private RadioGroup mRadioGroup;
    private RadioButton mStartButton;
    private RadioButton mStopButton;
    private CoordinateStorageDatabaseHelper coordHelper;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        if (coordinates == null) coordinates = new ArrayList<>();
        coordHelper = new CoordinateStorageDatabaseHelper(getApplicationContext());
        loadCoordinates();
        initializeInstance();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //START location manager setup
        LocalStorage.putDBFlag(true, getApplicationContext());

        // mSelectedSampleRate = some_var -- TODO This variable will be set by the power and network management classes

        initializeButtons();

        // network stuff
        checkNetworkConnection();
    }

    private void initializeInstance() {
        mAccountActivity = this;
    }

    private void selectStartButton() {
        mRadioGroup.clearCheck();
        mRadioGroup.check(mStartButton.getId());
    }

    private void selectStopButton() {
        mRadioGroup.clearCheck();
        mRadioGroup.check(mStopButton.getId());
    }

    /**
     * $ -- START & STOP ACTIONS DEPENDENT UPON APPLICATION CONDITIONS -- $
     * @param thePlotter is a reference to the GPSPlotter for initiating
     *                   specific tracking actions.
     */
    private void doStartSelectedAction(GPSPlotter thePlotter) {
        if (thePlotter.hasApiClientConnectivity() && mStartButton.isChecked()) {
            thePlotter.beginManagedLocationRequests(mSelectedSampleRate, mServiceType, this);

        } else if (thePlotter.hasApiClientConnectivity() && !mStartButton.isChecked()
                || !thePlotter.hasApiClientConnectivity() && !mStartButton.isChecked()) {
            selectStartButton();
            Toast.makeText(getApplicationContext(), API_ERROR, Toast.LENGTH_SHORT).show();

        } else if (!thePlotter.hasApiClientConnectivity() && mStartButton.isChecked()) {
            selectStopButton();
            Toast.makeText(getApplicationContext(), API_ERROR, Toast.LENGTH_SHORT).show();

        }
    }

    private void doStopSelectedAction(GPSPlotter thePlotter) {
        if (thePlotter.hasApiClientConnectivity() && mStopButton.isChecked()) {
            thePlotter.endManagedLocationRequests(mSelectedSampleRate, mServiceType);
        } else if (thePlotter.hasApiClientConnectivity() && !mStopButton.isChecked()
                || !thePlotter.hasApiClientConnectivity() && !mStopButton.isChecked()) {
            selectStartButton();
            Toast.makeText(getApplicationContext(), API_ERROR, Toast.LENGTH_SHORT).show();
        } else if (!thePlotter.hasApiClientConnectivity() && mStopButton.isChecked()) {
            selectStopButton();
            Toast.makeText(getApplicationContext(), API_ERROR, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     *
     * $ -- INITIALIZE THE TRACKING BUTTONS -- START & STOP -- $
     *
     */
    private void initializeButtons() {
        myGPSPlotter = GPSPlotter.getInstance(getApplicationContext());
        mStartButton = (RadioButton) findViewById(R.id.startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doStartSelectedAction(myGPSPlotter);
            }
        });
        mStopButton = (RadioButton) findViewById(R.id.stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doStopSelectedAction(myGPSPlotter);
            }
        });

        //Set the default clicked Radio Button
        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroupTracking);

        if (myGPSPlotter.isRunningLocationUpdates()) {
            selectStartButton();
        } else {
            selectStopButton();
        }

        setTitle(mTitle);
    }

    /**
     *
     *
     * $ -- EVALUATING NETWORK CONNECTIONS -- $
     * Check whether the device is connected, and if so, whether the connection
     * is wifi or mobile (it could be something else).
     */
    private boolean checkNetworkConnection() {

        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();

        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            if (wifiConnected) {
                wifizInTheHouse = true;
                Log.i(TAG, "@@The active connection is wifi.");
            } else if (mobileConnected) {
                Log.i(TAG, "@@The active connection is mobile.");
                wifizInTheHouse = false;
            }
        } else {
            Log.i(TAG, "@@No wireless or mobile connection.");
            wifizInTheHouse = false;
        }
        return wifizInTheHouse;

    }


    /**
     * Overrides the onRestoreInstanceState and maintains the state of tracking when screen is rotated.
     *
     * @param savedInstanceState is the Bundle with saved values to be returned to the current application
     *                           context.
     */
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        if (!savedInstanceState.isEmpty()) {
            if (savedInstanceState.getBoolean(mStartButton.getText().toString())) {
                mRadioGroup.check(mStartButton.getId());
            } else {
                mRadioGroup.check(mStopButton.getId());
            }

        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Overrides the onRestoreInstanceState and maintains the state of tracking when screen is rotated.
     *
     * @param outState           is the Bundle that the transient user-entered variables need to be saved to.
     * @param outPersistentState is the bundle that persists throughout the application orientation change.
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean(mStartButton.getText().toString(), mStartButton.isChecked());
        super.onSaveInstanceState(outState, outPersistentState);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        //Update fragment view on navigation selection
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        boolean choice = true;

        switch (position) {
            case 0:
                fragment = new MyMap();
                break;
            case 1:
                fragment = new MyAccountFragment();
                break;
            case 2:
                fragment = new SettingsFragment();
                break;
            case 3:
                LocalStorage.clearPrefs(getApplicationContext());
                choice = false;
                finish();
                break;
        }

        if (choice) {
            this.fragment = (UIUpdater) fragment;
            //this.fragment.update(mLastLocation, coordinates);
            //this.fragment.updateView();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }

    /**
     * Change title when navigation has been selected.
     *
     * @param number The nav item index
     */
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*if (!mNavigationDrawerFragment.isDrawerOpen()) {
            restoreActionBar();
            return true;
        }*/
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Public method to return a reference of myAccount to the
     * GPS Plotter class for updating points on the maps.
     */
    public static MyAccount getInstance() {
        return mAccountActivity;
    }

    /**
     * This method is meant to grab the remote coordinates so that a local list can be kept and save
     * data usage.
     */
    private void loadCoordinates() {

        coordinates = new ArrayList<>();

        //Naturally in time order due to the local points being the most recent
        List<Coordinate> moreCoords = coordHelper.getAllCoordinates(LocalStorage.getUserIDCoordinateQuery(getApplicationContext()));

        for (Coordinate c : moreCoords) {
            coordinates.add(c);
        }

        try {
            List<Coordinate> theList =
                    WebDriver.getLoggedCoordinates(LocalStorage.getUserID(getApplicationContext()),
                            LocalStorage.getStartTime(getApplicationContext()),
                            LocalStorage.getEndTimeCurrentTimeBackup(getApplicationContext()));

            if (theList != null) {
//                Toast.makeText(getApplicationContext(), "Web Driver list length " + theList.size(), Toast.LENGTH_SHORT).show();
                for (Coordinate c : theList) {
                    Log.w("UPDATE COORDINATE: ", c.toString());
                    coordinates.add(c);
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void addCoordinateToList(Coordinate coord) {
        coordinates.add(coord);

        if (publishCounter % PUBLISH_INTERVAL == 0 &&  publishCounter != 0 && checkNetworkConnection()) {
            coordHelper.publishCoordinateBatch(LocalStorage.getUserID(getApplicationContext()));
            publishCounter = 0;
            Log.w("PUBLISH: ", Integer.toString(publishCounter));
        }
    }



    @Override
    public List<Coordinate> getList() {
        Log.d("GETLIST CALL", "list returned length" + coordinates.size());
        return coordinates;
    }

    @Override
    public GPSPlotter getGPSPlotter() {
        return myGPSPlotter;
    }
}
