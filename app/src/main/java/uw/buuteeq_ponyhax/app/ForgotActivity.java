/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import db.LocalStorage;
import webservices.JsonBuilder;
import webservices.WebDriver;

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

    private static final String RESET_PROMPT = "Your password can be reset with the link sent to: ";
    private static final String RESET_FAILED = "Unable to execute reset request. Please try again later.";

    boolean isCurrentUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_page);
        setTitle("");
        isCurrentUser = false;
        (findViewById(R.id.passResetCancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        (findViewById(R.id.passResetSubmit)).setOnClickListener(new ResetPasswordListener());

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

            EditText resetEmail = (EditText) findViewById(R.id.notLoggedInPassReset);


            try {
                String result = WebDriver.resetPassword(resetEmail.getText().toString());

                if (result.matches(JsonBuilder.VAL_FAIL)) {
                    Toast.makeText(getApplicationContext(), RESET_FAILED, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), RESET_PROMPT + resetEmail.getText().toString(), Toast.LENGTH_SHORT).show();
                    LocalStorage.clearPrefs(getApplicationContext());
                    finish();
                }
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }


        }
    }
}