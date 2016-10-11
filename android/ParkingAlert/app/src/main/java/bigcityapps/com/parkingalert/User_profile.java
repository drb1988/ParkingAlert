package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 20/09/2016.
 */
public class User_profile extends Activity implements View.OnClickListener{
    Context ctx;
    SharedPreferences prefs;
    ImageView poza_patrata_user_profile,poza_rotunda_user_profile;
    EditText nume, nickname, mobile, email, driver_license, city;
    RequestQueue queue;
    RelativeLayout inapoi, salvare;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.user_profile);
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        initComponents();
        getUser(prefs.getString("user_id",""));
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
    }
    public void UpdateUser(final String id){
       String url = Constants.URL+"users/updateUser/"+id;
        if(nume.getText().length()==0 || nickname.getText().length()==0 || email.getText().length()==0 || driver_license.getText().length()==0 || city.getText().length()==0)
            Toast.makeText(ctx,"Completati toate campurile",Toast.LENGTH_LONG).show();
            else{
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            String json = response;
                            Log.w("meniuu", "response:update_user" + response);
                            finish();
                        }
                    }, ErrorListener) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("first_name", nume.getText().toString());
                    params.put("last_name", nume.getText().toString());
                    params.put("edNickname", nickname.getText().toString());
                    params.put("email", email.getText().toString());
                    params.put("edDriverLicense", driver_license.getText().toString());
                    params.put("photo", "photo");
                    params.put("platform", "Android");
                    params.put("user_city", city.getText().toString());
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
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.salvare_user_profile:
            UpdateUser("57e11909853b0122ac974e23");
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
                    Log.w("meniuu","response getuser:"+response);
                    JSONObject user = new JSONObject(json);
                    nume.setText(user.getString("first_name")+" "+user.getString("last_name"));
                    nickname.setText(user.getString("nickname"));
                    email.setText(user.getString("email"));
                    driver_license.setText(user.getString("edDriverLicense"));
                    Glide.with(ctx).load(user.getString("photo")).asBitmap().centerCrop().into(new BitmapImageViewTarget(poza_rotunda_user_profile) {
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Log.w("meniuu","authtoken:"+auth_token_string);
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}
