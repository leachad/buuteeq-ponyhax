package uw.buuteeq_ponyhax.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by BrentYoung on 4/12/15.
 */
public class MyAccountFragment extends Fragment {


    TextView mNameField;
    TextView mEmailField;
    TextView mDataPointsField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_my_account, container, false);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getActivity().getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        ((TextView) getActivity().findViewById(R.id.text_account_name)).setText("User ID: \n" + Long.toString(prefs.getLong(User.USER_ID, 0)));
        ((TextView) getActivity().findViewById(R.id.text_account_email)).setText("Email: \n" + prefs.getString(User.USER_EMAIL, "N/A"));
        ((TextView) getActivity().findViewById(R.id.text_account_numDataPoints)).setText("Data Points Logged: \n" + "N/A");
//        prefs.edit().clear().commit();

    }
}
