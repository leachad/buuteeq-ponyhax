/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import db.User;
import db.UserStorageDatabaseHelper;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Test
        WebDriver wd = new WebDriver();
        try {
            Log.e("TEST", wd.getUserAgreement());
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
        UserStorageDatabaseHelper mDbHelper = new UserStorageDatabaseHelper(getApplicationContext());

        /**
         * DEBUGGING: If necessary to delete a localized database, comment in the line below:
         * getApplicationContext().deleteDatabase(mDbHelper.getDatabaseName());
         *
         * DEBUGGING: If you need to show the number of entries, comment in the line below:
         * Toast.makeText(getApplicationContext(), "Database has " + mDbHelper.getNumEntries() + " entries", Toast.LENGTH_SHORT).show();
         */

        if (!mPasswordField.getText().toString().trim().matches("") && !mEmailField.getText().toString().trim().matches("")) {
            //String userID = mDbHelper.retrieveUniqueUserID(mEmailField.getText().toString().trim(), mPasswordField.getText().toString().trim());
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
                SharedPreferences prefs = getSharedPreferences(User.USER_PREFS, MODE_PRIVATE);
                prefs.edit().putString(User.USER_ID, userID).apply();

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

//                String email = ((EditText) findViewById(R.id.email_field)).getText().toString().toLowerCase().trim();
//                SharedPreferences resetPrefs = getSharedPreferences(email, MODE_PRIVATE);
//                boolean reset = resetPrefs.getBoolean(User.USER_RESET, false);
//                Intent intent;
//                intent = new Intent(LoginActivity.this, MyAccount.class);
//                if (reset) {
//                    intent = new Intent(LoginActivity.this, CreateNewPasswordActivity.class);
//                    intent.putExtra(User.USER_EMAIL, email);
//                } else {
//                    intent = new Intent(LoginActivity.this, MyAccount.class);
//                }

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
