/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Range;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import db.Coordinate;
import db.CoordinateStorageDatabaseHelper;
import db.User;
import location_services.MyLocationManager;
import location_services.MyLocationReceiver;
import webservices.WebDriver;

public class MyAccount extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Used to store the last screen title.
     */
    private CharSequence mTitle;

    MyLocationManager myLocationManager;
    private Button mStartButton;
    private Button mStopButton;
    private Location mLastLocation;
    private SharedPreferences prefs;
    private UIUpdater fragment;
    private List<Coordinate> coordinates;

    //SETUP RECEIVER WITH INNER CLASS
    private BroadcastReceiver mLocationReceiver = new MyLocationReceiver() {

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation = location;
            if (location != null) {
                Toast.makeText(getApplicationContext(), "Got Coord", Toast.LENGTH_SHORT).show();
                //Make new coordinate and insert into coordinate database
                Coordinate locationCoordinate = new Coordinate(location.getLongitude(), location.getLatitude(), location.getTime(),
                        location.getSpeed(), location.getBearing(), prefs.getString(User.USER_ID, "N/A"));

                coordHelper.insertCoordinate(locationCoordinate);

                addCoordinateToList(locationCoordinate);

                fragment.update(mLastLocation, coordinates);

            }
//            myLocationManager = MyLocationManager.getInstance(getApplicationContext()); //reinstantiate in case wifi state changes
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            myLocationManager.startLocationUpdates((android.location.LocationListener) mLocationReceiver);
        }

        @Override
        public void onProviderEnabled(String provider) {
//            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
//            Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show(); //toast to show enabled
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

    };
    private CoordinateStorageDatabaseHelper coordHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        NavigationDrawerFragment mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        coordinates = new ArrayList<>();
        coordHelper = new CoordinateStorageDatabaseHelper(getApplicationContext());
        loadCoordinates();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //START location manager setup
        prefs = getSharedPreferences(User.USER_PREFS, MODE_PRIVATE);
        myLocationManager = MyLocationManager.getInstance(getApplicationContext());

        mStartButton = (Button) findViewById(R.id.startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocationManager.startLocationUpdates((android.location.LocationListener) mLocationReceiver);
                enabledStopButton();
            }
        });
        mStopButton = (Button) findViewById(R.id.stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myLocationManager.stopLocationUpdates((android.location.LocationListener) mLocationReceiver);
                enableStartButton();
            }
        });



        //END location manager setup
        registerReceiver(mLocationReceiver, new IntentFilter(MyLocationManager.ACTION_LOCATION));
        enabledStopButton();
        myLocationManager.startLocationUpdates((android.location.LocationListener) mLocationReceiver);
        setTitle("");
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mLocationReceiver);
        mStopButton.performClick();
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
                fragment = new RangePickerFragment();
                break;
            case 4:
                SharedPreferences prefs = getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
                prefs.edit().clear().apply();
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
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }
    }

    /**
     * Public method to restore the state of the action bar.
     */
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowTitleEnabled(true);
//        actionBar.setTitle(mTitle);
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

    private void enableStartButton() {
        mStartButton.setEnabled(true);
        mStopButton.setEnabled(false);
    }

    private void enabledStopButton() {
        mStopButton.setEnabled(true);
        mStartButton.setEnabled(false);
    }

    /**
     * This method is meant to grab the remote coordinates so that a local list can be kept and save
     * data usage.
     */
    private void loadCoordinates() {
        SharedPreferences prefs = getSharedPreferences(Coordinate.COORDINATE_PREFS, MODE_PRIVATE);
        SharedPreferences userPrefs = getSharedPreferences(User.USER_PREFS, MODE_PRIVATE);
        User theUser = new User();
        theUser.setID(userPrefs.getString(User.USER_ID, "0"));
        coordinates = new ArrayList<>();

        //Naturally in time order due to the local points being the most recent
        List<Coordinate> moreCoords = coordHelper.getAllCoordinates(getApplicationContext());
        for (Coordinate c: moreCoords) {
            coordinates.add(c);
        }

        try {
            List<Coordinate> theList = WebDriver.getLoggedCoordinates(theUser, prefs.getLong(Coordinate.START_TIME, 0), prefs.getLong(Coordinate.END_TIME, Calendar.getInstance().getTimeInMillis()));
            if (theList != null) {
                for (Coordinate c : theList) {
                    coordinates.add(c);
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Toast.makeText(getApplicationContext(), "load finished", Toast.LENGTH_SHORT).show();
    }

    private void addCoordinateToList(Coordinate coord) {
        coordinates.add(coord);
    }


}
