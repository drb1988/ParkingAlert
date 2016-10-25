package bigcityapps.com.parkingalert;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
    RelativeLayout inapoi,anuleaza, nu, da;
    String mNotification_id, mPlates;
    String ora, nr_carString, timer, mLat,mLng, mImage, notification_id;
    public ReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.review, container, false);
        initcomponents(rootView);
        ctx = getContext();
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(ctx);
//        Intent iin= getIntent();
        Bundle b = this.getArguments();
        if(b!=null) {
            try {
                Log.w("meniuu", "timer");
                timer = (String) b.get("time");
                ora = (String) b.get("mHour");
                nr_carString = (String) b.get("mPlates");
                notification_id = b.getString("notification_id");
                mLat = b.getString("lat");
                mLng = b.getString("lng");
//                time_answer.setText("Raspuns la " + ora);
                mImage=b.getString("image");
//                senderRead(notification_id);
//                Calculate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rootView;
    }
    public void initcomponents(View rootview){
        inapoi=(RelativeLayout)rootview.findViewById(R.id.inapoi_review);
        inapoi.setOnClickListener(this);
        anuleaza=(RelativeLayout)rootview.findViewById(R.id.anuleaza_review);
        anuleaza.setOnClickListener(this);
        nu=(RelativeLayout)rootview.findViewById(R.id.nu_review);
        nu.setOnClickListener(this);
        da=(RelativeLayout)rootview.findViewById(R.id.da_review);
        da.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.inapoi_review:
//                finish();
                break;
            case R.id.anuleaza_review:
//                finish();
                break;
            case R.id.nu_review:
                Review(true);
                break;

            case R.id.da_review:
                Review(false);
                break;
        }
    }
    public void Review(final boolean feedback){
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
                        harta.putString("lat", mLat);
                        harta.putString("lng", mLng);
                        harta.putString("image", mImage);
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
