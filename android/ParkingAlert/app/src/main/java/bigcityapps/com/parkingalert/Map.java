package bigcityapps.com.parkingalert;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by fasu on 19/09/2016.
 */
public class Map extends AppCompatActivity implements OnMapReadyCallback {
    RelativeLayout back_maps;
    private GoogleMap mMap;
    TextView adress;
    private String provider;
    private LocationManager locationManager;
    String mHour, mLat="", mLng="", image="";
    String mText;
    String mPlates;
//    double mLongitude, mLatitude;
    String TAG = "meniuu";
    long  time, actualDate;
    Context ctx;

    protected void onStop() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(locationListenerNetwork);
        super.onStop();
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
            // TODO Auto-generated method stub
            ContextThemeWrapper cw = new ContextThemeWrapper(getApplicationContext(), R.style.Transparent);
            // AlertDialog.Builder b = new AlertDialog.Builder(cw);
            LayoutInflater inflater = (LayoutInflater) cw.getSystemService(LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.infoview, null);
            if (marker != null) {
                final ImageView image = (ImageView) v.findViewById(R.id.image_info_view);
                TextView tvTitle = ((TextView) v.findViewById(R.id.nr_masina_infoview));
                tvTitle.setText("Notificat " + mPlates);
                TextView tvSnippet = ((TextView) v.findViewById(R.id.time_info_view));
                tvSnippet.setText(marker.getSnippet());

//                Glide.with(ctx).load(marker.getTitle()).into(image);
                Log.w("meniu","image in harta:"+marker.getTitle());
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.harta);
        initComponents();
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        ctx=this;
        Log.w("meniuu", "map");
        if (b != null) {
            try {
                mHour = (String) b.get("mHour");
                mPlates = (String) b.get("mPlates");
                mLat = b.getString("lat");
                mLng = b.getString("lng");
                image = b.getString("image");
                Log.w("meniuu","lat in map:"+mLat+" lng:"+mLng+" image:"+image);


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                Date myDate = simpleDateFormat.parse(mHour);
                time=myDate.getTime();
                Date date2 = new Date();
                actualDate=date2.getTime();


//                SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
//                Date d = df.parse(mHour);
//                Date date2 = new Date();
//                String actual_date = df.format(date2);
//                Log.w("meniuu", "data_actuala:" + actual_date);
//                Date date_actual = df.parse(actual_date);
//                long diff = getDateDiff(date_actual, d);
                long diff=actualDate-time;
                diff = diff / 1000;
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
                    minutes = (int)diff/ 60 / 60 / 24;
                    Log.w("meniuu","time:"+minutes);
                    mText = "Acum " + minutes + " zile";
                }

            } catch (Exception e) {
                Log.w("meniuu", "catch la lueare putextra in harta");
                e.printStackTrace();
            }
        }
//        mMap = ((MapFragment) getFragmentManager().findFragmentById(bigcityapps.com.parkingalert.R.id.map)).getMap();
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(mLng.length()!=0)
            adress.setText(getAddress(Double.parseDouble(mLat), Double.parseDouble(mLng)));
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
    }

    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            if(mLng.length()!=0) {
                mLng = location.getLongitude() + "";
                mLat = location.getLatitude() + "";
                adress.setText(getAddress(Double.parseDouble(mLat), Double.parseDouble(mLng)));
                final LatLng CIU = new LatLng(Double.parseDouble(mLat), Double.parseDouble(mLng));
                LatLng sydney = new LatLng(Double.parseDouble(mLat), Double.parseDouble(mLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(mLat), Double.parseDouble(mLng)), 12.0f));
                Marker marker = mMap.addMarker(new MarkerOptions().position(CIU).title(image).snippet(mText + ""));
//            mMap.addMarker(new MarkerOptions().position(CIU).tvTitle("My Office").snippet(mText+""));
                marker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            }
        }

        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        public void onProviderEnabled(String s) {
        }

        public void onProviderDisabled(String s) {
        }
    };

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

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    @Override
    protected void onResume() {

        if (!checkLocation())
            return;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, locationListenerNetwork);
        super.onResume();
    }

    /**
     *
     */
    public void initComponents() {
        adress = (TextView) findViewById(R.id.adress);
//        back_maps = (RelativeLayout) findViewById(R.id.back_maps);
//        back_maps.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                finish();
//            }
//        });
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
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            for (int i = 0; i < addresses.size(); i++)
                Log.w("meniuu", "adress:" + addresses.get(i));
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            adresa = add + ", \n" + obj.getLocality();
            Log.w("meniuu","postal code:"+obj.getPostalCode());
            Log.w("meniuu", "adresa:" + add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return adresa;
    }
}
