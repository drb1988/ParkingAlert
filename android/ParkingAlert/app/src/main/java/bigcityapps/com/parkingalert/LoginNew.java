package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import Util.Constants;
import Util.SecurePreferences;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Sistem1 on 08/10/2016.
 */
public class LoginNew extends Activity implements View.OnClickListener {
    SharedPreferences prefs;
    RequestQueue queue;
    EditText edEmail, edPassword;
    TextView tvLogin,tvPasswordForget;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    TextInputLayout inputEmail, inputPassword;
    Context ctx;
    RelativeLayout rlBack, rlSignup;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.intra_in_cont);
        queue = Volley.newRequestQueue(this);
        ctx=this;
        prefs = new SecurePreferences(ctx);
        initComponents();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends", "user_location", "user_about_me", "user_hometown"));
        callbackManager = CallbackManager.Factory.create();

//        edPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (!hasFocus) {
//                    {
//                        hideKeyboard(v);
//                        if(edPassword.getText().length()<6)
//                            inputPassword.setError("Parola trebuie sa fie de cel putin 6 caractere");
//                        else
//                            inputPassword.setErrorEnabled(false);
//
//                    }
//                }else
//                    showKeyboard(edPassword);
//            }
//        });

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.w("meniuu", "callback:" + object);
                        try {
                            Log.w("meniuu","oemail"+object.getString("email"));
                            Log.w("meniuu","nume"+object.getString("name"));
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
    public void initComponents(){
        inputEmail=(TextInputLayout)findViewById(R.id.input_email);
        inputPassword=(TextInputLayout)findViewById(R.id.input_password);
        edEmail=(EditText)findViewById(R.id.email);
        edPassword=(EditText)findViewById(R.id.parola);
        tvLogin=(TextView) findViewById(R.id.intra_in_cont);
        tvLogin.setOnClickListener(this);
        tvPasswordForget=(TextView)findViewById(R.id.forget_password);
        tvPasswordForget.setOnClickListener(this);
        rlBack=(RelativeLayout)findViewById(R.id.inapoi_intra_in_cont);
        rlBack.setOnClickListener(this);
        rlSignup=(RelativeLayout)findViewById(R.id.inregistrare_intra_in_cont);
        rlSignup.setOnClickListener(this);

    }
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.intra_in_cont:
            login();
            break;
        case R.id.forget_password:

            break;
        case R.id.inapoi_intra_in_cont:
            finish();
            break;
        case R.id.inregistrare_intra_in_cont:

            break;
    }

    }
    //check email validation
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void showKeyboard(EditText ed) {
        InputMethodManager keyboard = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(ed, 0);
    }

    public void login() {
        String url = Constants.URL + "signup/login/"+edEmail.getText().toString().trim()+"&"+edPassword.getText().toString();
        Log.w("meniuu","email:"+edEmail.getText()+" pass:"+edPassword.getText()+" url:"+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        try {
                            JSONObject obj = new JSONObject(json);
                            JSONObject token= new JSONObject(obj.getString("token"));
                            prefs.edit().putString("user_id", obj.getString("userID")).commit();
                            prefs.edit().putString("token", token.getString("value")).commit();
                            Intent continuare= new Intent(LoginNew.this, MainActivity.class);
                            startActivity(continuare);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                        Snackbar snackbar = Snackbar
//                                .make(coordinatorLayout, "Masina a fost stearsa!", Snackbar.LENGTH_LONG);
////                                    .setAction("SETARI", new View.OnClickListener() {
////                                        public void onClick(View view) {
////                                            startActivityForResult(new Intent(android. provider.Settings.ACTION_SETTINGS), 0);
////                                        }
////                                    });
//                        snackbar.show();
                    }
                }, ErrorListener) {
//            protected java.util.Map<String, String> getParams() {
//                java.util.Map<String, String> params = new HashMap<String, String>();
//                params.put("email", email);
//                return params;
//            }
        };
        queue.add(stringRequest);
    }
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
        }
    };
}
