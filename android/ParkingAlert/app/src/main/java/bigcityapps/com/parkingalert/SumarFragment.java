package bigcityapps.com.parkingalert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by fasu on 10/12/15.
 */
public class SumarFragment extends Fragment  {
    TextView ok;
    TextView report, dataReport, answer, dataAnswer, feedback;
    public SumarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sumar, container, false);
        inicomponents(rootView);
        Bundle b = this.getArguments();
        if(b!=null) {
            try {
                Log.w("meniuu", "timer_sender");
                report.setText("Raportat masina:"+b.getString("mPlates"));
                dataReport.setText("La data:"+b.getString("mHour"));
                dataAnswer.setText("Raspuns la:"+b.getString("mHour"));
                feedback.setText("Feedback:"+b.getString("feedback"));
            } catch (Exception e) {
                Log.w("meniuu","cacth in sumarfragment");
                e.printStackTrace();
            }
        }

        return rootView;
    }
public void inicomponents(View rootview){
    report=(TextView)rootview.findViewById(R.id.report_name);
    dataReport=(TextView)rootview.findViewById(R.id.data);
    answer=(TextView)rootview.findViewById(R.id.raspuns);
    dataAnswer=(TextView)rootview.findViewById(R.id.data_raspuns);
    feedback=(TextView)rootview.findViewById(R.id.feedback);
    ok=(TextView)rootview.findViewById(R.id.ok);
    ok.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(SumarFragment.this).commit();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    });
}
//    public void postAnswer(final String time){
//        String url = Constants.URL+"notifications/receiverAnswered/"+notification_id;
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                new Response.Listener<String>() {
//                    public void onResponse(String response) {
//                        String json = response;
//                        Log.w("meniuu", "response:post answer" + response);
//                        Fragment  fragment = new MapFragment();
//                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
////                        Intent harta= new Intent(Scan.this, Map.class);
////                        startActivity(harta);
////                        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
//                    }
//                }, ErrorListener) {
//            protected java.util.Map<String, String> getParams() {
//                java.util.Map<String, String> params = new HashMap<String, String>();
//                params.put("latitude", latitude+"");
//                params.put("longitude", longitude+"");
//                params.put("estimated",time);
//                Log.w("meniuu","lat in receiver answered:"+latitude+" lng:"+longitude);
//                return params;
//            }
//            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
//                String auth_token_string = prefs.getString("token", "");
//                java.util.Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization","Bearer "+ auth_token_string);
//                return params;
//            }
//        };
//        queue.add(stringRequest);
//    }
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
        }
    };
}
