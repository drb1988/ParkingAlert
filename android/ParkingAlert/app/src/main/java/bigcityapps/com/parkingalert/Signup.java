package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
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

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import Util.SecurePreferences;

/**
 * Created by fasu on 08/10/2016.
 */
public class Signup extends Activity implements View.OnClickListener {
    SharedPreferences prefs;
    RequestQueue queue;
    Context ctx;
    EditText edFname, edLname, edEmail, edCheckEmail, edPassword;
    TextInputLayout inputCheckEmail, inputPassword;
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
public void FluiEdittext(){
    edFname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                hideKeyboard(v);
            }else
                showKeyboard(edFname);
        }
    });
    edLname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                hideKeyboard(v);
            }else
                showKeyboard(edLname);
        }
    });
    edCheckEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                {
                    hideKeyboard(v);
                    if(edEmail.getText().toString().equals(edCheckEmail.getText().toString())) {

                        inputCheckEmail.setErrorEnabled(false);
                        Log.w("meniuu","nu este bun");
                    }
                    else {   inputCheckEmail.setError("Emailul nu este bun");

                        Log.w("meniuu","dispate");
                    }
                }
            }else
                showKeyboard(edCheckEmail);
        }
    });
    edPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                {
                    hideKeyboard(v);
                    if(edPassword.getText().length()<6)
                        inputPassword.setError("Parola trebuie sa fie de cel putin 6 caractere");
                    else
                        inputPassword.setErrorEnabled(false);
                        
                }
            }else
                showKeyboard(edPassword);
        }
    });
    edEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                hideKeyboard(v);
            }else
                showKeyboard(edEmail);
        }
    });

}
    public void initComponents() {
        inputCheckEmail =(TextInputLayout)findViewById(R.id.input_verifica_email);
        inputPassword =(TextInputLayout)findViewById(R.id.inputPassword);
        edFname = (EditText) findViewById(R.id.nume);
        edLname = (EditText) findViewById(R.id.prenume);
        edEmail = (EditText) findViewById(R.id.email);
        edCheckEmail = (EditText) findViewById(R.id.verifica_email);
        edPassword = (EditText) findViewById(R.id.parola);
        tvSignup = (TextView) findViewById(R.id.inregistreazate);
        rlBack = (RelativeLayout) findViewById(R.id.back_signup);
        rlBack.setOnClickListener(this);
        rlLogin = (RelativeLayout) findViewById(R.id.login_signup);
        rlLogin.setOnClickListener(this);


    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_signup:
                finish();
                break;

            case R.id.login_signup:

                break;
            case R.id.inregistreazate:

                break;
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
    //check email validation
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}
