package uw.buuteeq_ponyhax.app.test;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.robotium.solo.Solo;

import junit.framework.Assert;

import uw.buuteeq_ponyhax.app.LoginActivity;
import uw.buuteeq_ponyhax.app.RegisterActivity;

/**
 * Created by eduard_prokhor on 6/3/15.
 */
public class ForgotActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo mySolo;

    public ForgotActivityTest() {
        super(LoginActivity.class);
    }

    public void setUp() throws Exception {
        mySolo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        mySolo.finishOpenedActivities();
    }

    public void testForgot() throws Exception {
        mySolo.clickOnButton(2);
        Assert.assertTrue(
                mySolo.searchText("Password Reset"));
        mySolo.enterText(0, "eprokhor@uw.edu");
        mySolo.clickOnButton(1);
        assertTrue(mySolo.waitForText("Your password can be reset"));
    }

    public void testForgotIncorrect() throws Exception {
        mySolo.clickOnButton(2);
        Assert.assertTrue(
                mySolo.searchText("Password Reset"));
        mySolo.clickOnButton(1);
        assertTrue(mySolo.waitForText("Please try again later."));
    }

    public void testForgotFail() throws Exception {
        mySolo.clickOnButton(2);
        Assert.assertTrue(
                mySolo.searchText("Password Reset"));
        mySolo.enterText(0, "eprokhor@hacker.edu");
        mySolo.clickOnButton(1);
        assertTrue(mySolo.waitForText("Please try again later."));
    }
}
