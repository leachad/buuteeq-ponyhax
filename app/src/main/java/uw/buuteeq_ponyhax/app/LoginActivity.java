package uw.buuteeq_ponyhax.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private UserStorageDatabaseHelper mDbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Initialize the database helper. */
        mDbHelper = new UserStorageDatabaseHelper(getApplicationContext());

        mDbHelper = new UserStorageDatabaseHelper(getApplicationContext());
        // getApplicationContext().deleteDatabase(UserStorageDatabaseHelper.DATABASE_NAME);
        Toast.makeText(getApplicationContext(), "Database has " +
                        DatabaseUtils.queryNumEntries(mDbHelper.getReadableDatabase(),
                                UserStorageContract.UserStorageEntry.TABLE_NAME) + " entries",
                Toast.LENGTH_SHORT).show();

        /** Set the first page view with activity_login.xml. */
        setContentView(R.layout.activity_login);

        //START ADDING LISTENERS

        /** Registers the custom login listener with the Login Button.*/
        (findViewById(R.id.login_button)).setOnClickListener(new LoginListener());

        (findViewById(R.id.new_account_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
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

        if (!mPasswordField.getText().toString().trim().matches("") || !mEmailField.getText().toString().trim().matches("")) {
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            String[] projection = {UserStorageContract.UserStorageEntry.USER_ENTRY_ID,
                    UserStorageContract.UserStorageEntry.USERNAME, UserStorageContract.UserStorageEntry.EMAIL_ADDRESS,
                    UserStorageContract.UserStorageEntry.PASSWORD, UserStorageContract.UserStorageEntry.SECURITY_QUESTION,
                    UserStorageContract.UserStorageEntry.SECURITY_ANSWER};

            String sortOrder = UserStorageContract.UserStorageEntry.EMAIL_ADDRESS + " DESC";

            String selection = UserStorageContract.UserStorageEntry.EMAIL_ADDRESS + " LIKE?";

            String[] selectionArgs = {String.valueOf(mEmailField.getText().toString().trim().hashCode())};


            Cursor c = null;
//                    db.query(
//                    UserStorageContract.UserStorageEntry.TABLE_NAME,
//                    projection,
//                    selection,
//                    selectionArgs,
//                    null,
//                    null,
//                    sortOrder
//            );
//
//            c.moveToFirst();

            long userID = c.getLong(c.getColumnIndexOrThrow(UserStorageContract.UserStorageEntry.USER_ENTRY_ID));
            //  c.close();

            toRet = (userID == mEmailField.getText().toString().trim().hashCode());

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
                Intent intent = new Intent(LoginActivity.this, MyAccount.class);
                startActivity(intent);
            } else {
                ((EditText) findViewById(R.id.email_field)).setText("");
                ((EditText) findViewById(R.id.password_field)).setText("");
                ((EditText) findViewById(R.id.email_field)).requestFocus();
                Toast.makeText(getApplicationContext(), "User not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
