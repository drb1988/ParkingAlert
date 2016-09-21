package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Util.Constants;

/**
 * Created by fasu on 20/09/2016.
 */
public class User_profile extends Activity implements View.OnClickListener{
    Context ctx;
    ImageView poza_patrata_user_profile,poza_rotunda_user_profile;
    EditText nume, nickname, mobile, email, driver_license, city, country;
    RequestQueue queue;
    RelativeLayout inapoi, salvare;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.user_profile);
        ctx = this;
        queue = Volley.newRequestQueue(this);
        initComponents();
        getUser("57e11909853b0122ac974e23");
    }
    public void initComponents(){
        salvare=(RelativeLayout)findViewById(R.id.salvare_user_profile);
        salvare.setOnClickListener(this);
        inapoi=(RelativeLayout)findViewById(R.id.inapoi_user_profile);
        inapoi.setOnClickListener(this);
        poza_patrata_user_profile=(ImageView)findViewById(bigcityapps.com.parkingalert.R.id.poza_patrata_user_profile);
        poza_rotunda_user_profile=(ImageView)findViewById(bigcityapps.com.parkingalert.R.id.poza_rotunda_user_profile);
        nume=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.numele_user_profile);
        nickname=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.nick_name);
        mobile=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.mobile_user_profile);
        email=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.email);
        driver_license=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.driver_license_user_profile);
        city=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.city_user_profile);
        country=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.country_user_profile);
    }
    public void postUser(final String id){
       String url = Constants.URL+"users/updateUser/"+id;
        if(nume.getText().length()==0 || nickname.getText().length()==0 || email.getText().length()==0 || driver_license.getText().length()==0 || city.getText().length()==0)
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
                    params.put("first_name", nume.getText().toString());
                    params.put("last_name", nume.getText().toString());
                    params.put("nickname", nickname.getText().toString());
                    params.put("email", email.getText().toString());
                    params.put("driver_license", driver_license.getText().toString());
                    params.put("photo", "photo");
                    params.put("platform", "Android");
                    params.put("user_city", city.getText().toString());
                    return params;
                }

//            public Map<String, String> getHeaders() throws AuthFailureError {
////                String auth_token_string = prefs.getString("token1", "");
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", auth_token_string);
//                return params;
//            }
            };
            queue.add(stringRequest);
        }
    }
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
        }
    };
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.salvare_user_profile:
            postUser("57e11909853b0122ac974e23");
            break;
        case R.id.inapoi_user_profile:
            finish();
            break;
}
    }
    public void getUser(String id){
        String url = Constants.URL+"users/getUser/"+id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                try {
                    JSONObject user = new JSONObject(json);
                    nume.setText(user.getString("first_name")+" "+user.getString("last_name"));
                    nickname.setText(user.getString("nickname"));
                    email.setText(user.getString("email"));
                    driver_license.setText(user.getString("driver_license"));
                    Glide.with(ctx).load(user.getString("media_link")).asBitmap().centerCrop().into(new BitmapImageViewTarget(poza_rotunda_user_profile) {
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            poza_rotunda_user_profile.setImageDrawable(circularBitmapDrawable);
                        }
                    });
                    Glide.with(ctx).load(user.getString("photo")).into(poza_patrata_user_profile);
                }catch (Exception e)
                {Log.w("meniuu","este catch");
                    e.printStackTrace();
                }
            }
        }, ErrorListener) {
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                String auth_token_string = prefs.getString("token1", "");
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", auth_token_string);
//                return params;
//            }
        };
        queue.add(stringRequest);

    }
}
