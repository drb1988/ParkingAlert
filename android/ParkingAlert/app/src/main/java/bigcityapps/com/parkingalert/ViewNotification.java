package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
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
import java.util.Map;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 22/09/2016.
 */
public class ViewNotification extends Activity implements View.OnClickListener{
    TextView  zece, cinci, trei, nupot, car_nr_view_notification;
    RelativeLayout inapoi;
    RequestQueue queue;
    SharedPreferences prefs;
    Context ctx;
    String notification_id;
    double latitude, longitude;
    LocationManager locationManager;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_notification);
        initComponents();
        ctx=this;
        queue = Volley.newRequestQueue(this);
        prefs = new SecurePreferences(this);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            car_nr_view_notification.setText((String) b.get("mPlates")+" \n creaza o problema");
            notification_id=(String) b.get("notification_id");
            Log.w("meniuu","notification_id"+notification_id);
        }
        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return ;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListenerNetwork);
    }
    public void initComponents(){
        zece=(TextView)findViewById(R.id.zece);
        zece.setOnClickListener(this);
        cinci=(TextView)findViewById(R.id.cinci);
        cinci.setOnClickListener(this);
        trei=(TextView)findViewById(R.id.trei);
        trei.setOnClickListener(this);
        nupot=(TextView)findViewById(R.id.nupot);
        nupot.setOnClickListener(this);
        inapoi=(RelativeLayout) findViewById(R.id.inapoi_notificare);
        inapoi.setOnClickListener(this);
        car_nr_view_notification=(TextView)findViewById(R.id.car_nr_view_notification);
    }
public void setClick(boolean set){
    zece.setClickable(set);
    cinci.setClickable(set);
    trei.setClickable(set);
    nupot.setClickable(set);
}
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.inapoi_notificare:
            finish();
            break;
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
//                        Intent harta= new Intent(Scan.this, Map.class);
//                        startActivity(harta);
                        finish();
                    }
                }, ErrorListener) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("latitude", latitude+"");
                params.put("longitude", longitude+"");
                params.put("estimated",time);
                Log.w("meniuu","lat in receiver answered:"+latitude+" lng:"+longitude);
                return params;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Map<String, String> params = new HashMap<String, String>();
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
