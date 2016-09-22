package Util;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Belal on 5/27/2016.
 */


//Class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "meniuu";
    SharedPreferences prefs;


    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        prefs = new SecurePreferences(this);
//        prefs.edit().putString("phone_token", String.valueOf(refreshedToken)).commit();
//        Log.w("meniuu","tokenul din pref"+prefs.getString("phone_token","nu este"));
        Log.d(TAG, "Refreshed token: " + refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
        //cL26MKHSOM4:APA91bHqtXhwodbDrOoxRHgTv_5kQfWXJRHFHtxQ_ruoi4Ht_7VLjakigF4j4kuNtBsThwJCWcRB6xETf8ig0aNgrK_05tJNH-0D8PxcptOTZqj9_DlLGRvFPGQDZ7aEx-8VSbq4rm1B
    }
}