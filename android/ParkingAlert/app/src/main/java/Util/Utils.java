package Util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Sistem1 on 05/10/2016.
 */
public class Utils {
    public static boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
