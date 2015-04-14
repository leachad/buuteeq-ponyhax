package uw.buuteeq_ponyhax.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import db.User;


/**
 * Created by eduard_prokhor on 4/13/15.
 */
public class SettingsFragment extends Fragment {

    private Spinner mQuestionSpinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.settings_fragment_layout, container, false);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = getActivity().getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        /** Instantiante the security question spinner.*/
        mQuestionSpinner = (Spinner) getActivity().findViewById(R.id.spinnerSecurityQuestion_reset);

        User myRegisteredUser = new User();
        /** String value to update the New User security question field is gleaned from the spinner.*/
        myRegisteredUser.setSecurityQuestion(mQuestionSpinner.getSelectedItem().toString().trim());

        prefs.edit().putString(User.USER_QUESTION, myRegisteredUser.getSecurityQuestion()).apply();

        getActivity().findViewById(R.id.resetpass_from_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), CreateNewPasswordActivity.class);
                startActivity(myIntent);
            }
            });
    }
}
