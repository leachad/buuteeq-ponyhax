package uw.buuteeq_ponyhax.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Set the first page view with activity_login.xml. */
        setContentView(R.layout.activity_login);

        //START ADDING LISTENERS

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

        //END ADDING LISTENERS
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

        /** If necessary to delete a localized database, comment in the line below.*/
        //getApplicationContext().deleteDatabase(mDbHelper.getDatabaseName());
        Toast.makeText(getApplicationContext(), "Database has " + mDbHelper.getNumEntries() + " entries", Toast.LENGTH_SHORT).show();

        if (!mPasswordField.getText().toString().trim().matches("") && !mEmailField.getText().toString().trim().matches("")
                && mDbHelper.obtainUserID(mEmailField.getText().toString().trim(), mPasswordField.getText().toString().trim()) != 0){


            long userID = mDbHelper.obtainUserID(mEmailField.getText().toString().trim(), mPasswordField.getText().toString().trim());

            if (mDbHelper.obtainUserEmail(userID).matches(mEmailField.getText().toString().trim())
                    && mDbHelper.obtainUserPassword(userID).matches(mPasswordField.getText().toString().trim())) {
                toRet = true;

                //setup shared preferences
                SharedPreferences prefs = getSharedPreferences(User.USER_PREFS, MODE_PRIVATE);

                prefs.edit().putLong(User.USER_ID, userID).apply();
                prefs.edit().putInt(User.USER_PASSWORD, mPasswordField.getText().toString().trim().hashCode()).apply();
                prefs.edit().putString(User.USER_EMAIL, mEmailField.getText().toString().trim()).apply();
                prefs.edit().putString(User.USER_QUESTION, mDbHelper.obtainUserSecurityQuestion(userID)).apply();
                prefs.edit().putString(User.USER_ANSWER, mDbHelper.obtainUserSecurityAnswer(userID)).apply();


            }
        }
        return toRet;

    }


    /**
     * Private class to implement a LoginListener
     *
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

                SharedPreferences resetPrefs = getSharedPreferences(User.PERM_PREFS, MODE_PRIVATE);
                boolean reset = resetPrefs.getBoolean(User.USER_RESET, true);
                Intent intent;
                if (!reset) {
                    intent = new Intent(LoginActivity.this, MyAccount.class);

                } else {
                    intent = new Intent(LoginActivity.this, CreateNewPasswordActivity.class);
                }
                startActivity(intent);
                finish();
            } else {
                ((EditText) findViewById(R.id.email_field)).setText("");
                ((EditText) findViewById(R.id.password_field)).setText("");
                (findViewById(R.id.email_field)).requestFocus();
                Toast.makeText(v.getContext().getApplicationContext(), "User not found!", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
