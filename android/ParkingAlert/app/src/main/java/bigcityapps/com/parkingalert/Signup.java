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
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 08/10/2016.
 */
public class Signup extends Activity implements View.OnClickListener {
    SharedPreferences prefs;
    RequestQueue queue;
    Context ctx;
    EditText edFname, edLname, edEmail, edCheckEmail, edPassword;
    TextInputLayout inputCheckEmail, inputPassword, inputEmail;
    TextView tvSignup;
    RelativeLayout rlBack, rlLogin;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        initComponents();
        FluiEdittext();

    }

    public void FluiEdittext() {
        edFname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                } else
                    showKeyboard(edFname);
            }
        });
        edLname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                } else
                    showKeyboard(edLname);
            }
        });
        edCheckEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    {
                        hideKeyboard(v);
                        if (edEmail.getText().toString().equals(edCheckEmail.getText().toString())) {
                            inputCheckEmail.setErrorEnabled(false);
                        } else {
                            inputCheckEmail.setError("Emailul nu este bun");
                        }
                    }
                } else
                    showKeyboard(edCheckEmail);
            }
        });
        edPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    {
                        hideKeyboard(v);
                        if (edPassword.getText().length() < 6)
                            inputPassword.setError("Parola trebuie sa fie de cel putin 6 caractere");
                        else
                            inputPassword.setErrorEnabled(false);

                    }
                } else
                    showKeyboard(edPassword);
            }
        });
        edEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                } else
                    showKeyboard(edEmail);
            }
        });

    }

    public void initComponents() {
        inputEmail = (TextInputLayout) findViewById(R.id.input_email);
        inputCheckEmail = (TextInputLayout) findViewById(R.id.input_verifica_email);
        inputPassword = (TextInputLayout) findViewById(R.id.inputPassword);
        edFname = (EditText) findViewById(R.id.nume);
        edLname = (EditText) findViewById(R.id.prenume);
        edEmail = (EditText) findViewById(R.id.email);
        edCheckEmail = (EditText) findViewById(R.id.verifica_email);
        edPassword = (EditText) findViewById(R.id.parola);
        tvSignup = (TextView) findViewById(R.id.inregistreazate);
        tvSignup.setOnClickListener(this);
        rlBack = (RelativeLayout) findViewById(R.id.back_signup);
        rlBack.setOnClickListener(this);
        rlLogin = (RelativeLayout) findViewById(R.id.login_signup);
        rlLogin.setOnClickListener(this);
        edEmail.addTextChangedListener(new MyTextWatcher(edEmail));
        edPassword.addTextChangedListener(new MyTextWatcher(edPassword));

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_signup:
                finish();
                break;

            case R.id.login_signup:
                Intent signup = new Intent(Signup.this, LoginNew.class);
                startActivity(signup);
                finish();
                break;
            case R.id.inregistreazate:
                if (validateEmail() && edEmail.getText().toString().equals(edCheckEmail.getText().toString()))
                    sendEmailVerification(edEmail.getText().toString());
                else
                    Toast.makeText(ctx, "Email incorect", Toast.LENGTH_LONG).show();
                break;
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

    //check email validation
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
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
                case R.id.parola:
                    validatePassword();
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
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
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

    private boolean validatePassword() {
        String password = edPassword.getText().toString().trim();
        if (password.length() < 6) {
            inputPassword.setError(getString(R.string.err_msg_email));
            requestFocus(edPassword);
            return false;
        } else {
            inputPassword.setErrorEnabled(false);
        }
        return true;
    }

    public void sendEmailVerification(final String email) {
        String url = Constants.URL + "signup/sendEmailVerification";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        try {
                            JSONObject user = new JSONObject(json);
                            String verificationId = user.getString("verificationID");
                            Log.w("meniuu", "response:email verification" + response);
                            Intent signup = new Intent(Signup.this, EmailValidation.class);
                            signup.putExtra("nume", edFname.getText().toString());
                            signup.putExtra("prenume", edLname.getText().toString());
                            signup.putExtra("parola", edPassword.getText().toString());
                            signup.putExtra("verificationId", verificationId);
                            signup.putExtra("email", edEmail.getText().toString());
                            startActivity(signup);
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

    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
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
