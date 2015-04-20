/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import db.User;
import db.UserStorageDatabaseHelper;


public class RegisterActivity extends ActionBarActivity {

    private static final int PASSWORD_LENGTH = 5;
    private static final String SPINNER = "spinner";
    private static final String PW_SUB = "passwordSubsequent";
    private static final String ANS_SUB = "answerSubsequent";

    /**
     * Instance of an array to hold all of the EditText widgets on the new user screen.
     */
    private EditText[] mNewUserFields;

    /**
     * Instance of a spinner to hold all the security questions for selection by the user.
     */
    private Spinner mQuestionSpinner;

    /**
     * Private field to hold a reference to the current user gleaned from User input.
     */
    private User myRegisteredUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle("");
        /** Instantiate the Array of Text Widgets.*/
        mNewUserFields = new EditText[RegisterField.getNumberIndices()];

        /** Instantiate the security question spinner.*/
        mQuestionSpinner = (Spinner) findViewById(R.id.spinnerSecurityQuestions);

        myRegisteredUser = null;

        /** Find all the EditText widgets.*/
        loadEditTextWidgets();

        /** Instance of the confirm button registered with the Confirm Listener.*/
        Button mConfirmButton = (Button) findViewById(R.id.confirmNewUserButton);
        mConfirmButton.setOnClickListener(new ConfirmUserListener());

