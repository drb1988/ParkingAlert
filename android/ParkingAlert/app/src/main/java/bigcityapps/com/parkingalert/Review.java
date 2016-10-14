package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import Util.SecurePreferences;

/**
 * Created by fasu on 06/10/2016.
 */
public class Review extends Activity implements View.OnClickListener{
    Context ctx;
    RequestQueue queue;
    SharedPreferences prefs;
    RelativeLayout inapoi,anuleaza, nu, da;
    String mNotification_id, mPlates;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review);
        initcomponents();
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null) {
            mNotification_id = (String) b.get("notification_id");
            mPlates = (String) b.get("mPlates");
        }
    }
    public void initcomponents(){
        inapoi=(RelativeLayout)findViewById(R.id.inapoi_review);
        inapoi.setOnClickListener(this);
        anuleaza=(RelativeLayout)findViewById(R.id.anuleaza_review);
        anuleaza.setOnClickListener(this);
        nu=(RelativeLayout)findViewById(R.id.nu_review);
        nu.setOnClickListener(this);
        da=(RelativeLayout)findViewById(R.id.da_review);
        da.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.inapoi_review:
                finish();
                break;
            case R.id.anuleaza_review:
                finish();
                break;
            case R.id.nu_review:

                break;

            case R.id.da_review:

                break;

}

    }
}
