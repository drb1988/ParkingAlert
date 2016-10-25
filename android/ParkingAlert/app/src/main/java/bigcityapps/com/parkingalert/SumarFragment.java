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
    public SumarFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sumar, container, false);
        inicomponents(rootView);
        return rootView;
    }
public void inicomponents(View rootview){
    ok=(TextView)rootview.findViewById(R.id.ok);
    ok.setOnClickListener(new View.OnClickListener() {
        public void onClick(View view) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
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
