/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package webservices;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import db.User;

/**
 * UserDriver will be the class that stores and gets users from the webservices
 * provided by Dr. Abraham.
 * Created by leachad on 4/22/15.
 */
public class WebDriver {

    /** Private field to hold a reference to the add user php file domain.*/
    private static final String ADD_USER_ADDRESS = "http://450.atwebpages.com/adduser.php";

    /** Private fields to hold variable strings needed to properly add a user via the url.*/
    private static final String URL_APPEND_ARGS = "?";
    private static final String URL_ADD_ARGS = "&";
    private static final String URL_SPACE = "%20";
    private static final String URL_EMAIL = "email=";
    private static final String URL_PASSWORD = "password=";
    private static final String URL_SEC_QUESTION = "question=";
    private static final String URL_SEC_ANSWER = "answer=";

    /** Private fields to hold the parameter variables before background tasks are executed.*/
    private User myUser;

    public WebDriver() {
        myUser = null;
    }


    public void addUser(User theUser) {
        myUser = theUser;
        new AddUser().execute();
    }

    /**
     * Private method to create an add user string.
     * @return addUserString
     */
    private String getAddUserString() {
        return ADD_USER_ADDRESS + URL_APPEND_ARGS + URL_EMAIL + myUser.getEmail() + URL_ADD_ARGS
                + URL_PASSWORD + myUser.getPassword() + URL_ADD_ARGS + URL_SEC_QUESTION
                + myUser.getSecurityQuestion().replaceAll(" ", URL_SPACE) + URL_ADD_ARGS
                + URL_SEC_ANSWER + myUser.getSecurityAnswer().replaceAll(" ", URL_SPACE);
    }


    private class AddUser extends AsyncTask<Void, Integer, String> {

        protected String doInBackground(Void... addUser) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(getAddUserString());

            String result = "failed";
            try {
                HttpResponse response = httpClient.execute(httpGet);
                result = EntityUtils.toString(response.getEntity());
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("Email service", result);
            return result;
        }
    }
}
