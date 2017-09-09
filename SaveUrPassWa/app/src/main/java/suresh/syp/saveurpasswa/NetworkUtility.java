package suresh.syp.saveurpasswa;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by ${Sureshsharma} on 8/12/2016.
 */
public class NetworkUtility {
    /**
     * To check while device is connected to internet
     *
     * @param context set a current context available to check internet status.
     * @return return true - Connected, false - Not Connected
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        boolean isNetworkAvailable = activeNetworkInfo != null;
        return isNetworkAvailable;
    }
}
