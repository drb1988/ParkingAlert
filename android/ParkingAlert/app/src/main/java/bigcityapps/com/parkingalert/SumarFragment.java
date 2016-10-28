package bigcityapps.com.parkingalert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by fasu on 10/12/15.
 */
public class SumarFragment extends Fragment  {
    TextView ok;
    TextView report, dataReport, answer, dataAnswer, feedback;
    TableRow tablerow;
    public SumarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sumar2, container, false);
        inicomponents(rootView);
        ((MainActivity) getActivity()).setTitle("Sumar");
        Bundle b = this.getArguments();
        if(b!=null) {
            try {
                report.setText(b.getString("mPlates"));
                try {
                    if (b.getString("answered_at").equals("null"))
                        tablerow.setVisibility(View.GONE);
                    else {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                        Date myDate = simpleDateFormat.parse(b.getString("answered_at"));
                        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                        String data = format1.format(myDate);
                        dataAnswer.setText(data);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                feedback.setText(b.getString("feedback"));

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                Date myDate = simpleDateFormat.parse(b.getString("mHour"));
                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                String data = format1.format(myDate);
                dataReport.setText(data);
            } catch (Exception e) {
                Log.w("meniuu","cacth in sumarfragment");
                e.printStackTrace();
            }
        }

        return rootView;
    }
public void inicomponents(View rootview){
    tablerow=(TableRow)rootview.findViewById(R.id.tablerow);
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
