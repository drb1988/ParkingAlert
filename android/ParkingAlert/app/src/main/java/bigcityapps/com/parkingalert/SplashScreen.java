package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import Util.SecurePreferences;

/**
 * just a splashscreen
 * Created by fasu on 23/09/2016.
 */
public class SplashScreen extends Activity {
    SharedPreferences prefs;
    private static int SPLASH_TIME_OUT = 2000;
    String nr_car, notification_id;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        prefs= new SecurePreferences(this);
        Bundle x = getIntent().getExtras();

        if(x!=null) {
            nr_car=(String)x.getString("car_id");
            notification_id=(String)x.getString("notification_id");
            Intent intent=new Intent(this, ViewNotification.class);
            intent.putExtra("nr_car",nr_car);
            intent.putExtra("notification_id",notification_id);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            Log.w("meniuu","sa deschis viewnotification");
        }else
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
