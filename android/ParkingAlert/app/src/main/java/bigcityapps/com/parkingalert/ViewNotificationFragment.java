package bigcityapps.com.parkingalert;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
 * Created by fasu on 10/12/15.
 */
public class ViewNotificationFragment extends Fragment implements View.OnClickListener {
    TextView zece, cinci, trei, nupot, car_nr_view_notification;
    RelativeLayout inapoi;
    RequestQueue queue;
    SharedPreferences prefs;
    Context ctx;
    String notification_id;
    double latitude, longitude;
    LocationManager locationManager;
    public ViewNotificationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(bigcityapps.com.parkingalert.R.layout.view_notification, container, false);
        initComponents(rootView);
        ctx=rootView.getContext();
        queue = Volley.newRequestQueue(rootView.getContext());
        prefs = new SecurePreferences(rootView.getContext());
//        Intent iin = getActivity().getIntent();
        Bundle b = this.getArguments();
        if (b != null) {
            car_nr_view_notification.setText((String) b.get("mPlates")+" \n creaza o problema");
            notification_id=(String) b.get("notification_id");
            Log.w("meniuu","notification_id"+notification_id);
        }
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(rootView.getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(rootView.getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return rootView;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListenerNetwork);
        return rootView;
    }
    public void initComponents(View rootView){
        zece=(TextView)rootView.findViewById(R.id.zece);
        zece.setOnClickListener(this);
        cinci=(TextView)rootView.findViewById(R.id.cinci);
        cinci.setOnClickListener(this);
        trei=(TextView)rootView.findViewById(R.id.trei);
        trei.setOnClickListener(this);
        nupot=(TextView)rootView.findViewById(R.id.nupot);
        nupot.setOnClickListener(this);
//        inapoi=(RelativeLayout) rootView.findViewById(R.id.inapoi_notificare);
//        inapoi.setOnClickListener(this);
        car_nr_view_notification=(TextView)rootView.findViewById(R.id.car_nr_view_notification);
    }
    public void setClick(boolean set){
        zece.setClickable(set);
        cinci.setClickable(set);
        trei.setClickable(set);
        nupot.setClickable(set);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.inapoi_notificare:
//                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
//                break;
            case R.id.zece:
                setClick(false);
                postAnswer("3");
                break;
            case R.id.cinci:
                setClick(false);
                postAnswer("5");
                break;

            case R.id.trei:
                setClick(false);
                postAnswer("7");
                break;

            case R.id.nupot:
                postAnswer("100");
                break;
        }
    }

    public void postAnswer(final String time){
        String url = Constants.URL+"notifications/receiverAnswered/"+notification_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:post answer" + response);
                        Fragment  fragment = new MapFragment();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//                        Intent harta= new Intent(Scan.this, Map.class);
//                        startActivity(harta);
//                        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                    }
                }, ErrorListener) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("latitude", latitude+"");
                params.put("longitude", longitude+"");
                params.put("estimated",time);
                Log.w("meniuu","lat in receiver answered:"+latitude+" lng:"+longitude);
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
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.w("meniu", "long in locationlistener: " + longitude + " lat:" + latitude);
        }
        public void onStatusChanged(String s, int i, Bundle bundle) {}
        public void onProviderEnabled(String s) {}
        public void onProviderDisabled(String s) {}
    };
}
