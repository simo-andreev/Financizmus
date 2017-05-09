package bg.o.sim.finansizmus.model;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import bg.o.sim.finansizmus.R;

/**
 * The CacheManager Singleton class handles all operations related to accessing the cache collections.
 */
public class CacheManager {

    private static CacheManager instance;

    private static User loggedUser;

    private ConcurrentHashMap<Long, Account> accounts;
    private ConcurrentHashMap<Long, Category> incomeCategories;
    private ConcurrentHashMap<Long, Category> expenseCategories;

    //Using 2 collections might be redundant 'double' storage of data,
    //but is should make it easier to fetch data for a single Category OR a single Account
    private ConcurrentHashMap<Long, ArrayList<Transaction>> accountTransactions;  //<AccountId - Transactions>
    private ConcurrentHashMap<Long, ArrayList<Transaction>> categoryTransactions; //<CategoryId - Transactions>


    //TODO - if I ever get to the point of optimizing this thing: int[]s should provide better performance than Integer ArrLists.
    /* A collection that maintains a list of all Sections (both Income and Expense) and distributes input accordingly. */
    private int[] expenseIcons;
    private int[] accountIcons;

    private CacheManager() {
        this.accounts = new ConcurrentHashMap<>();
        this.incomeCategories = new ConcurrentHashMap<>();
        this.expenseCategories = new ConcurrentHashMap<>();
        this.accountTransactions = new ConcurrentHashMap<>();
        this.categoryTransactions = new ConcurrentHashMap<>();

        loadIcons();
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
        CacheManager.loggedUser = loggedUser;
    }

    protected boolean addCategory(Category category) {
        if (category == null){
            Log.e("CACHE ADD CATEGORY: ", "CATEGORY == NULL");
            return false;
        }

        if (category.getType() == Category.Type.INCOME && !incomeCategories.containsKey(category.getId())) {
            incomeCategories.put(category.getId(), category);
            Log.i("CACHED CAT:", category.getName() + " IN INCOME");
            return true;
        }
        if (category.getType() == Category.Type.EXPENSE && !expenseCategories.containsKey(category.getId())) {
            expenseCategories.put(category.getId(), category);
            Log.i("CACHED CAT:", category.getName() + " IN EXPENSE");
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

    public boolean addTransaction(Transaction t) {
        if (t == null || t.getAccount() == null || t.getCategory() == null) return false;

        long accId = t.getAccount().getId();
        long catId = t.getCategory().getId();

        if (!accountTransactions.containsKey(accId) || accountTransactions.get(accId) == null)
            accountTransactions.put(accId, new ArrayList<Transaction>());
        if (!categoryTransactions.containsKey(catId) || categoryTransactions.get(catId) == null)
            categoryTransactions.put(catId, new ArrayList<Transaction>());

        accountTransactions.get(accId).add(t);
        categoryTransactions.get(catId).add(t);

        //TODO - as-is this returns true without actually verifying that the Transaction was successfully added.
        Log.i("CACHED TRANS: ", t.getSum() + "$ " + "IN: " + t.getAccount().getName() + " FROM: " + t.getCategory().getName());
        return true;
    }

    public Category getCategory(long catFk) {
        if (expenseCategories.containsKey(catFk))return expenseCategories.get(catFk);
        if (incomeCategories.containsKey(catFk))return incomeCategories.get(catFk);
        return null;
    }

    public Account getAccount(long accFk) {
        return accounts.get(accFk);
    }



    /**
     * Empty all cache collections.
     */
    public void clearCache() {
        this.accounts = new ConcurrentHashMap<>();
        this.incomeCategories = new ConcurrentHashMap<>();
        this.expenseCategories = new ConcurrentHashMap<>();
        this.accountTransactions = new ConcurrentHashMap<>();
        this.categoryTransactions = new ConcurrentHashMap<>();
    }

    public ArrayList<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public ArrayList<Category> getAllExpenseCategories() {
        return new ArrayList<>(expenseCategories.values());
    }

    public ArrayList<Category> getAllIncomeCategories() {
        return new ArrayList<>(incomeCategories.values());
    }

    public ArrayList<Transaction> getAccountTransactions(Account account) {
        accountTransactions.putIfAbsent(account.getId(), new ArrayList<Transaction>());
        return accountTransactions.get(account.getId());
    }

    public ArrayList<Transaction> getCategoryTransactions(Category category) {
        categoryTransactions.putIfAbsent(category.getId(), new ArrayList<Transaction>());
        return new ArrayList<>(categoryTransactions.get(category.getId()));
    }


    public int[] getExpenseIcons() {
        return expenseIcons;
    }

    public int[] getAccountIcons() {
        return accountIcons;
    }


    private void loadIcons() {
        int[] tempExpense = {
                R.mipmap.car,
                R.mipmap.clothes,
                R.mipmap.heart,
                R.mipmap.plane,
                R.mipmap.home,
                R.mipmap.swimming,
                R.mipmap.restaurant,
                R.mipmap.train,
                R.mipmap.cocktail,
                R.mipmap.phone,
                R.mipmap.books,
                R.mipmap.cafe,
                R.mipmap.cats,
                R.mipmap.household,
                R.mipmap.food_and_wine,
                R.mipmap.wifi,
                R.mipmap.flowers,
                R.mipmap.gas,
                R.mipmap.cleaning,
                R.mipmap.gifts,
                R.mipmap.kids,
                R.mipmap.makeup,
                R.mipmap.music,
                R.mipmap.gamming,
                R.mipmap.hair,
                R.mipmap.car_service,
                R.mipmap.doctor,
                R.mipmap.art,
                R.mipmap.beach,
                R.mipmap.bicycle,
                R.mipmap.bowling,
                R.mipmap.football,
                R.mipmap.bus,
                R.mipmap.taxi,
                R.mipmap.games,
                R.mipmap.fitness,
                R.mipmap.shoes,
                R.mipmap.dancing,
                R.mipmap.shopping_bag,
                R.mipmap.shopping_cart,
                R.mipmap.tennis,
                R.mipmap.tent,
                R.mipmap.hotel,
                R.mipmap.ping_pong,
                R.mipmap.rollerblade
        };

        int[] tempAccount = {
                R.mipmap.money_box,
                R.mipmap.gift_card,
                R.drawable.accounts,
                R.mipmap.funding,
                R.mipmap.mattress,
                R.mipmap.paypal,
                R.drawable.calculator,
                R.mipmap.safe,
                R.mipmap.coins,
                R.drawable.income
        };

        expenseIcons = tempExpense;
        accountIcons = tempAccount;
    }
}
