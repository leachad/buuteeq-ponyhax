/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import db.User;
import db.UserStorageDatabaseHelper;

/**
 * CreateNewPasswordActivity is utilized in a couple different areas. Mainly it is called when
 * User requests a password reset. It also "sits" in the Navigation drawer so that if a user is
 * logged in they can change their password while in the system.
 */
public class CreateNewPasswordActivity extends Activity {


    private EditText mPassword;
    private EditText mPassConfirm;
    private UserStorageDatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);


        mPassword = (EditText) findViewById(R.id.password_first);
        mPassConfirm = (EditText) findViewById(R.id.password_second);
        findViewById(R.id.newPasswordSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPassword.getText().toString().trim().equals(mPassConfirm.getText().toString().trim())) {

                    //This should be done either way to ensure that our local copy of the user is up to date.
                    SharedPreferences prefs = getSharedPreferences(User.USER_PREFS, MODE_PRIVATE);
                    prefs.edit().putInt(User.USER_PASSWORD, mPassword.getText().hashCode()).apply();

                    //attempt to update the database with the new user password
                    helper = new UserStorageDatabaseHelper(getApplicationContext());
                    helper.modifyUserPassword(mPassword.getText().toString(), prefs.getLong(User.USER_ID, MODE_PRIVATE));

                    String email = getIntent().getStringExtra(User.USER_EMAIL);

                    if (!email.equals("")) {
                        SharedPreferences resetPrefs = getSharedPreferences(email, MODE_PRIVATE);
                        resetPrefs.edit().putBoolean(User.USER_RESET, false).apply();
                        Toast.makeText(getApplicationContext(), "Password Reset!", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(CreateNewPasswordActivity.this, MyAccount.class);
                        startActivity(myIntent);
                        finish();
                    } else {
                        throw new IllegalArgumentException("Passed empty email value");
                    }


                }

            }
        });

        findViewById(R.id.newPasswordCancel).setOnClickListener(new View.OnClickListener(){
           @Override
            public void onClick(View v) {
               startActivity(new Intent(CreateNewPasswordActivity.this, MyAccount.class));
               finish();
           }
        });


    }
}
