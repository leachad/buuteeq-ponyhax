/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

import db.Coordinate;
import db.LocalStorage;
import db.User;
import webservices.JsonBuilder;
import webservices.WebDriver;


/**
 * Created by eduard_prokhor on 4/13/15.
 * edited by andrew leach on 4/17/15
 */
public class SettingsFragment extends Fragment implements UIUpdater {

    private static final String RESET_PROMPT = "Your password can be reset with the link sent to: ";
    private static final String RESET_FAILED = "Unable to execute reset request. Please try again later.";
    Button resetPassword;

    public void update(Location currentLocation, List<Coordinate> locations) {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.settings_fragment_layout, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        resetPassword = (Button) getActivity().findViewById(R.id.resetPasswordSettings);
        resetPassword.setOnClickListener(new ResetPasswordListener());

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

            String email =  LocalStorage.getUserEmail(getActivity());

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
