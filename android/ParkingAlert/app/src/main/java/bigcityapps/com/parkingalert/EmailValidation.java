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

import com.android.volley.RequestQueue;
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.email_validation);
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
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
        inputLayout =(TextInputLayout)findViewById(R.id.input_email);
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
                Intent mainactivity=new Intent(EmailValidation.this, MainActivity.class);
                startActivity(mainactivity);
                break;
            case R.id.resend_email:

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
}
