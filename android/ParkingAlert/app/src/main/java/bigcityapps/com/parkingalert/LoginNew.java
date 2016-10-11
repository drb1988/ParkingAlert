package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

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
    TextView tvLogin, tvPasswordForget;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    TextInputLayout inputEmail, inputPassword;
    Context ctx;
    RelativeLayout rlBack, rlSignup;
    String mFirstName, mEmail, mFacebookId,mLastName;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.intra_in_cont);
        queue = Volley.newRequestQueue(this);
        ctx = this;
        prefs = new SecurePreferences(ctx);
        initComponents();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends", "user_location", "user_about_me", "user_hometown"));
        callbackManager = CallbackManager.Factory.create();
        edEmail.addTextChangedListener(new MyTextWatcher(edEmail));


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
                            Toast.makeText(ctx,"Eroare la conectarea cu Facebook-ul",Toast.LENGTH_LONG).show();
                            Log.w("meniuu", "catch");
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
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    public void initComponents() {
        inputEmail = (TextInputLayout) findViewById(R.id.input_email);
        inputPassword = (TextInputLayout) findViewById(R.id.input_password);
        edEmail = (EditText) findViewById(R.id.email);
        edPassword = (EditText) findViewById(R.id.parola);
        tvLogin = (TextView) findViewById(R.id.intra_in_cont);
        tvLogin.setOnClickListener(this);
        tvPasswordForget = (TextView) findViewById(R.id.forget_password);
        tvPasswordForget.setOnClickListener(this);
        rlBack = (RelativeLayout) findViewById(R.id.inapoi_intra_in_cont);
        rlBack.setOnClickListener(this);
        rlSignup = (RelativeLayout) findViewById(R.id.inregistrare_intra_in_cont);
        rlSignup.setOnClickListener(this);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.intra_in_cont:
                login();
                break;
            case R.id.forget_password:
                Intent reset=new Intent(LoginNew.this, PasswordReset.class);
                    startActivity(reset);
                break;
            case R.id.inapoi_intra_in_cont:
                finish();
                break;
            case R.id.inregistrare_intra_in_cont:
                Intent login = new Intent(LoginNew.this, Signup.class);
                startActivity(login);
                finish();
                break;
        }

    }

    //check email validation
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard(EditText ed) {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(ed, 0);
    }

    public void login() {
        if (validateEmail()) {
            String url = Constants.URL + "signup/login/" + edEmail.getText().toString().trim() + "&" + edPassword.getText().toString();
            Log.w("meniuu", "email:" + edEmail.getText() + " pass:" + edPassword.getText() + " url:" + url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            String json = response;
                            try {
                                Log.w("meniuu", "response la login" + json);
                                JSONObject obj = new JSONObject(json);
                                JSONObject token = new JSONObject(obj.getString("token"));
                                prefs.edit().putString("user_id", obj.getString("userID")).commit();
                                prefs.edit().putString("token", token.getString("value")).commit();
                                Intent continuare = new Intent(LoginNew.this, MainActivity.class);
                                startActivity(continuare);
                                finish();
                            } catch (Exception e) {
                                try {
                                    JSONObject obj = new JSONObject(json);
                                    Toast.makeText(ctx,obj.getString("error"),Toast.LENGTH_LONG).show();
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
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
        } else
            Toast.makeText(ctx, "Complectati doate campurile!", Toast.LENGTH_LONG).show();
    }

    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
        }
    };

    private class MyTextWatcher implements TextWatcher {
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }
        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.email:
                    validateEmail();
                    break;
            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateEmail() {
        String email = edEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputEmail.setError(getString(R.string.err_msg_email));
            requestFocus(edEmail);
            return false;
        } else {
            inputEmail.setErrorEnabled(false);
        }
        return true;
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
                            Intent continuare = new Intent(LoginNew.this, MainActivity.class);
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
}
