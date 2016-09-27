package bigcityapps.com.parkingalert;//package com.journaldev.navigationdrawer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.zxing.Result;

import java.util.HashMap;
import java.util.Map;

import Util.Constants;
import Util.SecurePreferences;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scan extends FragmentActivity implements ZXingScannerView.ResultHandler {
    private GoogleMap mMap;
    private ZXingScannerView mScannerView;
    ImageView mesaj_preintampinare;
    RequestQueue queue;
    Context ctx;
    SharedPreferences prefs;

    /**
     * oncreate method
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.scan);
        queue = Volley.newRequestQueue(this);
        prefs = new SecurePreferences(this);
        mesaj_preintampinare=(ImageView)findViewById(bigcityapps.com.parkingalert.R.id.mesaj_preintampinare);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                QrScanner();
            }
        }, 3000);

    }

    /**
     * start qrscan
     */
    public void QrScanner(){
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        mScannerView.setResultHandler((ZXingScannerView.ResultHandler) this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
Log.w("meniuu","scanezi");
    }
    @Override
    public void onPause() {
        super.onPause();
        if(mScannerView!=null)
            mScannerView.stopCamera();           // Stop camera on pause
    }

    /**
     * result qrcode scan
     * @param rawResult
     */
    public void handleResult(Result rawResult) {
        // Do something with the result here
        mScannerView.stopCamera();
//        setContentView(R.layout.scan);
        mesaj_preintampinare.setVisibility(View.GONE);
        Log.e("meniuu", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)
        postNotification(rawResult.getText());
        // show the scanner result into dialog box.
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Scan Result");
//        builder.setMessage(rawResult.getText());
//        AlertDialog alert1 = builder.create();
//        alert1.show();

    }
    public void postNotification(final String receiver_id){
        String url = Constants.URL+"notifications/notification";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:post notification" + response);
                        Intent harta= new Intent(Scan.this, Harta.class);
                        startActivity(harta);
                        finish();
                    }
                }, ErrorListener) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "3");
                params.put("is_active", "false");
                params.put("latitude","24");
                params.put("longitude", "24");
                params.put("vehicle", "nr masina");
                params.put("sender_id", prefs.getString("user_id",""));
                params.put("sender_nickname", "sender_nickname");
                params.put("receiver_id", receiver_id);
                params.put("receiver_nickname", "nick_name_Receiver");
                return params;
            }
            public Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization","Bearer "+ auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
            Toast.makeText(ctx,"Something went wrong",Toast.LENGTH_LONG ).show();
        }
    };
}
