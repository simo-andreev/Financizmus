package bg.o.sim.finansizmus.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Abstract Class that holds a number of utility static methods.
 */
public abstract class Util {

    //Tag for Logging purposes
    private static final String TAG = "UTIL";


    /**Display a long-lasting {@link Toast}*/
    public static void toastLong(Context context, String text){
        toast(context, text, Toast.LENGTH_LONG);
    }
    /**Display a short-lasting {@link Toast}*/
    public static void toastShort(Context context, String text){
        toast(context, text, Toast.LENGTH_SHORT);
    }

    private static void toast(Context c, String t, int l){
        if (c == null)
            Log.e(TAG, "Attempted to make Toast with NULL value!");
        if (t == null)
            t = "";
        Toast.makeText(c, t, l).show();
    }

}
