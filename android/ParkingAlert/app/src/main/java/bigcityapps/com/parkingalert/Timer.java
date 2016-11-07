package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

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
    String ora, nr_carString, notification_id, mImage;
    boolean run = true;
    TextView car_nr, time_answer;
    RequestQueue queue;
    String mLat, mLng;
    SharedPreferences prefs;
    Long estimetedTime, time , actualDate;
    Context ctx;
    @Override
    protected void onStop() {
        run = false;
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        Calculate();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_sender);
        ctx=this;
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
                time_answer.setText("Raspuns la " + ora);
                mImage=b.getString("image");
                senderRead(notification_id);
                Calculate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
public void Calculate(){
    try {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
        Date myDate = simpleDateFormat.parse(ora);
        time=myDate.getTime();
        estimetedTime=(long)timer*60*1000;
        time=time+estimetedTime;
        Date date2 = new Date();
        actualDate=date2.getTime();
        long diff=time-actualDate;
        diff = diff / 1000;
        Log.w("meniuu","diff in timer:"+diff);
        if (diff > 0) {
            progBar.setMax(timer * 60);
            mProgressStatus = (int) diff;
            dosomething();
        } else {
            Intent harta = new Intent(Timer.this, Map.class);
            harta.putExtra("mHour", ora);
            harta.putExtra("mPlates", nr_carString);
            harta.putExtra("time", timer);
            harta.putExtra("lat", mLat);
            harta.putExtra("lng", mLng);
            harta.putExtra("image", mImage);
            startActivity(harta);
            finish();
        }
        Log.w("meniuu", "data mHour:" + ora);
    } catch (Exception e) {
        e.printStackTrace();
        Log.w("meniuu0", "cahct");
    }
}
    public void initcComponents() {
        car_nr = (TextView) findViewById(R.id.car_nr_timer);
        time_answer = (TextView) findViewById(R.id.answer_timer);
//        back = (RelativeLayout) findViewById(R.id.back_timer);
        back.setOnClickListener(this);
        image = (ImageView) findViewById(R.id.mImage);
        Glide.with(ctx).load(mImage).asBitmap().centerCrop().into(new BitmapImageViewTarget(image) {
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                image.setImageDrawable(circularBitmapDrawable);
            }
        });
        progBar = (ProgressBar) findViewById(R.id.progressBar);
        text = (TextView) findViewById(R.id.textView1);
    }

    public void dosomething() {
        new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus > 0) {
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

    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.back_timer:
//                finish();
//                break;
//        }
    }

    public void senderRead(String notification_id) {
        String url = Constants.URL + "notifications/senderRead/" + notification_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response: receiveranswer" + response);
                    }
                }, ErrorListener) {
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