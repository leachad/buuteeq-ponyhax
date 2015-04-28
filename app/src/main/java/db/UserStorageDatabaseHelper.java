/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import webservices.JSON;
import webservices.WebDriver;

/**
 * Utilization of the SQLiteOpenHelper to create a database for storing User Data for
 * account management.
 * Created by Andrew on 4/4/2015.
 */
public class UserStorageDatabaseHelper extends SQLiteOpenHelper {

    /**
     * Database information used on upgrade or downgrade.
     */
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "user_storage.db";

    /**
     * Name of the table containing fields of data relevant to each registered user.
     */
    private static final String TABLE_USER = "user";

    /**
     * Column headings of the user table used to retrieve pertinent data.
     */
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_EMAIL_ADDRESS = "email_address";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_SECURITY_QUESTION = "security_question";
    private static final String COLUMN_SECURITY_ANSWER = "security_answer";
    private static final String COLUMN_ISSUED_RESET = "issued_reset";


    /**
     * Constructor of a SQLiteOpenHelper that access Users stored
     * in the local database.
     *
     * @param context is the current application context gathered from
     *                the calling code that instantiated the database
     *                helper.
     */
    public UserStorageDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Tables relevant to this database are when the database is first created.
     * Generates a table based on the column headers provided in the field declarations.
     *
     * @param db is the database passed as a parameter to this helper class.
     */
    public void onCreate(SQLiteDatabase db) {
        /** Create the User Table.*/
        db.execSQL("create table user (" + "user_id varchar(100), " +
                "email_address varchar(100), " +
                "password varchar(100), security_question varchar(100), " +
                "security_answer varchar(100), issued_reset integer )");

    }

