package bigcityapps.com.parkingalert;

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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.HashMap;

import Util.Constants;
import Util.SecurePreferences;
import Util.Utils;

/**
 *
 * Created by fasu on 1 19/09/2016.
 */
public class ConnectFragment extends Fragment {
    RelativeLayout notificaProprietar;
    double latitude, longitude;
    TextView notify_maker;
    SharedPreferences prefs;
    private CoordinatorLayout coordinatorLayout;
    LocationManager locationManager;
    boolean hasCar=false;
    RequestQueue queue;

    public ConnectFragment() {
    }

    public void onResume() {
        super.onResume();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (!Utils.isNetworkAvailable(getContext())) {
                Snackbar snackbar = Snackbar
                        .make(coordinatorLayout, "Nu exista conexiune la internet!", Snackbar.LENGTH_LONG)
                        .setDuration(Snackbar.LENGTH_INDEFINITE)
                        .setAction("SETARI", new View.OnClickListener() {
                            public void onClick(View view) {
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                            }
                        });
                snackbar.show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(bigcityapps.com.parkingalert.R.layout.notifica_proprietar, container, false);
        notify_maker=(TextView)rootView.findViewById(R.id.notify_maker);
        prefs = new SecurePreferences(getContext());
        queue = Volley.newRequestQueue(getContext());
        getCars(prefs.getString("user_id", ""));
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return rootView;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 10, locationListenerNetwork);
        notificaProprietar=(RelativeLayout) rootView.findViewById(bigcityapps.com.parkingalert.R.id.relative_notifica_proprietarul);
        notificaProprietar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (Utils.isNetworkAvailable(getContext())) {
                    if (!checkLocation())
                        return;
                    if(hasCar)
                    {  Intent intent = new Intent(getActivity(), SimpleScannerActivity.class);
                        intent.putExtra("lat",latitude+"");
                        intent.putExtra("lng",longitude+"");
                        startActivity(intent);
                    }else
                    {
                        Intent addCar= new Intent(getActivity(), Cars.class);
                        startActivity(addCar);
                    }
                }else
                {
                    Snackbar snackbar = Snackbar
                            .make(coordinatorLayout, "Nu exista conexiune la internet!", Snackbar.LENGTH_LONG)
                            .setDuration(Snackbar.LENGTH_INDEFINITE)
                            .setAction("SETARI", new View.OnClickListener() {
                                public void onClick(View view) {
                                    startActivityForResult(new Intent(android. provider.Settings.ACTION_SETTINGS), 0);
                                }
                            });
                    snackbar.show();
                }

            }
        });

        if (!Utils.isNetworkAvailable(rootView.getContext())) {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Nu exista conexiune la internet!", Snackbar.LENGTH_LONG)
                    .setDuration(Snackbar.LENGTH_INDEFINITE)
                    .setAction("SETARI", new View.OnClickListener() {
                        public void onClick(View view) {
                            startActivityForResult(new Intent(android. provider.Settings.ACTION_SETTINGS), 0);
                        }
                    });
//
//            // Changing message text color
//            snackbar.setActionTextColor(Color.RED);
//
//            // Changing action button text color
//            View sbView = snackbar.getView();
//            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
//            textView.setTextColor(Color.YELLOW);

            snackbar.show();
        }

        return rootView;
    }
    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }
    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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
    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
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
    public void getCars(String id) {
        String url = Constants.URL + "users/getCars/" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                Log.w("meniuu", "response: getcar" + response);
                try {
                    JSONArray obj = new JSONArray(json);
                    if(obj.length()==0){
                        notify_maker.setText("Adauga o masina");
                        hasCar=false;
                    }else {
                        notify_maker.setText("Notifica proprietarul");
                        hasCar=true;
                    }
                } catch (Throwable t) {
                    Log.w("meniuu", "cacth get questions");
                    t.printStackTrace();
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
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
        }
    };
}
