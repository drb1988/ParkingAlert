package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by Sistem1 on 11/10/2016.
 */
public class PasswordReset extends Activity implements View.OnClickListener {
    SharedPreferences prefs;
    RequestQueue queue;
    Context ctx;
    RelativeLayout back;
    TextView send;
    TextInputLayout inputEmail;
    EditText edEmail;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_reset);
        queue = Volley.newRequestQueue(this);
        ctx = this;
        prefs = new SecurePreferences(ctx);
        initComponents();
        edEmail.addTextChangedListener(new MyTextWatcher(edEmail));
    }
    public void initComponents(){
        inputEmail=(TextInputLayout)findViewById(R.id.input_email);
        edEmail=(EditText)findViewById(R.id.email);
        back=(RelativeLayout)findViewById(R.id.back_password_reset);
        back.setOnClickListener(this);
        send=(TextView)findViewById(R.id.password_reset);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_password_reset:
                finish();
                break;
            case R.id.password_reset:
                if(validateEmail())
                    facebookLogin(edEmail.getText().toString());
                break;
        }
    }

    public void facebookLogin( final String email){
        String url = Constants.URL+"signup/resetPassword/"+email;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        try {
                            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                            builder.setTitle("Vei primi pe email noua parola");
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent continuare = new Intent(PasswordReset.this, LoginNew.class);
                                    startActivity(continuare);
                                    finish();
                                    dialog.dismiss();
                                }
                            });
                            AlertDialog alert1 = builder.create();
                            alert1.show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, ErrorListener) {
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
            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Eroare de server");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            AlertDialog alert1 = builder.create();
            alert1.show();
        }
    };
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
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
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
