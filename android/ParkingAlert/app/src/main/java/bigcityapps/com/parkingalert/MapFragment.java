package bigcityapps.com.parkingalert;

/**
 * Created by Sistem1 on 25/10/2016.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 10/12/15.
 */
public class MapFragment extends Fragment {
    RelativeLayout back_maps;
    private GoogleMap mMap;
    TextView adress, tvTime;
    private String provider;
    private LocationManager locationManager;
    String mHour, mLat = "", mLng = "", image = "", notification_id;
    String mText;
    String mPlates;
    //    double mLongitude, mLatitude;
    String TAG = "meniuu";
    long time, actualDate;
    Context ctx;
    MapView mMapView;
    private GoogleMap googleMap;
    private int mProgressStatus = 0;
    private Handler mHandler = new Handler();
    boolean isActiv=true;
    RequestQueue queue;
    SharedPreferences prefs;
    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.harta, container, false);
        initComponents(rootView);
        Constants.isActivMap=true;
        Bundle b = this.getArguments();
        ctx = getContext();
        prefs = new SecurePreferences(getContext());
        queue = Volley.newRequestQueue(getContext());
        Log.w("meniuu", "mapfragment");
        if (b != null) {
            try {
                mHour = (String) b.get("mHour");
                mPlates = (String) b.get("mPlates");
                mLat = b.getString("lat");
                mLng = b.getString("lng");
                image = b.getString("image");
                notification_id=b.getString("notification_id");
                Log.w("meniuu", "lat:" + mLat + " mlng:" + mLng);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                Date myDate = simpleDateFormat.parse(mHour);
                time = myDate.getTime();
                time = time + 30 * 1000;
                Date date2 = new Date();
                actualDate = date2.getTime();

                long diff = time - actualDate;
                diff = diff / 1000;
                mProgressStatus = (int) diff;
                dosomething();
                Log.w("meniuu", "diff in harta:" + diff);
                int minutes = 0;
                if (diff < 3600) {
                    minutes = ((int) diff % 3600) / 60;
                    mText = "Acum " + minutes + " minute";
                } else if (diff < 86400) {
                    minutes = (int) diff / 60 / 60;
                    if (minutes == 1)
                        mText = "Acum " + minutes + " ora";
                    else
                        mText = "Acum " + minutes + " ore";
                } else if (diff > 86400) {
                    minutes = (int) diff / 60 / 60 / 24;
                    mText = "Acum " + minutes + " zile";
                }

            } catch (Exception e) {
                Log.w("meniuu", "catch la lueare putextra in harta");
                e.printStackTrace();
            }
            mMapView = (MapView) rootView.findViewById(R.id.mapView);
            mMapView.onCreate(savedInstanceState);

            mMapView.onResume(); // needed to get the map to display immediately

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMapView.getMapAsync(new OnMapReadyCallback() {
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;
                    // For showing a move to my location button
                    if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);

                    if (mLng.length() != 0)
                        adress.setText(getAddress(Double.parseDouble(mLat), Double.parseDouble(mLng)));
                    mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                    final LatLng CIU = new LatLng(Double.parseDouble(mLat), Double.parseDouble(mLng));
                    LatLng sydney = new LatLng(Double.parseDouble(mLat), Double.parseDouble(mLng));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mLat), Double.parseDouble(mLng)), 12.0f));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(CIU).title(image).snippet(mText + ""));
                    marker.showInfoWindow();
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                }
            });


            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (mLng.length() != 0)
                adress.setText(getAddress(Double.parseDouble(mLat), Double.parseDouble(mLng)));
            Criteria criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, false);
            if (ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return rootView;
            }
        }
        return rootView;
    }


    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
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

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }


    /**
     *
     */
    public void initComponents(View rootview) {
        adress = (TextView) rootview.findViewById(R.id.adress);
        tvTime = (TextView) rootview.findViewById(R.id.time);
        back_maps = (RelativeLayout) rootview.findViewById(R.id.back_maps);
        back_maps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                finish();
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        final LatLng CIU = new LatLng(Double.parseDouble(mLat), Double.parseDouble(mLng));
        LatLng sydney = new LatLng(Double.parseDouble(mLat), Double.parseDouble(mLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mLat), Double.parseDouble(mLng)), 12.0f));
        Marker marker = mMap.addMarker(new MarkerOptions().position(CIU).title("My Office").snippet(mText + ""));
