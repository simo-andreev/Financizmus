package bg.o.sim.finansizmus.model;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import bg.o.sim.finansizmus.R;

/**
 * The Cacher abstract class handles all operations related to accessing the cache collections. All of that is done in a static manner with a bunch of "volatile" thrown in for flavour.
 */
public abstract class Cacher {

    private static volatile User loggedUser;

    //I' not sure how good of an idea using a stored total is, but might as well try//
    private static volatile double sum;

    private static volatile ConcurrentHashMap<Long, Account> accounts;
    private static volatile  ConcurrentHashMap<Long, Category> incomeCategories;
    private static volatile  ConcurrentHashMap<Long, Category> expenseCategories;

    //Using 2 collections might be redundant 'double' storage of data,
    //but is should make it easier to fetch data for a single Category OR a single Account
    private static volatile ConcurrentHashMap<Long, ArrayList<Transaction>> accountTransactions;  //<AccountId - Transactions>
    private static volatile  ConcurrentHashMap<Long, ArrayList<Transaction>> categoryTransactions; //<CategoryId - Transactions>


    //TODO - if I ever get to the point of optimizing this thing: int[]s should provide better performance than Integer ArrLists.
    /* A collection that maintains a list of all Sections (both Income and Expense) and distributes input accordingly. */
    private static volatile int[] expenseIcons;
    private static volatile int[] accountIcons;

    static{
        if (accounts == null) accounts = new ConcurrentHashMap<>();
        if (incomeCategories == null) incomeCategories = new ConcurrentHashMap<>();
        if (expenseCategories == null) expenseCategories = new ConcurrentHashMap<>();
        if (accountTransactions == null) accountTransactions = new ConcurrentHashMap<>();
        if (categoryTransactions == null) categoryTransactions = new ConcurrentHashMap<>();

        loadIcons();

        sum = 0.0;
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    protected static User setLoggedUser(@NonNull User loggedUser) {
        if (loggedUser == null)
            throw new IllegalArgumentException("The loggedUser value MUST be NON NULL!");
        return Cacher.loggedUser = loggedUser;
    }

    protected static boolean addCategory(Category category) {
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

    protected static boolean addAccount(Account acc) {
        if (acc == null || accounts.containsKey(acc.getId()))
            return false;
        accounts.put(acc.getId(), acc);
        return true;
    }

    public static boolean addTransaction(Transaction t) {
        if (t == null || t.getAccount() == null || t.getCategory() == null) return false;

        long accId = t.getAccount().getId();
        long catId = t.getCategory().getId();

        if (!accountTransactions.containsKey(accId) || accountTransactions.get(accId) == null)
            accountTransactions.put(accId, new ArrayList<>());
        if (!categoryTransactions.containsKey(catId) || categoryTransactions.get(catId) == null)
            categoryTransactions.put(catId, new ArrayList<>());

        accountTransactions.get(accId).add(t);
        categoryTransactions.get(catId).add(t);

        //Increments the cached total sum. If the transaction is an expense -> decrements.
        sum += t.getSum() * (t.getCategory().getType() == Category.Type.EXPENSE ? -1 : 1);

        //TODO - as-is this returns true without actually verifying that the Transaction was successfully added.
        Log.i("CACHED TRANS: ", t.getSum() + "$ " + "IN: " + t.getAccount().getName() + " FROM: " + t.getCategory().getName());
        return true;
    }

    //TODO - docs
    public static boolean removeAccount(Account account) {
        return accounts.remove(account.getId()) != null;
    }

    public static Category getCategory(long catFk) {
        if (expenseCategories.containsKey(catFk))return expenseCategories.get(catFk);
        if (incomeCategories.containsKey(catFk))return incomeCategories.get(catFk);
        return null;
    }

    public static Account getAccount(long accFk) {
        return accounts.get(accFk);
    }



    /**
     * Empty all cache collections.
     */
    public static void clearCache() {
        accounts = new ConcurrentHashMap<>();
        incomeCategories = new ConcurrentHashMap<>();
        expenseCategories = new ConcurrentHashMap<>();
        accountTransactions = new ConcurrentHashMap<>();
        categoryTransactions = new ConcurrentHashMap<>();
    }

    public static ArrayList<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public static  ArrayList<Category> getAllExpenseCategories() {
        return new ArrayList<>(expenseCategories.values());
    }

    public static  ArrayList<Category> getAllIncomeCategories() {
        return new ArrayList<>(incomeCategories.values());
    }

    public static  ArrayList<Transaction> getAccountTransactions(Account account) {
        accountTransactions.putIfAbsent(account.getId(), new ArrayList<>());
        return accountTransactions.get(account.getId());
    }

    public static  ArrayList<Transaction> getCategoryTransactions(Category category) {
        categoryTransactions.putIfAbsent(category.getId(), new ArrayList<>());
        return new ArrayList<>(categoryTransactions.get(category.getId()));
    }


    public static  double getCurrentTotal() {
        return sum;
    }


    public static  int[] getExpenseIcons() {
        return expenseIcons;
    }

    public static  int[] getAccountIcons() {
        return accountIcons;
    }


    private static void loadIcons() {
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
