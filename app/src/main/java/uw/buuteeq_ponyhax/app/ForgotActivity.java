/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import db.UserStorageDatabaseHelper;
import webservices.WebDriver;
//import email.EmailSend;

/**
 * Created by eduard_prokhor on 4/4/15.
 * Edited by leach on 4/16/15
 * Edited by eduard on 5/1/15
 * <p/>
 * Forgot activity allows the user to generate a new password that will be sent to their
 * email address for signing back in. Utilizes the EmailSend class developed by Huy that
 * routes the email through a hosted site with the php file that kicks the auto-generated
 * password back to the users registered email account.
 */
public class ForgotActivity extends ActionBarActivity {

    /**
     * Variables referenced by the entire class.
     */
    //EmailSend mEmailSend;
    EditText mEmailEntryField;

    UserStorageDatabaseHelper mDbHelper;
    String mUserID;
    boolean isCurrentUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_page);
        setTitle("");

        //mEmailSend = new EmailSend();
        mEmailEntryField = (EditText) findViewById(R.id.userForPasswordreset);
        mDbHelper = new UserStorageDatabaseHelper(getApplicationContext());
        isCurrentUser = false;

        registerListeners();
    }

    private void registerListeners() {

        (findViewById(R.id.passResetCancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        (findViewById(R.id.passResetSubmit)).setOnClickListener(new SubmitListener());
    }

    private boolean checkTextEntered() {
        return (!mEmailEntryField.getText().toString().matches(""));
    }

    /**
     * Private class to implement a SubmitListener
     *
     * @author leachad
     * @author eprokhor
     * @version 4.29.15
     */
    private class SubmitListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (checkTextEntered()) {

                String userEmail = mEmailEntryField.getText().toString().trim();
                WebDriver.resetPassword(userEmail);

                Toast.makeText(getApplicationContext(),
                        "Your new password can be reset with the email link.",
                        Toast.LENGTH_LONG).show();
                finish();

            } else {
                Toast.makeText(getApplicationContext(),
                        "Email Must be vaild!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}