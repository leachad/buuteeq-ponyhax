/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import webservices.WebDriver;

/**
 * Class to implement a database for storing coordinate points.
 * Created by Andrew on 4/10/2015.
 */
public class CoordinateStorageDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "coordinate_storage_db";
    public static final String TABLE_NAME = "coordinate";
    public static final String COLUMN_ROW_ID = "row_id";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_TIME_STAMP = "timestamp";
    public static final String COLUMN_USER_SPEED = "speed";
    public static final String COLUMN_USER_HEADING = "heading";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_PHOTO = "photo";


    /**
     * Constructor for a Coordinate Database
     */
    public CoordinateStorageDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " ( " + COLUMN_ROW_ID + " integer primary key autoincrement, " + COLUMN_LONGITUDE + " double, "
                + COLUMN_LATITUDE + " double, " + COLUMN_TIME_STAMP + " long, " + COLUMN_USER_ID + " varchar(100), " + COLUMN_USER_SPEED + " double, "
                + COLUMN_USER_HEADING + " double, " + COLUMN_PHOTO + " blob(1024) )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void wipeTable() {
        getWritableDatabase().delete(TABLE_NAME, null, null);
        Log.d("Database Table", "Successfully Wiped!");
        //getWritableDatabase().close();
    }

    /******************************WRITE TO THE DATABASE*******************************/
    /**
     * Publicly accessible method to add a new User to the database.
     *
     * @param coordinate is the coordinate passed to the database.
     */
    public long insertCoordinate(Coordinate coordinate) {

        ContentValues cv = new ContentValues();
        long insertConfirm = 0;

        if (isUnique(coordinate)) {
            cv.put(COLUMN_LONGITUDE, coordinate.getLongitude());
            cv.put(COLUMN_LATITUDE, coordinate.getLatitude());
            cv.put(COLUMN_TIME_STAMP, coordinate.getTimeStamp());
            cv.put(COLUMN_USER_ID, coordinate.getUserID());
            cv.put(COLUMN_USER_SPEED, coordinate.getUserSpeed());
            cv.put(COLUMN_USER_HEADING, coordinate.getHeading());
            insertConfirm = getWritableDatabase().insert(TABLE_NAME, null, cv);
            Log.d("INSERT TO DATABASE", coordinate.toString() + "");
        }
        return insertConfirm;
    }

    /**
     * Private helper method to determine if a coordinate is unique within the database.
     *
     * @param coordinate is the Coordinate being tested
     * @return isUnique
     */
    private boolean isUnique(Coordinate coordinate) {
        CoordinateCursor cursor = queryCoordinates();
        boolean isUnique = true;
        while (cursor.moveToNext()) {
            Coordinate temp = cursor.getCoordinate();
            if (temp.equals(coordinate)) {
                isUnique = false;
                break;
            }
        }
        cursor.close();
        return isUnique;
    }

    /**
     * Publicly accessible method to batch publish local coordinates to the database based on the data
     * that the user has obtained while polling gps every 'n' seconds.
     * <p/>
     * theLocalCoordinates is a list of Coordinates generated before pushed to webservices
     */
    public void publishCoordinateBatch(final String theUserID) {
        List<Coordinate> theLocalCoordinates = getAllCoordinates(User.ALL_USERS);
        WebDriver.addCoordinates(theLocalCoordinates, theUserID);
        wipeTable();


    }


    /**
     * This method returns an instance of a UserCursor to the calling code. Does NOT ensure that
     * the cursor is closed.
     *
     * @return userCursor
     */
    public CoordinateCursor queryCoordinates() {
        Cursor wrapped = getReadableDatabase().query(TABLE_NAME, null, null, null, null, null, COLUMN_ROW_ID + " asc");
        return new CoordinateCursor(wrapped);
    }

    public List<Coordinate> getAllCoordinates(String userID) {
        List<Coordinate> coordinates = new ArrayList<>();
        Cursor cursor = getCoordinateCursor(userID);

        while (cursor.moveToNext()) {

            Coordinate coordinate = new Coordinate(cursor.getDouble(cursor.getColumnIndex(COLUMN_LATITUDE)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_LONGITUDE)),
                    cursor.getLong(cursor.getColumnIndex(COLUMN_TIME_STAMP)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_USER_SPEED)),
                    cursor.getDouble(cursor.getColumnIndex(COLUMN_USER_HEADING)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)));
            coordinates.add(coordinate);

        }
        Log.d("LOCAL COORD LENGTH", "" + coordinates.size());
        return coordinates;
    }


    /**
     * This method returns an iterative cursor over the Coordinates associated with the userID passed.
     *
     * @param userID the user who's coordinate list you are interested in iterating over, ALL_USERS for all
     * @return a iterative cursor for the return sql query
     */
    private Cursor getCoordinateCursor(String userID) {
        Cursor cursor;
        if (userID.equals(User.ALL_USERS)) {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_TIME_STAMP + ";", new String[]{});
        } else {
            cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USER_ID + " = '" + userID + "' ORDER BY " + COLUMN_TIME_STAMP + ";", new String[]{});
        }
        return cursor;
    }

    /**
     * Instance of a private inner class that returns a Cursors that probes the rows
     * from the coordinate table.
     */
    public static class CoordinateCursor extends CursorWrapper {
        public CoordinateCursor(Cursor c) {
            super(c);
        }

        /**
         * Returns a Coordinate object configured for the current row, or null
         * if the current row is invalid. Allows the calling code to iterate
         * over the entirety of the rows in the database.
         *
         * @return Coordinate with or without a photo blob
         */
        public Coordinate getCoordinate() {
            if (isBeforeFirst() || isAfterLast())
                return null;

            double longitude = getLong(getColumnIndex(COLUMN_LONGITUDE));
            double latitude = getLong(getColumnIndex(COLUMN_LATITUDE));
            long time_stamp = getLong(getColumnIndex(COLUMN_TIME_STAMP));
            double speed = getDouble(getColumnIndex(COLUMN_USER_SPEED));
            double heading = getDouble(getColumnIndex(COLUMN_USER_HEADING));
            String source = getString(getColumnIndex(COLUMN_USER_ID));
            byte[] photo = getBlob(getColumnIndex(COLUMN_PHOTO));

            if (photo == null) {
                return new Coordinate(longitude, latitude, time_stamp, speed, heading, source);
            } else {
                return new Coordinate(longitude, latitude, time_stamp, speed, heading, source, photo);
            }
        }


    }
}
