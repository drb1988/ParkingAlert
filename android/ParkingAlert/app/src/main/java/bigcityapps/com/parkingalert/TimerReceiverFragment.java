package bigcityapps.com.parkingalert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by anupamchugh on 10/12/15.
 */
public class TimerReceiverFragment extends Fragment implements View.OnClickListener {
    RelativeLayout back;
    int timer, extented_time=0;
    boolean run=true;
    String ora,nr_carString,notification_id;
    private Handler mHandler = new Handler();
    private ProgressBar progBar;
    private int mProgressStatus=0;
    TextView text;
    Context ctx;
    String mLat, mLng,image,answered_at;
    RequestQueue queue;
    SharedPreferences prefs;
    RelativeLayout extended;
    long time, estimetedTime, actualDate;
    boolean isActiv=true;

    public TimerReceiverFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.timer_receiver, container, false);
        MainActivity.active=true;
        ((MainActivity) getActivity()).setTitle("Notificari");
        queue = Volley.newRequestQueue(getContext());
        prefs = new SecurePreferences(getContext());
        ctx=getContext();
        initComponents(rootView);
//        Intent iin= getActivity().getIntent();
        Bundle b = this.getArguments();
        if(b!=null) {
            try {
                try {
                    extented_time = Integer.parseInt(b.getString("extension_time"));
                    Log.w("meniuu","extended_time:"+extented_time);
                }catch (Exception e){
                    Log.w("meniuu","catch la luarea extendet_time");
                    e.printStackTrace();
                }
                timer = Integer.parseInt((String) b.get("time"));
                ora = (String) b.get("mHour");
                mLat =b.getString("lat");
                mLng =b.getString("lng");
                nr_carString = (String) b.get("mPlates");
                notification_id = (String) b.get("notification_id");
                image = b.getString("image");
                answered_at=b.getString("answered_at");
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                    Date myDate = simpleDateFormat.parse(ora);
                    time=myDate.getTime();
                    Log.w("meniuu","extendet_time:"+extented_time);
                    estimetedTime=(long)timer*60*1000+((long)extented_time*60*1000);
                    time=time+estimetedTime;
                    Date date2 = new Date();
                    actualDate=date2.getTime();
                    long diff=time-actualDate;

                    diff=diff/1000;
                    if(diff>0) {
                        Log.w("meniuu","diff in if:"+diff);
                        progBar.setMax(timer*60);
                        mProgressStatus=(int)diff;
                        Log.w("meniuu","start");
                        dosomething();
                    }else {
                        getActivity().getSupportFragmentManager().beginTransaction().remove(TimerReceiverFragment.this).commit();
                        Fragment  fragment = new SumarFragment();
                        Bundle harta = new Bundle();
                        harta.putString("mHour", ora);
                        harta.putString("mPlates", nr_carString);
                        harta.putString("answered_at", answered_at);
                        harta.putString("notification_id", notification_id);
                        harta.putString("feedback", "A expirat timpul");
                        fragment.setArguments(harta);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

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
        return rootView;
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
                            if(mProgressStatus==0 && isActiv ){
                                getActivity().getSupportFragmentManager().beginTransaction().remove(TimerReceiverFragment.this).commit();
                                Fragment  fragment = new SumarFragment();
                                Bundle harta = new Bundle();
                                harta.putString("mHour", ora);
                                harta.putString("mPlates", nr_carString);
                                harta.putString("answered_at", answered_at);
                                harta.putString("notification_id", notification_id);
                                harta.putString("feedback", "A expirat timpul");
                                fragment.setArguments(harta);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                            }
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
    public void initComponents(View rootview){
//        back=(RelativeLayout)rootview.findViewById(R.id.back_timer_sender);
//        back.setOnClickListener(this);
        progBar= (ProgressBar)rootview.findViewById(R.id.progressBar);
        text = (TextView)rootview.findViewById(R.id.textView1);
        extended=(RelativeLayout)rootview.findViewById(R.id.bottom);
        extended.setOnClickListener(this);
    }

    /**
     * onclick method
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.back_timer_sender:
////                finish();
//                break;
            case R.id.bottom:
                Log.w("meniuu","sa dat click");
                postExtended();
                break;
        }
    }

    @Override
    public void onPause() {
        isActiv=false;
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        Log.w("meniuu", "on destroyview in mapfragment");
        isActiv=false;
        super.onDestroyView();
    }
    public void postExtended(){
        String url = Constants.URL+"notifications/receiverExtended/"+notification_id;
        Log.w("meniuu","url extended"+url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:post receiver extended" + response);
                        Intent main= new Intent(getActivity(), MainActivity.class);
                        startActivity(main);
                        getActivity().finish();
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