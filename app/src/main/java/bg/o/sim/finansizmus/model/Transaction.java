package bg.o.sim.finansizmus.model;

import android.support.annotation.Nullable;

import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Class representing a single entry in an account or an expense category.
 */
public class Transaction implements Serializable {

    public interface TransactionComparator extends Comparator<Transaction> {}
    private static ArrayList<TransactionComparator> sorters;

    private final long id;
    private final long userFk;

    private final Category category;
    private final Account account;

    private final DateTime date;
    private String note;
    private final double sum;


    protected Transaction(long id, long userFk, Category cat, Account acc, DateTime date, String note, double sum) {
        //TODO - !VALIDATION!

        this.id = id;
        this.userFk = userFk;
        this.category = cat;
        this.account = acc;
        this.date = date;
        this.note = note;
        this.sum = sum;
    }

    public static ArrayList<TransactionComparator> getComparators() {
        if (sorters == null || sorters.isEmpty()) {
            sorters = new ArrayList<>();
            sorters.add(new TransactionSumComparator());
            sorters.add(new TransactionCategoryComparator());
            sorters.add(new TransactionDateComparator());
        }
        return sorters;
    }

    public long getId() {
        return id;
    }
    public long getUserFk() {
        return userFk;
    }
    public Account getAccount() {
        return account;
    }
    public DateTime getDate() {
        return date;
    }
    public String getNote() {
        return note;
    }
    public double getSum() {
        return sum;
    }

    public
    @Nullable
    Category getCategory() {
        return category;
    }

    /**
     * A chronological comparator for the {@link Transaction} data type.
     * It sorts by the LogEntry's date in ascending order
     * and returns -1 if the LocalDate property of the 2 dates is identical within a millisecond resolution.
     */
    private static class TransactionDateComparator implements TransactionComparator {

        @Override
        public int compare(Transaction o1, Transaction o2) {
            if (o1.date.equals(o2.date))
                return Double.compare(o2.sum, o1.sum);

            return o2.date.compareTo(o1.date);
        }

        @Override
        public String toString() {
            return "SORT BY DATE";
        }
    }

    public static class TransactionSumComparator implements TransactionComparator {

        @Override
        public int compare(Transaction o1, Transaction o2) {
            if (o1.sum == o2.sum) return o2.date.compareTo(o1.date);
            return Double.compare(o2.sum, o1.sum);
        }

        @Override
        public String toString() {
            return "SORT BY SUM";
        }
    }

    private static class TransactionCategoryComparator implements TransactionComparator {

        @Override
        public int compare(Transaction o1, Transaction o2) {
            if (o1.category.equals(o2.category)) {
                if (o1.date.equals(o2.date))
                    return Double.compare(o2.sum, o1.sum);
                return o2.date.compareTo(o1.date);
            }

            return o2.category.getName().compareTo(o1.category.getName());
        }

        @Override
        public String toString() {
            return "SORT BY CATEGORY";
        }
    }
}


