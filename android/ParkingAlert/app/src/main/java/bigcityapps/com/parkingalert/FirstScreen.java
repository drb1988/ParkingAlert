package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import Util.Constants;
import Util.SecurePreferences;
import io.fabric.sdk.android.Fabric;

/**
 * Created by fasu on 08/10/2016.
 */
public class FirstScreen extends Activity implements View.OnClickListener {
    Context ctx;
    SharedPreferences prefs;
    RequestQueue queue;
    TextView mLogin, mSignupEmail;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    String mFirstName, mEmail, mFacebookId,mLastName;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
//        AppEventsLogger.activateApp(this);
        setContentView(R.layout.first_screen);
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        initComponents();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends", "user_location", "user_about_me", "user_hometown"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.w("meniuu", "callback:" + object);
                        try {
                            Log.w("meniuu", "mail" + object.getString("email"));
                            Log.w("meniuu", "nume" + object.getString("name"));
                            if(object.getString("name").contains(" ")) {
                                try {
                                    String a[] = object.getString("name").split(" ");
                                    mFirstName = a[0];
                                    mLastName = a[1];
                                }catch (Exception e){
                                    mFirstName=object.getString("name");
                                    e.printStackTrace();
                                }
                            }
                             mEmail=object.getString("email");
                            mFacebookId=object.getString("id");
                            facebookLogin(mFirstName,mLastName, mFacebookId, mEmail);

                        } catch (JSONException e) {
                            Log.w("meniuu", "catch");
                            Toast.makeText(ctx,"Eroare la conectarea cu Facebook-ul",Toast.LENGTH_LONG).show();
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
                Toast.makeText(ctx,"cancel",Toast.LENGTH_LONG).show();
                Log.w("meniuu", "on cancel");
            }

            public void onError(FacebookException e) {
                e.printStackTrace();
                Toast.makeText(ctx,"FacebookException",Toast.LENGTH_LONG).show();
                Log.w("meniuu", "facebookexception");
                e.printStackTrace();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public void initComponents() {
        mLogin = (TextView) findViewById(R.id.intra_in_cont);
        mLogin.setOnClickListener(this);
        mSignupEmail = (TextView) findViewById(R.id.inregistreazate_email);
        mSignupEmail.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inregistreazate_email:
                Intent signup = new Intent(FirstScreen.this, Signup.class);
                startActivity(signup);
                break;
            case R.id.intra_in_cont:
                Intent login = new Intent(FirstScreen.this, LoginNew.class);
                startActivity(login);
                break;

        }
    }

    public void facebookLogin(final String fname, final String lname, final String facebookID, final String email){
        String url = Constants.URL+"signup/facebookLogin";
        Log.w("meniuu","fname:"+fname+" lname:"+lname+" facebookid:"+facebookID+" email:"+email);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            String json = response;
                            try {
                                Log.w("meniuu", "response la login facebook in firstscreen" + json);
                                JSONObject obj = new JSONObject(json);
                                JSONObject token = new JSONObject(obj.getString("token"));
                                prefs.edit().putString("user_id", obj.getString("userID")).commit();
                                prefs.edit().putString("token", token.getString("value")).commit();
                                Intent continuare = new Intent(FirstScreen.this, MainActivity.class);
                                startActivity(continuare);
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, ErrorListener) {
                protected java.util.Map<String, String> getParams() {
                    java.util.Map<String, String> params = new HashMap<String, String>();
                    params.put("first_name", fname);
                    params.put("last_name", lname);
                    params.put("email", email);
                    params.put("platform", "Android");
                    params.put("facebookID", facebookID);
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
            AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
            alertDialog.setTitle("Error");
            alertDialog.setMessage("Server error");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    };
}
