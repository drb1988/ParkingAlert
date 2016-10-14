package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import Util.SecurePreferences;

/**
 * Created by fasu on 13/10/2016.
 */
public class ShowQRCode extends Activity implements OnClickListener{
    public final static String APP_PATH_SD_CARD = "/Friendly/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "Image";
    Context ctx;
    RequestQueue queue;
    ImageView ivQrcode;
    TextView tvNextStep;
    SharedPreferences prefs;
    String qrcode;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_qrcode);
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        initcomponents();
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null)
            qrcode =(String) b.get("qrcode");

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(qrcode, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            ivQrcode.setImageBitmap(bmp);
            saveImageToExternalStorage(bmp);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    public void initcomponents() {
        ivQrcode = (ImageView) findViewById(R.id.iv_qrcode);
        tvNextStep = (TextView) findViewById(R.id.next_step);
        tvNextStep.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.next_step:
                Intent addCar=new Intent(ShowQRCode.this, AddCar.class);
                addCar.putExtra("qrcode",qrcode);
                startActivity(addCar);
                finish();
                break;
        }
    }
    public boolean saveImageToExternalStorage(Bitmap image) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(fullPath, "qrcode.png");
            file.createNewFile();
            fOut = new FileOutputStream(file);

// 100 means no compression, the lower you go, the stronger the compression
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(ctx.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

            return true;

        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            return false;
        }
    }

}
