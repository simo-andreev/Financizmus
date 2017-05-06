package bg.o.sim.finansizmus.dataManagment;

//TODO - Currently the Db is structured to simulate a server-side Db on the device it is installed,
//TODO - exemplī grātiā - it holds all Users' data, meaning all registrations and accounts are solely local.
//TODO - I should research and implement a central server on my VPS, to which the app will then connect, post and receive data
//TODO - if possible, avoid making it an always-online app, rather require networking only on registration and occasional server-sync.


//TODO!!! - hash dem passwords boi. The whole plain-text storing, manipulating and transferring of User psswrds thing is pure cancer.

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import bg.o.sim.finansizmus.LoginActivity;
import bg.o.sim.finansizmus.MainActivity;
import bg.o.sim.finansizmus.R;
import bg.o.sim.finansizmus.RegisterActivity;
import bg.o.sim.finansizmus.model.Account;
import bg.o.sim.finansizmus.model.Category;
import bg.o.sim.finansizmus.model.Transaction;
import bg.o.sim.finansizmus.model.User;
import bg.o.sim.finansizmus.utils.Util;

/**
 * Data Access Singleton Object for the app's's SQLite Db.
 */
public class DAO {

    private static DAO instance;
    private DbHelper h;
    private Context context;
    private CacheManager cache;

    private DAO(@NonNull Context context) {
        if (context == null)
            throw new IllegalArgumentException("Context MUST ne non-null!!!");
        this.h = new DbHelper(context);
        this.cache = CacheManager.getInstance();
        this.context = context;

    }

    public static DAO getInstance(Context context) {
        if (instance == null)
            instance = new DAO(context);
        return instance;
    }

    /**
     * Checks the SQLite Db for a {@link User} entry, matching the parameters. If one is found, it loads the user's data and proceeds to MainActivity.
     *
     * @param mail     User e-mail.
     * @param password User password.
     * @param activity LogInActivity instance for visualizations.
     * @return <code>true</code> if logIn successful. <code>false</code> if logIn fails.
     */
    @Nullable
    public boolean logInUser(String mail, String password, final LoginActivity activity) {
        if (mail == null || password == null || mail.isEmpty() || password.isEmpty())
            return false;

        String selection = h.USER_COLUMN_MAIL + " = ? AND " + h.USER_COLUMN_PASSWORD + " = ? ";
        String[] selectionArgs = {mail, password};

        Cursor cursor = h.getReadableDatabase().query(h.TABLE_USER, null, selection, selectionArgs, null, null, null);
        if (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(h.USER_COLUMN_NAME));
            long id = cursor.getLong(cursor.getColumnIndex(h.USER_COLUMN_ID));
            User user = new User(mail, name, id);

            new AsyncTask<User, Void, User>() {

                @Override
                protected void onPreExecute() {
                    //TODO - display loading "circlet" in activity
                }

                @Override
                protected User doInBackground(User... params) {
                    long id = params[0].getId();

                    loadUserAccounts(id);
                    loadUserCategories(id);
                    loadUserTransactions(id);

                    return params[0];
                }

                @Override
                protected void onPostExecute(User user) {
                    cache.setLoggedUser(user);
                    Intent i = new Intent(activity, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Util.toastLong(context, context.getString(R.string.welcome_message) + " " + user.getName());
                    activity.startActivity(i);
                    activity.finish();
                }
            }.execute(user);

            return true;

        } else {
            Util.toastShort(context, context.getString(R.string.message_invalid_login));
            return false;
        }
    }

