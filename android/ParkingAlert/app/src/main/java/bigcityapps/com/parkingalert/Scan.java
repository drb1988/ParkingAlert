package bigcityapps.com.parkingalert;//package com.journaldev.navigationdrawer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.zxing.Result;

import org.json.JSONObject;

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
    double latitude, longitude;
    LocationManager locationManager;
    String user_id, plates;
    protected void onResume() {
//        if (!checkLocation())
//            return;
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListenerNetwork);
//        mesaj_preintampinare = (ImageView) findViewById(R.id.mesaj_preintampinare);
//        mesaj_preintampinare.setImageResource(R.drawable.mesaj_preintampinare);
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                QrScanner();
//            }
//        }, 3000);
        super.onResume();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan);
        queue = Volley.newRequestQueue(this);
        ctx = this;
        prefs = new SecurePreferences(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Constants.ctx = this;
//        mScannerView = new ZBarScannerView(this);    // Programmatically initialize the scanner view
//        setContentView(mScannerView);
        QrScanner();
        // Check if GPS enabled
//        if (!checkLocation())
//            return;
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListenerNetwork);
//        mesaj_preintampinare = (ImageView) findViewById(R.id.mesaj_preintampinare);
//        mesaj_preintampinare.setImageResource(R.drawable.mesaj_preintampinare);
//        new Handler().postDelayed(new Runnable() {
//            public void run() {
//                QrScanner();
//            }
//        }, 3000);
    }

    /**
     * start qrscan
     */
    public void QrScanner() {
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        mScannerView.setResultHandler((ZXingScannerView.ResultHandler) this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
        Log.w("meniuu", "scanezi");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScannerView != null)
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
        Log.w("meniuu", "resultat scanare:"+rawResult.getText()); // Prints scan results
        getUsersForCode(rawResult.getText().toString());

        // show the scanner result into dialog box.


    }

    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.w("meniu", "long:" + longitude + " lat:" + latitude);
        }
        public void onStatusChanged(String s, int i, Bundle bundle) {}
        public void onProviderEnabled(String s) {}
        public void onProviderDisabled(String s) {}
    };

    protected void onStop() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListenerNetwork);
        super.onStop();
    }

    public void postNotification(final String receiver_id){
        String url = Constants.URL+"notifications/notification";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setTitle(plates+" a fost notificat cu succes");
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
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", "1");
                params.put("is_active", "false");
                params.put("mLatitude",latitude+"");
                params.put("mLongitude", longitude+"");
                params.put("vehicle", plates);
                params.put("sender_id", prefs.getString("user_id",""));
                params.put("sender_nickname", "sender_nickname");
                params.put("receiver_id", receiver_id);
                params.put("receiver_nickname", "nick_name_Receiver");
                Log.w("meniuu","lat+"+latitude+" lang:"+longitude+"receiver_id:"+receiver_id);
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
            Log.w("meniuu","error volley:"+error.getMessage());
            final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
            builder.setTitle("A aparut o eroare" + "Va rog sa reincercati");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
        }
    };

    public void getUsersForCode(String qrcode) {
        Log.w("meniuu","qrcode: in getusersforcode"+qrcode);
        String url = Constants.URL + "users/getUsersForCode/" + qrcode;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                Log.w("meniuu", "response: getusersforcode" + response);
                try {
                    JSONObject obj = new JSONObject(json);
                    JSONObject car= new JSONObject(obj.getString("car"));
                     user_id=obj.getString("userID");
                     plates=car.getString("plates");
                    Log.w("meniuu","user_id:"+user_id+" se apeleaza postnotification");
                    postNotification(user_id);
                } catch (Throwable t) {
                    Log.w("meniuu", "cacth get questions");
                    t.printStackTrace();
                }
            }
        }, ErrorListener) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Log.w("meniuu", "token:" + auth_token_string);
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }

}
