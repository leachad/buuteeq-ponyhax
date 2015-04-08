package uw.buuteeq_ponyhax.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by eduard_prokhor on 4/4/15.
 */
public class ForgotActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_page);

    }


    public void findUser() {
        UserStorageDatabaseHelper dbHelper = new UserStorageDatabaseHelper(getApplicationContext());
        UserStorageDatabaseHelper.UserCursor cursor = dbHelper.queryUsers();

        while(cursor.moveToNext()){
            cursor.
        }
    }

}
