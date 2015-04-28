/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package webservices;

/**
 * Created by Andrew on 4/28/2015.
 * Enums used to maintain global scope for JSON evaluations.
 */
public enum JSON {
    KEY_RESULT("result"), KEY_USER_ID("userid"), VAL_SUCCESS("success"), VAL_FAIL("fail");
    private String myText;

    JSON(String theText) {
        myText = theText;
    }

    /**
     * Returns the text of this JSON enum.
     */
    public String getText() {
        return myText;
    }
}
