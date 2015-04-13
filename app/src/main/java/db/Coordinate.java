package db;

/**
 * Java class that holds all the fields pertinent to a coordinate in a given
 * point. Can be modified to hold all applicable data points that may be useful
 * during the course of this assignment.
 * Created by Andrew on 4/10/2015.
 */
public class Coordinate {

    /**
     * Private fields to hold reference to the fields of the Coordinate object.
     */
    private long myLongitude;
    private long myLatitude;
    private long myTimeStamp;
    private long myUserID;

    /**
     * Constructor of a coordinate object.
     *
     * @param theLongitude is the longitude of the coordinate
     * @param theLatitude  is the latitude of the coordinate
     * @param theTimeStamp is the time the point was recorded
     * @param theUserID    is the unique user ID issued by the WebServices API
     */

    public Coordinate(final long theLongitude, final long theLatitude, final long theTimeStamp, final long theUserID) {
        myLongitude = theLongitude;
        myLatitude = theLatitude;
        myTimeStamp = theTimeStamp;
        myUserID = theUserID;
    }
}
