package uw.buuteeq_ponyhax.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Utilization of the SQLiteOpenHelper to create a database for storing User Data for
 * account management.
 * Created by Andrew on 4/4/2015.
 */
public class UserStorageDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "user_storage.db";

    /** Name and Column of the "key" table for accessing Users.*/
    private static final String TABLE_USER = "user";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL_ADDRESS = "email_address";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_SECURITY_QUESTION = "security_question";
    private static final String COLUMN_SECURITY_ANSWER = "security_answer";


    public UserStorageDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        /** Create the User Table.*/
        db.execSQL("create table user (" + "_id integer primary ket autoincrement, " +
                "username varchar(100), email_address varchar(100), " +
                        "password varchar(100), security_question varchar(100), " +
                        "security_answer varchar(100)");

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Understand what the onUpgrade() means, does, how it should be implemented
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Public helper method to return the current number of entries to the user.
     * @return theNumEntries as a long
     */
    public long getNumEntries() {
        return DatabaseUtils.queryNumEntries(getReadableDatabase(), DATABASE_NAME);
    }

    /******************************WRITE TO THE DATABASE*******************************/
    /**
     * Publicly accessible method to add a new User to the database.
     * @param user is the user passed to the database.
     */
    public long insertUser(User user) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_EMAIL_ADDRESS, user.getEmail());
        cv.put(COLUMN_USERNAME, user.getUserName());
        cv.put(COLUMN_PASSWORD, user.getPassword());
        cv.put(COLUMN_SECURITY_QUESTION, user.getSecurityQuestion());
        cv.put(COLUMN_SECURITY_ANSWER, user.getSecurityAnswer());
        return getWritableDatabase().insert(TABLE_USER, null, cv);
    }

    /*****************************READ FROM THE DATABASE*******************************/
    public UserCursor queryUsers() {
        Cursor wrapped = getReadableDatabase()
                .query(TABLE_USER, null, null, null, null, null, COLUMN_ID + " asc");
        return new UserCursor(wrapped);
    }


    /** Instance of a private inner class that returns a Cursors that probes the rows
     * from the user table.
     */
    public static class UserCursor extends CursorWrapper {
        private UserCursor(Cursor c) {
            super(c);
        }

        /** Returns a User object configured for the current row, or null
         * if the current row is invalid.
         */
        public User getUser() {
            User user = new User();
            if (isBeforeFirst() || isAfterLast())
                return null;
            long userID = getLong(getColumnIndex(COLUMN_ID));
            user.setID(userID);

            return user;
        }



        //TODO Create the rest of the methods that will return to the user
        // each individual data field represented within the User class.
    }
}
