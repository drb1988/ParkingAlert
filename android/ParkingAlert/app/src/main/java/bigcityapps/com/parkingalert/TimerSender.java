package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by Sistem1 on 01/10/2016.
 */
public class TimerSender extends Activity implements View.OnClickListener {
    RelativeLayout back;
    int timer;
    boolean run=true;
    String ora,nr_carString,notification_id;
    private Handler mHandler = new Handler();
    private ProgressBar progBar;
    private int mProgressStatus=0;
    TextView text;
    Context ctx;
    String mLat, mLng,image;
    RequestQueue queue;
    SharedPreferences prefs;
    RelativeLayout extended;
    long time, estimetedTime, actualDate;
    @Override
    protected void onStop() {
        run=false;
        super.onStop();
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer_sender);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        queue = Volley.newRequestQueue(this);
        prefs = new SecurePreferences(this);
        ctx=this;
        initComponents();
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null) {
            try {
                timer = Integer.parseInt((String) b.get("time"));
                ora = (String) b.get("mHour");
                mLat =b.getString("lat");
                mLng =b.getString("lng");
                Log.w("meniuu","lat:"+mLat);
                nr_carString = (String) b.get("mPlates");
                notification_id = (String) b.get("notification_id");
                image = b.getString("image");
                Log.w("meniuu","mHour:"+ora);
                try {
//                    SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
//                    Date d = df.parse(ora);
//                    Date date2= new Date();
//                    String actual_date=df.format(date2);
//                    Log.w("meniuu","data_actuala:"+actual_date);
//                    Date date_actual=df.parse(actual_date);
//                    Calendar cal = Calendar.getInstance();
//                    cal.setTime(d);
//                    cal.add(Calendar.MINUTE, timer);
//                    String newTime = df.format(cal.getTime());
//                    Date date_plus=df.parse(newTime);
//
//
//                    Log.w("meniuu","diff:"+ getDateDiff(date_actual,date_plus));
//                    long diff=getDateDiff(date_plus,date_actual);

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                    Date myDate = simpleDateFormat.parse(ora);
                    time=myDate.getTime();
                    estimetedTime=(long)timer*60*1000;
                    time=time+estimetedTime;
                    Date date2 = new Date();
                    actualDate=date2.getTime();
                    long diff=time-actualDate;

                    diff=diff/1000;
                    Log.w("meniuu","diff inainte:"+diff);
                    if(diff>0) {
                        Log.w("meniuu","diff in if:"+diff);
                        progBar.setMax(timer*60);
                        mProgressStatus=(int)diff;
                        Log.w("meniuu","start");
                        dosomething();
                    }else {
                        Intent harta = new Intent(TimerSender.this, Map.class);
                        harta.putExtra("mHour", ora);
                        harta.putExtra("mPlates", nr_carString);
                        harta.putExtra("time", timer);
                        harta.putExtra("lat", mLat);
                        harta.putExtra("lng", mLng);
                        harta.putExtra("image", image);
                        startActivity(harta);
                        finish();
                    }
                    Log.w("meniuu","data mHour:"+ora);
                }catch (Exception e){
                    e.printStackTrace();
                    Log.w("meniuu0","cahct");
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.w("meniuu","catch");
            }
        }
    }
    public void dosomething() {
        new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus > 0) {
                    if(run==false)
                        mProgressStatus=1;
                    mProgressStatus -= 1;
                    // Update the progress bar
                    mHandler.post(new Runnable() {
                        public void run() {
                            if(mProgressStatus<21){
                                if(extended.getVisibility()==View.INVISIBLE)
                                    extended.setVisibility(View.VISIBLE);
                            }
//                            if(mProgressStatus==0){
//                                Intent map = new Intent(TimerSender.this, Map.class);
//                                map.putExtra("mHour", ora);
//                                map.putExtra("mPlates", nr_carString);
//                                map.putExtra("time", timer);
//                                map.putExtra("lat", mLat);
//                                map.putExtra("lng", mLng);
//                                startActivity(map);
//                                finish();
//                            }
                            progBar.setProgress(mProgressStatus);
                            int minutes=(mProgressStatus%3600)/60;
                            int sec=mProgressStatus%60;
                            if(minutes<10) {
                                if (sec < 10)
                                    text.setText("0" + minutes + ":0" + sec);
                                else
                                    text.setText("0" + minutes + ":" + sec);
                            } else {
                                if(sec<10)
                                    text.setText(minutes + ":0" + sec);
                                else
                                    text.setText(minutes + ":" + sec);
                            }
                            Log.w("meniuu","sec in timersender:"+sec+" min:"+minutes);
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
    public static long getDateDiff(Date date1, Date date2) {
        Log.w("meniuu","date2:"+date2+" date1:"+date1);

        long diffInMillies = date1.getTime() - date2.getTime();
        return diffInMillies;
    }
    public void initComponents(){
//        back=(RelativeLayout)findViewById(R.id.back_timer_sender);
        back.setOnClickListener(this);
        progBar= (ProgressBar)findViewById(R.id.progressBar);
        text = (TextView)findViewById(R.id.textView1);
        extended=(RelativeLayout)findViewById(R.id.bottom);
        extended.setOnClickListener(this);
    }

    /**
     * onclick method
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.back_timer_sender:
//                finish();
//                break;
            case R.id.bottom:
                postExtended();
                break;
        }
    }

    public void postExtended(){
        String url = Constants.URL+"notifications/receiverExtended/"+notification_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:post answer" + response);
//                        Intent map= new Intent(Scan.this, Map.class);
//                        startActivity(map);
                        finish();
                    }
                }, ErrorListener) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("extension_time","5");
                return params;
            }
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization","Bearer "+ auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
            Toast.makeText(ctx,"Something went wrong",Toast.LENGTH_LONG ).show();
        }
    };
}
