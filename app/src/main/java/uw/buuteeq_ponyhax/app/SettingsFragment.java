package uw.buuteeq_ponyhax.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
        final SharedPreferences prefs = getActivity().getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        /** Instantiante the security question spinner.*/
        mQuestionSpinner = (Spinner) getActivity().findViewById(R.id.spinnerSecurityQuestion_reset);

        final User myRegisteredUser = new User();
        /** String value to update the New User security question field is gleaned from the spinner.*/
        myRegisteredUser.setSecurityQuestion(mQuestionSpinner.getSelectedItem().toString().trim());

        getActivity().findViewById(R.id.resetpass_from_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), CreateNewPasswordActivity.class);
                startActivity(myIntent);
            }
        });

        final EditText userinput = (EditText) getActivity().findViewById(R.id.newSecurityAnswer);

        userinput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    prefs.edit().putString(User.USER_QUESTION, myRegisteredUser.getSecurityQuestion()).apply();
                    prefs.edit().putString(User.USER_ANSWER, userinput.toString()).apply();
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Your security Question and answer have been changed.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
            });

    }
}
