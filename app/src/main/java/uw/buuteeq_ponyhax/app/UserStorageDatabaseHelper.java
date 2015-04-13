package uw.buuteeq_ponyhax.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

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
    private static final String COLUMN_ISSUED_RESET = "issued_reset";


    public UserStorageDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        /** Create the User Table.*/
        db.execSQL("create table user (" + "user_id integer primary key autoincrement, " +
                "username varchar(100), email_address varchar(100), " +
                "password varchar(100), security_question varchar(100), " +
                "security_answer varchar(100), issued_reset integer )");

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
        cv.put(COLUMN_EMAIL_ADDRESS, user.getEmail());
        cv.put(COLUMN_PASSWORD, user.getPassword());
        cv.put(COLUMN_SECURITY_QUESTION, user.getSecurityQuestion());
        cv.put(COLUMN_SECURITY_ANSWER, user.getSecurityAnswer());
        cv.put(COLUMN_ISSUED_RESET, user.getResetStatus());

        return getWritableDatabase().insert(TABLE_USER, null, cv);
    }


    /**
     * Publicly accessible method to modify the password of a given User.
     * Does NOT check for null values.
     *
     * @param theNewPassword is the newly generated password
     * @param theUserRowID   is the row id for the current user
     * @return updateConfirmation
     */
    public long modifyUserPassword(final String theNewPassword, final long theUserRowID) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PASSWORD, theNewPassword);
        return getWritableDatabase().update(TABLE_USER, cv, (COLUMN_USER_ID + " " + "= " + Long.toString(theUserRowID)), null);
    }

    /**
     * Publicly accessible method to modify the username of a given User.
     * Does NOT check for null values.
     *
     * @param theNewUsername is the new username as changed by the user
     * @param theUserRowID   is the row id for the current user
     * @return updateConfirmation
     */
    public long modifyUsername(final String theNewUsername, final long theUserRowID) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_USERNAME, theNewUsername);
        return getWritableDatabase().update(TABLE_USER, cv, (COLUMN_USER_ID + " " + "= " + Long.toString(theUserRowID)), null);

    }

    /**
     * Publicly accessible method to modify the email address of a given User.
     * Does NOT check for null values.
     *
     * @param theNewEmailAddress is the new email address as changed by the user
     * @param theUserRowID       is the row id for the current user
     * @return updateConfirmation
     */
    public long modifyEmailAddress(final String theNewEmailAddress, final long theUserRowID) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EMAIL_ADDRESS, theNewEmailAddress);
        return getWritableDatabase().update(TABLE_USER, cv, (COLUMN_USER_ID + " " + "= " + Long.toString(theUserRowID)), null);
    }

    /**
     * Publicly accessible method to modify the security question of a given User.
     * Does NOT check for null values.
     *
     * @param theNewSecurityQuestion is the new security question as changed by the user
     * @param theUserRowID           is the row id for the current user
     * @return updateConfirmation
     */
    public long modifySecurityQuestion(final String theNewSecurityQuestion, final long theUserRowID) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SECURITY_QUESTION, theNewSecurityQuestion);
        return getWritableDatabase().update(TABLE_USER, cv, (COLUMN_USER_ID + " " + "= " + Long.toString(theUserRowID)), null);
    }

    /**
     * Publicly accessible method to modify the security answer of a given User.
     * Does NOT check for null values.
     *
     * @param theNewSecurityAnswer is the new security question as changed by the user
     * @param theUserRowID         is the row id for the current user
     * @return updateConfirmation
     */
    public long modifySecurityAnswer(final String theNewSecurityAnswer, final long theUserRowID) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SECURITY_ANSWER, theNewSecurityAnswer);
        return getWritableDatabase().update(TABLE_USER, cv, (COLUMN_USER_ID + " " + "= " + Long.toString(theUserRowID)), null);
    }

    /**
     * **************************READ FROM THE DATABASE******************************
     */

    public long obtainUserID(final String theEmail, final String thePassword) {
        Cursor currentRow = getReadableDatabase().rawQuery("select * from " + TABLE_USER + " where " + COLUMN_EMAIL_ADDRESS + "='" + theEmail + "' and "
                + COLUMN_PASSWORD + "='" + thePassword + "'", null);

        long userID = 0;
        if (currentRow.moveToNext()) {
            userID = currentRow.getLong(currentRow.getColumnIndex(COLUMN_USER_ID));
        }
        currentRow.close();
        return userID;
    }

    public String obtainUserEmail(final long theUserID) {
        Cursor currentRow = getReadableDatabase().rawQuery("select * from " + TABLE_USER + " where " + COLUMN_USER_ID + "='" + Long.toString(theUserID) + "'", null);
        String userEmail = null;
        if (currentRow.moveToNext()) {
            userEmail = currentRow.getString(currentRow.getColumnIndex(COLUMN_EMAIL_ADDRESS));
        }
        currentRow.close();
        return userEmail;
    }

    public String obtainUserPassword(final long theUserID) {
        Cursor currentRow = getReadableDatabase().rawQuery("select * from " + TABLE_USER + " where " + COLUMN_USER_ID + "='" + Long.toString(theUserID) + "'", null);
        String userPassword = null;
        if (currentRow.moveToNext()) {
            userPassword = currentRow.getString(currentRow.getColumnIndex(COLUMN_PASSWORD));
        }
        currentRow.close();
        return userPassword;
    }


    public String obtainUserSecurityQuestion(final long theUserID) {
        Cursor currentRow = getReadableDatabase().rawQuery("select * from " + TABLE_USER + " where " + COLUMN_USER_ID + "='" + Long.toString(theUserID) + "'", null);
        String userSecurityQuestion = null;
        if (currentRow.moveToNext()) {
            userSecurityQuestion = currentRow.getString(currentRow.getColumnIndex(COLUMN_SECURITY_QUESTION));
        }
        currentRow.close();
        return userSecurityQuestion;
    }


    public String obtainUserSecurityAnswer(final long theUserID) {
        Cursor currentRow = getReadableDatabase().rawQuery("select * from " + TABLE_USER + " where " + COLUMN_USER_ID + "='" + Long.toString(theUserID) + "'", null);
        String userSecurityAnswer = null;
        if (currentRow.moveToNext()) {
            userSecurityAnswer = currentRow.getString(currentRow.getColumnIndex(COLUMN_SECURITY_ANSWER));
        }
        currentRow.close();
        return userSecurityAnswer;
    }

    /**
     * This method returns an instance of a UserCursor to the calling code.
     *
     * @return userCursor
     */
    public UserCursor queryUsers() {
        Cursor wrapped = getReadableDatabase().query(TABLE_USER, null, null, null, null, null, COLUMN_USER_ID + " asc");
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
         * if the current row is invalid. Allows the calling code to iterate
         * over the entirety of the rows in the database.
         *
         * @return user
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
            user.setResetStatus(getInt(getColumnIndex(COLUMN_ISSUED_RESET)));
            return user;
        }

        public String getSecurityQuestion() {
            return getString(getColumnIndex(COLUMN_SECURITY_QUESTION));
        }

    }
}
