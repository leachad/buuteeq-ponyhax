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
public class RegisterActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Solo mySolo;

    public RegisterActivityTest() {
        super(LoginActivity.class);
    }

    public void setUp() throws Exception {
        mySolo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        mySolo.finishOpenedActivities();
    }

    public void testRegister() throws Exception {
        mySolo.clickOnButton(1);
        mySolo.clickOnButton(1);
        mySolo.enterText(0, "eprokhor@uw.edu");
        mySolo.enterText(1, "123456");
        mySolo.enterText(2, "123456");

        View view1 = mySolo.getView(Spinner.class, 0);
        mySolo.clickOnView(view1);
        mySolo.scrollToTop();
        mySolo.clickOnView(mySolo.getView(TextView.class, 1));

        mySolo.enterText(3, "bob");
        mySolo.enterText(4, "bob");
        // Testing Orientation
        mySolo.setActivityOrientation(Solo.LANDSCAPE);
        boolean textFound = mySolo.searchText("eprokhor@uw.edu");
        assertTrue("Orientation change failed", textFound);
        mySolo.setActivityOrientation(Solo.PORTRAIT);
        textFound = mySolo.searchText("eprokhor@uw.edu");
        assertTrue("Orientation change failed", textFound);
        mySolo.clickOnButton(0);
        assertTrue(mySolo.waitForText("That User already exists!"));
    }

    public void testRegisterIncorrect() throws Exception {
        mySolo.clickOnButton(1);

        mySolo.clickOnButton(1);
        mySolo.enterText(0, "eprokhor@uw.edu");

        mySolo.clickOnButton(0);
        assertTrue(mySolo.waitForText("All Fields must have values!"));
    }
}
