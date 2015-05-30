/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package db;

/**
 * Created by andrew on 4/6/15.
 * Instance of a new user that is being accessed from the database or
 * sent to the database.
 */
public class User {

    /**
     * Static final String used for storing and retrieving data from
     * Prefs.
     */
    public static final String USER_PREFS = "userPrefs";
    public static final String USER_ID = "userID";
    public static final String USER_PASSWORD = "password";
    public static final String USER_ANSWER = "secAnswer";
    public static final String USER_EMAIL = "userEmail";
    public static final String USER_AGREEMENT = "agreement";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String ALL_USERS = "All_Users";
    public static final String DB_FLAG = "loadLocalDB";
    public static final String USER_EXISTS = "userExistCheck";
    public static final String SAMPLE_RATE = "sampleRate";
    public static final String UPLOAD_RATE = "uploadRate";
    public static final String REQUESTING_LOCATION = "requestingLocation";
    public static final String DESTROYED_STATE = "destroyedState";


    /**
     * Private field to hold an instance of the User ID generated by a SQL query.
     */
    private String myUserID;


    /**
     * The following fields are explicity entered and edited by the User.
     */
    private String myUserEmail;

    private String myPassword;

    private String mySecurityQuestion;

    private String mySecurityAnswer;

    private int myResetStatus;


    /**
     * Constructor of a user object initializes all fields to null or 0.
     * All fields are not easily accessible to a user object.
     */
    public User() {

        this.myUserID = null;
        this.myUserEmail = null;
        this.myPassword = null;
        this.mySecurityQuestion = null;
        this.mySecurityAnswer = null;
        myResetStatus = 0;
    }

    /**
     * Overrides toString to return information about the current User.
     *
     * @return information about the user.
     */
    @Override
    public String toString() {
        return " Email:" + myUserEmail + " ID:" + myUserID + " Password:" + myPassword + " Question:" + mySecurityQuestion;
    }

    /**
     * Method to get the User's ID.
     *
     * @return theUserID
     */
    public String getUserID() {
        return myUserID;
    }

    /**
     * Method to get the User's email.
     *
     * @return theUserEmail
     */
    public String getEmail() {
        return myUserEmail;
    }

    /**
     * Method to set the Users Email address.
     *
     * @param theEmail is the user email address.
     */
    public void setEmail(final String theEmail) {
        this.myUserEmail = theEmail;
    }

    /**
     * Returns the User's password.
     *
     * @return thePassword is the User's password.
     */
    public String getPassword() {
        return myPassword;
    }

    /**
     * Method to set the User's password.
     *
     * @param thePassword is the User's password.
     */
    public void setPassword(final String thePassword) {
        this.myPassword = thePassword;
    }

    /**
     * Method to get the security question
     * from the User object.
     *
     * @return theUser's security question
     */
    public String getSecurityQuestion() {
        return mySecurityQuestion;
    }

    /**
     * Method to set the Security question
     *
     * @param theSecurityQuestion is the User's chosen security question
     */
    public void setSecurityQuestion(final String theSecurityQuestion) {
        this.mySecurityQuestion = theSecurityQuestion;
    }

    /**
     * Method to return the security answer
     *
     * @return theSecurityAnswer
     */
    public String getSecurityAnswer() {
        return mySecurityAnswer;
    }

    /**
     * Method to set the security answer as determined by the user.
     *
     * @param theSecurityAnswer is theusers chosen security answer
     */
    public void setSecurityAnswer(final String theSecurityAnswer) {
        this.mySecurityAnswer = theSecurityAnswer;
    }

    /**
     * Method to return the reset status to the calling code.
     *
     * @return theResetStatus
     */
    public int getResetStatus() {
        return myResetStatus;
    }

    /**
     * method to set the reset status
     *
     * @param theResetStatus is the status that tells the user if the account
     *                       was reset.
     */
    public void setResetStatus(final int theResetStatus) {
        this.myResetStatus = theResetStatus;
    }

    /**
     * Sets the user id.
     *
     * @param theUserID is the User ID generated by the WebService backend/.
     */
    public void setID(final String theUserID) {
        this.myUserID = theUserID;
    }

}