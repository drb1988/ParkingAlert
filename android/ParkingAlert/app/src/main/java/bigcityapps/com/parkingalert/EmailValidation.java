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
import android.view.View;
import android.view.WindowManager;
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

import java.util.*;

import Util.Constants;
import Util.SecurePreferences;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Sistem1 on 10/10/2016.
 */
public class EmailValidation extends Activity implements View.OnClickListener {
    Context ctx;
    RequestQueue queue;
    SharedPreferences prefs;
    RelativeLayout rlBack;
    EditText edValidationCode, edEmail;
    TextView tvValidation, tvResendEmail;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    TextInputLayout inputLayout;
    String nume, prenume, parola, verificationId, email;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.email_validation);
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            nume = (String) b.get("nume");
            prenume = (String) b.get("prenume");
            parola = (String) b.get("parola");
            verificationId = (String) b.get("verificationId");
            email = (String) b.get("email");
        }

        initComponents();
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "edemail", "user_birthday", "user_friends", "user_location", "user_about_me", "user_hometown"));
        callbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.w("meniuu", "callback:" + object);
                        try {
                            Log.w("meniuu", "oemail" + object.getString("edemail"));
                            Log.w("meniuu", "nume" + object.getString("name"));
                        } catch (JSONException e) {
                            Log.w("meniuu", "catch");
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

    public void initComponents() {
        inputLayout = (TextInputLayout) findViewById(R.id.input_email);
        rlBack = (RelativeLayout) findViewById(R.id.back_email_validation);
        rlBack.setOnClickListener(this);
        edValidationCode = (EditText) findViewById(R.id.validation_code);
        edEmail = (EditText) findViewById(R.id.email);
        edEmail.addTextChangedListener(new MyTextWatcher(edEmail));
        tvValidation = (TextView) findViewById(R.id.email_validation);
        tvValidation.setOnClickListener(this);
        tvResendEmail = (TextView) findViewById(R.id.resend_email);
        tvResendEmail.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_email_validation:
                finish();
                break;
            case R.id.email_validation:
                verifyEmail(email, verificationId);
                break;
            case R.id.resend_email:
                if(validateEmail())
                sendEmailVerification(edEmail.getText().toString());
                break;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean validateEmail() {
        String email = edEmail.getText().toString().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            inputLayout.setError(getString(R.string.err_msg_email));
            requestFocus(edEmail);
            return false;
        } else {
            inputLayout.setErrorEnabled(false);
        }

        return true;
    }

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

    public void verifyEmail(final String email, final String token) {
        String url = Constants.URL + "signup/sendEmailVerification";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response verify email:" + json);
                        postUser();
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
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("token", token);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
        }
    };

    public void postUser(){
        final SharedPreferences prefs = new SecurePreferences(ctx);
        String url = Constants.URL+"signup/user";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            String json = response;
                            try {
                                JSONObject obj = new JSONObject(json);
                                JSONObject token= new JSONObject(obj.getString("token"));
                                prefs.edit().putString("user_id", obj.getString("userID")).commit();
                                prefs.edit().putString("token", token.getString("value")).commit();
                                Intent mainactivity = new Intent(EmailValidation.this, MainActivity.class);
                                mainactivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainactivity);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.w("meniuu","catch la response, signup");
                                Toast.makeText(ctx,"erro",Toast.LENGTH_LONG).show();
                            }
                            Log.w("meniuu", "response:post user" + response);

                        }
                    }, ErrorListener) {
                protected java.util.Map<String, String> getParams() {
                    java.util.Map<String, String> params = new HashMap<String, String>();
                    params.put("first_name", nume);
                    params.put("last_name", prenume);
                    params.put("edNickname", nume+prenume);
                    params.put("edemail", email);
                    params.put("password", parola);
//                    params.put("photo", "photo");
                    params.put("platform", "Android");
                    return params;
                }
            };
            queue.add(stringRequest);
    }
    public void sendEmailVerification(final String email) {
        String url = Constants.URL + "signup/sendEmailVerification";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        try {
                            JSONObject user = new JSONObject(json);
                            verificationId=user.getString("verificationID");
                            Log.w("meniuu", "response:email verification" + response);
                            Toast.makeText(ctx,"A fost trimisa cu succes", Toast.LENGTH_LONG).show();
                            edEmail.setText("");
                            inputLayout.setErrorEnabled(false);
                            requestFocus(edValidationCode);
                        } catch (JSONException e) {
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
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
