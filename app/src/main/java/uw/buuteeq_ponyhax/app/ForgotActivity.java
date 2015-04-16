package uw.buuteeq_ponyhax.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import db.User;
import db.UserStorageDatabaseHelper;
import email.EmailSend;

/**
 * Created by eduard_prokhor on 4/4/15.
 */
public class ForgotActivity extends ActionBarActivity {

    EmailSend email = new EmailSend();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forgot_page);
        setTitle("");
        (findViewById(R.id.passResetCancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent myIntent = new Intent(ForgotActivity.this, LoginActivity.class);
//                startActivity(myIntent);
                finish();
            }
        });


        checkUser();
    }

    public void checkUser() {
        final UserStorageDatabaseHelper dbHelper = new UserStorageDatabaseHelper(getApplicationContext());

        final EditText userInput = (EditText) findViewById(R.id.userForPasswordreset);

        userInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT && !userInput.getText().toString().trim().matches("")) {
                    String userEmail = userInput.getText().toString().toLowerCase().trim();
                    if (dbHelper.obtainUserEmail(dbHelper.obtainUserID(userEmail)).matches(userEmail)) {
                        Log.d(ForgotActivity.this.getLocalClassName(), "Setting question");
                        ((TextView) findViewById(R.id.resetSecurityQuestion)).setText(dbHelper.obtainUserSecurityQuestion(dbHelper.obtainUserID(userEmail)));
                    }

            } else {
                Toast.makeText(getApplicationContext(), "Email not found in the database", Toast.LENGTH_SHORT);
                userInput.setText("");
                userInput.requestFocus();
            }
            return false;
        }
    }

    );

    final UserStorageDatabaseHelper.UserCursor cursor2 = dbHelper.queryUsers();

    (

    findViewById(R.id.passResetSubmit)

    ).

    setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View v){
            EditText secAnswer = (EditText) findViewById(R.id.resetPassSecAnswer);
            if (!secAnswer.getText().toString().trim().matches("")) {
                boolean found = false;
                while (cursor2.moveToNext() && !found) {
                    User temp = cursor2.getUser();
                    if (temp.getEmail().toLowerCase().trim().matches(userInput.getText().toString().toLowerCase().trim())
                            && temp.getSecurityAnswer().trim().matches(secAnswer.getText().toString().trim())) {
                        found = true;
                        String usersEmail = temp.getEmail().trim();

                        //make a random pass and send it to their email.
                        String testPass = Long.toHexString(Double.doubleToLongBits(Math.random()));

                        //update the database with the new pass of the user
                        dbHelper.modifyUserPassword(testPass, temp.getUserID());


                        //testing to send to my email.
                        email.sendEmail(usersEmail, testPass);
                        Toast.makeText(getApplicationContext(),
                                "Your new randomly generated password was sent to your email", Toast.LENGTH_SHORT).show();

                        //create prefs from email so that it is independent from other email resets.
                        SharedPreferences resetPrefs = getSharedPreferences(usersEmail, MODE_PRIVATE);
                        resetPrefs.edit().putBoolean(User.USER_RESET, true).commit();

                        finish();

                    }
                }
                if (!found) {
                    Toast.makeText(getApplicationContext(), "User not found or incorrect answer!", Toast.LENGTH_SHORT).show();
                }
            }
            cursor2.close();
        }
    }

    );
}
}