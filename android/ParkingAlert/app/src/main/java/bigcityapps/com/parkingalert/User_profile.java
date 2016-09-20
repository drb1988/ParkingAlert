package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by fasu on 20/09/2016.
 */
public class User_profile extends Activity {
    Context ctx;
    ImageView poza_patrata_user_profile,poza_rotunda_user_profile;
    EditText nume, nickname, mobile, email, driver_license, city, country;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.user_profile);
        ctx = this;
        initComponents();
    }
    public void initComponents(){
        poza_patrata_user_profile=(ImageView)findViewById(bigcityapps.com.parkingalert.R.id.poza_patrata_user_profile);
        poza_rotunda_user_profile=(ImageView)findViewById(bigcityapps.com.parkingalert.R.id.poza_rotunda_user_profile);
        nume=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.numele_user_profile);
        nickname=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.nick_name);
        mobile=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.mobile_user_profile);
        email=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.email);
        driver_license=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.driver_license_user_profile);
        city=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.city_user_profile);
        country=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.country_user_profile);
    }
}
