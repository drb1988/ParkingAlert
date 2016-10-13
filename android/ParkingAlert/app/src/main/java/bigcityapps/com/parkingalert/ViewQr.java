package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import Util.SecurePreferences;

/**
 * Created by fasu on 13/10/2016.
 */
public class ViewQr extends Activity implements View.OnClickListener {
    Context ctx;
    RequestQueue queue;
    SharedPreferences prefs;
    RelativeLayout rlShare, rlGoSite, back;
    TextView text, tvTitle_viewqr;
    ImageView ivQR;
    String mPlates, mQrcode;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewqr);
        initcomponents();
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {
            mPlates = b.getString("mPlates");
            mQrcode = b.getString("mQrcode");
            text.setText("Acesta este codul QR asociat masinii "+mPlates+". Printeaza-l si lipeste-l pe parbrizul masinii");
            tvTitle_viewqr.setText("Cod QR al "+mPlates);
            QRCodeWriter writer = new QRCodeWriter();
            try {
                BitMatrix bitMatrix = writer.encode(mQrcode, BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }
                ivQR.setImageBitmap(bmp);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    public void initcomponents() {
        back = (RelativeLayout) findViewById(R.id.back_viewqr);
        back.setOnClickListener(this);
        rlShare = (RelativeLayout) findViewById(R.id.share_viewqr);
        rlShare.setOnClickListener(this);
        rlGoSite = (RelativeLayout) findViewById(R.id.go_site);
        rlGoSite.setOnClickListener(this);
        text = (TextView) findViewById(R.id.text);
        ivQR = (ImageView) findViewById(R.id.qr);
        tvTitle_viewqr=(TextView)findViewById(R.id.title_viewqr);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_viewqr:

                break;
            case R.id.go_site:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bihon.ro"));
                startActivity(browserIntent);
                break;
            case R.id.back_viewqr:
                finish();
                break;
        }
    }
}
