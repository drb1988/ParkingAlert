package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
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

import java.util.Arrays;

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
                        Toast.makeText(ctx,"callback",Toast.LENGTH_LONG).show();
                        try {
                            Log.w("meniuu", "mail" + object.getString("email"));
                            Log.w("meniuu", "nume" + object.getString("name"));
                        } catch (JSONException e) {
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
}
