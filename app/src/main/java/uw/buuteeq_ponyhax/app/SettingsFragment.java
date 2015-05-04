/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import db.User;
import webservices.JsonBuilder;
import webservices.WebDriver;


/**
 * Created by eduard_prokhor on 4/13/15.
 * edited by andrew leach on 4/17/15
 */
public class SettingsFragment extends Fragment {

    private static final String RESET_PROMPT = "Your password can be reset with the link sent to: ";
    private static final String RESET_FAILED = "Unable to execute reset request. Please try again later.";
    Button resetPassword;

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

            SharedPreferences prefs = getActivity().getSharedPreferences(User.USER_PREFS, FragmentActivity.MODE_PRIVATE);
            try {
                String result = WebDriver.resetPassword(prefs.getString(User.USER_EMAIL, null));

                if (result.matches(JsonBuilder.VAL_FAIL)) {
                    Toast.makeText(getActivity().getApplicationContext(), RESET_FAILED, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), RESET_PROMPT + prefs.getString(User.USER_EMAIL, null), Toast.LENGTH_LONG).show();
                    prefs.edit().clear().apply();
                    getActivity().finish();
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }


        }
    }

}
