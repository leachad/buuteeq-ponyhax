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

    /** Private field to hold the reference to a byte array (photo) should the user elect
     * to store a photo of that particular location.
     */
    private byte[] myCoordinatePhoto;

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
        myCoordinatePhoto = null;
    }


    /**
     * Overloaded constructor of a coordinate object that takes in a byte[] argument.
     *
     * @param theLongitude is the longitude of the coordinate
     * @param theLatitude  is the latitude of the coordinate
     * @param theTimeStamp is the time the point was recorded
     * @param theUserID    is the unique user ID issued by the WebServices API
     * @param theCoordinatePhoto is the byte array representing the photo that the user took
     *                           at that specific location.
     */

    public Coordinate(final long theLongitude, final long theLatitude, final long theTimeStamp,
                      final long theUserID, final byte[] theCoordinatePhoto) {
        myLongitude = theLongitude;
        myLatitude = theLatitude;
        myTimeStamp = theTimeStamp;
        myUserID = theUserID;
        myCoordinatePhoto = theCoordinatePhoto;
    }


    public long getLongitude() {
        return myLongitude;
    }

    public long getLatitude() {
        return myLatitude;
    }

    public long getTimeStamp() {
        return myTimeStamp;
    }

    public long getUserID() {
        return myUserID;
    }

    public byte[] getCoordinatePhoto() {
        return myCoordinatePhoto;
    }
}
