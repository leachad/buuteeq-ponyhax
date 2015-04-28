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
     * Private fields to hold the parameter variables before background tasks are executed.
     */
    private User myUser;
    private List<Coordinate> myCoordinateList;
    private String myEmailAddress;
    private String myPassword;
    private long myStartTime;
    private long myEndTime;

    /**
     * Constructor of a WebDriver class.
     */
    public WebDriver() {
        myUser = null;
        myCoordinateList = null;
        myEmailAddress = null;
        myPassword = null;
        myEndTime = 0;
        myStartTime = 0;
    }

    /**
     * POST OPERATIONS.
     */
    public String addUser(User theUser) throws ExecutionException, InterruptedException {
        myUser = theUser;
        return new AddUser().execute().get();
    }

    public void addCoordinates(List<Coordinate> theCoordinateList) {
        myCoordinateList = theCoordinateList;
        new AddCoordinates().execute();
    }

    /**
     * GET OPERATIONS.
     */
    public String checkUserCredentials(final String theEmail, final String thePassword) throws ExecutionException, InterruptedException {
        myEmailAddress = theEmail;
        myPassword = thePassword;
        return new UserLogin().execute().get();
    }

    public List<Coordinate> getLoggedCoordinates(final long theStartTime, final long theEndTime) throws ExecutionException, InterruptedException {
        myStartTime = theStartTime;
        myEndTime = theEndTime;
        return new GetUserCoordinates().execute().get();
    }


    /**
     * AddUser class executes an AsnycTask to post a user to the database.
     *
     * @author leachad
     * @version 4.25.15
     */
    private class AddUser extends AsyncTask<Void, Integer, String> {

        protected String doInBackground(Void... addUser) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(PHP.getAddUserRequest(myUser));

            String result = JSON.VAL_FAIL;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (JSON.jSONResultIsSuccess(result)) {
                    result = JSON.VAL_SUCCESS;
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
    private class AddCoordinates extends AsyncTask<Void, Integer, String> {

        /**
         * Private helper method to execute a series of coordinate posts and
         * gets.
         *
         * @param httpClient is the default client web service
         * @param httpPost   is the http post URL
         * @return result
         */
        private String executePost(HttpClient httpClient, HttpPost httpPost) {
            String result = JSON.VAL_FAIL;
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
                HttpPost httpPost = new HttpPost(PHP.getAddCoordinateRequest(coordinate, myUser.getUserID()));
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
    private class UserLogin extends AsyncTask<Void, Integer, String> {

        protected String doInBackground(Void... userLogin) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(PHP.getUserLoginRequest(myUser));

            Log.d("http string", httpPost.getURI().toString());

            String result = JSON.VAL_FAIL;
            String userID = null;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (JSON.jSONResultIsSuccess(result))
                    userID = JSON.jSONUserID(result);

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
    public class GetUserCoordinates extends AsyncTask<Void, Integer, List<Coordinate>> {

        protected List<Coordinate> doInBackground(Void... getUserCoordinates) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(PHP.getUserCoordinateRequest(myUser.getUserID(), myStartTime, myEndTime));

            String result = JSON.VAL_FAIL;
            List<Coordinate> loggedPoints = null;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (JSON.jSONResultIsSuccess(result))
                    loggedPoints = JSON.jSONLoggedPoints(result);

            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("CoordinateUpdate result", result);
            return loggedPoints;
        }
    }
}
