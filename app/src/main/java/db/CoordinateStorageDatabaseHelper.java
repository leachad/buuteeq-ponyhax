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

/**
 * Class to implement a database for storing coordinate points.
 * Created by Andrew on 4/10/2015.
 */
public class CoordinateStorageDatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "coordinate_storage.db";
    public static final String TABLE_NAME = "coordinate";
    public static final String COLUMN_ROW_ID = "row_id";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_TIME_STAMP = "time_stamp";
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
        db.execSQL("create table " + TABLE_NAME + " ( " + COLUMN_ROW_ID + " integer primary key autoincrement " + COLUMN_LONGITUDE + " long "
                + COLUMN_LATITUDE + " long " + COLUMN_TIME_STAMP + " long " + COLUMN_USER_ID + " blob(1024) " + COLUMN_PHOTO + ") ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
            cv.put(COLUMN_PHOTO, coordinate.getCoordinatePhoto());
            insertConfirm = getWritableDatabase().insert(TABLE_NAME, null, cv);
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

            //TODO should we be making evaluations of unique coordinates? Perhaps a user
            //wants to update the picture or data in regards to a specific location or set of
            //locations?
        }
        cursor.close();
        return isUnique;
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

    /**
     * Instance of a private inner class that returns a Cursors that probes the rows
     * from the user table.
     */
    public static class CoordinateCursor extends CursorWrapper {
        public CoordinateCursor(Cursor c) {
            super(c);
        }

        /**
         * Returns a User object configured for the current row, or null
         * if the current row is invalid. Allows the calling code to iterate
         * over the entirety of the rows in the database.
         *
         * @return user
         */
        public Coordinate getCoordinate() {
            if (isBeforeFirst() || isAfterLast())
                return null;

            long longitude = getLong(getColumnIndex(COLUMN_LONGITUDE));
            long latitude = getLong(getColumnIndex(COLUMN_LATITUDE));
            long time_stamp = getLong(getColumnIndex(COLUMN_TIME_STAMP));
            long user_id = getLong(getColumnIndex(COLUMN_USER_ID));
            byte[] photo = getBlob(getColumnIndex(COLUMN_PHOTO));

            if (photo == null) {
                return new Coordinate(longitude, latitude, time_stamp, user_id);
            } else {
                return new Coordinate(longitude, latitude, time_stamp, user_id, photo);
            }
        }


    }
}
