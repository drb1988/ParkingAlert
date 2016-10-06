package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import Util.SecurePreferences;

/**
 * just a splashscreen
 * Created by fasu on 23/09/2016.
 */
public class SplashScreen extends Activity {
    SharedPreferences prefs;
    private static int SPLASH_TIME_OUT = 2000;
    Context ctx;
    String nr_car, notification_id,notification_type,estimated_time,answered_at,mPlates;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);
        prefs= new SecurePreferences(this);
        Bundle x = getIntent().getExtras();
        ctx=this;

        if(x!=null) {
            try{
            notification_type = (String) x.getString("notification_type");
            nr_car = (String) x.getString("car_id");
            notification_id = (String) x.getString("notification_id");
            estimated_time = (String) x.getString("estimated_time");
            answered_at = (String) x.getString("answered_at");
            mPlates = (String) x.getString("mPlates");

            if (notification_type.equals("sender")) {
                Intent intent = new Intent(this, ViewNotification.class);
                intent.putExtra("mPlates", nr_car);
                intent.putExtra("notification_id", notification_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            } else if (notification_type.equals("receiver")) {
                Intent timer = new Intent(SplashScreen.this, Timer.class);
                timer.putExtra("time", estimated_time);
                timer.putExtra("mHour", answered_at);
                timer.putExtra("mPlates", mPlates);
                startActivity(timer);
                finish();
            } else if (notification_type.equals("review")) {
                Intent timer = new Intent(SplashScreen.this, Review.class);
                timer.putExtra("mHour", answered_at);
                timer.putExtra("mPlates", mPlates);
                startActivity(timer);
                finish();
                Toast.makeText(ctx, "revieew", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
                e.printStackTrace();
                Log.w("meniuu","catch la notificare");
            }

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
