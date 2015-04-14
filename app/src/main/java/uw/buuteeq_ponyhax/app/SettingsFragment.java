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

        /** Instantiante the security question spinner.*/
        mQuestionSpinner = (Spinner) getActivity().findViewById(R.id.spinnerSecurityQuestions);
        (getActivity().findViewById((R.id.resetpass_from_settings)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SettingsFragment.this, CreateNewPasswordActivity.class);
                startActivity(myIntent);
            }
        });


    }
}
