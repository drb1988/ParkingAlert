package bigcityapps.com.parkingalert;

import android.app.Activity;
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
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import org.json.JSONArray;

import java.util.HashMap;

import Util.Constants;
import Util.SecurePreferences;
import Util.Utils;

/**
 *
 * Created by fasu on 1 19/09/2016.
 */
public class ConnectFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    RelativeLayout notificaProprietar;
    double latitude, longitude;
    TextView notify_maker;
    SharedPreferences prefs;
    private CoordinatorLayout coordinatorLayout;
    LocationManager locationManager;
    boolean hasCar=false;
    RequestQueue queue;
    private SwipeRefreshLayout swipeRefreshLayout;
    Context ctx;
    Activity act;
    public ConnectFragment() {
    }

    public void onResume() {
        Log.w("meniuu","on resume connectfragment");
        MainActivity.active=true;
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
                                Log.w("meniuu","sa dat click");
                                startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
                            }
                        });
                snackbar.show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Permisiune");
                    builder.setMessage("Ca sa poti folosi aplicatia trebuie sa dai permisiunea la accesul locatiei. Multumesc");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alert1 = builder.create();
                    alert1.show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 2:
                Intent intent = new Intent(getActivity(), SimpleScannerActivity.class);
                intent.putExtra("lat", latitude + "");
                intent.putExtra("lng", longitude + "");
                startActivity(intent);
                break;

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.notifica_proprietar, container, false);
        notify_maker=(TextView)rootView.findViewById(R.id.notify_maker);
        prefs = new SecurePreferences(getContext());
        queue = Volley.newRequestQueue(getContext());
        getCars(prefs.getString("user_id", ""));
        MainActivity.active=true;
        ctx=getContext();
        act=getActivity();
        coordinatorLayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorLayout);
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(this);
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
        notificaProprietar=(RelativeLayout) rootView.findViewById(bigcityapps.com.parkingalert.R.id.relative_notifica_proprietarul);
        notificaProprietar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.w("meniuu","click pe notifica proprietar");
                if (Utils.isNetworkAvailable(getContext())) {
                    if (!checkLocation()) {
                        Log.w("meniuu","nu are locatie");
                        return;
                    }
                    if(hasCar)
                    {
                        if(latitude!=0 && longitude!=0)
                        {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                                // Should we show an explanation?
                                if (ActivityCompat.shouldShowRequestPermissionRationale(act, android.Manifest.permission.CAMERA)) {

                                    android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ctx);
                                    builder.setTitle("Acces locatie");
                                    builder.setPositiveButton(android.R.string.ok, null);
                                    builder.setMessage("Te rog confirma accesul la locatie");//TODO put real question
                                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        public void onDismiss(DialogInterface dialog) {
                                            requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
                                        }
                                    });
                                    builder.show();
                                } else {
                                    ActivityCompat.requestPermissions(act, new String[]{android.Manifest.permission.CAMERA}, 1);
                                }
                            } else {
                                Intent intent = new Intent(getActivity(), SimpleScannerActivity.class);
                                intent.putExtra("lat", latitude + "");
                                intent.putExtra("lng", longitude + "");
                                startActivity(intent);
                            }
                        }
                        else {
                            Intent intent = new Intent(getActivity(), SimpleScannerActivity.class);
                            intent.putExtra("lat", latitude + "");
                            intent.putExtra("lng", longitude + "");
                            startActivity(intent);
                        }

                    }else
                            Toast.makeText(ctx,"Nu exista coordonate, Va rugam asteptati!", Toast.LENGTH_LONG).show();
                    }else
                    {
                        Intent addCar= new Intent(getActivity(), Cars.class);
                        startActivity(addCar);
                    }
                }else
                { Log.w("meniuu","nu este net");
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
                    notify_maker.setText("Adauga o masina");
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

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
      Intent main= new Intent(getActivity(), MainActivity.class);
        startActivity(main);
        swipeRefreshLayout.setRefreshing(false);

    }
}
