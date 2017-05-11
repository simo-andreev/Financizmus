package bg.o.sim.finansizmus.model;

import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * Meant to group Classes like Account and the Category classes that can be displayed as a single 'row'
 * and must have an implementation of getters and/or setters as listed below.
 */
public interface RowDisplayable {

    long getId();

    String getName();

    int getIconId();

    void setName(String newName);

    void setIconId(int newIconId);

    @RequiresApi(api = Build.VERSION_CODES.N)
    default protected boolean foo(){return false;}
}
