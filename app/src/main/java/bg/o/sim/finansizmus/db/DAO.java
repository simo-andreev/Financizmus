package bg.o.sim.finansizmus.db;

//TODO - Currently the Db is structured to simulate a server-side Db on the device it is installed,
//TODO - exemplī grātiā - it holds all Users' data, meaning all registrations and accounts are solely local.
//TODO - I should research and implement a central server on my VPS, to which the app will then connect, post and receive data
//TODO - if possible, avoid making it an always-online app, rather require networking only on registration and occasional server-sync.


//TODO!!! - hash dem passwords boi. The whole plain-text storing, manipulating and transferring of User psswrds thing is pure cancer.

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import bg.o.sim.finansizmus.LoginActivity;
import bg.o.sim.finansizmus.R;
import bg.o.sim.finansizmus.utils.Util;

/**
 * Data Access Singleton Object for the device's SQLite Db.
 */
public class DAO {

    private static class DbHelper extends SQLiteOpenHelper {

        //DataBase version const:
        private static final int DB_VERSION = 0;

        //DateBase name const
        private static final String DB_NAME = "finansizmus.db";

        //Table name consts
        private static final String TABLE_USER = "user";
        private static final String TABLE_ACCOUNT = "account";
        private static final String TABLE_CATEGORY = "category";
        private static final String TABLE_TRANSACTION = "transaction";
        private static final String[] TABLES = { TABLE_USER, TABLE_ACCOUNT, TABLE_CATEGORY, TABLE_TRANSACTION };
        //Common column name consts for easier editing
        private static final String COMMON_COLUMN_ID = "_id";
        private static final String COMMON_COLUMN_USER_FK = "userFK";
        private static final String COMMON_COLUMN_ICON_ID = "iconID";
        private static final String COMMON_COLUMN_NAME = "name";

        //Specific table columns consts
        //USER columns:
        private static final String USER_COLUMN_ID = COMMON_COLUMN_ID;
        private static final String USER_COLUMN_NAME = COMMON_COLUMN_NAME;
        private static final String USER_COLUMN_MAIL = "mail";
        private static final String USER_COLUMN_PASSWORD = "password";
        //ACCOUNT columns:
        private static final String ACCOUNT_COLUMN_ID = COMMON_COLUMN_ID;
        private static final String ACCOUNT_COLUMN_USER_FK = COMMON_COLUMN_USER_FK;
        private static final String ACCOUNT_COLUMN_ICON_ID = COMMON_COLUMN_ICON_ID;
        private static final String ACCOUNT_COLUMN_NAME = COMMON_COLUMN_NAME;
        //CATEGORY columns:
        private static final String CATEGORY_COLUMN_ID = COMMON_COLUMN_ID;
        private static final String CATEGORY_COLUMN_USER_FK = COMMON_COLUMN_USER_FK;
        private static final String CATEGORY_COLUMN_ICON_ID = COMMON_COLUMN_ICON_ID;
        private static final String CATEGORY_COLUMN_NAME = COMMON_COLUMN_NAME;
        private static final String CATEGORY_COLUMN_IS_EXPENSE = "isExpense";
        private static final String CATEGORY_COLUMN_IS_FAVOURITE = "isFavourite";
        //TRANSACTION columns:
        private static final String TRANSACTION_COLUMN_ID = COMMON_COLUMN_ID;
        private static final String TRANSACTION_COLUMN_USER_FK = COMMON_COLUMN_USER_FK;
        private static final String TRANSACTION_COLUMN_CATEGORY_FK = "categoryFK";
        private static final String TRANSACTION_COLUMN_ACCOUNT_FK = "accountFK";
        private static final String TRANSACTION_COLUMN_DATE = "date";
        private static final String TRANSACTION_COLUMN_NOTE = "note";

