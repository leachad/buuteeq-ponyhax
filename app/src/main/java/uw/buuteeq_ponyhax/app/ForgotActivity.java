package uw.buuteeq_ponyhax.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import db.User;
import db.UserStorageDatabaseHelper;
import email.EmailSend;

/**
 * Created by eduard_prokhor on 4/4/15.
 *
 * Edited by leach on 4/16/15
 */
public class ForgotActivity extends ActionBarActivity {

    /**
     * Variables referenced by the entire class.
     */
    EmailSend mEmailSend;
    EditText mEmailEntryField;
    EditText mSecurityAnswerField;
    UserStorageDatabaseHelper mDbHelper;
    long mUserID;
    boolean isCurrentUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_page);
        setTitle("");

        mEmailSend = new EmailSend();
        mEmailEntryField = (EditText) findViewById(R.id.userForPasswordreset);
        mSecurityAnswerField = (EditText) findViewById(R.id.resetPassSecAnswer);
        mSecurityAnswerField.setEnabled(false); //disabled until user types correct email address
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

        mEmailEntryField.setOnEditorActionListener(new DisplayQuestionListener());


        (findViewById(R.id.passResetSubmit)).setOnClickListener(new SubmitListener());


    }

    private boolean checkTextEntered() {
        if (mSecurityAnswerField.getText().toString().matches("") && mEmailEntryField.getText().toString().matches("")
            || !mSecurityAnswerField.getText().toString().matches("") && mEmailEntryField.getText().toString().matches("")
            || mSecurityAnswerField.getText().toString().matches("") && !mEmailEntryField.getText().toString().matches("")) {
            return false;
        } else {
            return true;
        }
    }

    private class SubmitListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (checkTextEntered()
                    && mSecurityAnswerField.getText().toString().trim().matches(mDbHelper.obtainUserSecurityAnswer(mUserID))
                    && mEmailEntryField.getText().toString().matches(mDbHelper.obtainUserEmail(mUserID))) {

                String userEmail = mDbHelper.obtainUserEmail(mUserID).trim();

                //make a random pass and send it to their email.
                String testPass = Long.toHexString(Double.doubleToLongBits(Math.random()));

                //update the database with the new pass of the user
                mDbHelper.modifyUserPassword(testPass, mUserID);

                //testing to send to my email.
                mEmailSend.sendEmail(userEmail, testPass);
                TextView newPassword = (TextView) findViewById(R.id.passwordResetField);
                newPassword.append(testPass);
                Log.d("TEST PASSWORD: ", testPass);

                Toast.makeText(getApplicationContext(),
                        "Your new randomly generated password was sent to your email", Toast.LENGTH_SHORT).show();

                //create prefs from email so that it is independent from other email resets.
                SharedPreferences resetPrefs = getSharedPreferences(userEmail, MODE_PRIVATE);
                resetPrefs.edit().putBoolean(User.USER_RESET, true).apply();

                finish();


            } else {
                Toast.makeText(getApplicationContext(), "New Answer Must Consist of at least one character!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class DisplayQuestionListener implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_NEXT && !mEmailEntryField.getText().toString().trim().matches("")) {
                String userEmail = mEmailEntryField.getText().toString().toLowerCase().trim();
                mUserID = mDbHelper.obtainUserID(userEmail);
                if (mDbHelper.obtainUserEmail(mUserID) != null && mDbHelper.obtainUserEmail(mUserID).matches(userEmail)) {
                    isCurrentUser = true;
                    mSecurityAnswerField.setEnabled(true);
                    ((TextView) findViewById(R.id.resetSecurityQuestion)).setText(mDbHelper.obtainUserSecurityQuestion(mDbHelper.obtainUserID(userEmail)));
                } else {
                    Toast.makeText(getApplicationContext(), "Email not found in the database", Toast.LENGTH_SHORT).show();
                    mEmailEntryField.setText("");
                    mEmailEntryField.requestFocus();
                }

            }
            return false;
        }

    }

}