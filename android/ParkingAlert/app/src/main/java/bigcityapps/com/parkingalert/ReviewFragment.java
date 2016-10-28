package bigcityapps.com.parkingalert;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.HashMap;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class ReviewFragment extends Fragment implements View.OnClickListener {
    Context ctx;
    RequestQueue queue;
    SharedPreferences prefs;
    RelativeLayout  nu, da;
    String ora, nr_carString, timer, mLat,mLng, mImage, notification_id,answered_at;
    TextView come, details;
    int mProgressStatus=30;
    private Handler mHandler = new Handler();
    boolean isActiv=true;

    @Override
    public void onDestroyView() {
        isActiv=false;
        super.onDestroyView();
    }

    public ReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.review, container, false);
        initcomponents(rootView);
        ctx = getContext();
        dosomething();
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(ctx);
        Bundle b = this.getArguments();
        if(b!=null) {
            try {
                timer = (String) b.get("time");
                ora = (String) b.get("mHour");
                nr_carString = (String) b.get("mPlates");
                notification_id = b.getString("notification_id");
                answered_at = b.getString("answered_at");
                Log.w("meniuu","notification_id in review:"+notification_id+" nrcar:"+nr_carString+" ora:"+ora+" timer:"+timer);
                come.setText("A venit "+nr_carString+"?");
                details.setText("Am vrea sa stim daca \n "+nr_carString+"\n a venit la masina in urma notificarii tale.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rootView;
    }
    public void initcomponents(View rootview){
        come=(TextView)rootview.findViewById(R.id.a_venit);
        details=(TextView)rootview.findViewById(R.id.details_review);
        nu=(RelativeLayout)rootview.findViewById(R.id.nu_review);
        nu.setOnClickListener(this);
        da=(RelativeLayout)rootview.findViewById(R.id.da_review);
        da.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.nu_review:
                Review(true);
                break;

            case R.id.da_review:
                Review(false);
                break;
        }
    }
    public void dosomething() {
        new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus > 0) {
                    mProgressStatus -= 1;
                    mHandler.post(new Runnable() {
                        public void run() {
                            if (mProgressStatus == 0 && isActiv) {
                                Fragment  fragment = new SumarFragment();
                                Bundle harta = new Bundle();
                                harta.putString("mHour", ora);
                                harta.putString("mPlates", nr_carString);
                                harta.putString("answered_at", answered_at);
                                harta.putString("feedback", "A expirat timpul");
                                fragment.setArguments(harta);
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
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

    public void Review(final boolean feedback) {
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
                        harta.putString("answered_at", answered_at);
                        if(feedback)
                            harta.putString("feedback", "A venit la masina");
                        else
                            harta.putString("feedback", "Nu a venit la masina");
                        fragment.setArguments(harta);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                    }
                }, ErrorListener) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("feedback",feedback+"");
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
