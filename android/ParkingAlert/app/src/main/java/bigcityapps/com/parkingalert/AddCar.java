package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 19/09/2016.
 */
public class AddCar extends Activity implements View.OnClickListener {
    public final static String APP_PATH_SD_CARD = "/Friendly/";
    public final static String APP_THUMBNAIL_PATH_SD_CARD = "thumbnails";
    Context ctx;
    SharedPreferences prefs;
    RelativeLayout rlBack, rlOk;
    EditText edname, edNr, edModel, edYear;
    EditText edMaker;
//    Uri imageUri;
//    String imagePath;
//    Activity act;
//    String realPath;
//    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
//    final int ACTIVITY_SELECT_IMAGE = 1234;
    RequestQueue queue;
    Switch receive_notification, all_drive;
    String qrcode;
    String nick_name;
    TextInputLayout input_car;
    /**
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adauga_masina);
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null)
            qrcode = (String) b.get("qrcode");
        initComponents();
        getUser(prefs.getString("user_id",""));
    }

    /**
     * initializing components
     */
    public void initComponents() {
        input_car=(TextInputLayout)findViewById(R.id.input_car);
        rlBack = (RelativeLayout) findViewById(R.id.inapoi_adauga_masina);
        rlOk = (RelativeLayout) findViewById(R.id.gata_adauga_masina);
        rlOk.setOnClickListener(this);
        rlBack.setOnClickListener(this);
        edname = (EditText) findViewById(R.id.et_numele_masina);
        edname.requestFocus();
        edNr = (EditText) findViewById(R.id.et_nr);
        edNr.addTextChangedListener(new MyTextWatcher(edNr));
        edMaker = (EditText) findViewById(R.id.et_producator);
        edModel = (EditText) findViewById(R.id.et_model);
        edYear = (EditText) findViewById(R.id.et_an_productie);
        receive_notification = (Switch) findViewById(R.id.receive_notification);
        all_drive = (Switch) findViewById(R.id.all_drive);
        receive_notification.setChecked(true);
        all_drive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if(isChecked){
                   final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ctx);
                   builder.setTitle("Informare");
                   builder.setMessage("Acuma se mai poate adauga si altcineva la aceasta masina, scanand QR codul");
                   builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int id) {
                           dialog.dismiss();
                       }
                   });
                   android.support.v7.app.AlertDialog alert1 = builder.create();
                   alert1.show();
               }
            }
        });
        all_drive.setChecked(false);
    }

    /**
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inapoi_adauga_masina:
                finish();
                break;
            case R.id.gata_adauga_masina:
                addCar(prefs.getString("user_id", ""), qrcode);
                break;
//            case R.id.poza_masina:
//                final Dialog dialog = new Dialog(ctx);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                dialog.setContentView(bigcityapps.com.parkingalert.R.layout.dialog_user_profile);
//                TextView take_photo = (TextView) dialog.findViewById(bigcityapps.com.parkingalert.R.id.take_photo);
//                TextView biblioteca = (TextView) dialog.findViewById(bigcityapps.com.parkingalert.R.id.biblioteca);
//                take_photo.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View view) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                                if (ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//                                    builder.setTitle("WRITE_EXTERNAL_STORAGE");
//                                    builder.setPositiveButton(android.R.string.ok, null);
//                                    builder.setMessage("please confirm WRITE_EXTERNAL_STORAGE");//TODO put real question
//                                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                        @TargetApi(Build.VERSION_CODES.M)
//                                        public void onDismiss(DialogInterface dialog) {
//                                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                                        }
//                                    });
//                                    builder.show();
//                                } else {
//                                    ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                                }
//                            } else {
//                                String fileName = "Camera_Example.jpg";
//                                ContentValues values = new ContentValues();
//                                values.put(MediaStore.Images.Media.TITLE, fileName);
//                                values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
//                                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//                                dialog.dismiss();
//                            }
//                        } else {
//                            String fileName = "Camera_Example.jpg";
//                            ContentValues values = new ContentValues();
//                            values.put(MediaStore.Images.Media.TITLE, fileName);
//                            values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
//                            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//                            dialog.dismiss();
//                        }
//                    }
//                });
//                biblioteca.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View view) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                                if (ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//                                    builder.setTitle("WRITE_EXTERNAL_STORAGE");
//                                    builder.setPositiveButton(android.R.string.ok, null);
//                                    builder.setMessage("please confirm WRITE_EXTERNAL_STORAGE");//TODO put real question
//                                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                                        @TargetApi(Build.VERSION_CODES.M)
//                                        public void onDismiss(DialogInterface dialog) {
//                                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                                        }
//                                    });
//                                    builder.show();
//                                } else {
//                                    ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//                                }
//                            } else {
//                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                                intent.setType("image/*");
//                                startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
//                                dialog.dismiss();
//                            }
//                        } else {
//                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                            intent.setType("image/*");
//                            startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
//                            dialog.dismiss();
//                        }
//                    }
//                });
//                TextView dialogButton = (TextView) dialog.findViewById(bigcityapps.com.parkingalert.R.id.anuler);
//                dialogButton.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                    }
//                });
//                dialog.show();
//                break;
        }
    }

    /**
     * add car method
     *
     * @param id
     */

