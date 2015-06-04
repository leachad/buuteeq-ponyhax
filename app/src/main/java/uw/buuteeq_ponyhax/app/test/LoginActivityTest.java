package uw.buuteeq_ponyhax.app.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.robotium.solo.Solo;
import uw.buuteeq_ponyhax.app.LoginActivity;
import junit.framework.Assert;


/**
 * Created by eduard_prokhor on 6/3/15.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity>{

    private Solo mySolo;

    public LoginActivityTest() {super(LoginActivity.class);}

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
        mySolo.clickOnButton("Login");
        Assert.assertTrue(mySolo.searchText("Overview"));
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
