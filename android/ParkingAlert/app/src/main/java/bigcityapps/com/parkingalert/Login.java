package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
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
import com.crashlytics.android.Crashlytics;
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
import io.fabric.sdk.android.Fabric;

/**
 * Created by fasu on 21/09/2016.
 */
/// ram- 57ef7f2103473a2a80e9a039
public class Login extends Activity implements View.OnClickListener {
    Context ctx;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    EditText edName, edemail, edNickname, edDriverLicense, edCity;
    RelativeLayout rlNext;
    RequestQueue queue;
    SharedPreferences prefs;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.login);
        queue = Volley.newRequestQueue(this);
        loginButton = (LoginButton) findViewById(R.id.login_button);
//        loginButton.setBackgroundResource(R.drawable.facebook);
//        loginButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        initComponents();
        loginButton.setReadPermissions(Arrays.asList("public_profile", "edemail", "user_birthday", "user_friends", "user_location", "user_about_me", "user_hometown"));
        ctx = this;
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.w("meniuu", "callback:" + object);
                        try {
                            Log.w("meniuu","obj.getemail:"+object.getString("edemail"));
                            edemail.setText(object.getString("edemail"));
                            edName.setText(object.getString("name"));
                            edCity.setText(object.getString("edCity"));
                        } catch (JSONException e) {
                            Log.w("meniuu","catch");
                            e.printStackTrace();
                        }
//                        loginButton.setVisibility(View.GONE);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,edemail,gender,birthday,location");
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

    /**
     *initializing components
     */
    public void initComponents(){
        edName =(EditText)findViewById(R.id.nume_login);
        edNickname =(EditText)findViewById(R.id.nick_name_login);
        edemail =(EditText)findViewById(R.id.email_login);
        edDriverLicense =(EditText)findViewById(R.id.driver_license_login);
        edCity =(EditText)findViewById(R.id.city_login);
        rlNext =(RelativeLayout) findViewById(R.id.continuare);
        rlNext.setOnClickListener(this);
    }

    /**
     *  onclick method
     * @param view
     */
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.continuare:
            postUser();

            break;
        }
    }

    /**
     * post token, password and device ip
     */
    public void postToken(String user_id, final String token) {
    prefs = new SecurePreferences(ctx);
    WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
    final String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    String url = Constants.URL + "users/addSecurity/"+user_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
//                        postUser();
                    }
                }, ErrorListener) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("device_token", prefs.getString("phone_token", ""));
                params.put("password", "nuamideecepltrebeaici");
                params.put("reg_ip", ip);
                return params;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization","Bearer "+ token);
                return params;
            }
        };
        queue.add(stringRequest);
}
    /**
     * post user method
     */
    public void postUser(){
        final SharedPreferences prefs = new SecurePreferences(ctx);
        String url = Constants.URL+"signup/user";
        if(edName.getText().length()==0 || edNickname.getText().length()==0 || edemail.getText().length()==0 || edDriverLicense.getText().length()==0 || edCity.getText().length()==0)
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
                                postToken( obj.getString("userID"),token.getString("value"));
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
                    params.put("first_name", edName.getText().toString());
                    params.put("last_name", edName.getText().toString());
                    params.put("edNickname", edNickname.getText().toString());
                    params.put("edemail", edemail.getText().toString());
                    params.put("edDriverLicense", edDriverLicense.getText().toString());
                    params.put("photo", "photo");
                    params.put("platform", "Android");
                    params.put("user_city", edCity.getText().toString());
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
