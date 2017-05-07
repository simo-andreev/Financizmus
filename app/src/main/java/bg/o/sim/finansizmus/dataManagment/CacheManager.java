package bg.o.sim.finansizmus.dataManagment;

import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import bg.o.sim.finansizmus.R;
import bg.o.sim.finansizmus.model.Account;
import bg.o.sim.finansizmus.model.Category;
import bg.o.sim.finansizmus.model.Transaction;
import bg.o.sim.finansizmus.model.User;

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
    private ArrayList<Integer> expenseIcons;
    private ArrayList<Integer> accountIcons;

    private CacheManager() {
        this.accounts = new ConcurrentHashMap<>();
        this.incomeCategories = new ConcurrentHashMap<>();
        this.expenseCategories = new ConcurrentHashMap<>();
        this.accountTransactions = new ConcurrentHashMap<>();
        this.categoryTransactions = new ConcurrentHashMap<>();

        this.accountIcons = new ArrayList<>();
        this.expenseIcons = new ArrayList<>();

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
        this.loggedUser = loggedUser;
    }

    protected boolean addCategory(Category category) {
        if (category == null){
            Log.e("CACHE ADD CATEGORY: ", "CATEGORY == NULL");
            return false;
        }

        if (category.getType() == Category.Type.INCOME && !incomeCategories.containsKey(category.getId())) {
            incomeCategories.put(category.getId(), category);
            Log.e("CACHED CAT:", category.getName() + " IN INCOME");
            return true;
        }
        if (category.getType() == Category.Type.EXPENSE && !expenseCategories.containsKey(category.getId())) {
            expenseCategories.put(category.getId(), category);
            Log.e("CACHED CAT:", category.getName() + " IN EXPENSE");
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


    public ArrayList<Integer> getExpenseIcons() {
        return expenseIcons;
    }

    public ArrayList<Integer> getAccountIcons() {
        return accountIcons;
    }


    private void loadIcons() {
        expenseIcons.add(R.mipmap.car);
        expenseIcons.add(R.mipmap.clothes);
        expenseIcons.add(R.mipmap.heart);
        expenseIcons.add(R.mipmap.plane);
        expenseIcons.add(R.mipmap.home);
        expenseIcons.add(R.mipmap.swimming);
        expenseIcons.add(R.mipmap.restaurant);
        expenseIcons.add(R.mipmap.train);
        expenseIcons.add(R.mipmap.cocktail);
        expenseIcons.add(R.mipmap.phone);
        expenseIcons.add(R.mipmap.books);
        expenseIcons.add(R.mipmap.cafe);
        expenseIcons.add(R.mipmap.cats);
        expenseIcons.add(R.mipmap.household);
        expenseIcons.add(R.mipmap.food_and_wine);
        expenseIcons.add(R.mipmap.wifi);
        expenseIcons.add(R.mipmap.flowers);
        expenseIcons.add(R.mipmap.gas);
        expenseIcons.add(R.mipmap.cleaning);
        expenseIcons.add(R.mipmap.gifts);
        expenseIcons.add(R.mipmap.kids);
        expenseIcons.add(R.mipmap.makeup);
        expenseIcons.add(R.mipmap.music);
        expenseIcons.add(R.mipmap.gamming);
        expenseIcons.add(R.mipmap.hair);
        expenseIcons.add(R.mipmap.car_service);
        expenseIcons.add(R.mipmap.doctor);
        expenseIcons.add(R.mipmap.art);
        expenseIcons.add(R.mipmap.beach);
        expenseIcons.add(R.mipmap.bicycle);
        expenseIcons.add(R.mipmap.bowling);
        expenseIcons.add(R.mipmap.football);
        expenseIcons.add(R.mipmap.bus);
        expenseIcons.add(R.mipmap.taxi);
        expenseIcons.add(R.mipmap.games);
        expenseIcons.add(R.mipmap.fitness);
        expenseIcons.add(R.mipmap.shoes);
        expenseIcons.add(R.mipmap.dancing);
        expenseIcons.add(R.mipmap.shopping_bag);
        expenseIcons.add(R.mipmap.shopping_cart);
        expenseIcons.add(R.mipmap.tennis);
        expenseIcons.add(R.mipmap.tent);
        expenseIcons.add(R.mipmap.hotel);
        expenseIcons.add(R.mipmap.ping_pong);
        expenseIcons.add(R.mipmap.rollerblade);

        accountIcons.add(R.mipmap.money_box);
        accountIcons.add(R.mipmap.gift_card);
        accountIcons.add(R.drawable.accounts);
        accountIcons.add(R.mipmap.funding);
        accountIcons.add(R.mipmap.mattress);
        accountIcons.add(R.mipmap.paypal);
        accountIcons.add(R.drawable.calculator);
        accountIcons.add(R.mipmap.safe);
        accountIcons.add(R.mipmap.coins);
        accountIcons.add(R.drawable.income);
    }
}
