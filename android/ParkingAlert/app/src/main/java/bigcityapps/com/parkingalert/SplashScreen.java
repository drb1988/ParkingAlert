package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import Util.SecurePreferences;

/**
 * just a splashscreen
 * Created by fasu on 23/09/2016.
 */
public class SplashScreen extends Activity {
    SharedPreferences prefs;
    private static int SPLASH_TIME_OUT = 2000;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        prefs= new SecurePreferences(this);
        new Handler().postDelayed(new Runnable() {
            public void run() {

                if(prefs.getString("token", null)==null)
                {   Intent i = new Intent(SplashScreen.this, Login.class);
                    startActivity(i);
                    finish();
                }
                else
                {   Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);
    }
}
