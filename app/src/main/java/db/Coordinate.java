/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package db;

import java.util.Date;

/**
 * Java class that holds all the fields pertinent to a coordinate in a given
 * point. Can be modified to hold all applicable data points that may be useful
 * during the course of this assignment.
 * Created by Andrew on 4/10/2015.
 * Updated by Huy on 5/6/2015
 */
public class Coordinate {

    /**
     * Public static final fields used to store and edit prefs.
     */
    public static final String COORDINATE_PREFS = "coordinate_prefs";
    public static final String START_TIME = "start_time"; //stored as a unix time stamp
    public static final String END_TIME = "end_time";
    public static final String LONG_TITLE = " Longitude";
    public static final String LAT_TITLE = " Latitude";
    public static final String TIME_TITLE = " Date/Time";
    public static final String SPEED_TITLE = " Speed";
    public static final String SPEED_UNITS = " mph ";
    public static final String HEADING_TITLE = " Heading";
    public static final String USER_TITLE = " User-id";
    public static final String COORDINATE_SOURCE = "coordinate_source";
    public static final String SEPARATOR = ": ";

    /**
     * Private fields to hold reference to the fields of the Coordinate object.
     */
    private static final int DATE_CONVERSION = 1000;
    private double myLongitude;
    private double myLatitude;
    private long myTimeStamp;
    private double mySpeed;
    private double myHeading;
    private String myUserID;


    /**
     * Private field to hold the reference to a byte array (photo) should the user elect
     * to store a photo of that particular location.
     */
    private byte[] myCoordinatePhoto;

    /**
     * Constructor of a coordinate object.
     *
     * @param theLongitude is the longitude of the coordinate
     * @param theLatitude  is the latitude of the coordinate
     * @param theTimeStamp is the time the point was recorded
     * @param theSpeed     is the current speed of the user
     * @param theHeading   is the current direction the user is moving
     * @param theUserID    is the unique user ID issued by the WebServices API
     */

    public Coordinate(final double theLongitude, final double theLatitude, final long theTimeStamp, final double theSpeed, final double theHeading, final String theUserID) {
        myLongitude = theLongitude;
        myLatitude = theLatitude;
        myTimeStamp = theTimeStamp;
        mySpeed = theSpeed;
        myHeading = theHeading;
        myUserID = theUserID;
        myCoordinatePhoto = null;
    }


    /**
     * Overloaded constructor of a coordinate object that takes in a byte[] argument.
     *
     * @param theLongitude       is the longitude of the coordinate
     * @param theLatitude        is the latitude of the coordinate
     * @param theTimeStamp       is the time the point was recorded
     * @param theUserID          is the unique user ID issued by the WebServices API
     * @param theSpeed           is the current speed of the user
     * @param theHeading         is the current direction the user is moving
     * @param theCoordinatePhoto is the byte array representing the photo that the user took
     *                           at that specific location.
     */

    public Coordinate(final double theLongitude, final double theLatitude, final long theTimeStamp,
                      final double theSpeed, final double theHeading, final String theUserID, final byte[] theCoordinatePhoto) {
        myLongitude = theLongitude;
        myLatitude = theLatitude;
        myTimeStamp = theTimeStamp;
        mySpeed = theSpeed;
        myHeading = theHeading;
        myUserID = theUserID;
        myCoordinatePhoto = theCoordinatePhoto;
    }


    public double getLongitude() {
        return myLongitude;
    }

    public double getLatitude() {
        return myLatitude;
    }

    public long getTimeStamp() {
        return myTimeStamp;
    }

    public String getUserID() {
        return myUserID;
    }

    public double getUserSpeed() {
        return mySpeed;
    }

    public double getHeading() {
        return myHeading;
    }

    public byte[] getCoordinatePhoto() {
        return myCoordinatePhoto;
    }

    @Override
    public String toString() {
        return LONG_TITLE + SEPARATOR + myLongitude + LAT_TITLE + SEPARATOR + myLatitude + TIME_TITLE + SEPARATOR + new Date(myTimeStamp * DATE_CONVERSION).toString()
                + SPEED_TITLE + SEPARATOR + mySpeed + SPEED_UNITS + HEADING_TITLE + SEPARATOR + myHeading;
    }
}