//    public void addCar(final  String id, final String qrcode){
//        String url = Constants.URL+"users/addCar/"+id;
//        JSONObject object = new JSONObject();
//        try {
//            object.put("enable_notifications", receive_notification.isChecked());
//            object.put("enable_others", all_drive.isChecked());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
//            public void onResponse(JSONObject response) {
////                String json = response;
//                Log.w("meniuu", "response:post user" + response);
//                saveImageToExternalStorageQrcode();
//                finish();
//            }
//        }, ErrorListener){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<>();
//                params.put("plates", edNr.getText().toString());
//                params.put("given_name", edname.getText().toString().length()>0?edname.getText().toString():"Masina lui");
//                params.put("make", edMaker.getText().toString().length()>0?edMaker.getText().toString():"");
//                params.put("model", edModel.getText().toString().length()>0?edModel.getText().toString():"");
//                params.put("year", edYear.getText().toString().length()>0?edYear.getText().toString():"");
//                params.put("qr_code", qrcode);
//                return params;
//            }
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                String auth_token_string = prefs.getString("token", "");
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization","Bearer "+ auth_token_string);
//                return params;
//            }
//        };
//        queue.add(request);
//    }
    public void addCar(final String id, final String qrcode) {
        String url = Constants.URL + "users/addCar/" + id;
        if (edNr.getText().length()<7)
            Toast.makeText(ctx, "Trebuie sa completezi numarul de inmatriculare", Toast.LENGTH_LONG).show();
        else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            String json = response;
                            Log.w("meniuu", "response:post user" + response);
                            saveImageToExternalStorageQrcode();
                            finish();
                        }
                    }, ErrorListener) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("plates", edNr.getText().toString().trim());
                    params.put("given_name", edname.getText().toString().length() > 0 ? edname.getText().toString() : "Masina lui "+nick_name);
                    params.put("make", edMaker.getText().toString().length() > 0 ? edMaker.getText().toString() : "");
                    params.put("model", edModel.getText().toString().length() > 0 ? edModel.getText().toString() : "");
                    params.put("year", edYear.getText().toString().length() > 0 ? edYear.getText().toString() : "");
                    params.put("qr_code", qrcode);
                    params.put("enable_notifications", receive_notification.isChecked()+"");
                    params.put("enable_others", all_drive.isChecked()+"");

                    return params;
                }

                public Map<String, String> getHeaders() throws AuthFailureError {
                    String auth_token_string = prefs.getString("token", "");
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer " + auth_token_string);
                    return params;
                }
            };
            queue.add(stringRequest);
        }
    }
    private boolean validateNr() {
        String email = edNr.getText().toString().trim();
        if (email.isEmpty() || email.length()!=7) {
            input_car.setError(getString(R.string.err_msg_car_nr));
            requestFocus(edNr);
            return false;
        } else {
            input_car.setErrorEnabled(false);
        }
        return true;
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    private class MyTextWatcher implements TextWatcher {
        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.et_nr:
                    validateNr();
                    break;
            }
        }
    }
    /**
     * error listener at volley library
     */
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
            Toast.makeText(ctx, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    };

    /**
     * on activity result, when we want to upload a picture
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
//            if (resultCode == RESULT_OK) {
//                try {
//                    String[] projection = {MediaStore.Images.Media.DATA};
//                    Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
//                    int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                    cursor.moveToLast();
//                    realPath = cursor.getString(column_index_data);
//                    Bitmap img = rotateBitmap(realPath);
////                    saveToInternalStorage(img);
////                    Glide.with(ctx).load(img).crossFade().override(100,100).into(ivImageCar);
////                    Glide.with(ctx).load(img).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivImageCar) {
////                        protected void setResource(Bitmap resource) {
////                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
////                            circularBitmapDrawable.setCircular(true);
////                            ivImageCar.setImageDrawable(circularBitmapDrawable);
////                        }
////                    });
//                    ivImageCar.setImageBitmap(img);
////                    persistImage(img, "parkingalert");
//                    ///dupa ce este ceva facut
////                    uplloadImageFile(persistImage(img, "eparti"));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else if (resultCode == RESULT_CANCELED) {
//                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
//            }
//        }
//        if (requestCode == ACTIVITY_SELECT_IMAGE) {
//            if (resultCode == RESULT_OK) {
//                if (data.getData() != null) {
//                    try {
//                        String[] projection = {MediaStore.Images.Media.DATA};
//                        Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
//                        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//                        cursor.moveToLast();
//                        imagePath = cursor.getString(column_index_data);
//                        realPath = GetFilePathFromDevice.getPath(this, data.getData());
//                        ivImageCar.setImageBitmap(BitmapFactory.decodeFile(imagePath));
//                        saveImageToExternalStorage((BitmapFactory.decodeFile(imagePath)));
//                        //dupa ce este ceva facut
////                        uplloadImage();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//        }
//    }

    /**
     * rotate mImage if si necessary
     *
     * @param src
     * @return
     */
    public static Bitmap rotateBitmap(String src) {
        Bitmap bitmap = BitmapFactory.decodeFile(src);
        try {
            ExifInterface exif = new ExifInterface(src);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.setScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.setRotate(180);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_TRANSPOSE:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                case ExifInterface.ORIENTATION_TRANSVERSE:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.setRotate(-90);
                    break;
                case ExifInterface.ORIENTATION_NORMAL:
                case ExifInterface.ORIENTATION_UNDEFINED:
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public boolean saveImageToExternalStorage(Bitmap image) {
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(fullPath, "car_image.png");
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

    public boolean isSdReadable() {

        boolean mExternalStorageAvailable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
// We can read and write the media
            mExternalStorageAvailable = true;
            Log.i("isSdReadable", "External storage card is readable.");
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
// We can only read the media
            Log.i("isSdReadable", "External storage card is readable.");
            mExternalStorageAvailable = true;
        } else {
// Something else is wrong. It may be one of many other
// states, but all we need to know is we can neither read nor write
            mExternalStorageAvailable = false;
        }

        return mExternalStorageAvailable;
    }

    public Bitmap getThumbnail(String filename) {

        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;
        Bitmap thumbnail = null;

// Look for the file on the external storage
        try {
            if (isSdReadable() == true) {
                thumbnail = BitmapFactory.decodeFile(fullPath + "/" + filename);
            }
        } catch (Exception e) {
            Log.e("getThumbnail() on external storage", e.getMessage());
        }

// If no file on external storage, look in internal storage
        if (thumbnail == null) {
            try {
                File filePath = ctx.getFileStreamPath(filename);
                FileInputStream fi = new FileInputStream(filePath);
                thumbnail = BitmapFactory.decodeStream(fi);
            } catch (Exception ex) {
                Log.e("getThumbnail() on internal storage", ex.getMessage());
            }
        }
        return thumbnail;
    }

    public boolean saveImageToExternalStorageQrcode() {
        Bitmap bmp = null;
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(qrcode, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
        String fullPath = Environment.getExternalStorageDirectory().getAbsolutePath() + APP_PATH_SD_CARD + APP_THUMBNAIL_PATH_SD_CARD;

        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(fullPath, "qrcode_" + edNr.getText().toString() + ".png");
            file.createNewFile();
            fOut = new FileOutputStream(file);

// 100 means no compression, the lower you go, the stronger the compression
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(ctx.getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

            return true;

        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            return false;
        }
    }

    public void getUser(String id) {
        String url = Constants.URL + "users/getUser/" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                try {
                    Log.w("meniuu", "response getuser:" + response);
                    JSONObject user = new JSONObject(json);
                     nick_name = user.getString("first_name");
                } catch (Exception e) {
                    Log.w("meniuu", "este catch");
                    e.printStackTrace();
                }
            }
        }, ErrorListener) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Log.w("meniuu", "authtoken:" + auth_token_string);
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
