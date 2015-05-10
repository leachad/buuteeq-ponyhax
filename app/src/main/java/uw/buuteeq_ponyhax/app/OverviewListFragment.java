/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

import db.Coordinate;
import db.User;
import webservices.WebDriver;

/**
 * Created by leachad on 5/9/2015. Used to display the list
 * of Coordinates that the user has obtained within the given
 * window set in the range chooser class
 */
public class OverviewListFragment extends ListFragment {

    private static final String ERROR_MESSAGE = "Unable to display coordinates for the user";
    private View mCurrentView;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String userID = getActivity().getApplicationContext().getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE).getString(User.USER_ID, null);
        long startDate = getActivity().getApplicationContext().getSharedPreferences(Coordinate.COORDINATE_PREFS, Context.MODE_PRIVATE).getLong(Coordinate.START_TIME, 0);
        long endDate = getActivity().getApplicationContext().getSharedPreferences(Coordinate.COORDINATE_PREFS, Context.MODE_PRIVATE).getLong(Coordinate.END_TIME, 0);

        List<Coordinate> coordinateList = null;
        try {
            coordinateList = WebDriver.getLoggedCoordinates(userID, startDate, endDate);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        if (coordinateList != null) {
            Log.d("SIZE OF COORD:OLF:", Integer.toString(coordinateList.size()));
            ArrayAdapter<Coordinate> coordinateArrayAdapter =
                    new ArrayAdapter<>(getActivity().getApplicationContext(),
                            android.R.layout.simple_list_item_1, coordinateList);
            setListAdapter(coordinateArrayAdapter);

        } else {
            Toast.makeText(getActivity().getApplicationContext(), ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        //TODO do something with the listitem
    }

}
