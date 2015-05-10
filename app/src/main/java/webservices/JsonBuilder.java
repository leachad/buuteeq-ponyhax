/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package webservices;

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
public final class JsonBuilder extends PhpBuilder {
    public static final String VAL_SUCCESS = "success";
    public static final String VAL_FAIL = "fail";
    public static String KEY_POINTS = "points";
    public static String KEY_AGREEMENT = "agreement";
    public static String KEY_RESULT = "result";

    public JsonBuilder(final String theCurrentDomain) {
        super(theCurrentDomain);
    }


    /**
     * Private method to return a JSON object to the requesting inner class.
     *
     * @return theJSON
     */
    public boolean jSONResultIsSuccess(String theResult) throws JSONException {
        JSONObject json = new JSONObject(theResult);
        boolean isSuccess = false;
        if (json.getString(KEY_RESULT).matches(VAL_SUCCESS)) {
            isSuccess = true;
        }

        return isSuccess;
    }

    /**
     * Private method to return the Users unique ID after successfully logging
     * in to the server.
     */
    public String jSONUserID(String theResult) throws JSONException {
        JSONObject json = new JSONObject(theResult);
        String userID = null;
        if (json.getString(KEY_RESULT).matches(VAL_SUCCESS))
            userID = json.getString(URL_USER_ID);
        return userID;
    }

    /**
     * Public method to return the User agreement to the calling code.
     *
     * @return theUserAgreement
     */
    public String jSONUserAgreement(String theResult) throws JSONException {
        JSONObject json = new JSONObject(theResult);
        String userAgreement = null;
        if (json.getString(KEY_RESULT).matches(VAL_SUCCESS))
            userAgreement = json.getString(KEY_AGREEMENT);
            System.out.println(userAgreement);
        return userAgreement;
    }

    /**
     * Private method to return the points gleaned from querying the database.
     */
    public List<Coordinate> jSONLoggedPoints(String theResult, String theUserID) throws JSONException {
        JSONObject json = new JSONObject(theResult);
        List<Coordinate> loggedPoints = null;

        if (json.getString(KEY_RESULT).matches(VAL_SUCCESS)) {
            loggedPoints = new ArrayList<>();
            JSONArray points = json.getJSONArray(KEY_POINTS);

            for (int i = 0; i < points.length(); i++) {
                JSONObject point = (JSONObject) points.get(i);
                loggedPoints.add(new Coordinate(point.getDouble(URL_LATITUDE), point.getDouble(URL_LONGITUDE),
                        point.getLong(URL_TIME), point.getDouble(URL_SPEED), point.getDouble(URL_HEADING), theUserID));
            }
        }
        return loggedPoints;
    }
}
