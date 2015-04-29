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
    public static String addUser(User theUser) throws ExecutionException, InterruptedException {
        myUser = theUser;
        return new AddUser().execute().get();
    }

    public static void addCoordinates(List<Coordinate> theCoordinateList) {
        myCoordinateList = theCoordinateList;
        new AddCoordinates().execute();
    }

    //TODO Write a similar static method in here for the Reset Password that will sit nicely inside a webview
    public static void resetPassword(final String theEmailAddress) {
        myEmailAddress = theEmailAddress;
        new ResetPassword().execute();
    }

    /**
     * GET OPERATIONS.
     */
    public static String checkUserCredentials(final String theEmail, final String thePassword) throws ExecutionException, InterruptedException {
        myEmailAddress = theEmail;
        myPassword = thePassword;
        return new UserLogin().execute().get();
    }

    public static List<Coordinate> getLoggedCoordinates(final long theStartTime, final long theEndTime) throws ExecutionException, InterruptedException {
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
    private static class AddUser extends AsyncTask<Void, Integer, String> {

        protected String doInBackground(Void... addUser) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(requestBuilder.getAddUserRequest(myUser));
            Log.d("http Adduser", httpPost.getURI().toString());
            String result = JsonBuilder.VAL_FAIL;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (requestBuilder.jSONResultIsSuccess(result)) {
                    result = JsonBuilder.VAL_SUCCESS;
                }
            } catch (Exception e) {
                e.printStackTrace();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("AddCoordinates result", result);
            return result;
        }

        protected String doInBackground(Void... addCoordinates) {

            String result = "";
            for (Coordinate coordinate : myCoordinateList) {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(requestBuilder.getAddCoordinateRequest(coordinate, myUser.getUserID()));
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

            Log.d("http string", httpPost.getURI().toString());

            String result = JsonBuilder.VAL_FAIL;
            String userID = null;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (requestBuilder.jSONResultIsSuccess(result))
                    userID = requestBuilder.jSONUserID(result);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            Log.d("UserLogin result", result);
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
            HttpPost httpPost = new HttpPost(requestBuilder.getUserCoordinateRequest(myUser.getUserID(), myStartTime, myEndTime));

            String result = JsonBuilder.VAL_FAIL;
            List<Coordinate> loggedPoints = null;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (requestBuilder.jSONResultIsSuccess(result))
                    loggedPoints = requestBuilder.jSONLoggedPoints(result);

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("CoordinateUpdate result", result);
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
            String result = JsonBuilder.VAL_FAIL;

            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (requestBuilder.jSONResultIsSuccess(result))
                    result = requestBuilder.jSONUserAgreement(result);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            Log.d("AGREEMENT RET: ", result);
            return result;
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
            String result = JsonBuilder.VAL_FAIL;

            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (requestBuilder.jSONResultIsSuccess(result))
                    result = requestBuilder.jSONUserAgreement(result);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            Log.d("AGREEMENT RET: ", result);
            return null;
        }
    }

}
