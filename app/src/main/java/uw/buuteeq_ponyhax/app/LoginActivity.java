/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import db.LocalStorage;
import webservices.WebDriver;

/**
 * This class propagates the LoginActivity and all the necessary widgets and conditions to check a
 * variety of conditions. The User may not exist, they may have mistyped fields. The User may be
 * returning from the application after requesting a new random password and needs to be redirected
 * to the reset password portion of the application. LoginActivity, upon correct entry and user is
 * located within database, sets the prefs to the necessary data fields that the user needs to
 * traverse the application.
 */
public class LoginActivity extends Activity {
    /**
     * Private static field to hold an error message.
     */
    private static final String MISSING_USER = "The User or Password is incorrect!";
    private static final int TIMESTAMP_DIVISOR = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Set the first page view with activity_login.xml. */
        setContentView(R.layout.activity_login);

        /** Registers the custom login listener with the Login Button.*/
        (findViewById(R.id.login_button)).setOnClickListener(new LoginListener());

        (findViewById(R.id.new_account_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, AgreementActivity.class);
                startActivity(myIntent);
            }
        });

        (findViewById(R.id.forgot_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, ForgotActivity.class);
                startActivity(myIntent);
            }
        });

        (findViewById(R.id.password_field)).setOnKeyListener(new View.OnKeyListener() {


            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    findViewById(R.id.login_button).callOnClick();
                    return true;
                }
                return false;
            }
        });

    }

    /**
     * Private method to show a Missing User Toast.
     */
    private void makeMissingUserToast() {
        Toast.makeText(getApplicationContext(), MISSING_USER, Toast.LENGTH_SHORT).show();
    }

    /**
     * Private helper method that will aid the LoginListener in determining
     * whether or not the user is already registered in the system.
     *
     * @return userFound
     */
    private boolean checkUserCredentials() {
        boolean toRet = false;
        EditText mPasswordField = (EditText) findViewById(R.id.password_field);
        EditText mEmailField = (EditText) findViewById(R.id.email_field);


        if (!mPasswordField.getText().toString().trim().matches("") && !mEmailField.getText().toString().trim().matches("")) {

            String userID = null;

            try {
                userID = WebDriver.checkUserCredentials(mEmailField.getText().toString().trim(), mPasswordField.getText().toString().trim());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            if (userID == null) {
                makeMissingUserToast();
            } else {
                toRet = true;
                LocalStorage.putUserId(userID, getApplicationContext());
                LocalStorage.putUserEmail(mEmailField.getText().toString().trim(), getApplicationContext());
                LocalStorage.putStartTime(0, getApplicationContext());
                LocalStorage.putEndTime(Calendar.getInstance().getTimeInMillis() / TIMESTAMP_DIVISOR, getApplicationContext());
                LocalStorage.putBackgroundRequestStatus(false, getApplicationContext());
                LocalStorage.putLocationRequestStatus(false, getApplicationContext());

            }
        }
        return toRet;

    }

    /**
     * Private class to implement a LoginListener
     *
     * @author leachad
     * @version 4.4.15
     */
    private class LoginListener implements View.OnClickListener {

        /**
         * Constructor in case we need to pass any data to the login
         * listener.
         */
        private LoginListener() {
            //Avoids instantiation of the default constructor
        }

        @Override
        public void onClick(View v) {
            if (checkUserCredentials()) {
                Intent intent;
                intent = new Intent(LoginActivity.this, MyAccount.class);
                startActivity(intent);
                finish();
            } else {
                ((EditText) findViewById(R.id.email_field)).setText("");
                ((EditText) findViewById(R.id.password_field)).setText("");
                (findViewById(R.id.email_field)).requestFocus();
                makeMissingUserToast();

            }
        }
    }
}
