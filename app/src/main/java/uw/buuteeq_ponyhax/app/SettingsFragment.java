/*
 * Copyright (c) 4.17.15 -- Eduard Prokhor, Huy Ngo, Andrew Leach, Brent Young
 */

package uw.buuteeq_ponyhax.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import db.User;
import db.UserStorageDatabaseHelper;


/**
 * Created by eduard_prokhor on 4/13/15.
 * edited by andrew leach on 4/17/15
 */
public class SettingsFragment extends Fragment {

    /**
     * Fields needed by the whole class.
     */
    private Spinner mQuestionSpinner;
    private EditText mNewSecurityAnswer;
    private Button mSubmitButton;
    private Button mCancelButton;
    private SharedPreferences mSharedPreferences;
    private UserStorageDatabaseHelper mDbHelper;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Instantiate the security question spinner.*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.settings_fragment_layout, container, false);
    }

    private boolean fieldsEmpty() {
        return mQuestionSpinner.getSelectedItem().toString().matches(mQuestionSpinner.getPrompt().toString())
                || mNewSecurityAnswer.getText().toString().matches("");
    }

    @Override
    public void onResume() {
        super.onResume();
        mQuestionSpinner = (Spinner) getActivity().findViewById(R.id.spinnerSecurityQuestion_reset);
        mNewSecurityAnswer = (EditText) getActivity().findViewById(R.id.securityQuestionAnswer);
        mSubmitButton = (Button) getActivity().findViewById(R.id.submitNewSecurityAnswer);
        mCancelButton = (Button) getActivity().findViewById(R.id.cancelSettingsFragment);
        mDbHelper = new UserStorageDatabaseHelper(getActivity().getApplicationContext());
        mSharedPreferences = getActivity().getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        mSubmitButton.setOnClickListener(new SubmitListener());
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MyAccount.class));
                getActivity().finish();
            }
        });

        getActivity().findViewById(R.id.resetpass_from_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), CreateNewPasswordActivity.class);
                myIntent.putExtra(User.USER_EMAIL, mSharedPreferences.getString(User.USER_EMAIL, ""));
                startActivity(myIntent);
                getActivity().finish();
            }
        });


    }

    private class SubmitListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (fieldsEmpty()) {
                Toast.makeText(getActivity(), "Please make sure all fields are entered", Toast.LENGTH_SHORT).show();
            } else {
                mSharedPreferences.edit().putString(User.USER_QUESTION, mQuestionSpinner.getSelectedItem().toString().trim()).apply();
                mSharedPreferences.edit().putString(User.USER_ANSWER, mNewSecurityAnswer.toString().trim()).apply();
                mDbHelper.modifySecurityQuestion(mQuestionSpinner.getSelectedItem().toString().trim(), mSharedPreferences.getLong(User.USER_ID, 0));
                mDbHelper.modifySecurityAnswer(mNewSecurityAnswer.getText().toString(), mSharedPreferences.getLong(User.USER_ID, 0));
                Toast.makeText(getActivity().getApplicationContext(),
                        "Your security Question and answer have been changed.", Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(getActivity(), MyAccount.class);
                myIntent.putExtra(User.USER_QUESTION, mSharedPreferences.getString(User.USER_QUESTION, ""));
                myIntent.putExtra(User.USER_ANSWER, mSharedPreferences.getString(User.USER_ANSWER, ""));
                startActivity(myIntent);
                getActivity().finish();
            }
        }
    }

}
