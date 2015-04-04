package uw.buuteeq_ponyhax.phase_one;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterActivity extends ActionBarActivity {

    /**
     * Instance of the SQLite database to hold UserData.
     */
    private UserStorageDatabaseHelper mDbHelper;

    /**
     * Instance of an array to hold all of the EditText widgets on the new user screen.
     */
    private EditText[] mNewUserFields = new EditText[RegisterField.getNumberIndices()];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mDbHelper = new UserStorageDatabaseHelper(getApplicationContext());
        /** Find all the EditText widgets.*/
        loadEditTextWidgets();

        /** Instance of the confirm button registered with the Confirm Listener.*/
        Button mConfirmButton = (Button) findViewById(R.id.confirmNewUserButton);
        mConfirmButton.setOnClickListener(new ConfirmUserListener());
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
     * Private helper method to load the array of EditText widgets
     * with the appropriate number of fields necessary to register a
     * new user. Utilizes the register field enums to maintain a flexibility
     * with the number of register fields in the register activity.
     */
    private void loadEditTextWidgets() {
        mNewUserFields[RegisterField.EMAIL_FIELD.indexValue] = (EditText) findViewById(R.id.emailEdit);
        mNewUserFields[RegisterField.USER_NAME.indexValue] = (EditText) findViewById(R.id.emailEdit);
        mNewUserFields[RegisterField.PASSWORD_INITIAL.indexValue] = (EditText) findViewById(R.id.emailEdit);
        mNewUserFields[RegisterField.PASSWORD_SUBSEQUENT.indexValue] = (EditText) findViewById(R.id.emailEdit);
        mNewUserFields[RegisterField.SECURITY_QUESTION.indexValue] = (EditText) findViewById(R.id.emailEdit);
        mNewUserFields[RegisterField.SECURITY_ANSWER_INITIAL.indexValue] = (EditText) findViewById(R.id.emailEdit);
        mNewUserFields[RegisterField.SECURITY_ANSWER_SUBSEQUENT.indexValue] = (EditText) findViewById(R.id.emailEdit);
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
        for (EditText text : mNewUserFields) {
            if (text.getText().toString().trim().matches("")) {
                allEntered = false;
                break;
            }
        }
        return allEntered;
    }

    /**
     * Private helper method to add an entry to the database.
     * TODO Determine if we need an entry id for the table.
     */
    private void addEntryToDatabase() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(UserStorageContract.UserStorageEntry.USER_ENTRY_ID, mNewUserFields[RegisterField.EMAIL_FIELD.indexValue].hashCode());
        values.put(UserStorageContract.UserStorageEntry.USERNAME, mNewUserFields[RegisterField.USER_NAME.indexValue].getText().toString());
        values.put(UserStorageContract.UserStorageEntry.EMAIL_ADDRESS, mNewUserFields[RegisterField.EMAIL_FIELD.indexValue].getText().toString());
        values.put(UserStorageContract.UserStorageEntry.PASSWORD, mNewUserFields[RegisterField.PASSWORD_INITIAL.indexValue].getText().toString());
        values.put(UserStorageContract.UserStorageEntry.SECURITY_QUESTION, mNewUserFields[RegisterField.SECURITY_QUESTION.indexValue].getText().toString());
        values.put(UserStorageContract.UserStorageEntry.SECURITY_ANSWER, mNewUserFields[RegisterField.SECURITY_ANSWER_INITIAL.indexValue].getText().toString());

        //TODO Determine if we need the returned long from insert a row into the table
        db.insert(UserStorageContract.UserStorageEntry.TABLE_NAME, UserStorageContract.UserStorageEntry.COLUMN_NAME_NULLABLE, values);
    }

    /**
     * Private helper method to make a bad password toast. Passwords do not match
     */
    private void makeBadPasswordToast() {
        Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_LONG).show();
    }

    /**
     * Private helper method to make a bad security answer toast. Answers do not match
     */
    private void makeBadSecurityAnswerToast() {
        Toast.makeText(getApplicationContext(), "Security Question Answers do not match!", Toast.LENGTH_LONG).show();
    }

    /**
     * Private helper method to make a bad field entry toast. Not all fields contain data.
     */
    private void makeBadFieldEntryToast() {
        Toast.makeText(getApplicationContext(), "All Fields must have values!", Toast.LENGTH_LONG).show();
    }

    public static enum RegisterField {
        EMAIL_FIELD(0), USER_NAME(1), PASSWORD_INITIAL(2), PASSWORD_SUBSEQUENT(3), SECURITY_QUESTION(4),
        SECURITY_ANSWER_INITIAL(5), SECURITY_ANSWER_SUBSEQUENT(6);

        public int indexValue;

        private RegisterField(int index) {
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

    public class ConfirmUserListener implements View.OnClickListener {

        public ConfirmUserListener() {
            //useful if we need to instantiate specific fields
        }

        @Override
        public void onClick(View v) {
            //First, need to confirm that all appropriate initial, subsequent fields match
            if (passwordsAgree() && securityAnswersAgree() && allFieldsEntered()) {
                addEntryToDatabase();
                // TODO Check for duplicate entries
                // TODO Clear all text fields.

            } else if (!passwordsAgree() || !securityAnswersAgree() || !allFieldsEntered()) {
                makeBadPasswordToast();
                makeBadFieldEntryToast();
                makeBadSecurityAnswerToast();
                // TODO Clear password entry fields
            }
        }
    }
}
