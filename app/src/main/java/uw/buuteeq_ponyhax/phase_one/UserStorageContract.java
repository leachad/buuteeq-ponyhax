package uw.buuteeq_ponyhax.phase_one;

import android.provider.BaseColumns;

/**
 * Created by Andrew on 4/4/2015.
 */
public final class UserStorageContract {

    /**
     * Do nothing empty constructor to prevent
     * instantiation of the super class
     */
    public UserStorageContract() {}

    /**
     * Inner class to define the contents of the table.
     */
    public static abstract class UserStorageEntry implements BaseColumns {
        public static final String TABLE_NAME = "userStorage";
        public static final String USER_ENTRY_ID = "userEntryID";
        public static final String USERNAME = "userName";
        public static final String EMAIL_ADDRESS = "emailAddress";
        public static final String PASSWORD = "passWord";
        public static final String SECURITY_QUESTION = "securityQuestion";
        public static final String SECURITY_ANSWER = "securityAnswer";


    }
}
