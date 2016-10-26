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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class TimerSenderFragmnet extends Fragment implements View.OnClickListener {
    ImageView image;
    private ProgressBar progBar;
    private TextView text;
    private Handler mHandler = new Handler();
    private int mProgressStatus = 0;
    RelativeLayout rl_come;
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
    boolean isActiv=true;
    public TimerSenderFragmnet() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(bigcityapps.com.parkingalert.R.layout.timer_sender, container, false);
        ctx=rootView.getContext();
        initcComponents(rootView);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        prefs = new SecurePreferences(rootView.getContext());
        queue = Volley.newRequestQueue(rootView.getContext());
        Bundle b = this.getArguments();
        if (b != null) {
            try {
                Log.w("meniuu", "timer_sender");
                timer = Integer.parseInt((String) b.get("time"));
                ora = (String) b.get("mHour");
                nr_carString = (String) b.get("mPlates");
                notification_id = b.getString("notification_id");
                mLat = b.getString("lat");
                mLng = b.getString("lng");
                time_answer.setText("Raspuns la " + ora);
                mImage=b.getString("image");
                Log.w("meniuu","notifin timerragment:"+notification_id);
//                senderRead(notification_id);
                Calculate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rootView;
    }
    public void initcComponents(View rootView) {
        rl_come=(RelativeLayout)rootView.findViewById(R.id.rl_come);
        rl_come.setOnClickListener(this);
        car_nr = (TextView) rootView.findViewById(R.id.car_nr_timer);
        time_answer = (TextView) rootView.findViewById(R.id.answer_timer);
        image = (ImageView) rootView.findViewById(R.id.mImage);
        Glide.with(ctx).load(mImage).asBitmap().centerCrop().into(new BitmapImageViewTarget(image) {
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                image.setImageDrawable(circularBitmapDrawable);
            }
        });
        progBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        text = (TextView) rootView.findViewById(R.id.textView1);
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
            Log.w("meniuu","diff in timer_sender:"+diff);
            if (diff > 0) {
                progBar.setMax(timer * 60);
                mProgressStatus = (int) diff;
                dosomething();
            } else {
                getActivity().getSupportFragmentManager().beginTransaction().remove(TimerSenderFragmnet.this).commit();
                Fragment  fragment = new ReviewFragment();
                Bundle harta = new Bundle();
                harta.putString("mHour", ora);
                harta.putString("mPlates", nr_carString);
                harta.putString("time", timer+"");
                harta.putString("notification_id", notification_id);
                fragment.setArguments(harta);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();



//                Intent harta = new Intent(getActivity(), Map.class);
//                harta.putExtra("mHour", ora);
//                harta.putExtra("mPlates", nr_carString);
//                harta.putExtra("time", timer_sender);
//                harta.putExtra("lat", mLat);
//                harta.putExtra("lng", mLng);
//                harta.putExtra("image", mImage);
//                startActivity(harta);
//                finish();
            }
            Log.w("meniuu", "data mHour:" + ora);
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("meniuu0", "cahct");
        }
    }

    @Override
    public void onDestroy() {
        isActiv=false;
        super.onDestroy();
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
                            if(mProgressStatus==0 & isActiv==true){
                                getActivity().getSupportFragmentManager().beginTransaction().remove(TimerSenderFragmnet.this).commit();
                                Fragment  fragment = new ReviewFragment();
                                Bundle harta = new Bundle();
                                harta.putString("mHour", ora);
                                harta.putString("mPlates", nr_carString);
                                harta.putString("time", timer+"");
                                harta.putString("lat", mLat);
                                harta.putString("lng", mLng);
                                harta.putString("image", mImage);
                                harta.putString("notification_id", notification_id);
                                fragment.setArguments(harta);
                                Log.e("meniuu","notif in timerfragmnet:"+notification_id);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
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
                Review();
                break;
        }
    }
    public void Review() {
        String url = Constants.URL+"notifications/sendReview/"+notification_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:review" + response);
                        Fragment  fragment = new SumarFragment();
                        Bundle harta = new Bundle();
                        harta.putString("mHour", ora);
                        harta.putString("mPlates", nr_carString);
                        harta.putString("time", timer+"");
                        fragment.setArguments(harta);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    }
                }, ErrorListener) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("feedback",true+"");
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
