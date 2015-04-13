package uw.buuteeq_ponyhax.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

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
        SharedPreferences prefs = getSharedPreferences(User.USER_PREFS, MODE_PRIVATE);
        if (prefs != null && prefs.contains(User.USER_ID)) {
            myIntent = new Intent(SplashActivity.this, MyAccount.class);
        } else {
            myIntent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(myIntent);

    }


}
