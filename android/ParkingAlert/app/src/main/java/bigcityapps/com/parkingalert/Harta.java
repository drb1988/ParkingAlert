package bigcityapps.com.parkingalert;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by fasu on 19/09/2016.
 */
public class Harta extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    RelativeLayout ok;
    private GoogleMap mMap;
    TextView adresaTextview;
    private String provider;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.harta);
        initComponents();
//        mMap = ((MapFragment) getFragmentManager().findFragmentById(bigcityapps.com.parkingalert.R.id.map)).getMap();
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);

        } else {
            Toast.makeText(this, "Not avaible", Toast.LENGTH_LONG).show();
        }
    }

    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this);
    }

    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.removeUpdates(this);
    }
    public void initComponents() {
        ok = (RelativeLayout) findViewById(bigcityapps.com.parkingalert.R.id.relative_ok_harta);
        adresaTextview = (TextView) findViewById(bigcityapps.com.parkingalert.R.id.adresa);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent notificari= new Intent(Harta.this, Notificari.class);
                startActivity(notificari);
//                Timer make = new Timer(Harta.this, 3);
//                make.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                make.show();
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(47.070605, 21.929072);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Bun Venit Acasa!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    public String getAddress(double lat, double lng) {
        Log.w("meniuu","lat:"+lat+" lng:"+lng);
        String adresa = "";
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            adresa = add + ", \n" + obj.getLocality();
            Log.w("meniuu", "adresa:" + add);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return adresa;
    }

    @Override
    public void onLocationChanged(Location location) {
        int lat = (int) (location.getLatitude());
        int lng = (int) (location.getLongitude());

        final LatLng CIU = new LatLng(lat,lng);
        adresaTextview.setText(getAddress(lat, lng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,lng), 12.0f));
        mMap.addMarker(new MarkerOptions().position(CIU).title("My Office"));
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    public void onProviderEnabled(String s) {
        Toast.makeText(this, "Enabled new provider " + s,
                Toast.LENGTH_SHORT).show();
    }

    public void onProviderDisabled(String s) {
        Toast.makeText(this, "Disabled provider " + s,
                Toast.LENGTH_SHORT).show();
    }
}
