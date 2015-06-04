package uw.buuteeq_ponyhax.app.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.robotium.solo.Solo;

import junit.framework.Assert;

import uw.buuteeq_ponyhax.app.RegisterActivity;

/**
 * Created by eduard_prokhor on 6/3/15.
 */
public class RegisterActivityTest extends ActivityInstrumentationTestCase2<RegisterActivity> {

    private Solo mySolo;

    public RegisterActivityTest() {
        super(RegisterActivity.class);
    }

    public void setUp() throws Exception {
        mySolo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        mySolo.finishOpenedActivities();
    }

//        public void testRegister() throws Exception {
//
//
//        }
}