    /**
     * Should the calling code care to readjust the format of the database, onUpgrade insures
     * that data within the database stays consistent.
     *
     * @param db         is the database passed as a parameter to build this helper class
     * @param oldVersion is the older version of the database
     * @param newVersion is the newer version of the database
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Understand what the onUpgrade() means, does, how it should be implemented
    }

    /**
     * Should the calling code care to readjust the format of the database, onDowngrade insures
     * that data within the database stays consistent.
     *
     * @param db         is the database passed as a parameter to build this helper class
     * @param oldVersion is the older version of the database
     * @param newVersion is the newer version of the database
     */
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
    public boolean insertUser(User user) {

        ContentValues cv = new ContentValues();
        boolean insertConfirm = false;

        if (isUnique(user)) {
            try {
                String result = new WebDriver().addUser(user);
                if (result.matches(JSON.VAL_SUCCESS))
                    insertConfirm = true;
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        return insertConfirm;
    }

    public String retrieveUniqueUserID(final String theEmailAddress, final String thePassword) {
        String userID = null;
        try {
            userID = new WebDriver().checkUserCredentials(theEmailAddress, thePassword);
            // TODO Add the user to the local database with correct security question and answer
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Log.d("USERID: ", userID);
        return userID;
    }

    public boolean addUserToLocalDatabase(User user) {
        ContentValues cv = new ContentValues();
        boolean insertConfirm = false;

        if (isUnique(user)) {
            cv.put(COLUMN_USER_ID, user.getUserID());
            cv.put(COLUMN_EMAIL_ADDRESS, user.getEmail());
            cv.put(COLUMN_PASSWORD, user.getPassword());
            cv.put(COLUMN_SECURITY_QUESTION, user.getSecurityQuestion());
            cv.put(COLUMN_SECURITY_ANSWER, user.getSecurityAnswer());
            cv.put(COLUMN_ISSUED_RESET, user.getResetStatus());
            getWritableDatabase().insert(TABLE_USER, null, cv);
            insertConfirm = true;
        }

        return insertConfirm;
    }

    /**
     * Private helper method to determine if a User is unique within the database.
     *
     * @param user is the User being tested
     * @return isUnique
     */
    private boolean isUnique(User user) {
        UserCursor cursor = queryUsers();
        boolean isUnique = true;
        while (cursor.moveToNext()) {
            if (cursor.getUser().getEmail().matches(user.getEmail())) {
                isUnique = false;
                break;
            }
        }
        return isUnique;
    }


    /**
     * Publicly accessible method to modify the password of a given User.
     * Does NOT check for null values.
     *
     * @param theNewPassword is the newly generated password
     * @param theUserRowID   is the row id for the current user
     * @return updateConfirmation
     */
    public long modifyUserPassword(final String theNewPassword, final String theUserRowID) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_PASSWORD, theNewPassword);
        return getWritableDatabase().update(TABLE_USER, cv, (COLUMN_USER_ID + " " + "= " + theUserRowID), null);
    }


    /**
     * Publicly accessible method to modify the email address of a given User.
     * Does NOT check for null values.
     *
     * @param theNewEmailAddress is the new email address as changed by the user
     * @param theUserRowID       is the row id for the current user
     * @return updateConfirmation
     */
    public long modifyEmailAddress(final String theNewEmailAddress, final String theUserRowID) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EMAIL_ADDRESS, theNewEmailAddress);
        return getWritableDatabase().update(TABLE_USER, cv, (COLUMN_USER_ID + " " + "= " + theUserRowID), null);
    }

    /**
     * Publicly accessible method to modify the security question of a given User.
     * Does NOT check for null values.
     *
     * @param theNewSecurityQuestion is the new security question as changed by the user
     * @param theUserRowID           is the row id for the current user
     * @return updateConfirmation
     */
    public long modifySecurityQuestion(final String theNewSecurityQuestion, final String theUserRowID) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SECURITY_QUESTION, theNewSecurityQuestion);
        return getWritableDatabase().update(TABLE_USER, cv, (COLUMN_USER_ID + " " + "= " + theUserRowID), null);
    }

    /**
     * Publicly accessible method to modify the security answer of a given User.
     * Does NOT check for null values.
     *
     * @param theNewSecurityAnswer is the new security question as changed by the user
     * @param theUserRowID         is the row id for the current user
     * @return updateConfirmation
     */
    public long modifySecurityAnswer(final String theNewSecurityAnswer, final String theUserRowID) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_SECURITY_ANSWER, theNewSecurityAnswer);
        return getWritableDatabase().update(TABLE_USER, cv, (COLUMN_USER_ID + " " + "= " + theUserRowID), null);
    }

    /**
     * **************************READ FROM THE DATABASE******************************
     */

    public String obtainUserID(final String theEmail) {
        Cursor currentRow = getReadableDatabase().rawQuery("select * from " + TABLE_USER + " where " + COLUMN_EMAIL_ADDRESS + "='" + theEmail + "'", null);

        String userID = null;
        if (currentRow.moveToNext()) {
            userID = currentRow.getString(currentRow.getColumnIndex(COLUMN_USER_ID));
        }
        return userID;
    }

    public String obtainUserEmail(final String theUserID) {
        Cursor currentRow = getReadableDatabase().rawQuery("select * from " + TABLE_USER + " where " + COLUMN_USER_ID + "='" + theUserID + "'", null);
        String userEmail = null;
        if (currentRow.moveToNext()) {
            userEmail = currentRow.getString(currentRow.getColumnIndex(COLUMN_EMAIL_ADDRESS));
        }
        return userEmail;
    }

    public String obtainUserPassword(final String theUserID) {
        Cursor currentRow = getReadableDatabase().rawQuery("select * from " + TABLE_USER + " where " + COLUMN_USER_ID + "='" + theUserID + "'", null);
        String userPassword = null;
        if (currentRow.moveToNext()) {
            userPassword = currentRow.getString(currentRow.getColumnIndex(COLUMN_PASSWORD));
        }
        return userPassword;
    }


    public String obtainUserSecurityQuestion(final String theUserID) {
        Cursor currentRow = getReadableDatabase().rawQuery("select * from " + TABLE_USER + " where " + COLUMN_USER_ID + "='" + theUserID + "'", null);
        String userSecurityQuestion = null;
        if (currentRow.moveToNext()) {
            userSecurityQuestion = currentRow.getString(currentRow.getColumnIndex(COLUMN_SECURITY_QUESTION));
        }
        return userSecurityQuestion;
    }


    public String obtainUserSecurityAnswer(final String theUserID) {
        Cursor currentRow = getReadableDatabase().rawQuery("select * from " + TABLE_USER + " where " + COLUMN_USER_ID + "='" + theUserID + "'", null);
        String userSecurityAnswer = null;
        if (currentRow.moveToNext()) {
            userSecurityAnswer = currentRow.getString(currentRow.getColumnIndex(COLUMN_SECURITY_ANSWER));
        }
        return userSecurityAnswer;
    }

    /**
     * This method returns an instance of a UserCursor to the calling code. Does NOT ensure that
     * the cursor is closed.
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
            user.setID(getString(getColumnIndex(COLUMN_USER_ID)));
            user.setEmail(getString(getColumnIndex(COLUMN_EMAIL_ADDRESS)));
            user.setPassword(getString(getColumnIndex(COLUMN_PASSWORD)));
            user.setSecurityQuestion(getString(getColumnIndex(COLUMN_SECURITY_QUESTION)));
            user.setSecurityAnswer(getString(getColumnIndex(COLUMN_SECURITY_ANSWER)));
            user.setResetStatus(getInt(getColumnIndex(COLUMN_ISSUED_RESET)));
            return user;
        }
    }
}