//            mMap.addMarker(new MarkerOptions().position(CIU).tvTitle("My Office").snippet(mText+""));
        marker.showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    /**
     * get adress from lat and lng
     *
     * @param lat
     * @param lng
     * @return
     */
    public String getAddress(double lat, double lng) {
        Log.w("meniuu", "lat:" + lat + " lng:" + lng);
        String adresa = "";
        Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            for (int i = 0; i < addresses.size(); i++)
                Log.w("meniuu", "adress:" + addresses.get(i));
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            adresa = add + ", \n" + obj.getLocality();
            Log.w("meniuu", "postal code:" + obj.getPostalCode());
            Log.w("meniuu", "adresa:" + add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
//            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return adresa;
    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        //        private final View myContentsView;
        LayoutInflater inflater = null;
//        public MyInfoWindowAdapter(LayoutInflater inflater) {
//            this.inflater = inflater;
//        }

        @Override
        public View getInfoContents(Marker marker) {
//            ImageView mImage=(ImageView)myContentsView.findViewById(R.id.image_info_view);
//            TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.nr_masina_infoview));
//            tvTitle.setText("Notificat "+mPlates);
//            TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.time_info_view));
//            tvSnippet.setText(marker.getSnippet());

            return (null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            ContextThemeWrapper cw = new ContextThemeWrapper(ctx, R.style.Transparent);
            LayoutInflater inflater = (LayoutInflater) cw.getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.infoview, null);
            if (marker != null) {
                final ImageView image = (ImageView) v.findViewById(R.id.image_info_view);
                TextView tvTitle = ((TextView) v.findViewById(R.id.nr_masina_infoview));
                tvTitle.setText("Notificat " + mPlates);
//                TextView tvSnippet = ((TextView) v.findViewById(R.id.time_info_view));
//                tvSnippet.setText(marker.getSnippet());

//                Glide.with(ctx).load(marker.getTitle()).into(image);
                Log.w("meniu", "image in harta:" + marker.getTitle());
                Glide.with(ctx).load(marker.getTitle()).asBitmap().centerCrop().into(new BitmapImageViewTarget(image) {
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        image.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
            return v;
        }
    }

    public void dosomething() {
        new Thread(new Runnable() {
            public void run() {
                while (mProgressStatus > 0) {
                    mProgressStatus -= 1;
                    Log.w("meniuu","COnstants.isactivmap:"+Constants.isActivMap);
                    if (mProgressStatus == 0 && Constants.isActivMap) {
                        Log.w("meniuu","in map se apeleaza review");
                        Review(notification_id,mHour,mPlates,time + "",mLat,mLng, image);
                    }
                    mHandler.post(new Runnable() {
                        public void run() {
                            tvTime.setText("Incercam sa il localizam proprietar:"+mProgressStatus);
                            Log.w("meniuu", "incercam sa il localizam pe receiver:" + mProgressStatus);
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        Log.w("meniuu", "on destroyview in mapfragment");
        isActiv=false;
        super.onDestroyView();
    }

    @Override
    public void onPause() {
        isActiv=false;
        super.onPause();
    }

    public void Review(final  String id, final String ora, final String nr_carString, final String timer, final String mLat, final String mLng, final String mImage){
        String url = Constants.URL+"notifications/sendReview/"+id;
        Log.w("meniuu","url review:"+url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:review" + response);
//                        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                        if(isActiv) {
                            Log.w("meniuu","se deschide fragmentul sumar din harta");
                            Fragment fragment = new SumarFragment();
                            Bundle harta = new Bundle();
                            harta.putString("mHour", ora);
                            harta.putString("mPlates", nr_carString);
                            harta.putString("time", timer + "");
                            harta.putString("feedback", "Nu a venit la masina");
                            fragment.setArguments(harta);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                        }
                    }
                }, ErrorListener) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("feedback",false+"");
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
        }
    };
}
