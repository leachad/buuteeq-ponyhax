package uw.buuteeq_ponyhax.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Utilization of the SQLiteOpenHelper to create a database for storing User Data for
 * account management.
 * Created by Andrew on 4/4/2015.
 */
public class UserStorageDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "user_storage.db";

    /**
     * Name and Column of the "key" table for accessing Users.
     */
    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL_ADDRESS = "email_address";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_SECURITY_QUESTION = "security_question";
    private static final String COLUMN_SECURITY_ANSWER = "security_answer";


    public UserStorageDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        /** Create the User Table.*/
        db.execSQL("create table user (" + "user_id integer primary key autoincrement, " +
                "username varchar(100), email_address varchar(100), " +
                "password varchar(100), security_question varchar(100), " +
                "security_answer varchar(100))");

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Understand what the onUpgrade() means, does, how it should be implemented
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Public helper method to return the current number of entries to the user.
     *
     * @return theNumEntries as a long
     */
    public long getNumEntries() {
        return DatabaseUtils.queryNumEntries(getReadableDatabase(), TABLE_USER);
    }

    /******************************WRITE TO THE DATABASE*******************************/
    /**
     * Publicly accessible method to add a new User to the database.
     *
     * @param user is the user passed to the database.
     */
    public long insertUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERNAME, user.getUserName());
        Log.d("CV USERNAME", user.getUserName() + " " + cv.get(COLUMN_USERNAME).toString());
        cv.put(COLUMN_EMAIL_ADDRESS, user.getEmail());
        Log.d("CV EMAIL", user.getEmail() + " " + cv.get(COLUMN_EMAIL_ADDRESS).toString());
        cv.put(COLUMN_PASSWORD, user.getPassword());
        Log.d("CV COLUMN_PASSWORD", cv.get(COLUMN_PASSWORD).toString());
        cv.put(COLUMN_SECURITY_QUESTION, user.getSecurityQuestion());
        Log.d("CV QUESTION", cv.get(COLUMN_SECURITY_QUESTION).toString());
        cv.put(COLUMN_SECURITY_ANSWER, user.getSecurityAnswer());
        Log.d("CV ANSWER", cv.get(COLUMN_SECURITY_ANSWER).toString());

        return getWritableDatabase().insert(TABLE_USER, null, cv);
    }

    /**
     * **************************READ FROM THE DATABASE******************************
     */
    public UserCursor queryUsers() {
        Cursor wrapped = getReadableDatabase()
                .query(TABLE_USER, null, null, null, null, null, COLUMN_USER_ID + " asc");
        return new UserCursor(wrapped);
    }


    /**
     * Instance of a private inner class that returns a Cursors that probes the rows
     * from the user table.
     */
    public static class UserCursor extends CursorWrapper {
        public UserCursor(Cursor c) {
            super(c);
        }

        /**
         * Returns a User object configured for the current row, or null
         * if the current row is invalid.
         */
        public User getUser() {
            User user = new User();
            if (isBeforeFirst() || isAfterLast())
                return null;
            user.setID(getLong(getColumnIndex(COLUMN_USER_ID)));
            user.setUserName(getString(getColumnIndex(COLUMN_USERNAME)));
            user.setEmail(getString(getColumnIndex(COLUMN_EMAIL_ADDRESS)));
            user.setPassword(getString(getColumnIndex(COLUMN_PASSWORD)));
            user.setSecurityQuestion(getString(getColumnIndex(COLUMN_SECURITY_QUESTION)));
            user.setSecurityAnswer(getString(getColumnIndex(COLUMN_SECURITY_ANSWER)));
            return user;
        }


        public long getUserID() {
            return getLong(getColumnIndex(COLUMN_USER_ID));
        }

        public String getUsername() {
            return getString(getColumnIndex(COLUMN_USERNAME));
        }

        public String getEmailAddress() {
            return getString(getColumnIndex(COLUMN_EMAIL_ADDRESS));
        }

        public String getPassword() {
            return getString(getColumnIndex(COLUMN_PASSWORD));
        }

        public String getSecurityQuestion() {
            return getString(getColumnIndex(COLUMN_SECURITY_QUESTION));
        }

        public String getSecurityAnswer() {
            return getString(getColumnIndex(COLUMN_SECURITY_ANSWER));
        }
    }
}
