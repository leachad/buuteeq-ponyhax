package uw.buuteeq_ponyhax.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
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
        checkUser();

    }

    public  void checkUser(){
        UserStorageDatabaseHelper dbHelper = new UserStorageDatabaseHelper(getApplicationContext());
        final UserStorageDatabaseHelper.UserCursor cursor = dbHelper.queryUsers();

        final EditText userinput = (EditText) findViewById(R.id.userForPasswordreset);

        userinput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
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
    }
}

