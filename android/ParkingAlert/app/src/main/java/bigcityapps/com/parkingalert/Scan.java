package bigcityapps.com.parkingalert;//package com.journaldev.navigationdrawer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scan extends FragmentActivity implements ZXingScannerView.ResultHandler {
    private GoogleMap mMap;
    private ZXingScannerView mScannerView;
    ImageView mesaj_preintampinare;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.scan);
        mesaj_preintampinare=(ImageView)findViewById(bigcityapps.com.parkingalert.R.id.mesaj_preintampinare);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                QrScanner();
            }
        }, 3000);

    }
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
    public void handleResult(Result rawResult) {
        // Do something with the result here
        mScannerView.stopCamera();
//        setContentView(R.layout.scan);
        mesaj_preintampinare.setVisibility(View.GONE);
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        // show the scanner result into dialog box.
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Scan Result");
//        builder.setMessage(rawResult.getText());
//        AlertDialog alert1 = builder.create();
//        alert1.show();
        Intent harta= new Intent(Scan.this, Harta.class);
        startActivity(harta);
    }
}
