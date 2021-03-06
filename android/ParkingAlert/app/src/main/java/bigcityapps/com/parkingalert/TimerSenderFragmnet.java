package bigcityapps.com.parkingalert;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 10/12/15.
 */
public class TimerSenderFragmnet extends Fragment implements View.OnClickListener {
    ImageView image;
    private ProgressBar progBar;
    private TextView text;
    private Handler mHandler = new Handler();
    private int mProgressStatus = -1;
    RelativeLayout rl_come;
    int timer;
    String TAG = "meniuu";
    String ora, nr_carString, notification_id, mImage, answered_at;
    boolean run = true;
    TextView car_nr, time_answer;
    RequestQueue queue;
    String mLat, mLng;
    SharedPreferences prefs;
    long estimetedTime, time, actualDate;
    Context ctx;
    boolean isActiv = true;
    int extented_time = 0;
    boolean running = true;
    long diff;
    public void onResume() {
        if(mProgressStatus==0) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(TimerSenderFragmnet.this).commit();
            Fragment fragment = new ReviewFragment();
            Bundle harta = new Bundle();
            harta.putString("mHour", ora);
            harta.putString("mPlates", nr_carString);
            harta.putString("time", timer + "");
            harta.putString("notification_id", notification_id);
            fragment.setArguments(harta);
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commitAllowingStateLoss();
            Log.w("meniuu", "on resume timersender + diff:" + mProgressStatus);
        }
        MainActivity.active=true;
        super.onResume();
    }
    public TimerSenderFragmnet() {
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.active=true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.timer_sender, container, false);
        ctx = rootView.getContext();
        MainActivity.active = true;
        Constants.isrunning=true;
        ((MainActivity) getActivity()).setTitle("Notificari");
        initcComponents(rootView);
        Constants.isActivMap = false;
        Log.w("meniuu", "oncreate in timersenderfragm");
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        prefs = new SecurePreferences(rootView.getContext());
        queue = Volley.newRequestQueue(rootView.getContext());
        Bundle b = this.getArguments();
        if (b != null) {
            try {
//                Log.w("meniuu", "acuma suntem in timer_senderfragment");
//                timer = Integer.parseInt((String) b.get("time"));
                ora = (String) b.get("mHour");
//                nr_carString = (String) b.get("mPlates");
                notification_id = b.getString("notification_id");
//                mLat = b.getString("lat");
//                mLng = b.getString("lng");
//                time_answer.setText("Raspuns la " + ora);
                mImage = b.getString("image");
                Glide.with(ctx).load(mImage).asBitmap().centerCrop().into(new BitmapImageViewTarget(image) {
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        image.setImageDrawable(circularBitmapDrawable);
                    }
                });
//                answered_at = b.getString("answered_at");
                Log.w("meniuu", "notifin timerragment:" + notification_id);
                senderRead(notification_id);
                getNotificationAll(notification_id);
//                senderRead(notification_id);
//                Calculate();
            } catch (Exception e) {
                Log.w("meniuu", "catch la luare in timersenderfr");
                e.printStackTrace();
            }
        } else
            Log.w("meniuu", "e nulll");
        return rootView;
    }

    public void initcComponents(View rootView) {
        rl_come = (RelativeLayout) rootView.findViewById(R.id.rl_come);
        rl_come.setOnClickListener(this);
        car_nr = (TextView) rootView.findViewById(R.id.car_nr_timer);
        time_answer = (TextView) rootView.findViewById(R.id.answer_timer);
        image = (ImageView) rootView.findViewById(R.id.mImage);

        progBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        text = (TextView) rootView.findViewById(R.id.textView1);
    }

    public void Calculate() {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
            Date myDate = simpleDateFormat.parse(answered_at);
            time = myDate.getTime();
            estimetedTime = (long) timer * 60 * 1000 + ((long) extented_time * 60 * 1000);
            time = time + estimetedTime;
            Date date2 = new Date();
            actualDate = date2.getTime();
            diff = time - actualDate;
            diff = diff / 1000;
            Log.w("meniuu", "diff in timer_sender:" + diff);
            if (diff > 0) {
                progBar.setVisibility(View.VISIBLE);
                progBar.setMax((int)diff );
                mProgressStatus = (int)diff;
                dosomething();
            } else {
                Log.w("meniuu", "se trece in review din timersendfr");
                getActivity().getSupportFragmentManager().beginTransaction().remove(TimerSenderFragmnet.this).commit();
                Fragment fragment = new ReviewFragment();
                Bundle harta = new Bundle();
                harta.putString("mHour", ora);
                harta.putString("mPlates", nr_carString);
                harta.putString("time", timer + "");
                harta.putString("notification_id", notification_id);
                fragment.setArguments(harta);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commitAllowingStateLoss();
            }
            Log.w("meniuu", "data mHour:" + ora);
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("meniuu0", "cahct");
        }
    }

    public void getNotificationAll(final String id) {
        String url = Constants.URL + "notifications/getNotification/" + id;
        Log.w("meniuu", "url in getnotifall:" + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                try {
                    Log.w("meniuu", "response getnotificationAll:" + json);
                    JSONObject c = new JSONObject(json);
                    ora = c.getString("create_date");
                    JSONObject review = new JSONObject(c.getString("review"));
                    nr_carString = c.getString("vehicle");
                    try {
                        JSONObject answer = new JSONObject(c.getString("answer"));
                        answered_at = answer.getString("answered_at");
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                            Date myDate = simpleDateFormat.parse(answered_at);
                            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                            String data=format1.format(myDate);

//            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
                            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
                            Date date = null;

                            date = format1.parse(data);
                            String ora=format2.format(date);
                        Log.w("meniuu","la ora:"+answered_at+"oraaaa: ora");
                        time_answer.setText("Raspuns la " + ora);
                        timer = Integer.parseInt(answer.getString("estimated"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        JSONArray extesions = new JSONArray(c.getString("extesions"));
                        JSONObject extesions1 = extesions.getJSONObject(0);
                        extented_time = Integer.parseInt(extesions1.getString("extension_time"));
                        Log.w("meniuu", "sa luat timextension:" + extented_time);
                    } catch (Exception e) {
                        Log.w("meniuu", "cacth la luarea extensiontime");
                        e.printStackTrace();
                    }
                    Calculate();
                    //// deschid fragment
                } catch (Exception e) {
                    Log.w("meniuu", "este catchla get notificationAll");
                }
            }
        }, ErrorListener) {
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    @Override
    public void onDestroyView() {
        Log.w("meniuu", "ondestroy timersenderfrag");
        isActiv = false;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {


        super.onDestroy();
    }

    @Override
    public void onPause() {
        Log.w("meniuu", "onpausa timersenderfr");
        isActiv=false;
        super.onPause();
    }

    public void dosomething() {
    new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus > 0 && running == true && Constants.isrunning ) {
                    if (run == false)
                        mProgressStatus = 1;
                    mProgressStatus -= 1;
                    mHandler.post(new Runnable() {
                        public void run() {
                            progBar.setProgress(mProgressStatus);
                            int minutes = (mProgressStatus % 3600) / 60;
                            int sec = mProgressStatus % 60;
                            Log.w("meniuu","mproressstatus in timersendfr:"+mProgressStatus);
                            if (mProgressStatus == 0) {
                                getNotificationForTest(notification_id);
//                                reviewExpirat();
                            }
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
            case R.id.rl_come:
                running = false;
                Review();
                break;
        }
    }

    public void Review() {
        String url = Constants.URL + "notifications/sendReview/" + notification_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:review" + response);
                        Fragment fragment = new SumarFragment();
                        Bundle harta = new Bundle();
                        harta.putString("mHour", ora);
                        harta.putString("mPlates", nr_carString);
                        harta.putString("answered_at", answered_at);
                        harta.putString("feedback", "A venit la masina");

                        fragment.setArguments(harta);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commitAllowingStateLoss();
                    }
                }, ErrorListener) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("feedback", true + "");
                params.put("is_ontime", true + "");
                return params;
            }

            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void reviewExpirat() {
        String url = Constants.URL + "notifications/sendReview/" + notification_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:review" + response);
                        if (isActiv) {
                            Log.w("meniuu", "intra in review din timersenderfragment");
                            getActivity().getSupportFragmentManager().beginTransaction().remove(TimerSenderFragmnet.this).commitAllowingStateLoss();
                            Fragment fragment = new ReviewFragment();
                            Bundle harta = new Bundle();
                            harta.putString("mHour", ora);
                            harta.putString("mPlates", nr_carString);
                            harta.putString("time", timer + "");
                            harta.putString("lat", mLat);
                            harta.putString("lng", mLng);
                            harta.putString("image", mImage);
                            harta.putString("notification_id", notification_id);
                            fragment.setArguments(harta);
                            Log.e("meniuu", "notif in timerfragmnet:" + notification_id + " nr_car:" + nr_carString + " time:" + timer + " ora:" + ora);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commitAllowingStateLoss();
                        }
                    }
                }, ErrorListener) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("is_ontime", false + "");
                return params;
            }

            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void senderRead(String notification_id) {
        String url = Constants.URL + "notifications/senderRead/" + notification_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response: receiveranswer:" + response);
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

    public void getNotificationForTest(final String id ) {
        String url = Constants.URL + "notifications/getNotification/" + id;
        Log.w("meniuu", "url in getnotif for test:" + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                    ModelNotification modelNotification = new ModelNotification();
                    JSONObject c = new JSONObject(json);
                    modelNotification.setId(c.getString("_id"));
                    JSONObject answer = new JSONObject(c.getString("answer"));
                    int t1= Integer.parseInt(answer.getString("estimated"));
                    int t2=0;
                    try {
                        JSONArray extesions = new JSONArray(c.getString("extesions"));
                        JSONObject extesions1 = extesions.getJSONObject(0);
                        t2 = Integer.parseInt(extesions1.getString("extension_time"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    Log.w("meniuu","t1:"+t1+" t2:"+t2);
                    Date myDate = simpleDateFormat.parse(c.getString("create_date"));
                    long  create = myDate.getTime();
                    long   estimetedTime = (long) t1 * 60 * 1000 + ((long) t2 * 60 * 1000);
                    Log.w("meniuu","timpul estimat"+estimetedTime);
                    create = create + estimetedTime;
                    Date date2 = new Date();
                    long  thisDate = date2.getTime();
                    long diff1 = create - thisDate;
                    diff1=diff1/1000;
                    if(diff1<10) {
                        reviewExpirat();
                        Log.w("meniuu","diff1 e<10 si se apeleaza review expirat");
                    }else
                        Log.w("meniuu","diff1:"+diff1);
                } catch (Exception e) {
                }
            }
        }, ErrorListener) {
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }


}
