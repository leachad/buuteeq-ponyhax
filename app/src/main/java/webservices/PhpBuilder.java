/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package webservices;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import db.Coordinate;
import db.User;

/**
 * Created by leachad on 4/28/15.
 * Enumerated constants used to created PHP posts.
 */
public class PhpBuilder {

    /**
     * Private field to hold a reference to the add user php file name.
     */
    public static final String ADD_USER_FILE = "adduser.php";

    /**
     * Private field to hold a reference to the add coordinate php file name.
     */
    public static final String ADD_COORDINATE_FILE = "logAdd.php";
    /**
     * Private fields to hold variable strings needed to properly add a coordinate via the url.
     */
    public static final String URL_LATITUDE = "lat";
    public static final String URL_LONGITUDE = "lon";
    public static final String URL_SPEED = "speed";
    public static final String URL_HEADING = "heading";
    public static final String URL_SOURCE = "source";
    public static final String URL_UID = "uid";
    public static final String URL_TIMESTAMP = "timestamp";
    public static final String URL_TIME = "time";
    public static final String URL_START = "start";
    public static final String URL_END = "end";
    public static final String URL_USER_ID = "userid";
    /**
     * Private field to hold a reference to the add coordinate php file name.
     */
    private static final String LOGIN_USER_FILE = "login.php";
    /**
     * Private field to hold a reference to the add coordinate php file name.
     */
    private static final String GET_USER_COORDINATES_FILE = "view.php";
    /**
     * Private field to hold a reference to the user agreement php file name.
     */
    private static final String USER_AGREEMENT_FILE = "agreement.php";
    /**
     * Private field to hold a reference to the user reset php file name.
     */
    private static final String PASSWORD_RESET_FILE = "reset.php";
    /**
     * Private fields to hold variable strings needed to properly add a user via the url.
     */
    private static final String URL_EMAIL = "email";
    private static final String URL_PASSWORD = "password";
    private static final String URL_SEC_QUESTION = "question";
    private static final String URL_SEC_ANSWER = "answer";

    /**
     * PHP and URL encoding literals.
     */
    private static final String START_ARGS = "?";
    private static final String APPEND_ARGS = "&";
    private static final String ASSIGN_ARGS = "=";
    private static final String ASCII_SPACE = " ";
    private static final String ENCODED_SPACE = "%20";
    private static final String ENCODE_FORMAT = "utf-8";

    /**
     * Privately accessible field that will hold a reference to the domain used for
     * holding all the respective php and mysql files.
     */
    private static String myCurrentHostDomain;


    public PhpBuilder(final String theCurrentDomain) {
        myCurrentHostDomain = theCurrentDomain;

    }

    private String getEncodedText(final String theText) {
        String toRet = null;

        try {
            toRet = theText.replaceAll(ASCII_SPACE, ENCODED_SPACE);
            toRet = URLEncoder.encode(theText, ENCODE_FORMAT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return toRet;
    }

    private String getEmailKeyValue(final String theEmailAddress) {
        return URL_EMAIL + ASSIGN_ARGS + getEncodedText(theEmailAddress);
    }

    private String getPasswordKeyValue(final String thePassword) {
        return URL_PASSWORD + ASSIGN_ARGS + getEncodedText(thePassword);
    }

    private String getQuestionKeyValue(final String theQuestion) {
        return URL_SEC_QUESTION + ASSIGN_ARGS + getEncodedText(theQuestion);
    }

    private String getAnswerKeyValue(final String theAnswer) {
        return URL_SEC_ANSWER + ASSIGN_ARGS + getEncodedText(theAnswer);
    }

    private String getSourceKeyValue(final String theUserID) {
        return URL_SOURCE + ASSIGN_ARGS + theUserID;
    }

    private String getUIDKeyValue(final String theUserID) {
        return URL_UID + ASSIGN_ARGS + theUserID;
    }

    private String getLatKeyValue(final Coordinate theCurrentCoordinate) {
        return URL_LATITUDE + ASSIGN_ARGS + theCurrentCoordinate.getLatitude();
    }

    private String getLongKeyValue(final Coordinate theCurrentCoordinate) {
        return URL_LONGITUDE + ASSIGN_ARGS + theCurrentCoordinate.getLongitude();
    }

    private String getSpeedKeyValue(final Coordinate theCurrentCoordinate) {
        return URL_SPEED + ASSIGN_ARGS + theCurrentCoordinate.getUserSpeed();
    }

    private String getHeadingKeyValue(final Coordinate theCurrentCoordinate) {
        return URL_HEADING + ASSIGN_ARGS + theCurrentCoordinate.getHeading();
    }

    private String getTimeStampKeyValue(final Coordinate theCurrentCoordinate) {
        return URL_TIMESTAMP + ASSIGN_ARGS + theCurrentCoordinate.getTimeStamp();
    }

    private String getStartKeyValue(final long theStart) {
        return URL_START + ASSIGN_ARGS + theStart;
    }

    private String getEndKeyValue(final long theEnd) {
        return URL_END + ASSIGN_ARGS + theEnd;
    }

    /**
     * Private method to create an add user request
     *
     * @return encodedEntity
     */
    public String getAddUserRequest(final User theUser) {
        return myCurrentHostDomain + ADD_USER_FILE + START_ARGS + getEmailKeyValue(theUser.getEmail()) + APPEND_ARGS
                + getPasswordKeyValue(theUser.getPassword()) + APPEND_ARGS + getQuestionKeyValue(theUser.getSecurityQuestion())
                + PhpBuilder.APPEND_ARGS + getAnswerKeyValue(theUser.getSecurityAnswer());
    }

    /**
     * Private method to create an add coordinate request
     *
     * @return encodedEntity
     */
    public String getAddCoordinateRequest(final Coordinate thisCoordinate, final String theUserID) {
        return myCurrentHostDomain + ADD_COORDINATE_FILE + START_ARGS + getLatKeyValue(thisCoordinate) + APPEND_ARGS
                + getLongKeyValue(thisCoordinate) + APPEND_ARGS + getSpeedKeyValue(thisCoordinate)
                + APPEND_ARGS + getHeadingKeyValue(thisCoordinate) + APPEND_ARGS
                + getTimeStampKeyValue(thisCoordinate) + APPEND_ARGS + getSourceKeyValue(theUserID);
    }

    /**
     * Private method to create a user login request
     *
     * @return encodedEntity
     */
    public String getUserLoginRequest(final String theEmailAddress, final String thePassword) {
        return myCurrentHostDomain + LOGIN_USER_FILE + START_ARGS + getEmailKeyValue(theEmailAddress)
                + APPEND_ARGS + getPasswordKeyValue(thePassword);
    }

    /**
     * Public method to grant a user access to their logged coordinates
     *
     * @return coordinateRequest
     */
    public String getUserCoordinateRequest(final String theUserID, final long theStart, final long theEnd) {
        return myCurrentHostDomain + GET_USER_COORDINATES_FILE + START_ARGS + getUIDKeyValue(theUserID) + APPEND_ARGS
                + getStartKeyValue(theStart) + APPEND_ARGS + getEndKeyValue(theEnd);
    }

    /**
     * Public method to grant a user access to the user agreement
     *
     * @return agreementRequest
     */
    public String getUserAgreementRequest() {
        return myCurrentHostDomain + USER_AGREEMENT_FILE;
    }

    /**
     * Public method to request a password reset from the server.
     *
     * @return resetRequest
     */
    public String getUserResetRequest(final String theEmailAddress) {
        return myCurrentHostDomain + PASSWORD_RESET_FILE + START_ARGS + getEmailKeyValue(theEmailAddress);
    }


}
