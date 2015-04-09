package uw.buuteeq_ponyhax.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by eduard_prokhor on 4/4/15.
 */
public class ForgotActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forgot_page);

        (findViewById(R.id.passResetCancelButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ForgotActivity.this, LoginActivity.class);
                startActivity(myIntent);
            }
        });

        checkUser();
    }

    public  void checkUser(){
        UserStorageDatabaseHelper dbHelper = new UserStorageDatabaseHelper(getApplicationContext());
        final UserStorageDatabaseHelper.UserCursor cursor = dbHelper.queryUsers();

        final EditText userinput = (EditText) findViewById(R.id.userForPasswordreset);

        userinput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (!userinput.getText().toString().trim().matches(""))
                        while (cursor.moveToNext()) {
                            User temp = cursor.getUser();

                            if (temp.getUserName().trim().matches(userinput.getText().toString().trim())) {
                                ((TextView) findViewById(R.id.resetSecurityQuestion)).setText(cursor.getSecurityQuestion());
                            }
                        }
                }
                return false;
            }
        });

        final UserStorageDatabaseHelper.UserCursor cursor2 = dbHelper.queryUsers();

        (findViewById(R.id.passResetSubmit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText secAnswer = (EditText) findViewById(R.id.resetPassSecAnswer);
                if (!secAnswer.getText().toString().trim().matches("")){
                    while (cursor2.moveToNext()) {
                        User temp = cursor2.getUser();
                        if (temp.getUserName().trim().matches(userinput.getText().toString().trim())
                                && temp.getSecurityAnswer().trim().matches(secAnswer.getText().toString().trim())){

                            //make a random pass and send it to their email.
                            String testPass = Long.toHexString(Double.doubleToLongBits(Math.random()));
                            System.out.println(testPass);

                            Toast.makeText(getApplicationContext(),
                                    "Your new Randomely generated pass was send to your email", Toast.LENGTH_SHORT).show();

                        }
                    }
                }
            }
        });
    }
}