        //This line keeps the email field from being selected automatically when the activity is started
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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
     * Overrides the onRestoreInstanceState and returns to the respective fields their unique user
     * entered values that were grabbed when orientation of the application was changed.
     *
     * @param savedInstanceState is the Bundle with saved values to be returned to the current application
     *                           context.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (!savedInstanceState.isEmpty()) {
            mNewUserFields[RegisterField.EMAIL_FIELD.indexValue].setText(savedInstanceState.getString(User.USER_EMAIL, ""));
            mNewUserFields[RegisterField.USER_NAME.indexValue].setText(savedInstanceState.getString(User.USER_NAME, ""));
            mNewUserFields[RegisterField.PASSWORD_INITIAL.indexValue].setText(savedInstanceState.getString(User.USER_PASSWORD, ""));
            mNewUserFields[RegisterField.PASSWORD_SUBSEQUENT.indexValue].setText(savedInstanceState.getString(PW_SUB, ""));
            mNewUserFields[RegisterField.SECURITY_ANSWER_INITIAL.indexValue].setText(savedInstanceState.getString(User.USER_ANSWER, ""));
            mNewUserFields[RegisterField.SECURITY_ANSWER_SUBSEQUENT.indexValue].setText(savedInstanceState.getString(ANS_SUB, ""));
            mQuestionSpinner.setSelection(savedInstanceState.getInt(SPINNER, 0));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    /**
     * Overrides the onRestoreInstanceState and returns to the respective fields their unique user
     * entered values that were grabbed when orientation of the application was changed.
     *
     * @param outState           is the Bundle that the transient user-entered variables need to be saved to.
     * @param outPersistentState is the bundle that persists throughout the application orientation change.
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(User.USER_EMAIL, mNewUserFields[RegisterField.EMAIL_FIELD.indexValue].getText().toString());
        outState.putString(User.USER_NAME, mNewUserFields[RegisterField.USER_NAME.indexValue].getText().toString());
        outState.putString(User.USER_PASSWORD, mNewUserFields[RegisterField.PASSWORD_INITIAL.indexValue].getText().toString());
        outState.putString(PW_SUB, mNewUserFields[RegisterField.PASSWORD_SUBSEQUENT.indexValue].getText().toString());
        outState.putString(User.USER_ANSWER, mNewUserFields[RegisterField.SECURITY_ANSWER_INITIAL.indexValue].getText().toString());
        outState.putString(ANS_SUB, mNewUserFields[RegisterField.SECURITY_ANSWER_SUBSEQUENT.indexValue].getText().toString());
        outState.putInt(SPINNER, mQuestionSpinner.getSelectedItemPosition());
        super.onSaveInstanceState(outState, outPersistentState);
    }

    /**
     * Private helper method to load the array of EditText widgets
     * with the appropriate number of fields necessary to register a
     * new user. Utilizes the register field enums to maintain a flexibility
     * with the number of register fields in the register activity.
     */
    private void loadEditTextWidgets() {
        mNewUserFields[RegisterField.EMAIL_FIELD.indexValue] = (EditText) findViewById(R.id.emailEdit);
        mNewUserFields[RegisterField.USER_NAME.indexValue] = (EditText) findViewById(R.id.userNameEdit);
        mNewUserFields[RegisterField.PASSWORD_INITIAL.indexValue] = (EditText) findViewById(R.id.passwordEditInitial);
        mNewUserFields[RegisterField.PASSWORD_SUBSEQUENT.indexValue] = (EditText) findViewById(R.id.passwordEditSubsequent);
        mNewUserFields[RegisterField.SECURITY_ANSWER_INITIAL.indexValue] = (EditText) findViewById(R.id.securityQuestionAnswerInitial);
        mNewUserFields[RegisterField.SECURITY_ANSWER_SUBSEQUENT.indexValue] = (EditText) findViewById(R.id.securityQuestionAnswerSubsequent);
    }

    /**
     * Private helper method to check if the passwords enter agree.
     *
     * @return the state of the passwords equivalence
     */
    private boolean passwordsAgree() {
        String initialPassword = mNewUserFields[RegisterField.PASSWORD_INITIAL.indexValue].getText().toString().trim();
        String copiedPassword = mNewUserFields[RegisterField.PASSWORD_SUBSEQUENT.indexValue].getText().toString().trim();
        return initialPassword.matches(copiedPassword);
    }

    /**
     * Private helper method to check if the password is long enough
     *
     * @return the state of the passwords length
     */
    private boolean passwordIsCorrectLength() {
        if (passwordsAgree())
            return mNewUserFields[RegisterField.PASSWORD_INITIAL.indexValue].getText().toString().trim().length() > PASSWORD_LENGTH;
        return false;
    }

    /**
     * Private helper method to check if the passwords enter agree.
     *
     * @return the state of the passwords equivalence
     */
    private boolean securityAnswersAgree() {
        String initialAnswer = mNewUserFields[RegisterField.SECURITY_ANSWER_INITIAL.indexValue].getText().toString().trim();
        String copiedAnswer = mNewUserFields[RegisterField.SECURITY_ANSWER_SUBSEQUENT.indexValue].getText().toString().trim();
        return initialAnswer.matches(copiedAnswer);
    }

    /**
     * Private helper method to check if the passwords enter agree.
     *
     * @return the state of the passwords equivalence
     */
    private boolean allFieldsEntered() {
        boolean allEntered = true;
        //Checks all EditText widgets
        for (EditText text : mNewUserFields) {
            if (text.getText().toString().trim().matches("")) {
                allEntered = false;
                break;
            }
        }
        //Checks to make sure that the user selected a question from the Spinner
        if (mQuestionSpinner.getSelectedItem().toString().matches(mQuestionSpinner.getPrompt().toString()))
            allEntered = false;

        return allEntered;
    }

    /**
     * Private helper method to determine where the first empty field
     * exists in the array of EditText widgets.
     *
     * @return index of the first empty EditText Widget
     */
    private int getFirstOccurrenceEmptyField() {
        int firstOccurrence = 0;

        for (int i = 0; i < mNewUserFields.length; i++) {
            if (mNewUserFields[i].getText().toString().trim().matches("")) {
                firstOccurrence = i;
                break;
            }
        }
        return firstOccurrence;
    }

    /**
     * Private helper method to add an entry to the database.
     */
    private boolean addEntryToDatabase() {
        UserStorageDatabaseHelper dBHelper = new UserStorageDatabaseHelper(getApplicationContext());
        long userAdded = dBHelper.insertUser(getNewUser());
        boolean added = true;
        if (userAdded == 0) {
            added = false;
        } else {
            myRegisteredUser.setID(dBHelper.obtainUserID(myRegisteredUser.getEmail()));
        }

        return added;
    }


    /**
     * Private helper method to generate a new user and add to the database via the helper.
     *
     * @return theNewUser
     */
    private User getNewUser() {
        myRegisteredUser = new User();

        /** String values to update the New User fields are gleaned from the EditText widgets.*/
        myRegisteredUser.setEmail(mNewUserFields[RegisterField.EMAIL_FIELD.indexValue].getText().toString().toLowerCase().trim());
        myRegisteredUser.setUserName(mNewUserFields[RegisterField.USER_NAME.indexValue].getText().toString().trim());
        myRegisteredUser.setPassword(mNewUserFields[RegisterField.PASSWORD_INITIAL.indexValue].getText().toString().trim());
        myRegisteredUser.setSecurityAnswer(mNewUserFields[RegisterField.SECURITY_ANSWER_INITIAL.indexValue].getText().toString().trim());
        myRegisteredUser.setResetStatus(0);

        /** String value to update the New User security question field is gleaned from the spinner.*/
        myRegisteredUser.setSecurityQuestion(mQuestionSpinner.getSelectedItem().toString().trim());

        return myRegisteredUser;
    }

    /**
     * Sets the UserPrefs based on the Registration information of the user that was created.
     */
    private void setPrefs() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(User.USER_PREFS, MODE_PRIVATE);

        prefs.edit().putLong(User.USER_ID, myRegisteredUser.getUserID()).apply();
        prefs.edit().putInt(User.USER_PASSWORD, myRegisteredUser.getPassword().trim().hashCode()).apply();
        prefs.edit().putString(User.USER_EMAIL, myRegisteredUser.getEmail().trim()).apply();
        prefs.edit().putString(User.USER_QUESTION, myRegisteredUser.getSecurityQuestion()).apply();
        prefs.edit().putString(User.USER_ANSWER, myRegisteredUser.getSecurityAnswer()).apply();

        SharedPreferences permPrefs = getSharedPreferences(User.PERM_PREFS, MODE_PRIVATE);
        permPrefs.edit().putString(User.USER_NAME, myRegisteredUser.getUserName()).apply();
    }


