package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 19/09/2016.
 */
public class ViewCar extends Activity implements View.OnClickListener {
    Context ctx;
    TextView tv_numele_masina, tv_nr;
    RelativeLayout rlBack, rlModify, rlViewQr;
    RequestQueue queue;
    String mPlatesOriginal;
    SharedPreferences prefs;
    Switch switch_cars, switch_other;
    TextView title;
    TextView ed_autovehicul;
    String qr_code;
    boolean enable_notifications, enable_others;
    String mMaker, mModel, mYear, mPlates, mImage;
    Bitmap bmp;
    ImageView imQrcode;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_car);
        initcomponents();
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            mMaker = b.getString("edMaker");
            mModel = b.getString("edModel");
            mYear = b.getString("edYear");
            mPlates = b.getString("edNr");
            mPlatesOriginal = (String) b.get("edNr");
            mImage = b.getString("image");
            tv_numele_masina.setText((String) b.get("edname"));
            qr_code = b.getString("qr_code");
            tv_nr.setText((String) b.get("edNr"));
            String a = "";
            if (!b.getString("edYear").equals("null"))
                a = a + b.getString("edYear");
            if (!b.getString("edMaker").equals("null"))
                a = a + " " + b.getString("edMaker");
            if (!b.getString("edModel").equals("null"))
                a = a + " " + b.getString("edModel");
            ed_autovehicul.setText(a);
            enable_notifications = b.getBoolean("enable_notifications");
            enable_others = b.getBoolean("enable_others");
            switch_cars.setChecked(enable_notifications);
            switch_other.setChecked(enable_others);

            switch_other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ctx);
                        builder.setTitle("Informare");
                        builder.setMessage("Acuma se mai poate adauga si altcineva la aceasta masina, scanand QR codul");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                        android.support.v7.app.AlertDialog alert1 = builder.create();
                        alert1.show();
                    }
                }
            });
            title.setText(mPlatesOriginal);
            Constants.plates=mPlatesOriginal;
            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(qr_code, BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                imQrcode.setImageBitmap(bmp);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    public void initcomponents() {
        imQrcode=(ImageView)findViewById(R.id.poza_masina);
        imQrcode.setOnClickListener(this);
        title = (TextView) findViewById(R.id.title);
        switch_cars = (Switch) findViewById(R.id.switch_cars);
        switch_other = (Switch) findViewById(R.id.switch_other);
        tv_numele_masina = (TextView) findViewById(R.id.et_numele_masina);
        tv_nr = (TextView) findViewById(R.id.et_nr);
        ed_autovehicul = (TextView) findViewById(R.id.ed_autovehicul);
        rlBack = (RelativeLayout) findViewById(R.id.inapoi_vizualizare_masina);
        rlModify = (RelativeLayout) findViewById(R.id.modify_view_car);
        rlViewQr = (RelativeLayout) findViewById(R.id.view_qr);
        rlViewQr.setOnClickListener(this);
        rlBack.setOnClickListener(this);
        rlModify.setOnClickListener(this);
        switch_cars.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    changeNotification("enableNotifications");
                else
                    changeNotification("disableNotifications");
            }
        });
        switch_other.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    changeAllowOthers(true);
                else
                    changeAllowOthers(false);
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inapoi_vizualizare_masina:
                finish();
//    Intent rlBack= new Intent(ViewCar.this, MainActivity.class);
//        startActivity(rlBack);
                break;

            case R.id.modify_view_car:
                Intent update = new Intent(ViewCar.this, ModifyCar.class);
                update.putExtra("edYear", mYear);
                update.putExtra("edname", tv_numele_masina.getText());
                update.putExtra("edNr", mPlates);
                update.putExtra("edMaker", mMaker);
                update.putExtra("edModel", mModel);
                update.putExtra("edYear", mYear);
                update.putExtra("image", mYear);
                startActivity(update);
//            updateCars(prefs.getString("user_id",""));

//        Intent salvare= new Intent(ViewCar.this, MainActivity.class);
//        startActivity(salvare);
                break;
//            case R.id.view_qr:
//                Log.w("meniuu", "a intrat in view_qr");
//                Intent viewQR = new Intent(ViewCar.this, ViewQr.class);
//                viewQR.putExtra("mPlates", mPlatesOriginal);
//                viewQR.putExtra("mQrcode", qr_code);
//                startActivity(viewQR);
//                break;
            case R.id.poza_masina:
                Intent viewQR = new Intent(ViewCar.this, ViewQr.class);
                viewQR.putExtra("mPlates", mPlatesOriginal);
                viewQR.putExtra("mQrcode", qr_code);
                startActivity(viewQR);
                break;
        }
    }

//    public void updateCars(final String id) {
//        String url = Constants.URL + "users/editCar/" + id + "&" + mPlatesOriginal;
//        Log.w("meniuu", "url:" + url);
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
//                    new Response.Listener<String>() {
//                        public void onResponse(String response) {
//                            String json = response;
//                            Log.w("meniuu", "response:post user" + response);
//                            finish();
//                        }
//                    }, ErrorListener) {
//                protected Map<String, String> getParams() {
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("plates", tv_nr.getText().toString());
//                    params.put("given_name", tv_numele_masina.getText().toString());
//                    params.put("make", tv_producator.getText().toString());
//                    params.put("edModel", tv_model.getText().toString());
//                    params.put("year", tv_an_producti.getText().toString());
//                    if (switch_cars.isChecked())
//                        params.put("enable_notifications", true + "");
//                    else
//                        params.put("enable_notifications", false + "");
//
//                    return params;
//                }
//
//                public Map<String, String> getHeaders() throws AuthFailureError {
//                    String auth_token_string = prefs.getString("token", "");
//                    Map<String, String> params = new HashMap<String, String>();
//                    params.put("Authorization", "Bearer " + auth_token_string);
//                    return params;
//                }
//            };
//            queue.add(stringRequest);
//    }

    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
        }
    };

    public void changeNotification(String action) {
        String url = Constants.URL + "users/" + action + "/" + prefs.getString("user_id", "") + "&" + mPlates;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                Log.w("meniuu", "response viewcar:" + json);
            }
        }, ErrorListener) {
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Log.w("meniuu", "token:" + auth_token_string);
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void changeAllowOthers(boolean allow) {
        String url = Constants.URL + "users/allowOthers/" + prefs.getString("user_id", "") + "&" + mPlates + "&" + allow;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        try {
                            Log.w("meniuu", "response alloothers:" + json);
                        } catch (Exception e) {
                        }
                    }
                }, ErrorListener) {
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Log.w("meniuu", "token:" + auth_token_string);
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
