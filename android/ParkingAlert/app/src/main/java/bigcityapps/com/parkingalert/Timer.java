package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 15/09/2016.
 */
public class Timer extends Activity implements View.OnClickListener {
    ImageView image;
    private ProgressBar progBar;
    private TextView text;
    private Handler mHandler = new Handler();
    private int mProgressStatus = 0;
    RelativeLayout back;
    int timer;
    String TAG = "meniuu";
    String ora, nr_carString, notification_id;
    boolean run = true;
    TextView car_nr, time_answer;
    RequestQueue queue;
    String mLat, mLng;
    SharedPreferences prefs;

    @Override
    protected void onStop() {
        run = false;
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer);
        initcComponents();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        prefs = new SecurePreferences(this);
        queue = Volley.newRequestQueue(this);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            try {
                Log.w("meniuu", "timer");
                timer = Integer.parseInt((String) b.get("time"));
                ora = (String) b.get("mHour");
                nr_carString = (String) b.get("mPlates");
                notification_id = b.getString("notification_id");
                mLat = b.getString("lat");
                mLng = b.getString("lng");
                Log.w("meniuu", "mHour:" + ora);
                time_answer.setText("Raspuns la " + ora);
                senderRead(notification_id);
                try {
                    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
                    Date d = df.parse(ora);
                    Date date2 = new Date();
                    String actual_date = df.format(date2);
                    Log.w("meniuu", "data_actuala:" + actual_date);
                    Date date_actual = df.parse(actual_date);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(d);
                    cal.add(Calendar.MINUTE, timer);
                    String newTime = df.format(cal.getTime());
                    Date date_plus = df.parse(newTime);


                    Log.w("meniuu", "diff:" + getDateDiff(date_actual, date_plus));
                    long diff = getDateDiff(date_plus, date_actual);
                    diff = diff / 1000;
                    Log.w("meniuu", "diff inainte:" + diff);
                    if (diff > 0) {
                        Log.w("meniuu", "diff in if:" + diff);
                        progBar.setMax(timer * 60);
                        mProgressStatus = (int) diff;
                        Log.w("meniuu", "start");
                        dosomething();
                    } else {
                        Intent harta = new Intent(Timer.this, Map.class);
                        harta.putExtra("mHour", ora);
                        harta.putExtra("mPlates", nr_carString);
                        harta.putExtra("time", timer);
                        harta.putExtra("lat", mLat);
                        harta.putExtra("lng", mLng);
                        startActivity(harta);
                        finish();
                    }
                    Log.w("meniuu", "data mHour:" + ora);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.w("meniuu0", "cahct");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static long getDateDiff(Date date1, Date date2) {
        Log.w("meniuu", "date2:" + date2 + " date1:" + date1);

        long diffInMillies = date1.getTime() - date2.getTime();
        return diffInMillies;
    }

    public void initcComponents() {
        car_nr = (TextView) findViewById(R.id.car_nr_timer);
        time_answer = (TextView) findViewById(R.id.answer_timer);
        back = (RelativeLayout) findViewById(R.id.back_timer);
        back.setOnClickListener(this);
        image = (ImageView) findViewById(R.id.image);
        progBar = (ProgressBar) findViewById(R.id.progressBar);
        text = (TextView) findViewById(R.id.textView1);
    }

    public void dosomething() {
        new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus > 0) {
                    Log.w("meniuu", "in while run=" + run);
                    if (run == false)
                        mProgressStatus = 1;
                    mProgressStatus -= 1;
                    mHandler.post(new Runnable() {
                        public void run() {
                            progBar.setProgress(mProgressStatus);
                            int minutes = (mProgressStatus % 3600) / 60;
                            int sec = mProgressStatus % 60;
                            if (minutes < 10) {
                                car_nr.setText(nr_carString + " vine in " + minutes + " minute");
                                if (sec < 10)
                                    text.setText("0" + minutes + ":0" + sec);
                                else
                                    text.setText("0" + minutes + ":" + sec);
                            } else {
                                car_nr.setText(nr_carString + " vine in " + minutes + " minute");
                                if (sec < 10)
                                    text.setText(minutes + ":0" + sec);
                                else
                                    text.setText(minutes + ":" + sec);
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_timer:
                finish();
                break;
        }
    }

    public void senderRead(String notification_id) {
        Log.w("meniuu", "notification id:" + notification_id);
        String url = Constants.URL + "notifications/senderRead/" + notification_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response: receiveranswer" + response);
                    }
                }, ErrorListener) {
//                protected java.util.Map getParams() {
//                    java.util.Map params = new HashMap<String, String>();
//                    params.put("mLatitude", "24");
//                    params.put("mLongitude", "24");
//                    return params;
//                }

            public java.util.Map getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
        }
    };
}
