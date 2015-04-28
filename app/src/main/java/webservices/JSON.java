/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package webservices;

import android.annotation.TargetApi;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import db.Coordinate;

/**
 * Created by Andrew on 4/28/2015.
 * Enums used to maintain global scope for JSON evaluations.
 */
public final class JSON extends PHP {
    public static final String KEY_POINTS = "points";
    public static final String KEY_RESULT = "result";
    public static final String VAL_SUCCESS = "success";
    public static final String VAL_FAIL = "fail";

    public JSON(final String theCurrentDomain) {
        super(theCurrentDomain);
    }


    /**
     * Private method to return a JSON object to the requesting inner class.
     *
     * @return theJSON
     */
    public static boolean jSONResultIsSuccess(String theResult) throws JSONException {
        JSONObject json = new JSONObject(theResult);
        boolean isSuccess = false;
        if (json.getString(JSON.KEY_RESULT).matches(JSON.VAL_SUCCESS)) {
            isSuccess = true;
        }

        return isSuccess;
    }

    /**
     * Private method to return the Users unique ID after successfully logging
     * in to the server.
     */
    public static String jSONUserID(String theResult) throws JSONException {
        JSONObject json = new JSONObject(theResult);
        String userID = null;
        if (json.getString(KEY_RESULT).matches(VAL_SUCCESS))
            userID = json.getString(URL_USER_ID);
        return userID;
    }

    /**
     * Private method to return the points gleaned from querying the database.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static List<Coordinate> jSONLoggedPoints(String theResult) throws JSONException {
        JSONObject json = new JSONObject(theResult);
        List<Coordinate> loggedPoints = null;
        if (json.getString(JSON.KEY_RESULT).matches(VAL_SUCCESS)) {
            loggedPoints = new ArrayList<>();
            JSONArray points = new JSONArray(json.getJSONArray(JSON.KEY_POINTS));
            for (int i = 0; i < points.length(); i++) {
                JSONObject point = (JSONObject) points.get(i);
                loggedPoints.add(new Coordinate(point.getDouble(URL_LATITUDE), point.getDouble(URL_LONGITUDE),
                        point.getLong(URL_TIMESTAMP), point.getDouble(URL_SPEED), point.getDouble(URL_HEADING),
                        point.getString(URL_SOURCE)));
            }
        }
        return loggedPoints;
    }
}
