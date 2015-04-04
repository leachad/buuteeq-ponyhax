package uw.buuteeq_ponyhax.phase_one;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;

/**
 * Created by Andrew on 4/4/2015.
 */
public class UserStorageDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "UserStorage.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserStorageContract.UserStorageEntry.TABLE_NAME + " (" +
                    UserStorageContract.UserStorageEntry.USER_ENTRY_ID + " INTEGER PRIMARY KEY," +
                    UserStorageContract.UserStorageEntry.USERNAME + TEXT_TYPE + COMMA_SEP +
                    UserStorageContract.UserStorageEntry.EMAIL_ADDRESS + TEXT_TYPE + COMMA_SEP +
                    UserStorageContract.UserStorageEntry.PASSWORD + TEXT_TYPE + COMMA_SEP +
                    UserStorageContract.UserStorageEntry.SECURITY_QUESTION + TEXT_TYPE + COMMA_SEP +
                    UserStorageContract.UserStorageEntry.SECURITY_ANSWER + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserStorageContract.UserStorageEntry.TABLE_NAME;

    public UserStorageDatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
