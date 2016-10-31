package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import Util.Constants;
import Util.SecurePreferences;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class SimpleScannerActivity extends Activity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    RequestQueue queue;
    Context ctx;
    SharedPreferences prefs;
    String latitude,longitude;
    String user_id, plates;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        queue = Volley.newRequestQueue(this);
        ctx = this;
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        if(b!=null)
        {
            latitude =(String) b.get("lat");
            longitude=(String) b.get("lng");
        }
        prefs = new SecurePreferences(this);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);
        Log.w("meniuu","oncreate simplescanneractivity");// Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.w("meniuu","resultat");
        if(rawResult.getContents().contains(" ")){
            setContentView(R.layout.test);
            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("Codul QR nu este intr-un format acceptat de aplicatie");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
//                                Intent harta= new Intent(Scan.this, Map.class);
//                                startActivity(harta);
                    finish();
                }
            });
//                        builder.setmMessage(rawResult.getText());
            AlertDialog alert1 = builder.create();
            alert1.show();
        }else
        getUsersForCode(rawResult.getContents());
    }
    public void postNotification(final String receiver_id){
        String url = Constants.URL+"notifications/notification";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:post notification" + response);
                        Intent harta= new Intent(SimpleScannerActivity.this, MainActivity.class);
                        startActivity(harta);
                        finish();
                    }
                }, ErrorListener) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("status", "1");
                params.put("is_active", "false");
                params.put("latitude",latitude);
                params.put("longitude",longitude);
                params.put("vehicle", plates);
                params.put("sender_id", prefs.getString("user_id",""));
                params.put("receiver_id", receiver_id);
                Log.w("meniuu","lat+"+latitude+" lang:"+longitude);
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
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            setContentView(R.layout.test);
            Log.w("meniuu","error volley:"+error.getMessage());
            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("A aparut o eroare" + "Va rog sa reincercati");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            AlertDialog alert1 = builder.create();
            alert1.show();
        }
    };
    public void getUsersForCode(String qrcode) {
        Log.w("meniuu","qrcode: in getusersforcode:"+qrcode);
        String url = Constants.URL + "users/getUsersForCode/" + qrcode;
        Log.w("meniuu","url:"+url+" in getusersforcode");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                Log.w("meniuu", "response: getusersforcode" + response);
                try {
                    JSONArray obj = new JSONArray(json);
                    if(obj.length()!=0) {
                        for (int i = 0; i < obj.length(); i++) {
                            JSONObject c = obj.getJSONObject(i);
                            JSONObject car = new JSONObject(c.getString("car"));
                            user_id = c.getString("userID");
                            plates = car.getString("plates");
                            Log.w("meniuu", "user_id:" + user_id + " se apeleaza postnotification");
                            postNotification(user_id);
                        }
                    }else {
                        Log.w("meniuu","nu exista userul");
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setTitle("Aceasta masina nu poate fi notificata");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        AlertDialog alert1 = builder.create();
                        alert1.show();
                    }
                } catch (Throwable t) {
                    Log.w("meniuu", "cacth get questions");
                    if(plates.equals("disabled"))
                    {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setTitle("Aceasta masina nu poate fi notificata");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        AlertDialog alert1 = builder.create();
                        alert1.show();
                    }
                    t.printStackTrace();
                }
            }
        }, ErrorListener) {
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}