package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.util.Map;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 19/09/2016.
 */
public class Vizualizare_masina extends Activity implements View.OnClickListener{
    Context ctx;
    EditText tv_numele_masina, tv_nr, tv_producator, tv_model, tv_an_producti;
    RelativeLayout inapoi, salvare;
    RequestQueue queue;
    SharedPreferences prefs;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.vizualizare_masina);
        initcomponents();
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            tv_numele_masina.setText((String) b.get("nume"));
            tv_nr.setText((String) b.get("nr"));
            tv_producator.setText((String) b.get("producator"));
            tv_model.setText((String) b.get("model"));
            tv_an_producti.setText((String) b.get("an"));
        }
    }
    public void initcomponents() {
        tv_numele_masina = (EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_numele_masina);
        tv_nr = (EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_nr);
        tv_producator = (EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_producator);
        tv_model = (EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_model);
        tv_an_producti = (EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_an_productie);
        inapoi=(RelativeLayout)findViewById(bigcityapps.com.parkingalert.R.id.inapoi_vizualizare_masina);
        salvare=(RelativeLayout)findViewById(bigcityapps.com.parkingalert.R.id.gata_vizualizare_masina);
        inapoi.setOnClickListener(this);
        salvare.setOnClickListener(this);
    }

    public void onClick(View view) {
    switch (view.getId()){
        case R.id.inapoi_vizualizare_masina:
            finish();
//    Intent inapoi= new Intent(Vizualizare_masina.this, MainActivity.class);
//        startActivity(inapoi);
        break;

        case R.id.gata_vizualizare_masina:
            updateCars(prefs.getString("user_id",""));

//        Intent salvare= new Intent(Vizualizare_masina.this, MainActivity.class);
//        startActivity(salvare);
        break;
}
    }

    public void updateCars(final String id){
        String url = Constants.URL+"users/updateCars/"+id;
        if(tv_numele_masina.getText().length()==0 || tv_nr.getText().length()==0 || tv_producator.getText().length()==0 || tv_model.getText().length()==0 || tv_an_producti.getText().length()==0)
            Toast.makeText(ctx,"Completati toate campurile",Toast.LENGTH_LONG).show();
        else{
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            String json = response;
                            Log.w("meniuu", "response:post user" + response);
                            finish();
                        }
                    }, ErrorListener) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("plates", tv_nr.getText().toString());
                    params.put("given_name", tv_numele_masina.getText().toString());
                    params.put("make", tv_producator.getText().toString());
                    params.put("model", tv_model.getText().toString());
                    params.put("year", tv_an_producti.getText().toString());
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
    }
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
        }
    };
}
