/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package webservices;

import android.os.AsyncTask;

import db.User;

/**
 * UserDriver will be the class that stores and gets users from the webservices
 * provided by Dr. Abraham.
 * Created by leachad on 4/22/15.
 */
public class UserDriver extends AsyncTask<User, Integer, Long> {

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

    protected Long doInBackground(User... theUsers) {
        long confirmation = 0;
        return confirmation;
    }
}
