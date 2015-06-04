package uw.buuteeq_ponyhax.app.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.EditText;

import com.robotium.solo.Solo;

import junit.framework.Assert;

import uw.buuteeq_ponyhax.app.AgreementActivity;

/**
 * Created by eduard_prokhor on 6/3/15.
 */
public class AgreementActivityTest extends ActivityInstrumentationTestCase2<AgreementActivity> {

    private Solo mySolo;

    public AgreementActivityTest() {
        super(AgreementActivity.class);
    }

    public void setUp() throws Exception {
        mySolo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        mySolo.finishOpenedActivities();
    }

    public void testAgrementShowedup() throws Exception {

        Assert.assertTrue(mySolo.searchText("Application Terms and Conditions of Use"));
    }
}
