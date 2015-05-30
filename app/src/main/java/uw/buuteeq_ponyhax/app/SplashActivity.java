/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;

import java.util.concurrent.ExecutionException;

import db.LocalStorage;
import webservices.WebDriver;

/**
 * SplashActivity to display a timed "entry" to the application. Displays the globe, application
 * title, and info about the authors of the application.
 */
public class SplashActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Thread myThread = new Thread() {

            @Override
            public void run() {

                try {

                    sleep(3000);
                    checkScreen();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    finish();
                }

            }


        };

        myThread.start();

    }


    private void checkScreen() {

        Intent myIntent;
        try {
            String agreement = WebDriver.getUserAgreement();
            if (agreement != null) {
                LocalStorage.putUserAgreement(Html.fromHtml(agreement).toString(), getApplicationContext());
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        if (LocalStorage.getUserID(getApplicationContext()) != null) {
            myIntent = new Intent(SplashActivity.this, MyAccount.class);
        } else {
            myIntent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(myIntent);

    }


}
