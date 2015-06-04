package uw.buuteeq_ponyhax.app.test;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.test.InstrumentationTestCase;

import com.robotium.solo.Condition;
import com.robotium.solo.Solo;
import uw.buuteeq_ponyhax.app.LoginActivity;
import junit.framework.Assert;


/**
 * Created by eduard_prokhor on 6/3/15.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity>{

    private Solo mySolo;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    public void setUp() throws Exception {
        mySolo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        mySolo.finishOpenedActivities();
    }

    public void testCorrectLogin() throws Exception {

        mySolo.clickOnText("Enter Email");
        EditText emial_feild = mySolo.getEditText("Enter Email");
        mySolo.enterText(emial_feild, "eprokhor@uw.edu");
        mySolo.enterText(1, "123456");
        // Testing Orientation
        mySolo.setActivityOrientation(Solo.LANDSCAPE);
        boolean textFound = mySolo.searchText("eprokhor@uw.edu");
        assertTrue("Orientation change failed", textFound);
        mySolo.setActivityOrientation(Solo.PORTRAIT);
        textFound = mySolo.searchText("eprokhor@uw.edu");
        assertTrue("Orientation change failed", textFound);
        mySolo.clickOnButton("Login");
        Assert.assertTrue(mySolo.searchText("Overview"));
        mySolo.clickOnActionBarHomeButton();
        // Logout
        mySolo.clickInList(4);
        // Log back in
        assertTrue(mySolo.searchText("Enter Email"));
        mySolo.enterText(0, "eprokhor@uw.edu");
        mySolo.enterText(1, "123456");
        mySolo.clickOnButton("Login");
        mySolo.sleep(5000);
    }



    public void testIncorrectLogin() throws Exception {

        mySolo.clickOnText("Enter Email");
        EditText emial_feild = mySolo.getEditText("Enter Email");
        mySolo.enterText(emial_feild, "eprokhor@uw.edu");
        mySolo.enterText(1, "1234567");
        mySolo.clickOnButton("Login");
        Assert.assertTrue(mySolo.searchText("incorrect"));
    }
}