        //CREATE TABLE statements
        private static final String CREATE_USER = "CREATE TABLE " + TABLE_USER +
                " ( " +
                USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_COLUMN_MAIL + " VARCHAR(80) UNIQUE NOT NULL, " +
                USER_COLUMN_NAME + " VARCHAR(80) NOT NULL, " +
                USER_COLUMN_PASSWORD + " VARCHAR(80) NOT NULL" +
                ");";
        private static final String CREATE_ACCOUNT =  "CREATE TABLE " + TABLE_ACCOUNT +
                " ( " +
                ACCOUNT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ACCOUNT_COLUMN_USER_FK + " INTEGER, " +
                ACCOUNT_COLUMN_ICON_ID + " INTEGER, " +
                ACCOUNT_COLUMN_NAME + " VARCHAR(40), " +
                "FOREIGN KEY (" + ACCOUNT_COLUMN_USER_FK + ") REFERENCES " + TABLE_USER + "(" + USER_COLUMN_ID + ")" +
                ");";
        private static final String CREATE_CATEGORY =  "CREATE TABLE " + TABLE_CATEGORY +
                " ( " +
                CATEGORY_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CATEGORY_COLUMN_USER_FK + " INTEGER, " +
                CATEGORY_COLUMN_ICON_ID + " INTEGER, " +
                CATEGORY_COLUMN_NAME + " VARCHAR(40), " +
                CATEGORY_COLUMN_IS_EXPENSE + " INTEGER, " +
                CATEGORY_COLUMN_IS_FAVOURITE + " INTEGER, " +
                "FOREIGN KEY (" + CATEGORY_COLUMN_USER_FK + ") REFERENCES " + TABLE_USER + "(" + USER_COLUMN_ID + ")" +
                ");";
        private static final String CREATE_TRANSACTION =  "CREATE TABLE " + TABLE_TRANSACTION +
                " ( " +
                TRANSACTION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TRANSACTION_COLUMN_DATE + " INTEGER, " +
                TRANSACTION_COLUMN_NOTE + " VARCHAR(255), " +
                TRANSACTION_COLUMN_USER_FK + " INTEGER, " +
                TRANSACTION_COLUMN_ACCOUNT_FK + " INTEGER, " +
                TRANSACTION_COLUMN_CATEGORY_FK + " INTEGER, " +
                "FOREIGN KEY (" + TRANSACTION_COLUMN_USER_FK + ") REFERENCES " + TABLE_USER + "(" + USER_COLUMN_ID + ")" +
                "FOREIGN KEY (" + TRANSACTION_COLUMN_ACCOUNT_FK + ") REFERENCES " + TABLE_ACCOUNT + "(" + ACCOUNT_COLUMN_ID + ")" +
                "FOREIGN KEY (" + TRANSACTION_COLUMN_CATEGORY_FK + ") REFERENCES " + TABLE_CATEGORY + "(" + CATEGORY_COLUMN_ID + ")" +
                ");";
        private static final String[] CREATE_STATEMENTS = {CREATE_USER, CREATE_ACCOUNT, CREATE_CATEGORY, CREATE_TRANSACTION};


        private static DbHelper instance;
        private Context context;

        private DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            this.context = context;
        }

        /**Singleton instance getter.
         * @param context Context instance required for SQLiteOpenHelper constructor.
         * @return Singleton instance of the DbHelper.
         */
        private static DbHelper getInstance(@NonNull Context context){
            if (context == null)
                throw new IllegalArgumentException("Context MUST ne non-null!!!");
            if (instance == null)
                instance = new DbHelper(context);
            return instance;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try{
                for (String s : CREATE_STATEMENTS)
                    db.execSQL(s);
                db.execSQL("CREATE INDEX user_mail_index ON " + TABLE_USER + " (" + USER_COLUMN_MAIL + ");");
            } catch (SQLiteException e){
                //In case of unsuccessful table creation, clear any created tables, display appropriate message and restart the app.
                //Fingers crossed, that this Toast never sees the light of day =]
                Util.toastLong(context, context.getString(R.string.sql_exception_message));

                for (String s : TABLES)
                    db.execSQL("DROP TABLE IF EXISTS " + s + " ;");

                Intent intent = new Intent(context, LoginActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmManager amgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                amgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 500, pendingIntent);

                System.exit(0);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            for (String s : TABLES)
                db.execSQL("DROP TABLE IF EXISTS " + s + " ;");
            onCreate(db);
        }
    }
}

