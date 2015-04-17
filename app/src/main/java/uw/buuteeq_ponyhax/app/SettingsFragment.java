package uw.buuteeq_ponyhax.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import db.User;
import db.UserStorageDatabaseHelper;


/**
 * Created by eduard_prokhor on 4/13/15.
 * edited by andrew leach on 4/17/15
 */
public class SettingsFragment extends Fragment {

    /** Fields needed by the whole class.*/
    private Spinner mQuestionSpinner;
    private EditText mNewSecurityAnswer;
    private Button mSubmitButton;
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

    @Override
    public void onResume() {
        super.onResume();
        mQuestionSpinner = (Spinner) getActivity().findViewById(R.id.spinnerSecurityQuestion_reset);
        mNewSecurityAnswer = (EditText) getActivity().findViewById(R.id.securityQuestionAnswer);
        mSubmitButton = (Button) getActivity().findViewById(R.id.submitNewSecurityAnswer);
        mDbHelper = new UserStorageDatabaseHelper(getActivity().getApplicationContext());
        mSharedPreferences = getActivity().getSharedPreferences(User.USER_PREFS, Context.MODE_PRIVATE);
        mSubmitButton.setOnClickListener(new SubmitListener());

        getActivity().findViewById(R.id.resetpass_from_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), CreateNewPasswordActivity.class);
                myIntent.putExtra(User.USER_EMAIL, mSharedPreferences.getString(User.USER_EMAIL, ""));
                startActivity(myIntent);
            }
        });



    }

    private class SubmitListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            mSharedPreferences.edit().putString(User.USER_QUESTION, mQuestionSpinner.getSelectedItem().toString().trim()).apply();
            mSharedPreferences.edit().putString(User.USER_ANSWER, mNewSecurityAnswer.toString().trim()).apply();
            Toast.makeText(getActivity().getApplicationContext(), "USER ID IS " + mSharedPreferences.getLong(User.USER_ID, 0), Toast.LENGTH_SHORT).show();
            mDbHelper.modifySecurityQuestion(mQuestionSpinner.getSelectedItem().toString().trim(), mSharedPreferences.getLong(User.USER_ID, 0));
            mDbHelper.modifySecurityAnswer(mNewSecurityAnswer.getText().toString(), mSharedPreferences.getLong(User.USER_ID, 0));
            Toast.makeText(getActivity().getApplicationContext(),
                    "Your security Question and answer have been changed.", Toast.LENGTH_SHORT).show();
        }
    }

}
