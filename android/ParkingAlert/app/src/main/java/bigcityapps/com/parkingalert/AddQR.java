package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

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
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class AddQR extends Activity implements ZBarScannerView.ResultHandler {
    private ZBarScannerView mScannerView;
    RequestQueue queue;
    Context ctx;
    SharedPreferences prefs;
    String latitude, longitude;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        queue = Volley.newRequestQueue(this);
        ctx = this;
//        Intent iin= getIntent();
//        Bundle b = iin.getExtras();
//
//        if(b!=null)
//        {
//            latitude =(String) b.get("lat");
//            longitude=(String) b.get("lng");
//        }
        prefs = new SecurePreferences(this);
        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view
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
        Log.w("meniuu", "resultat");
        // Do something with the result here
//        Log.v(TAG, rawResult.getContents()); // Prints scan results
//        Log.v(TAG, rawResult.getBarcodeFormat().getName()); // Prints the scan format (qrcode, pdf417 etc.)
//        postNotification(rawResult.getContents());

        Intent harta = new Intent(AddQR.this, ShowQRCode.class);
        harta.putExtra("qrcode", rawResult.getContents());
        startActivity(harta);
        finish();


        // If you would like to resume scanning, call this method below:
//        mScannerView.resumeCameraPreview(this);
    }

    public void postNotification(final String receiver_id) {
        String url = Constants.URL + "notifications/notification";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setTitle("BH12ZEU a fost notificat cu succes");
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
                        Log.w("meniuu", "response:post notification" + response);
//                        Intent harta= new Intent(Scan.this, Map.class);
//                        startActivity(harta);
//                        finish();
                    }
                }, ErrorListener) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("status", "1");
                params.put("is_active", "false");
                params.put("mLatitude", latitude);
                params.put("mLongitude", longitude);
                params.put("vehicle", "edNr masina");
                params.put("sender_id", prefs.getString("user_id", ""));
                params.put("sender_nickname", "sender_nickname");
                params.put("receiver_id", receiver_id);
                params.put("receiver_nickname", "nick_name_Receiver");
                Log.w("meniuu", "lat+" + latitude + " lang:" + longitude);
                return params;
            }

            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error volley:" + error.getMessage());
            Toast.makeText(ctx, "Something went wrong", Toast.LENGTH_LONG).show();
            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("A aparut o eroare" + "Va rog sa reincercati");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
        }
    };
}