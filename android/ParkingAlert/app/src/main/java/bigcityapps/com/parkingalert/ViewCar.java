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
import android.widget.Switch;
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
 * Created by fasu on 19/09/2016.
 */
public class ViewCar extends Activity implements View.OnClickListener {
    Context ctx;
    EditText tv_numele_masina, tv_nr, tv_producator, tv_model, tv_an_producti;
    RelativeLayout inapoi, salvare;
    RequestQueue queue;
    String mPlatesOriginal;
    SharedPreferences prefs;
    Switch switch_cars, switch_other;
    TextView title;
    EditText ed_autovehicul;
    boolean enable_notifications, enable_others;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vizualizare_masina);
        initcomponents();
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            mPlatesOriginal = (String) b.get("edNr");
            tv_numele_masina.setText((String) b.get("edname"));
            tv_nr.setText((String) b.get("edNr"));
            String a = "";
            if (!b.getString("edYear").equals("null"))
                a = a + b.getString("edYear");
            if (!b.getString("edMaker").equals("null"))
                a = a + " " + b.getString("edMaker");
            if (!b.getString("edModel").equals("null"))
                a = a + " " + b.getString("edModel");
            ed_autovehicul.setText(a);
            Log.w("meniuu", "nrinmatriculare:" + mPlatesOriginal + " marime:" + mPlatesOriginal.length());
            enable_notifications = b.getBoolean("enable_notifications");
            enable_others = b.getBoolean("enable_others");
            switch_cars.setChecked(enable_notifications);
            switch_other.setChecked(enable_others);
            title.setText(mPlatesOriginal);
            Log.w("meniuu", "producator:" + tv_producator);
        }
    }

    public void initcomponents() {
        title = (TextView) findViewById(R.id.title);
        switch_cars = (Switch) findViewById(R.id.switch_cars);
        switch_other = (Switch) findViewById(R.id.switch_other);
        tv_numele_masina = (EditText) findViewById(R.id.et_numele_masina);
        tv_nr = (EditText) findViewById(R.id.et_nr);
        tv_producator = (EditText) findViewById(R.id.et_producator);
        tv_model = (EditText) findViewById(R.id.et_model);
        ed_autovehicul = (EditText) findViewById(R.id.ed_autovehicul);
        tv_an_producti = (EditText) findViewById(R.id.et_an_productie);
        inapoi = (RelativeLayout) findViewById(R.id.inapoi_vizualizare_masina);
        salvare = (RelativeLayout) findViewById(R.id.gata_vizualizare_masina);
        inapoi.setOnClickListener(this);
        salvare.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inapoi_vizualizare_masina:
                finish();
//    Intent rlBack= new Intent(ViewCar.this, MainActivity.class);
//        startActivity(rlBack);
                break;

            case R.id.gata_vizualizare_masina:
                Intent update = new Intent(ViewCar.this, ModifyCar.class);
                startActivity(update);
//            updateCars(prefs.getString("user_id",""));

//        Intent salvare= new Intent(ViewCar.this, MainActivity.class);
//        startActivity(salvare);
                break;
        }
    }

    public void updateCars(final String id) {
        String url = Constants.URL + "users/editCar/" + id + "&" + mPlatesOriginal;
        Log.w("meniuu", "url:" + url);
        if (tv_numele_masina.getText().length() == 0 || tv_nr.getText().length() == 0 || tv_producator.getText().length() == 0 || tv_model.getText().length() == 0 || tv_an_producti.getText().length() == 0)
            Toast.makeText(ctx, "Completati toate campurile", Toast.LENGTH_LONG).show();
        else {
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
                    params.put("edModel", tv_model.getText().toString());
                    params.put("year", tv_an_producti.getText().toString());
                    if (switch_cars.isChecked())
                        params.put("enable_notifications", true + "");
                    else
                        params.put("enable_notifications", false + "");

                    return params;
                }

                public Map<String, String> getHeaders() throws AuthFailureError {
                    String auth_token_string = prefs.getString("token", "");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer " + auth_token_string);
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
