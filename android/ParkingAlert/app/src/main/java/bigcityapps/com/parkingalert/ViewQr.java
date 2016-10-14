package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
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
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 13/10/2016.
 */
public class ViewQr extends Activity implements View.OnClickListener {
    Context ctx;
    RequestQueue queue;
    SharedPreferences prefs;
    RelativeLayout rlShare, rlGoSite, back;
    TextView text, tvTitle_viewqr, tvModifyQR;
    ImageView ivQR;
    String mPlates, mQrcode;
    RelativeLayout layout_dialog;
    TextView tvExist_qr, tvNo_exist_qr, tvCancel;
    Bitmap bmp;
    public final static String APP_PATH_SD_CARD = "/Friendly/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "Share";
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
                 bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
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
        tvModifyQR=(TextView)findViewById(R.id.modify_qr);
        tvModifyQR.setOnClickListener(this);
        layout_dialog=(RelativeLayout)findViewById(R.id.layout_dialog);
        tvExist_qr=(TextView)findViewById(R.id.exist_qr);
        tvExist_qr.setOnClickListener(this);
        tvNo_exist_qr=(TextView)findViewById(R.id.no_exist_qr);
        tvNo_exist_qr.setOnClickListener(this);
        tvCancel=(TextView)findViewById(R.id.cancel);
        tvCancel.setOnClickListener(this);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_viewqr:
                Bitmap bitmap;
                OutputStream output;
                bitmap = bmp;
                File filepath = Environment.getExternalStorageDirectory();
//                File dir = new File(filepath.getAbsolutePath() + "/Share Image Tutorial/");

                String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
                try {
                    File dir = new File(fullPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
//                dir.mkdirs();
                // Create a name for the saved image
                File file = new File(dir, "share_image.png");
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("image/jpeg");
                    output = new FileOutputStream(file);
                    // Compress into png format image from 0% - 100%
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
                    output.flush();
                    output.close();
                    Uri uri = Uri.fromFile(file);
                    share.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(share, "Share QR cod"));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case R.id.go_site:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.bihon.ro"));
                startActivity(browserIntent);
                break;
            case R.id.back_viewqr:
                finish();
                break;
            case R.id.modify_qr:
                layout_dialog.setVisibility(View.VISIBLE);
                break;
            case R.id.exist_qr:
                Constants.change=false;
                Intent addQr = new Intent(ViewQr.this, AddQR.class);
                startActivity(addQr);
                break;
            case R.id.no_exist_qr:
                Constants.change=false;
                generateQr(prefs.getString("user_id",""));
                break;

            case R.id.cancel:
            layout_dialog.setVisibility(View.GONE);
                break;
        }
    }
    public void generateQr(String id) {
        String url = Constants.URL + "users/generateCarCode/" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                Log.w("meniuu", "response: getcar" + response);
                try {
                    JSONObject obj = new JSONObject(json);
                    String carCode = obj.getString("carCode");

                    Intent showQr=new Intent(ViewQr.this, ShowQRCode.class);
                    showQr.putExtra("qrcode",carCode);
                    startActivity(showQr);
                    finish();
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
