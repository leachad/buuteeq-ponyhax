/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package webservices;

import db.Coordinate;
import db.User;

/**
 * Created by leachad on 4/28/15.
 * Enumerated constants used to created PHP posts.
 */
public class PHP {

    /**
     * Private field to hold a reference to the add user php file domain.
     */
    public static final String ADD_USER_FILE = "adduser.php";

    /**
     * Private field to hold a reference to the add coordinate php file domain.
     */
    public static final String ADD_COORDINATE_FILE = "addLog.php";
    /**
     * Private fields to hold variable strings needed to properly add a coordinate via the url.
     */
    public static final String URL_LATITUDE = "latitude";
    public static final String URL_LONGITUDE = "longitude";
    public static final String URL_SPEED = "speed";
    public static final String URL_HEADING = "heading";
    public static final String URL_SOURCE = "source";
    public static final String URL_UID = "uid";
    public static final String URL_TIMESTAMP = "timestamp";
    public static final String URL_START = "start";
    public static final String URL_END = "end";
    public static final String URL_USER_ID = "userid";
    /**
     * Private field to hold a reference to the add coordinate php file domain.
     */
    private static final String LOGIN_USER_FILE = "login.php";
    /**
     * Private field to hold a reference to the add coordinate php file domain.
     */
    private static final String GET_USER_COORDINATES_FILE = "view.php";
    /**
     * Private fields to hold variable strings needed to properly add a user via the url.
     */
    private static final String URL_EMAIL = "email";
    private static final String URL_PASSWORD = "password";
    private static final String URL_SEC_QUESTION = "question";
    private static final String URL_SEC_ANSWER = "answer";
    private static final String START_ARGS = "?";
    private static final String APPEND_ARGS = "&";
    private static final String ASSIGN_ARGS = "=";
    /**
     * Privately accessible field that will hold a reference to the domain used for
     * holding all the respective php and mysql files.
     */
    private static String myCurrentHostDomain;


    public PHP(final String theCurrentDomain) {
        myCurrentHostDomain = theCurrentDomain;

    }

    private static String getEmailKeyValue(final String theEmailAddress) {
        return URL_EMAIL + ASSIGN_ARGS + theEmailAddress;
    }

    private static String getPasswordKeyValue(final String thePassword) {
        return URL_PASSWORD + ASSIGN_ARGS + thePassword;
    }

    private static String getQuestionKeyValue(final String theQuestion) {
        return URL_SEC_QUESTION + ASSIGN_ARGS + theQuestion;
    }

    private static String getAnswerKeyValue(final String theAnswer) {
        return URL_SEC_ANSWER + ASSIGN_ARGS + theAnswer;
    }

    private static String getSourceKeyValue(final String theUserID) {
        return URL_SOURCE + ASSIGN_ARGS + theUserID;
    }

    private static String getUIDKeyValue(final String theUserID) {
        return URL_UID + ASSIGN_ARGS + theUserID;
    }

    private static String getLatKeyValue(final Coordinate theCurrentCoordinate) {
        return URL_LATITUDE + ASSIGN_ARGS + theCurrentCoordinate.getLatitude();
    }

    private static String getLongKeyValue(final Coordinate theCurrentCoordinate) {
        return URL_LONGITUDE + ASSIGN_ARGS + theCurrentCoordinate.getLongitude();
    }

    private static String getSpeedKeyValue(final Coordinate theCurrentCoordinate) {
        return URL_SPEED + ASSIGN_ARGS + theCurrentCoordinate.getUserSpeed();
    }

    private static String getHeadingKeyValue(final Coordinate theCurrentCoordinate) {
        return URL_HEADING + ASSIGN_ARGS + theCurrentCoordinate.getHeading();
    }

    private static String getTimeKeyValue(final Coordinate theCurrentCoordinate) {
        return URL_TIMESTAMP + ASSIGN_ARGS + theCurrentCoordinate.getTimeStamp();
    }

    private static String getStartKeyValue(final long theStart) {
        return URL_START + ASSIGN_ARGS + theStart;
    }

    private static String getEndKeyValue(final long theEnd) {
        return URL_END + ASSIGN_ARGS + theEnd;
    }

    /**
     * Private method to create an add user request
     *
     * @return encodedEntity
     */
    public static String getAddUserRequest(final User theUser) {
        return myCurrentHostDomain + ADD_USER_FILE + START_ARGS + getEmailKeyValue(theUser.getEmail()) + APPEND_ARGS
                + getPasswordKeyValue(theUser.getEmail()) + APPEND_ARGS + getQuestionKeyValue(theUser.getSecurityQuestion())
                + PHP.APPEND_ARGS + getAnswerKeyValue(theUser.getSecurityQuestion());
    }

    /**
     * Private method to create an add coordinate request
     *
     * @return encodedEntity
     */
    public static String getAddCoordinateRequest(final Coordinate thisCoordinate, final String theUserID) {
        return myCurrentHostDomain + ADD_COORDINATE_FILE + START_ARGS + getLatKeyValue(thisCoordinate) + APPEND_ARGS
                + getLongKeyValue(thisCoordinate) + APPEND_ARGS + getSpeedKeyValue(thisCoordinate)
                + APPEND_ARGS + getHeadingKeyValue(thisCoordinate) + APPEND_ARGS
                + getTimeKeyValue(thisCoordinate) + APPEND_ARGS + getSourceKeyValue(theUserID);
    }

    /**
     * Private method to create a user login request
     *
     * @return encodedEntity
     */
    public static String getUserLoginRequest(final User theUser) {
        return myCurrentHostDomain + LOGIN_USER_FILE + START_ARGS + getEmailKeyValue(theUser.getEmail())
                + APPEND_ARGS + getPasswordKeyValue(theUser.getPassword());
    }

    /**
     * Private method to grant a user access to their logged coordinates
     *
     * @return encodedEntity
     */
    public static String getUserCoordinateRequest(final String theUserID, final long theStart, final long theEnd) {
        return myCurrentHostDomain + GET_USER_COORDINATES_FILE + START_ARGS + getUIDKeyValue(theUserID) + APPEND_ARGS
                + getStartKeyValue(theStart) + APPEND_ARGS + getEndKeyValue(theEnd);
    }


}
