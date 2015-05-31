/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package webservices;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import db.Coordinate;
import db.User;

/**
 * UserDriver will be the class that stores and gets users from the webservices
 * provided by Dr. Abraham.
 * Created by leachad on 4/22/15.
 */
public class WebDriver {

    /**
     * Sets the Default domain of the application.
     */
    private static final String DEFAULT_DOMAIN = "http://450.atwebpages.com/";

    /**
     * Ensure that the domain is set before application executes.
     */
    private static JsonBuilder requestBuilder = new JsonBuilder(DEFAULT_DOMAIN);
    /**
     * Private fields to hold the parameter variables before background tasks are executed.
     */
    private static User myUser;
    private static String myUserID;
    private static List<Coordinate> myCoordinateList;
    private static String myEmailAddress;
    private static String myPassword;
    private static long myStartTime;
    private static long myEndTime;


    /**
     * Public method to set the variable domain to determine where the php files and
     * the databases will be
     */
    public static void setDomain(final String theDomain) {
        requestBuilder = new JsonBuilder(theDomain);
    }

    /**
     * POST OPERATIONS.
     */
    public static boolean addUser(User theUser) throws ExecutionException, InterruptedException {
        myUser = theUser;
        return new AddUser().execute().get();
    }

    public static void addCoordinates(List<Coordinate> theCoordinateList, String theUserID) {
        myCoordinateList = theCoordinateList;
        myUserID = theUserID;

        try {
            new AddCoordinates().execute().get();
            //TODO Use the obtained result for data integrity.
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    public static String resetPassword(final String theEmailAddress) throws ExecutionException, InterruptedException {
        myEmailAddress = theEmailAddress;
        return new ResetPassword().execute().get();
    }

    /**
     * GET OPERATIONS.
     */
    public static String checkUserCredentials(final String theEmail, final String thePassword) throws ExecutionException, InterruptedException {
        myEmailAddress = theEmail;
        myPassword = thePassword;
        return new UserLogin().execute().get();
    }

    public static List<Coordinate> getLoggedCoordinates(String theUserID, final long theStartTime, final long theEndTime) throws ExecutionException, InterruptedException {
        myUserID = theUserID;
        myStartTime = theStartTime;
        myEndTime = theEndTime;
        return new GetUserCoordinates().execute().get();
    }

    public static String getUserAgreement() throws ExecutionException, InterruptedException {
        return new GetUserAgreement().execute().get();
    }


    /**
     * AddUser class executes an AsnycTask to post a user to the database.
     *
     * @author leachad
     * @version 4.25.15
     */
    private static class AddUser extends AsyncTask<Void, Integer, Boolean> {

        protected Boolean doInBackground(Void... addUser) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(requestBuilder.getAddUserRequest(myUser));
            Log.w("http Adduser", httpPost.getURI().toString());
            String result;
            boolean userIsUnique = false;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (requestBuilder.jSONResultIsSuccess(result)) {
                    userIsUnique = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return userIsUnique;
        }
    }

    /**
     * AddUser class executes an AsnycTask to post a user to the database.
     *
     * @author leachad
     * @version 4.25.15
     */
    private static class AddCoordinates extends AsyncTask<Void, Integer, String> {

        /**
         * Private helper method to execute a series of coordinate posts and
         * gets.
         *
         * @param httpClient is the default client web service
         * @param httpPost   is the http post URL
         * @return result
         */
        private String executePost(HttpClient httpClient, HttpPost httpPost) {
            String result = JsonBuilder.VAL_FAIL;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (requestBuilder.jSONResultIsSuccess(result))
                    result = JsonBuilder.VAL_SUCCESS;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.w("AddCoordinates result", result);
            return result;
        }

        protected String doInBackground(Void... addCoordinates) {

            String result = JsonBuilder.VAL_FAIL;
            for (Coordinate coordinate : myCoordinateList) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(requestBuilder.getAddCoordinateRequest(coordinate, myUserID));
                Log.w("ADD COORD:", requestBuilder.getAddCoordinateRequest(coordinate, myUserID));
                result = executePost(httpClient, httpPost);
            }
            return result;
        }
    }

    /**
     * AddUser class executes an AsnycTask to post a user to the database.
     *
     * @author leachad
     * @version 4.25.15
     */
    private static class UserLogin extends AsyncTask<Void, Integer, String> {

        protected String doInBackground(Void... userLogin) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(requestBuilder.getUserLoginRequest(myEmailAddress, myPassword));

            String result;
            String userID = null;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (requestBuilder.jSONResultIsSuccess(result))
                    userID = requestBuilder.jSONUserID(result);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return userID;
        }


    }

    /**
     * AddUser class executes an AsnycTask to update the local list of coordinates.
     *
     * @author leachad
     * @version 4.25.15
     */
    private static class GetUserCoordinates extends AsyncTask<Void, Integer, List<Coordinate>> {

        protected List<Coordinate> doInBackground(Void... getUserCoordinates) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(requestBuilder.getUserCoordinateRequest(myUserID, myStartTime, myEndTime));
            String result;
            List<Coordinate> loggedPoints = null;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (requestBuilder.jSONResultIsSuccess(result))
                    loggedPoints = requestBuilder.jSONLoggedPoints(result, myUserID);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return loggedPoints;
        }
    }


    /**
     * Private static class that runs an AsyncTask to grab the User Agreement
     * on the server and returns a String to the user.
     *
     * @author leachad
     * @version 4/29/15
     */
    private static class GetUserAgreement extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(requestBuilder.getUserAgreementRequest());
            Log.w("AgreementRequest", httpPost.getURI().toString());
            String result;
            String agreement = null;

            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                agreement = requestBuilder.jSONUserAgreement(result);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return agreement;
        }

    }

    /**
     * Private static class that runs an AsyncTask to reset the User Password via a
     * Web Browser.
     *
     * @author leachad
     * @version 4/29/15
     *          TODO Needs to sit inside a web view once logic is completed on server side
     */
    private static class ResetPassword extends AsyncTask<Void, Integer, String> {

        @Override
        protected String doInBackground(Void... params) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(requestBuilder.getUserResetRequest(myEmailAddress));
            String result;
            String instructions = JsonBuilder.VAL_FAIL;

            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (requestBuilder.jSONResultIsSuccess(result))
                    instructions = requestBuilder.jSONResetPassInstructions(result);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return instructions;
        }
    }

}
