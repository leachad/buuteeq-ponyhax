/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;

import db.CoordinateStorageDatabaseHelper;
import db.User;
import location_services.MyLocationManager;
import location_services.MyLocationReceiver;

public class MyAccount extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Used to store the last screen title.
     */
    private CharSequence mTitle;

    private Button mStartButton;
    private Button mStopButton;
    private Location mLastLocation;
    //SETUP RECEIVER WITH INNER CLASS
    private BroadcastReceiver mLocationReceiver = new MyLocationReceiver() {

        @Override
        protected void onLocationReceived(Context context, Location location) {
            Date polledDate = new Date(); //grabs date stamp
            mLastLocation = location;
            if (mLastLocation != null) { //This may or may not be a good condition
                Toast.makeText(getApplicationContext(), location.toString(), Toast.LENGTH_SHORT).show(); // for testing polling rates
//                updateUI();
                //add to database here and update UI appropriately
            }

            //After everything -- reset LocationManager to reset data polling rate
            MyLocationManager.getInstance(getApplicationContext()); //This could be the cause of an infinite loop if it polls right away..a slow yet battery sucking infinite loop
        }

        @Override
        protected void onProviderEnabledChanged(boolean enabled) {
            int toastText = enabled ? R.string.gps_enabled : R.string.gps_disabled;
            Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG).show(); //toast to show enabled
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

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //START location manager setup
        mStartButton = (Button) findViewById(R.id.startButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLocationManager.getInstance(getApplicationContext()).startLocationUpdates();
                enabledStopButton();
            }
        });
        mStopButton = (Button) findViewById(R.id.stopButton);
        mStopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyLocationManager.getInstance(getApplicationContext()).stopLocationUpdates();
                enableStartButton();
            }
        });
        //END location manager setup

        registerReceiver(mLocationReceiver, new IntentFilter(MyLocationManager.ACTION_LOCATION));
        enabledStopButton();
        setTitle("");
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mLocationReceiver);
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
                SharedPreferences prefs = getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
                prefs.edit().clear().apply();
                choice = false;
                finish();
                break;
        }

        if (choice) {
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

}
