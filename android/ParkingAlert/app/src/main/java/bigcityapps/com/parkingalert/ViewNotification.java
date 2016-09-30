package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
    TextView detalii, zece, cinci, trei, nupot, car_nr_view_notification;
    RelativeLayout inapoi;
    RequestQueue queue;
    SharedPreferences prefs;
    Context ctx;
    String notification_id;
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
//            detalii.setText((String) b.get("detalii"));
            car_nr_view_notification.setText((String) b.get("nr_car")+" \n creaza o problema");
        }
        notification_id=prefs.getString("user_id","");
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

    public void onClick(View view) {
    switch (view.getId()){
        case R.id.inapoi_notificare:
            finish();
            break;
        case R.id.zece:
            postAnswer("3");
            break;
        case R.id.cinci:
            postAnswer("5");
            break;

        case R.id.trei:
            postAnswer("7");
            break;

        case R.id.nupot:
            postAnswer("nu pot");
            break;
        }
    }

    public void postAnswer(final String time){
        String url = Constants.URL+"notifications/receiverRead/"+notification_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:post answer" + response);
//                        Intent harta= new Intent(Scan.this, Harta.class);
//                        startActivity(harta);
                        finish();
                    }
                }, ErrorListener) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("latitude", "24");
                params.put("longitude", "24");
                params.put("estimated",time);
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
}