    /**
     * Private helper method to make a bad password toast. Passwords do not match
     */
    private void makeBadPasswordToast() {
        Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Private helper method to make a short password toast. Passwords are not long enough
     */
    private void makeShortPasswordToast() {
        Toast.makeText(getApplicationContext(), "Password is not long enough", Toast.LENGTH_SHORT).show();
    }

    /**
     * Private helper method to make a bad security answer toast. Answers do not match
     */
    private void makeBadSecurityAnswerToast() {
        Toast.makeText(getApplicationContext(), "Security Question Answers do not match!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Private helper method to make a bad field entry toast. Not all fields contain data.
     */
    private void makeBadFieldEntryToast() {
        Toast.makeText(getApplicationContext(), "All Fields must have values!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Private helper method to make a toast when a user already exists in the database.
     */
    private void makeDuplicateEntryToast() {
        Toast.makeText(getApplicationContext(), "That User already exists!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Private helper method to reset the fields in the Register Activity.
     */
    private void resetFields() {
        /** Reset the EditText widgets back to the original hints.*/
        mNewUserFields[RegisterField.EMAIL_FIELD.indexValue].setHint(mNewUserFields[RegisterField.EMAIL_FIELD.indexValue].getHint());
        mNewUserFields[RegisterField.USER_NAME.indexValue].setHint(mNewUserFields[RegisterField.USER_NAME.indexValue].getHint());
        mNewUserFields[RegisterField.PASSWORD_INITIAL.indexValue].setHint(mNewUserFields[RegisterField.PASSWORD_INITIAL.indexValue].getHint());
        mNewUserFields[RegisterField.PASSWORD_SUBSEQUENT.indexValue].setHint(mNewUserFields[RegisterField.PASSWORD_SUBSEQUENT.indexValue].getHint());
        mNewUserFields[RegisterField.SECURITY_ANSWER_INITIAL.indexValue].setHint(mNewUserFields[RegisterField.SECURITY_ANSWER_INITIAL.indexValue].getHint());
        mNewUserFields[RegisterField.SECURITY_ANSWER_SUBSEQUENT.indexValue].setHint(mNewUserFields[RegisterField.SECURITY_ANSWER_SUBSEQUENT.indexValue].getHint());

        /** Reset the Security Question spinner.*/
        mQuestionSpinner.setPrompt(mQuestionSpinner.getPrompt());
    }

    /**
     * Enum to help determine positions of integral EditText widgets within a larger array of widgets. Helps reduce code redundancy and
     * improves readability.
     */
    public enum RegisterField {
        EMAIL_FIELD(0), USER_NAME(1), PASSWORD_INITIAL(2), PASSWORD_SUBSEQUENT(3),
        SECURITY_ANSWER_INITIAL(4), SECURITY_ANSWER_SUBSEQUENT(5);

        public int indexValue;

        RegisterField(int index) {
            indexValue = index;
        }

        /**
         * If fields are adjusted in any way, this method needs to return the last enum declared above
         * so as to return the correct number of array indices.
         *
         * @return number of EditText instances
         */
        public static int getNumberIndices() {
            return RegisterField.SECURITY_ANSWER_SUBSEQUENT.ordinal() + 1;
        }
    }

    /**
     * @author leachad
     * @version 4.4.15
     *          <p/>
     *          Class that implements an OnClickListener. Moves all the hefty logic required for checking every
     *          aspect of a user into a separate inner class. Ensures that all fields are entered, passwords agree,
     *          security answers agree and that the user has indeed picked a security question.
     */
    public class ConfirmUserListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            //First, need to confirm that all appropriate initial, subsequent fields match
            if (passwordsAgree() && securityAnswersAgree() && allFieldsEntered() && passwordIsCorrectLength()) {

                boolean unique = addEntryToDatabase();
                if (!unique) {
                    resetFields();
                    makeDuplicateEntryToast();
                } else {
                    Toast.makeText(getApplicationContext(), "User Account Created!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MyAccount.class);
                    startActivity(intent);
                    setPrefs();
                    finish();
                }

            } else if (!passwordsAgree()) {
                makeBadPasswordToast();
                mNewUserFields[RegisterField.PASSWORD_INITIAL.indexValue].setText("");
                mNewUserFields[RegisterField.PASSWORD_SUBSEQUENT.indexValue].setText("");
            } else if (!securityAnswersAgree()) {
                makeBadSecurityAnswerToast();
                mNewUserFields[RegisterField.SECURITY_ANSWER_INITIAL.indexValue].setText("");
                mNewUserFields[RegisterField.SECURITY_ANSWER_SUBSEQUENT.indexValue].setText("");
            } else if (!allFieldsEntered()) {
                makeBadFieldEntryToast();
                int resumeCursor = getFirstOccurrenceEmptyField();
                mNewUserFields[resumeCursor].requestFocus();
            } else if(!passwordIsCorrectLength()) {
		makeShortPasswordToast();
		mNewuserFields[RegisterField.PASSWORD_INITIAL.indexValue].setText("");
		mNewUserFields[RegisterField.PASSWORD_SUBSEQUENT.indexValue].setText("");
		mNewUserFields[RegisterField.PASSWORD_INITIAL.indexValue].requestFocus();
        }
    }
}
