package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 21/09/2016.
 */
public class Login extends Activity implements View.OnClickListener {
    Context ctx;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    TextView titlu;
    EditText nume,email, nickname,driver_license, city;
    RelativeLayout continuare;
    RequestQueue queue;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.login);
        queue = Volley.newRequestQueue(this);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Fun Raiser.ttf");
        titlu=(TextView)findViewById(R.id.titlu);
        titlu.setTypeface(type);
        loginButton = (LoginButton) findViewById(R.id.login_button);
//        loginButton.setBackgroundResource(R.drawable.facebook);
//        loginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        initComponents();
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends", "user_location", "user_about_me", "user_hometown"));
        ctx = this;
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.w("meniuu", "callback:" + object);
                        try {
                            Log.w("meniuu","obj.getemail:"+object.getString("email"));
                            email.setText(object.getString("email"));
                            nume.setText(object.getString("name"));
                            city.setText(object.getString("city"));
                        } catch (JSONException e) {
                            Log.w("meniuu","catch");
                            e.printStackTrace();
                        }
//                        loginButton.setVisibility(View.GONE);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday,location");
                request.setParameters(parameters);
                request.executeAsync();
            }

            public void onCancel() {
                Log.w("meniuu", "on cancel");
            }

            public void onError(FacebookException e) {
                Log.w("meniuu", "facebookexception");
                e.printStackTrace();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public void initComponents(){
        nume=(EditText)findViewById(R.id.nume_login);
        nickname=(EditText)findViewById(R.id.nick_name_login);
        email=(EditText)findViewById(R.id.email_login);
        driver_license=(EditText)findViewById(R.id.driver_license_login);
        city=(EditText)findViewById(R.id.city_login);
        continuare=(RelativeLayout) findViewById(R.id.continuare);
        continuare.setOnClickListener(this);
    }

    public void onClick(View view) {
    switch (view.getId()){
        case R.id.continuare:
       postUser();
            break;
}
    }
    public void postUser(){
        final SharedPreferences prefs = new SecurePreferences(ctx);
        String url = Constants.URL+"signup/user";
        if(nume.getText().length()==0 || nickname.getText().length()==0 || email.getText().length()==0 || driver_license.getText().length()==0 || city.getText().length()==0)
            Toast.makeText(ctx,"Completati toate campurile",Toast.LENGTH_LONG).show();
        else{
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            String json = response;
                            try {
                                JSONObject obj = new JSONObject(json);
                                JSONObject token= new JSONObject(obj.getString("token"));
                                prefs.edit().putString("user_id", obj.getString("userID")).commit();
                                prefs.edit().putString("token", token.getString("value")).commit();
                                Intent continuare= new Intent(Login.this, MainActivity.class);
                                startActivity(continuare);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.w("meniuu","catch la response, signup");
                                Toast.makeText(ctx,"erro",Toast.LENGTH_LONG).show();
                            }
                            Log.w("meniuu", "response:post user" + response);

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
            };
            queue.add(stringRequest);
        }
    }
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
            Toast.makeText(ctx,"Something went wrong",Toast.LENGTH_LONG ).show();
        }
    };
}
