/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import db.User;

/**
 * Class that propagates to the screen what the user is accepting by downloading our
 * GeoTracker application. Makes the user aware of Camera, GPS, and Internet usage.
 */
public class AgreementActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agreement);
        setTitle("");
        SharedPreferences prefs = getSharedPreferences(User.USER_PREFS, MODE_PRIVATE);
        TextView agreement = (TextView) findViewById(R.id.userAgreementView);

        String agreementText = prefs.getString(User.USER_AGREEMENT, null);
        if (agreementText != null) {
            agreementText = agreementText.replace("<h2>", "");
            agreementText = agreementText.replace("</h2>", "");
            agreementText = agreementText.replace("<h3>", "");
            agreementText = agreementText.replace("</h3>", "");
            agreementText = agreementText.replace("<p>", "");
            agreementText = agreementText.replace("</p>", "");
            agreementText = agreementText.replace("<ol type=\"a\">", "");
            agreementText = agreementText.replace("<li>", "");
            agreementText = agreementText.replace("<ol type=\"i\">", "");
            agreementText = agreementText.replace("</li>", "");
            agreementText = agreementText.replace("</ol>", "");
            agreementText = agreementText.replace("<ul>", "");
            agreementText = agreementText.replace("</ul>", "");

            agreement.setText(agreementText);
        }


        Button agreeButton = (Button) findViewById(R.id.agreeButton);
        agreeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AgreementActivity.this, RegisterActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
                finish();
            }
        });

        Button disagreeButton = (Button) findViewById(R.id.disagreeButton);
        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(AgreementActivity.this, LoginActivity.class);
                startActivity(myIntent);
                finish();

            }
        });
    }

    /**
     * Overrides the onCreateOptionsMenu to add the agreement
     * menu to the actionbar if the actionbar is present
     *
     * @param menu is the menu item passed from the calling code
     * @return boolean determining success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_agreement, menu);
        return true;
    }

    /**
     * Overrides the onOptionsItemSelected menu that determines
     * behavior if user clicks on home/ up buttons.
     *
     * @param item is the menu item passed from the calling code
     * @return boolean determining success of super method
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
