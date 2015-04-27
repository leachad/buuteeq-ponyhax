/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package webservices;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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

    /** Private field to hold a reference to the add user php file domain.*/
    private static final String ADD_USER_ADDRESS = "http://450.atwebpages.com/adduser.php";

    /** Private field to hold a reference to the add coordinate php file domain.*/
    private static final String ADD_COORDINATE_ADDRESS = "http://450.atwebpages.com/";

    /** Private field to hold a reference to the add coordinate php file domain.*/
    private static final String LOGIN_USER_ADDRESS = "http://450.atwebpages.com/login.php";

    /** Private field to hold a reference to the add coordinate php file domain.*/
    private static final String GET_USER_COORDINATES_ADDRESS = "http://450.atwebpages.com/";

    /** Private fields to hold variable strings needed to properly add a user via the url.*/
    private static final String URL_EMAIL = "email";
    private static final String URL_PASSWORD = "password";
    private static final String URL_SEC_QUESTION = "question";
    private static final String URL_SEC_ANSWER = "answer";

    /** Private fields to hold variable strings needed to properly add a coordinate via the url.*/
    private static final String URL_LATITUDE = "latitude";
    private static final String URL_LONGITUDE = "longitude";
    private static final String URL_SPEED = "speed";
    private static final String URL_HEADING = "heading";
    private static final String URL_SOURCE = "user_id";
    private static final String URL_TIMESTAMP = "timestamp";

    /** Private fields to hold references to JSON keys.*/
    private static final String KEY_RESULT = "result";
    private static final String KEY_USERID = "userid";
    private static final String VAL_SUCCESS = "success";
    private static final String VAL_FAIL = "fail";

    /** Private fields to hold the parameter variables before background tasks are executed.*/
    private User myUser;
    private List<Coordinate> myCoordinateList;
    private String myEmailAddress;
    private String myPassword;

    /**
     * Constructor of a WebDriver class.
     */
    public WebDriver() {
        myUser = null;
        myCoordinateList = null;
        myEmailAddress = null;
        myPassword = null;
    }

    /** POST OPERATIONS.*/
    public String addUser(User theUser) throws ExecutionException, InterruptedException {
        myUser = theUser;
        return new AddUser().execute().get();
    }

    public void addCoordinates(List<Coordinate> theCoordinateList) {
        myCoordinateList = theCoordinateList;
        new AddCoordinates().execute();
    }

    /** GET OPERATIONS.*/
    public long checkUserCredentials(final String theEmail, final String thePassword) throws ExecutionException, InterruptedException {
        myEmailAddress = theEmail;
        myPassword = thePassword;
        return new UserLogin().execute().get();
    }

    public List<Coordinate> updateCoordinates(final int theUserID) throws ExecutionException, InterruptedException {
        return new GetUserCoordinates().execute().get();
    }


    /**
     * Private method to create an add user request
     * @return encodedEntity
     */
    private List<NameValuePair> getAddUserRequest() {
        List<NameValuePair> encodedEntity = new ArrayList<>();
        encodedEntity.add(new BasicNameValuePair(URL_EMAIL, myUser.getEmail()));
        encodedEntity.add(new BasicNameValuePair(URL_PASSWORD, myUser.getPassword()));
        encodedEntity.add(new BasicNameValuePair(URL_SEC_QUESTION, myUser.getSecurityQuestion()));
        encodedEntity.add(new BasicNameValuePair(URL_SEC_ANSWER, myUser.getSecurityAnswer()));
        return encodedEntity;
    }

    /**
     * Private method to create an add coordinate request
     * @return encodedEntity
     */
    private List<NameValuePair> getAddCoordinateRequest(Coordinate thisCoordinate) {
        List<NameValuePair> encodedEntity = new ArrayList<>();
        encodedEntity.add(new BasicNameValuePair(URL_LATITUDE, Long.toString(thisCoordinate.getLatitude())));
        encodedEntity.add(new BasicNameValuePair(URL_LONGITUDE, Long.toString(thisCoordinate.getLongitude())));
        encodedEntity.add(new BasicNameValuePair(URL_SPEED, Long.toString(thisCoordinate.getUserSpeed())));
        encodedEntity.add(new BasicNameValuePair(URL_HEADING, Long.toString(thisCoordinate.getHeading())));
        encodedEntity.add(new BasicNameValuePair(URL_SOURCE, Long.toString(thisCoordinate.getUserID())));
        encodedEntity.add(new BasicNameValuePair(URL_TIMESTAMP, Long.toString(thisCoordinate.getTimeStamp())));
        return encodedEntity;
    }

    /**
     * Private method to create a user login request
     * @return encodedEntity
     */
    private List<NameValuePair> getUserLoginRequest() {
        List<NameValuePair> encodedEntity = new ArrayList<>();
        encodedEntity.add(new BasicNameValuePair(URL_EMAIL, myEmailAddress));
        encodedEntity.add(new BasicNameValuePair(URL_PASSWORD, myPassword));
        return encodedEntity;
    }

    /**
     * Private method to grant a user access to their logged coordinates
     * @return encodedEntity
     */
    private List<NameValuePair> getUserCoordinateRequest(final int theUserID) {
        List<NameValuePair> encodedEntity = new ArrayList<>();
        encodedEntity.add(new BasicNameValuePair(URL_SOURCE, Integer.toString(theUserID)));
        return encodedEntity;
    }

    /**
     * Private method to return a JSON object to the requesting inner class.
     * @return theJSON
     */
    private JSONObject getJSONObject(String theResult) {
        JSONObject json = new JSONObject();
        try {
            json = new JSONObject(theResult);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }


    /**
     * AddUser class executes an AsnycTask to post a user to the database.
     * @author leachad
     * @version 4.25.15
     */
    private class AddUser extends AsyncTask<Void, Integer, String> {

        protected String doInBackground(Void... addUser) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(ADD_USER_ADDRESS);

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(getAddUserRequest()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String result = VAL_FAIL;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (getJSONObject(result).get(KEY_RESULT).toString().matches(VAL_SUCCESS)) {
                    result = VAL_SUCCESS;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("AddUser result", result);
            return result;
        }
    }

    /**
     * AddUser class executes an AsnycTask to post a user to the database.
     * @author leachad
     * @version 4.25.15
     */
    private class AddCoordinates extends AsyncTask<Void, Integer, String> {

        /**
         * Private helper method to execute a series of coordinate posts and
         * gets.
         * @param httpClient is the default client web service
         * @param httpPost is the http post URL
         * @return result
         */
        private String executePost(HttpClient httpClient, HttpPost httpPost) {
            String result = VAL_FAIL;
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
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(ADD_COORDINATE_ADDRESS);
            String result = "";
            for (Coordinate coordinate : myCoordinateList) {
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(getAddCoordinateRequest(coordinate)));
                    result = executePost(httpClient, httpPost);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    }

    /**
     * AddUser class executes an AsnycTask to post a user to the database.
     * @author leachad
     * @version 4.25.15
     */
    private class UserLogin extends AsyncTask<Void, Integer, Long> {

        protected Long doInBackground(Void... userLogin) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(LOGIN_USER_ADDRESS + "?" + URL_EMAIL + "=" + myEmailAddress
                                        + "&" + URL_PASSWORD + "=" + myPassword);

            Log.d("http string", httpPost.getURI().toString());

            String result = VAL_FAIL;
            long userID = 0;
            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
                if (getJSONObject(result).getString(KEY_RESULT).matches(VAL_SUCCESS)) {
                    userID = getJSONObject(result).getLong(KEY_USERID);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("UserLogin result", result);
            return userID;
        }


    }

    /**
     * AddUser class executes an AsnycTask to update the local list of coordinates.
     * @author leachad
     * @version 4.25.15
     */
    public class GetUserCoordinates extends AsyncTask<Void, Integer, List<Coordinate>> {

        protected List<Coordinate> doInBackground(Void... getUserCoordinates) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(GET_USER_COORDINATES_ADDRESS);

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(getAddUserRequest()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String result = "failed";
            /**
             * TODO Database will return a JSON object, or a blob, still undetermined as
             * we are waiting on the php files from Menaka
             */

            try {
                HttpResponse response = httpClient.execute(httpPost);
                result = EntityUtils.toString(response.getEntity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("CoordinateUpdate result", result);
            return null;
        }
    }
}
