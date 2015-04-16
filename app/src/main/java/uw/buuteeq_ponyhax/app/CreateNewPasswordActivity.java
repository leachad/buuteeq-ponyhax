package uw.buuteeq_ponyhax.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import db.User;
import db.UserStorageDatabaseHelper;

public class CreateNewPasswordActivity extends Activity {


    private EditText mPassword;
    private EditText mPassConfirm;
    private Button mSubmitButton;
    private UserStorageDatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);


        mPassword = (EditText) findViewById(R.id.password_first);
        mPassConfirm = (EditText) findViewById(R.id.password_second);
        mSubmitButton = (Button) findViewById(R.id.newPasswordSubmit);


        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mPassword.getText().toString().trim().equals(mPassConfirm.getText().toString().trim())) {

                    //This should be done either way to ensure that our local copy of the user is up to date.
                    SharedPreferences prefs = getSharedPreferences(User.USER_PREFS, MODE_PRIVATE);
                    prefs.edit().putInt(User.USER_PASSWORD, mPassword.getText().hashCode()).commit();

                    //attempt to update the database with the new user password
                    helper = new UserStorageDatabaseHelper(getApplicationContext());
                    helper.modifyUserPassword(mPassword.getText().toString(), prefs.getLong(User.USER_ID, MODE_PRIVATE));

                    String email = getIntent().getStringExtra(User.USER_EMAIL);

                    if (!email.equals("")) {
                        SharedPreferences resetPrefs = getSharedPreferences(email, MODE_PRIVATE);
                        resetPrefs.edit().putBoolean(User.USER_RESET, false).commit();

                        Intent myIntent = new Intent(CreateNewPasswordActivity.this, MyAccount.class);
                        startActivity(myIntent);
                        finish();
                    } else {
                        throw new IllegalArgumentException("Passed empty email value");
                    }



                }

            }
        });


    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_create_new_password, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
