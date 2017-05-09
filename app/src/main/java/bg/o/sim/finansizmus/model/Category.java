package bg.o.sim.finansizmus.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Category implements RowDisplayable, Serializable {
    public enum Type {INCOME, EXPENSE}

    private final long id;
    private final long userFk;

    private String name;
    private final Type type;
    private int iconId;

    protected Category(@NonNull String name, int iconId, long id, long userFk, Type type) {
        //TODO - VALIDATION!
        this.name = name;
        this.type = type;
        this.iconId = iconId;

        this.id = id;
        this.userFk = userFk;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        return name.equals(category.name);
    }
    @Override
    public int hashCode() {return name.hashCode();}
    @Override
    public long getId() {return id;}
    @Override
    public String getName() {return name;}
    @Override
    public int getIconId() {return iconId;}
    @Override
    public void setName(String newName) {
        //TODO
    }
    @Override
    public void setIconId(int newIconId) {
        //TODO
    }
    public Type getType() {
        return type;
    }

    public double getSum() {
        double sum = 0;
        for(Transaction t : CacheManager.getInstance().getCategoryTransactions(this))
            sum += t.getSum();
        return sum;
    }
}