    /**
     * If no previous {@link User} entry with the same e-mail exists, adds a new User to the Db.
     * @param name User's personal name.
     * @param mail User's e-mail. <b>Must be unique.</b>
     * @param password User's password.
     * @return <code>false</code> if e-mail is already taken, <code>true</code> otherwise.
     */
    @Nullable
    public boolean registerUser(final String name, final String mail, final String password, final RegisterActivity activity){
        if (name == null || mail == null || password == null || name.isEmpty() || mail.isEmpty() || password.isEmpty())
            return false;

        new AsyncTask<Void, Void, User>(){

            @Override
            protected User doInBackground(Void... params) {
                ContentValues values = new ContentValues(3);
                values.put(h.USER_COLUMN_MAIL, mail);
                values.put(h.USER_COLUMN_NAME, name);
                values.put(h.USER_COLUMN_PASSWORD, password);

                long id = -1;

                try{
                    id = h.getWritableDatabase().insertWithOnConflict(h.TABLE_USER, null, values, SQLiteDatabase.CONFLICT_ROLLBACK);
                } catch (SQLiteException e){
                    // From what I gather, insertWithOnConflict is 'obsolete' and doesn't work properly... since at-least 2010...
                    // https://issuetracker.google.com/issues/36923483
                    // but our overlords at Google decided that marking that in the method docs or a @Deprecated annot
                    // would make life too easy I guess.
                    // TODO - reconsider using plain .insert() or check if insertOrThrow() works.
                }

                if (id == -1) return null;

                User u = new User(mail, name, id);

                addDefaultEntries(u);

                return u;
            }

            @Override
            protected void onPostExecute(User user) {
                if (user == null)
                    Util.toastLong(activity, activity.getString(R.string.message_email_taken));
                else{
                    cache.setLoggedUser(user);
                    Intent i = new Intent(activity, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Util.toastLong(context, context.getString(R.string.hello) + " " + user.getName() + "!");
                    activity.startActivity(i);
                    activity.finish();
                }
            }
        }.execute();
        return true;
    }

    private void addDefaultEntries(User u) {
        long id = u.getId();
        addCategory("Vehicle", R.mipmap.car, id, Category.Type.INCOME, false);
        addCategory("Clothes", R.mipmap.clothes,  id, Category.Type.INCOME, false);
        addCategory("Health", R.mipmap.heart,  id, Category.Type.INCOME, false);
        addCategory("Travel", R.mipmap.plane,  id, Category.Type.INCOME, false);
        addCategory("House", R.mipmap.home,  id, Category.Type.INCOME, false);
        addCategory("Sport", R.mipmap.swimming,  id, Category.Type.INCOME, false);
        addCategory("Food", R.mipmap.restaurant,  id, Category.Type.INCOME, false);
        addCategory("Transport", R.mipmap.train,  id, Category.Type.INCOME, false);
        addCategory("Entertainment", R.mipmap.cocktail,  id, Category.Type.INCOME, false);
        addCategory("Phone", R.mipmap.phone,  id, Category.Type.INCOME, false);

        addAccount("Cash", R.mipmap.cash, id);
        addAccount("Debit", R.mipmap.visa, id);
        addAccount("Credit", R.mipmap.mastercard, id);

        addCategory("Salary", R.mipmap.coins,   id, Category.Type.INCOME, false);
        addCategory("Savings", R.mipmap.money_box,   id, Category.Type.INCOME, false);
        addCategory("Other", R.mipmap.money_bag,   id, Category.Type.INCOME, false);
    }

    private void addAccount(String name, int iconId, long userId) {
        if (name == null || name.isEmpty() || iconId <= 0 || userId < 0) return;

        ContentValues values = new ContentValues(3);
        values.put(h.ACCOUNT_COLUMN_NAME, name);
        values.put(h.ACCOUNT_COLUMN_ICON_ID, iconId);
        values.put(h.ACCOUNT_COLUMN_USER_FK, userId);

        long id = -1;

        try{
            id = h.getWritableDatabase().insertWithOnConflict(h.TABLE_ACCOUNT, null, values, SQLiteDatabase.CONFLICT_ROLLBACK);
        } catch (SQLiteException e){
            //See @ previous use of insertWithOnConflict, for info, whi this try-catch is here.
        }
        if (id < 0) return;
        Account acc = new Account(name, iconId, id, userId);
        cache.addAccount(acc);
    }

    private void addCategory(String name, int iconId, long userId, Category.Type type, boolean isFavourite) {
        if (name == null || name.isEmpty() || iconId <= 0 || userId < 0 || type == null) return;

        ContentValues values = new ContentValues(5);
        values.put(h.CATEGORY_COLUMN_NAME, name);
        values.put(h.CATEGORY_COLUMN_ICON_ID, iconId);
        values.put(h.CATEGORY_COLUMN_USER_FK, userId);
        values.put(h.CATEGORY_COLUMN_IS_EXPENSE, type == Category.Type.EXPENSE ? 1 : 0);
        values.put(h.CATEGORY_COLUMN_IS_FAVOURITE, isFavourite ? 1 : 0);

        long id = -1;

        try {
            id = h.getWritableDatabase().insertWithOnConflict(h.TABLE_CATEGORY, null, values, SQLiteDatabase.CONFLICT_ROLLBACK);
        } catch (SQLiteException e) {
            //See @ previous use of insertWithOnConflict, for info, whi this try-catch is here.
        }

        if (id < 0) return;
        Category cat = new Category(name, iconId, id, userId, isFavourite, type);
        cache.addCategory(cat);
    }

    private void loadUserAccounts(long userId) {

        String[] columns = {
                h.ACCOUNT_COLUMN_ID,
                h.ACCOUNT_COLUMN_ICON_ID,
                h.ACCOUNT_COLUMN_NAME
        };

        String selection = h.ACCOUNT_COLUMN_USER_FK + " = " + userId;

        Cursor c = h.getReadableDatabase().query(h.TABLE_ACCOUNT, columns, selection, null, null, null, null);

        int indxId = c.getColumnIndex(h.ACCOUNT_COLUMN_ID);
        int indxIcon = c.getColumnIndex(h.ACCOUNT_COLUMN_ICON_ID);
        int indxName = c.getColumnIndex(h.ACCOUNT_COLUMN_NAME);

        while (c.moveToNext()) {
            long id = c.getLong(indxId);
            int icon = c.getInt(indxIcon);
            String name = c.getString(indxName);

            Account acc = new Account(name, icon, id, userId);
            cache.addAccount(acc);
        }
    }
    private void loadUserCategories(long userId) {

        String[] columns = {
                h.CATEGORY_COLUMN_ID,
                h.CATEGORY_COLUMN_ICON_ID,
                h.CATEGORY_COLUMN_NAME,
                h.CATEGORY_COLUMN_IS_EXPENSE,
                h.CATEGORY_COLUMN_IS_FAVOURITE
        };

        String selection = h.CATEGORY_COLUMN_USER_FK + " = " + userId;

        Cursor c = h.getReadableDatabase().query(h.TABLE_CATEGORY, columns, selection, null, null, null, null);

        int indxId = c.getColumnIndex(h.CATEGORY_COLUMN_ID);
        int indxIcon = c.getColumnIndex(h.CATEGORY_COLUMN_ICON_ID);
        int indxName = c.getColumnIndex(h.CATEGORY_COLUMN_NAME);
        int indxIsExp = c.getColumnIndex(h.CATEGORY_COLUMN_IS_EXPENSE);
        int indxIsFav = c.getColumnIndex(h.CATEGORY_COLUMN_IS_FAVOURITE);

        while (c.moveToNext()) {

            long id = c.getLong(indxId);
            int icon = c.getInt(indxIcon);
            String name = c.getString(indxName);
            Category.Type type = c.getInt(indxIsExp) == 1 ? Category.Type.EXPENSE : Category.Type.INCOME;
            Boolean isFavourite = c.getInt(indxIsFav) == 1;

            Category cat = new Category(name, icon, id, userId, isFavourite, type);

            cache.addCategory(cat);
        }
    }
    private void loadUserTransactions(long userId) {

        String[] columns = {
                h.TRANSACTION_COLUMN_ACCOUNT_FK,
                h.TRANSACTION_COLUMN_CATEGORY_FK,
                h.TRANSACTION_COLUMN_ID,
                h.TRANSACTION_COLUMN_DATE,
                h.TRANSACTION_COLUMN_NOTE,
                h.TRANSACTION_COLUMN_SUM
        };
        String selection = h.TRANSACTION_COLUMN_USER_FK + " = " + userId;

        Cursor c = h.getReadableDatabase().query(h.TABLE_TRANSACTION, columns, selection, null, null, null, null);

        int indxAccId = c.getColumnIndex(h.TRANSACTION_COLUMN_ACCOUNT_FK);
        int indxCatId = c.getColumnIndex(h.TRANSACTION_COLUMN_CATEGORY_FK);
        int indxId = c.getColumnIndex(h.TRANSACTION_COLUMN_ID);
        int indxDate = c.getColumnIndex(h.TRANSACTION_COLUMN_DATE);
        int indxNote = c.getColumnIndex(h.TRANSACTION_COLUMN_NOTE);
        int indxSum = c.getColumnIndex(h.TRANSACTION_COLUMN_SUM);

        Account acc = null;
        Category cat = null;

        while (c.moveToNext()) {

            long accFk = c.getLong(indxAccId);
            if ((acc = cache.getAccount(accFk)) == null) continue;

            long catFk = c.getLong(indxCatId);
            if ((cat = cache.getCategory(catFk)) == null) continue;

            double sum = c.getDouble(indxSum);
            String note = c.getString(indxNote);

            long timestamp = c.getLong(indxDate);
            DateTime date = new DateTime(timestamp, DateTimeZone.UTC);

            Transaction t = new Transaction(date, sum, note, acc, cat);
            t.setId(c.getLong(indxId));

            cache.addTransaction(t);
        }
    }

    /**
     * Singleton {@link SQLiteOpenHelper} implementation class.
     */
    private static class DbHelper extends SQLiteOpenHelper {

        //DataBase version const:
        private static final int DB_VERSION = 3;

        //DateBase name const
        private static final String DB_NAME = "finansizmus.db";

        //Table name consts
        private static final String TABLE_USER = "user";
        private static final String TABLE_ACCOUNT = "account";
        private static final String TABLE_CATEGORY = "category";
        private static final String TABLE_TRANSACTION = "transaction_table"; //3 hours...... 3 FUCKING hours of debugging and repetitive head injuries, because 'transaction' is not a valid SQL table name. fml  (ノಠ益ಠ)ノ彡┻━┻
        private static final String[] TABLES = {TABLE_USER, TABLE_ACCOUNT, TABLE_CATEGORY, TABLE_TRANSACTION};
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
        private static final String TRANSACTION_COLUMN_SUM = "sum";

        //CREATE TABLE statements
        private static final String CREATE_USER = "CREATE TABLE " + TABLE_USER +
                " ( " +
                USER_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USER_COLUMN_MAIL + " VARCHAR(80) UNIQUE NOT NULL, " +
                USER_COLUMN_NAME + " VARCHAR(80) NOT NULL, " +
                USER_COLUMN_PASSWORD + " VARCHAR(80) NOT NULL" +
                ");";
        private static final String CREATE_ACCOUNT = "CREATE TABLE " + TABLE_ACCOUNT +
                " ( " +
                ACCOUNT_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ACCOUNT_COLUMN_USER_FK + " INTEGER, " +
                ACCOUNT_COLUMN_ICON_ID + " INTEGER, " +
                ACCOUNT_COLUMN_NAME + " VARCHAR(40), " +
                "FOREIGN KEY (" + ACCOUNT_COLUMN_USER_FK + ") REFERENCES " + TABLE_USER + "(" + USER_COLUMN_ID + ")" +
                ");";
        private static final String CREATE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY +
                " ( " +
                CATEGORY_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CATEGORY_COLUMN_USER_FK + " INTEGER, " +
                CATEGORY_COLUMN_ICON_ID + " INTEGER, " +
                CATEGORY_COLUMN_NAME + " VARCHAR(40), " +
                CATEGORY_COLUMN_IS_EXPENSE + " INTEGER, " +
                CATEGORY_COLUMN_IS_FAVOURITE + " INTEGER, " +
                "FOREIGN KEY (" + CATEGORY_COLUMN_USER_FK + ") REFERENCES " + TABLE_USER + "(" + USER_COLUMN_ID + ")" +
                ");";
        private static final String CREATE_TRANSACTION = "CREATE TABLE " + TABLE_TRANSACTION +
                " ( " +
                TRANSACTION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TRANSACTION_COLUMN_DATE + " INTEGER, " +
                TRANSACTION_COLUMN_NOTE + " VARCHAR(255), " +
                TRANSACTION_COLUMN_SUM + " REAL, " +
                TRANSACTION_COLUMN_USER_FK + " INTEGER, " +
                TRANSACTION_COLUMN_ACCOUNT_FK + " INTEGER, " +
                TRANSACTION_COLUMN_CATEGORY_FK + " INTEGER, " +
                "FOREIGN KEY (" + TRANSACTION_COLUMN_USER_FK + ") REFERENCES " + TABLE_USER + "(" + USER_COLUMN_ID + "), " +
                "FOREIGN KEY (" + TRANSACTION_COLUMN_ACCOUNT_FK + ") REFERENCES " + TABLE_ACCOUNT + "(" + ACCOUNT_COLUMN_ID + "), " +
                "FOREIGN KEY (" + TRANSACTION_COLUMN_CATEGORY_FK + ") REFERENCES " + TABLE_CATEGORY + "(" + CATEGORY_COLUMN_ID + ") " +
                ");";
        //TODO /\ foreign keys to different tables in SQLite, is it doable and if it is - how?

        private static final String[] CREATE_STATEMENTS = {CREATE_USER, CREATE_ACCOUNT, CREATE_CATEGORY, CREATE_TRANSACTION};


        private static DbHelper instance;
        private Context context;

        private DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            this.context = context;
        }

        /**
         * Singleton instance getter.
         *
         * @param context Context instance required for SQLiteOpenHelper constructor.
         * @return Singleton instance of the DbHelper.
         */
        private static DbHelper getInstance(@NonNull Context context) {
            if (context == null)
                throw new IllegalArgumentException("Context MUST ne non-null!!!");
            if (instance == null)
                instance = new DbHelper(context);
            return instance;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {

                for (String s : CREATE_STATEMENTS) {
                    db.execSQL(s+"S");
                    Log.e("CREATED ",s);
                }

                db.execSQL("CREATE INDEX user_mail_index ON " + TABLE_USER + " (" + USER_COLUMN_MAIL + ")");
                db.execSQL("CREATE INDEX acc_user_index ON " + TABLE_ACCOUNT + " (" + ACCOUNT_COLUMN_USER_FK + ")");
                db.execSQL("CREATE INDEX cat_user_index ON " + TABLE_CATEGORY + " (" + CATEGORY_COLUMN_USER_FK + ")");
                db.execSQL("CREATE INDEX trans_user_index ON " + TABLE_TRANSACTION + " (" + TRANSACTION_COLUMN_USER_FK + ")");


            } catch (SQLiteException e) {
                Log.e("ERROR: ", e.toString());
                //In case of unsuccessful table creation, clear any created tables, display appropriate message and restart the app.
                //Fingers crossed, that this Toast never sees the light of day =]
                //TODO - THIS TOAST HAS SPAT EXCEPTIONS BEFORE. TEST THE HELL OUT OF IT.
                // yes, I realize the irony in the error-message throwing an error, but whatyagonnado. The joys of a learning experience I guess =]
                Util.toastLong(context, context.getString(R.string.sql_exception_message));
                for (String s : TABLES)
                    db.execSQL("DROP TABLE IF EXISTS "+ s);

                Intent intent = new Intent(context, LoginActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmManager amgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                amgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 500, pendingIntent);

                System.exit(0);
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //TODO keep data from old table.
            for (String s : TABLES)
                db.execSQL("DROP TABLE IF EXISTS " + s + " ;");
            onCreate(db);
        }
    }
}

