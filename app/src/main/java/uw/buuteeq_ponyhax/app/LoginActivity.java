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
    private UserStorageDatabaseHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Initialize the database helper. */
        mDbHelper = new UserStorageDatabaseHelper(getApplicationContext());

        mDbHelper = new UserStorageDatabaseHelper(getApplicationContext());
        // getApplicationContext().deleteDatabase(UserStorageDatabaseHelper.DATABASE_NAME

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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_login, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

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

        UserStorageDatabaseHelper helper = new UserStorageDatabaseHelper(getApplicationContext());
        UserStorageDatabaseHelper.UserCursor cursor = helper.queryUsers();
        /** If necessary to delete a localized database, comment in the line below.*/
       // getApplicationContext().deleteDatabase(helper.getDatabaseName());
        Toast.makeText(getApplicationContext(), "Database has " + helper.getNumEntries() + " entries", Toast.LENGTH_SHORT).show();

        if (!mPasswordField.getText().toString().trim().matches("") && !mEmailField.getText().toString().trim().matches("")) {


            long userID = helper.obtainUserID(mEmailField.getText().toString().trim(), mPasswordField.getText().toString().trim());

            if (helper.obtainUserEmail(userID).matches(mEmailField.getText().toString().trim())
                        && helper.obtainUserPassword(userID).matches(mPasswordField.getText().toString().trim())) {
                    toRet = true;

                //setup shared preferences
                SharedPreferences prefs = getSharedPreferences(User.USER_PREFS, MODE_PRIVATE);

                prefs.edit().putLong(User.USER_ID, userID).commit();
                prefs.edit().putInt(User.USER_PASSWORD, mPasswordField.getText().toString().trim().hashCode()).commit();
                prefs.edit().putString(User.USER_EMAIL, mEmailField.getText().toString().trim()).commit();
                prefs.edit().putString(User.USER_QUESTION, helper.obtainUserSecurityQuestion(userID)).commit();
                prefs.edit().putString(User.USER_ANSWER, helper.obtainUserSecurityAnswer(userID)).commit();


             }
//
//            while (cursor.moveToNext()) {
//                User temp = cursor.getUser();
//                if (temp.getEmail().trim().matches(mEmailField.getText().toString().trim())
//                        && temp.getPassword().trim().matches(mPasswordField.getText().toString().trim())) {
//                    toRet = true;
//                    break;
//                }
//
//            }
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
