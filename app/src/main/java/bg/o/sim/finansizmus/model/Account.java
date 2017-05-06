package bg.o.sim.finansizmus.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Account implements Serializable, RowDisplayable {

    private long id;
    private long userFk;

    private String name;
    private int iconId;

    public Account(@NonNull String name, int iconId, long id, long userFk) {
        //TODO - revisit validation
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("The Section name must be a non-null and not-empty string!");
        if (iconId == -1)
            throw new IllegalArgumentException();

        this.name = name;
        this.iconId = iconId;

        this.id = id;
        this.userFk = userFk;
    }

    @Override
    public long getId() {
        return id;
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public int getIconId() {
        return iconId;
    }
    @Override
    public void setName(String newName) {
        if (name == null || name.isEmpty()) return;
        this.name = newName;
    }

    @Override
    public void setIconId(int newIconId) {
        //TODO - how to validate this is an existing mipmap's id?
        if (newIconId > 0) this.iconId = newIconId;
    }
}
