/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import db.User;
import db.UserStorageDatabaseHelper;
import webservices.WebDriver;


/**
 * Created by eduard_prokhor on 4/13/15.
 * edited by andrew leach on 4/17/15
 */
public class SettingsFragment extends Fragment {

    private UserStorageDatabaseHelper mDbHelper;

    EditText emailFeildInSettings;
    Button resetPassFromSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emailFeildInSettings = (EditText) getActivity().findViewById(R.id.emailFeildInSettings);
        resetPassFromSettings = (Button) getActivity().findViewById(R.id.resetpass_from_settings);
        resetPassFromSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkTextEntered()) {
                    String userEmail = emailFeildInSettings.getText().toString().trim();
                    WebDriver.resetPassword(userEmail);
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Your new password can be reset with the email " +
                                    "link that was sent to your email.",
                            Toast.LENGTH_LONG).show();
                    getActivity().finish();
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Email must be vaild!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.settings_fragment_layout, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();


    }
    private boolean checkTextEntered() {
        return (!emailFeildInSettings.getText().toString().matches(""));
    }

}
