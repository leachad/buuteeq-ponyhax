package uw.buuteeq_ponyhax.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends ActionBarActivity {

    EditText mEmailField;
    EditText mPasswordField;
    Button mLoginButton;
    Button mCreateAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Grab instances of the email and password fields.*/
        mEmailField = (EditText) findViewById(R.id.email_field);
        mPasswordField = (EditText) findViewById(R.id.password_field);

        /** Grab instances of the login and new account buttons.*/
        mLoginButton = (Button) findViewById(R.id.login_button);
        mCreateAccountButton = (Button) findViewById(R.id.new_account_button);

        /** Set the first page view with activity_login.xml. */
        setContentView(R.layout.activity_login);

        (findViewById(R.id.new_account_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(myIntent);
                }
            });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

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

    /**
     * Private helper method that will aid the LoginListener in determining
     * whether or not the user is already registered in the system.
     * @return userFound
     */
    private boolean checkUserCredentials(){
        boolean userFound = false;

        /**
         * TODO I'll mimic the logic in here from the RegisterActivity class
         * for querying the database in order to determine if the user exists.
         */
        return userFound;
    }


    /**
     * Private class to implement a LoginListener
     * @author Andrew
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
               // TODO Decide what Activity will come next.
               // Example: Intent intent = new Intent(LoginActivity.this, AccountActivity.this);
            } else {
                // TODO Toast that displays an error message and clears the password field
            }
        }
    }
}
