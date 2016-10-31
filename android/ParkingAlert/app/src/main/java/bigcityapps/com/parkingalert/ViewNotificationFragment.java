package bigcityapps.com.parkingalert;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

/**
 * Created by fasu on 10/12/15.
 */
public class ViewNotificationFragment extends Fragment implements View.OnClickListener {
    TextView zece, cinci, trei, nupot, car_nr_view_notification;
    RelativeLayout inapoi;
    RequestQueue queue;
    SharedPreferences prefs;
    Context ctx;
    String notification_id;
    double latitude, longitude;
    LocationManager locationManager;
    String mPlates, mHour;
    public ViewNotificationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(bigcityapps.com.parkingalert.R.layout.view_notification, container, false);
        initComponents(rootView);
        ((MainActivity) getActivity()).setTitle("Raspunde");
        MainActivity.active=true;
        ctx=rootView.getContext();
        queue = Volley.newRequestQueue(rootView.getContext());
        prefs = new SecurePreferences(rootView.getContext());
//        Intent iin = getActivity().getIntent();
        Bundle b = this.getArguments();
        if (b != null) {
            mPlates=b.getString("mPlates");
            car_nr_view_notification.setText((String) b.get("mPlates")+" \n creaza o problema");
            notification_id=(String) b.get("notification_id");
            mHour=b.getString("mHour");
            Log.w("meniuu","notification_id in viewnotificationfrag:"+notification_id);
        }else
        Toast.makeText(ctx,"NU s-au putut lua datele",Toast.LENGTH_LONG).show();
        locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
                    builder.setTitle("Acces locatie");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Te rog confirma accesul la locatie");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListenerNetwork);
            }
        }
        else
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListenerNetwork);
        return rootView;
    }
    public void initComponents(View rootView){
        zece=(TextView)rootView.findViewById(R.id.zece);
        zece.setOnClickListener(this);
        cinci=(TextView)rootView.findViewById(R.id.cinci);
        cinci.setOnClickListener(this);
        trei=(TextView)rootView.findViewById(R.id.trei);
        trei.setOnClickListener(this);
        nupot=(TextView)rootView.findViewById(R.id.nupot);
        nupot.setOnClickListener(this);
//        inapoi=(RelativeLayout) rootView.findViewById(R.id.inapoi_notificare);
//        inapoi.setOnClickListener(this);
        car_nr_view_notification=(TextView)rootView.findViewById(R.id.car_nr_view_notification);
    }
    public void setClick(boolean set){
        zece.setClickable(set);
        cinci.setClickable(set);
        trei.setClickable(set);
        nupot.setClickable(set);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
//            case R.id.inapoi_notificare:
//                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
//                break;
            case R.id.zece:
                setClick(false);
                getNotification(prefs.getString("user_id",null), "1");
//                postAnswer("3");
                break;
            case R.id.cinci:
                setClick(false);
                getNotification(prefs.getString("user_id",null), "5");
//                postAnswer("5");
                break;

            case R.id.trei:
                setClick(false);
                getNotification(prefs.getString("user_id",null), "7");
//                postAnswer("7");
                break;

            case R.id.nupot:
                postAnswer("100");
                break;
        }
    }

    public void postAnswer(final String time){
        String url = Constants.URL+"notifications/receiverAnswered/"+notification_id;
        Log.w("meniuu","url receiveranswered:"+url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:post answer" + response);
                        getActivity().getSupportFragmentManager().beginTransaction().remove(ViewNotificationFragment.this).commit();
                        Fragment  fragment = new TimerReceiverFragment();
                        Bundle timerSender = new Bundle();
                        timerSender.putString("time", time);
                        timerSender.putString("mHour", mHour);
                        timerSender.putString("mPlates", mPlates);
                        timerSender.putString("notification_id", notification_id);
                        timerSender.putString("lat",latitude+"");
                        timerSender.putString("lng", longitude+"");
//                        timerSender.putString("image", modelNotification.getPicture());
                        fragment.setArguments(timerSender);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//                        Intent harta= new Intent(Scan.this, Map.class);
//                        startActivity(harta);
//                        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                    }
                }, ErrorListener) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("latitude", latitude+"");
                params.put("longitude", longitude+"");
                params.put("estimated",time);
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
            Log.w("meniuu", "error: errorlistener:" + error);
            Toast.makeText(ctx,"Something went wrong",Toast.LENGTH_LONG ).show();
        }
    };
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        }
        public void onStatusChanged(String s, int i, Bundle bundle) {}
        public void onProviderEnabled(String s) {}
        public void onProviderDisabled(String s) {}
    };
    public void getNotification(final String id, final String time){
        String url = Constants.URL+"users/getNotification/"+id;
        Log.w("meniuu","url in getnotif:"+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                Log.w("meniuu","response getnotification:"+response);
                try {
                    if(json.length()!=0)
                        postAnswer(time);
                    else
                    {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setTitle("Nu ai raspuns la timp!!");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                getActivity().getSupportFragmentManager().beginTransaction().remove(ViewNotificationFragment.this).commit();
                                Intent main= new Intent(getActivity(),MainActivity.class);
                                startActivity(main);
                            }
                        });
                        AlertDialog alert1 = builder.create();
                        alert1.show();
                    }
                }catch (Exception e)
                {Log.w("meniuu","este catch");
                    e.printStackTrace();
                }
            }
        }, ErrorListener) {
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 0, locationListenerNetwork);
                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }
}
