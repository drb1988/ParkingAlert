package bigcityapps.com.parkingalert;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import Util.SecurePreferences;

/**
 * Created by fasu on 5/27/2016.
 */

//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "meniuu";
    SharedPreferences prefs;


    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        prefs = new SecurePreferences(this);
        prefs.edit().putString("phone_token", String.valueOf(refreshedToken)).commit();
        Log.w("meniuu","tokenul din pref"+prefs.getString("phone_token","nu este"));
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
        //cDPmN1BC4Gc:APA91bE6F0AHNr38L2175NWpKXAT60ggTOpN3GE77s3cZP3X7kQCD4TEN-tWFF62LZr9VKzHeSNgnWdl_wnLmtZqjfVX8BhWOjdi2aa32sp0PCtnxBsvcPrfO_KqwZwp_U_URt1DWMgn

    }
}