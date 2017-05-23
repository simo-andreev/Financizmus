package bg.o.sim.finansizmus.model;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import bg.o.sim.finansizmus.utils.Util;

public class LoaderService extends Service {

    private DAO.DbHelper h;
    private long userId;
    private boolean loadedCategories;
    private boolean loadedAccounts;
    private boolean loadedTransactions;

    public LoaderService() {
        h = DAO.DbHelper.getInstance(this);
        userId = Cacher.getLoggedUser().getId();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        loadAccounts();
        loadCategories();
        return super.onStartCommand(intent, flags, startId);
    }

    private void notifyTableLoaded(@TableName String table) {
        switch (table) {
            case DAO.DbHelper.TABLE_ACCOUNT:
                loadedAccounts = true;
                break;

            case DAO.DbHelper.TABLE_CATEGORY:
                loadedCategories = true;
                break;

            case DAO.DbHelper.TABLE_TRANSACTION:
                loadedTransactions = true;
                break;
        }

        if (loadedTransactions) {
            sendBroadcast(new Intent(Util.BROADCAST_LOADING_FINISH));
            stopSelf();
            return;
        }

        if (loadedAccounts && loadedAccounts) {
            loadTransactions();
        }
    }

    private void loadTransactions() {
        new TransactionLoaderTask().execute();
    }

    private void loadAccounts() {
        new AccountsLoaderTask().execute();
    }

    private void loadCategories() {
        new CategoryLoaderTask().execute();
    }

    private abstract class LoaderTask extends AsyncTask<Void, Void, Void> {
        private @TableName
        String table;

        LoaderTask(@TableName String table) {
            //TODO - validate
            this.table = table;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            notifyTableLoaded(table);
        }
    }

    private class AccountsLoaderTask extends LoaderTask {

        AccountsLoaderTask() {
            super(DAO.DbHelper.TABLE_ACCOUNT);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String[] columns = {
                    DAO.DbHelper.ACCOUNT_COLUMN_ID,
                    DAO.DbHelper.ACCOUNT_COLUMN_ICON_ID,
                    DAO.DbHelper.ACCOUNT_COLUMN_NAME
            };

            String selection = DAO.DbHelper.ACCOUNT_COLUMN_USER_FK + " = " + userId;

            Cursor c = h.getReadableDatabase().query(DAO.DbHelper.TABLE_ACCOUNT, columns, selection, null, null, null, null);

            int indxId = c.getColumnIndex(DAO.DbHelper.ACCOUNT_COLUMN_ID);
            int indxIcon = c.getColumnIndex(DAO.DbHelper.ACCOUNT_COLUMN_ICON_ID);
            int indxName = c.getColumnIndex(DAO.DbHelper.ACCOUNT_COLUMN_NAME);

            while (c.moveToNext()) {
                long id = c.getLong(indxId);
                int icon = c.getInt(indxIcon);
                String name = c.getString(indxName);

                Account acc = new Account(name, icon, id, userId);
                Cacher.addAccount(acc);
            }

            c.close();
            return null;
        }
    }

    private class CategoryLoaderTask extends LoaderTask {

        CategoryLoaderTask() {
            super(DAO.DbHelper.TABLE_CATEGORY);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String[] columns = {
                    DAO.DbHelper.CATEGORY_COLUMN_ID,
                    DAO.DbHelper.CATEGORY_COLUMN_ICON_ID,
                    DAO.DbHelper.CATEGORY_COLUMN_NAME,
                    DAO.DbHelper.CATEGORY_COLUMN_IS_EXPENSE
            };

            String selection = DAO.DbHelper.CATEGORY_COLUMN_USER_FK + " = " + userId;

            Cursor c = h.getReadableDatabase().query(DAO.DbHelper.TABLE_CATEGORY, columns, selection, null, null, null, null);

            int indxId = c.getColumnIndex(DAO.DbHelper.CATEGORY_COLUMN_ID);
            int indxIcon = c.getColumnIndex(DAO.DbHelper.CATEGORY_COLUMN_ICON_ID);
            int indxName = c.getColumnIndex(DAO.DbHelper.CATEGORY_COLUMN_NAME);
            int indxIsExp = c.getColumnIndex(DAO.DbHelper.CATEGORY_COLUMN_IS_EXPENSE);

            while (c.moveToNext()) {

                long id = c.getLong(indxId);
                int icon = c.getInt(indxIcon);
                String name = c.getString(indxName);
                Category.Type type = c.getInt(indxIsExp) == 1 ? Category.Type.EXPENSE : Category.Type.INCOME;

                Category cat = new Category(name, icon, id, userId, type);

                Cacher.addCategory(cat);
            }
            c.close();
            return null;
        }
    }

    private class TransactionLoaderTask extends LoaderTask {

        TransactionLoaderTask() {
            super(DAO.DbHelper.TABLE_TRANSACTION);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            String[] columns = {
                    DAO.DbHelper.TRANSACTION_COLUMN_ACCOUNT_FK,
                    DAO.DbHelper.TRANSACTION_COLUMN_CATEGORY_FK,
                    DAO.DbHelper.TRANSACTION_COLUMN_ID,
                    DAO.DbHelper.TRANSACTION_COLUMN_DATE,
                    DAO.DbHelper.TRANSACTION_COLUMN_NOTE,
                    DAO.DbHelper.TRANSACTION_COLUMN_SUM
            };
            String selection = DAO.DbHelper.TRANSACTION_COLUMN_USER_FK + " = " + userId;

            Cursor c = h.getReadableDatabase().query(DAO.DbHelper.TABLE_TRANSACTION, columns, selection, null, null, null, null);

            int indxAccId = c.getColumnIndex(DAO.DbHelper.TRANSACTION_COLUMN_ACCOUNT_FK);
            int indxCatId = c.getColumnIndex(DAO.DbHelper.TRANSACTION_COLUMN_CATEGORY_FK);
            int indxId = c.getColumnIndex(DAO.DbHelper.TRANSACTION_COLUMN_ID);
            int indxDate = c.getColumnIndex(DAO.DbHelper.TRANSACTION_COLUMN_DATE);
            int indxNote = c.getColumnIndex(DAO.DbHelper.TRANSACTION_COLUMN_NOTE);
            int indxSum = c.getColumnIndex(DAO.DbHelper.TRANSACTION_COLUMN_SUM);

            Account acc;
            Category cat;

            Log.e("LOADER:  ", "TRANSACTIONS: COUNT: " + c.getCount());

            while (c.moveToNext()) {
                Log.e("LOADER:  ", "TRANSACTIONS: COUNT: " + c.getPosition());

                long accFk = c.getLong(indxAccId);
                if ((acc = Cacher.getAccount(accFk)) == null) continue;

                long catFk = c.getLong(indxCatId);
                if ((cat = Cacher.getCategory(catFk)) == null) continue;

                long id = c.getLong(indxId);

                long timestamp = c.getLong(indxDate);
                DateTime date = new DateTime(timestamp, DateTimeZone.UTC);

                String note = c.getString(indxNote);

                double sum = c.getDouble(indxSum);

                Cacher.addTransaction(new Transaction(id, userId, cat, acc, date, note, sum));
            }

            c.close();
            return null;
        }
    }
}
