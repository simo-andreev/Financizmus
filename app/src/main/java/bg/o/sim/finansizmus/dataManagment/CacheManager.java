package bg.o.sim.finansizmus.dataManagment;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import bg.o.sim.finansizmus.model.Account;
import bg.o.sim.finansizmus.model.Category;
import bg.o.sim.finansizmus.model.Transaction;
import bg.o.sim.finansizmus.model.User;

/**
 * The CacheManager Singleton class handles all operations related to accessing the cache collections.
 */
public class CacheManager {

    private static CacheManager instance;

    private User loggedUser;

    private ConcurrentHashMap<Long, Account> accounts;
    private ConcurrentHashMap<Long, Category> incomeCategories;
    private ConcurrentHashMap<Long, Category> expenseCategories;

    //Using 2 collections might be redundant 'double' storage of data,
    //but is should make it easier to fetch data for a single Category OR a single Account
    private ConcurrentHashMap<Long, ArrayList<Transaction>> accountTransactions;  //<AccountId - Transactions>
    private ConcurrentHashMap<Long, ArrayList<Transaction>> categoryTransactions; //<CategoryId - Transactions>

    private CacheManager() {
        this.accounts = new ConcurrentHashMap<>();
        this.incomeCategories = new ConcurrentHashMap<>();
        this.expenseCategories = new ConcurrentHashMap<>();
        this.accountTransactions = new ConcurrentHashMap<>();
        this.categoryTransactions = new ConcurrentHashMap<>();
    }

    public static CacheManager getInstance() {
        if (instance == null)
            instance = new CacheManager();
        return instance;
    }

    public User getLoggedUser() {
        return loggedUser;
    }

    protected void setLoggedUser(@NonNull User loggedUser) {
        if (loggedUser == null)
            throw new IllegalArgumentException("The loggedUser value MUST be NON NULL!");
        this.loggedUser = loggedUser;
    }

    protected boolean addCategory(Category category) {
        if (category == null) return false;

        if (category.getType() == Category.Type.INCOME && !incomeCategories.containsKey(category.getId())) {
            incomeCategories.put(category.getId(), category);
            return true;
        }
        if (category.getType() == Category.Type.EXPENSE && !expenseCategories.containsKey(category.getId())) {
            expenseCategories.put(category.getId(), category);
            return true;
        }

        return false;
    }

    protected boolean addAccount(Account acc) {
        if (acc == null || accounts.containsKey(acc.getId()))
            return false;
        accounts.put(acc.getId(), acc);
        return true;
    }

    public Category getCategory(long catFk) {
        if (incomeCategories.containsKey(catFk))
            return incomeCategories.get(catFk);
        return expenseCategories.get(catFk);
    }

    public Account getAccount(long accFk) {
        return accounts.get(accFk);
    }

    public boolean addTransaction(Transaction t) {
        if (t == null) return false;

        accountTransactions.putIfAbsent(t.getAccount().getId(), new ArrayList<Transaction>()).add(t);
        categoryTransactions.putIfAbsent(t.getAccount().getId(), new ArrayList<Transaction>()).add(t);

        //TODO - as-is this returns true without actually verifying that the Transaction was successfully added.
        return true;
    }

    /**Empty all cache collections.*/
    public void clearCache(){
        this.accounts = new ConcurrentHashMap<>();
        this.incomeCategories = new ConcurrentHashMap<>();
        this.expenseCategories = new ConcurrentHashMap<>();
        this.accountTransactions = new ConcurrentHashMap<>();
        this.categoryTransactions = new ConcurrentHashMap<>();
    }
}
