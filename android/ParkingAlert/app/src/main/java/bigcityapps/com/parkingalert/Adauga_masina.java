package bigcityapps.com.parkingalert;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import Util.Constants;
import Util.GetFilePathFromDevice;

/**
 * Created by fasu on 19/09/2016.
 */
public class Adauga_masina extends Activity implements View.OnClickListener{
    Context ctx;
    RelativeLayout inapoi, gata;
    EditText nume, nr, producator, model, an;
    ImageView poza_masina;
    Uri imageUri;
    String imagePath;
    Activity act;
    String realPath;
    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    final int ACTIVITY_SELECT_IMAGE = 1234;
    RequestQueue queue;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.adauga_masina);
        ctx = this;
        queue = Volley.newRequestQueue(this);
        act=this;
        initComponents();
    }

    public void initComponents(){
        inapoi=(RelativeLayout)findViewById(bigcityapps.com.parkingalert.R.id.inapoi_adauga_masina);
        gata=(RelativeLayout)findViewById(bigcityapps.com.parkingalert.R.id.gata_adauga_masina);
        gata.setOnClickListener(this);
        inapoi.setOnClickListener(this);
        nume=(EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_numele_masina);
        nr=(EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_nr);
        producator=(EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_producator);
        model=(EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_model);
        an=(EditText) findViewById(bigcityapps.com.parkingalert.R.id.et_an_productie);
        poza_masina=(ImageView)findViewById(bigcityapps.com.parkingalert.R.id.poza_masina);
        poza_masina.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()){
            case R.id.inapoi_adauga_masina:
                finish();
                break;
            case R.id.gata_adauga_masina:
                postUser("57e11909853b0122ac974e23");
                break;
            case R.id.poza_masina:
                final Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(bigcityapps.com.parkingalert.R.layout.dialog_user_profile);

                TextView take_photo = (TextView) dialog.findViewById(bigcityapps.com.parkingalert.R.id.take_photo);
                TextView biblioteca = (TextView) dialog.findViewById(bigcityapps.com.parkingalert.R.id.biblioteca);
                take_photo.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                    builder.setTitle("WRITE_EXTERNAL_STORAGE");
                                    builder.setPositiveButton(android.R.string.ok, null);
                                    builder.setMessage("please confirm WRITE_EXTERNAL_STORAGE");//TODO put real question
                                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @TargetApi(Build.VERSION_CODES.M)
                                        public void onDismiss(DialogInterface dialog) {
                                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                        }
                                    });
                                    builder.show();
                                } else {
                                    ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }
                            }else{
                                String fileName = "Camera_Example.jpg";
                                ContentValues values = new ContentValues();
                                values.put(MediaStore.Images.Media.TITLE, fileName);
                                values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
                                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                                dialog.dismiss();
                            }
                        }
                        else{
                            String fileName = "Camera_Example.jpg";
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, fileName);
                            values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
                            imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                            dialog.dismiss();
                        }
                    }
                });
                biblioteca.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                if (ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                    builder.setTitle("WRITE_EXTERNAL_STORAGE");
                                    builder.setPositiveButton(android.R.string.ok, null);
                                    builder.setMessage("please confirm WRITE_EXTERNAL_STORAGE");//TODO put real question
                                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @TargetApi(Build.VERSION_CODES.M)
                                        public void onDismiss(DialogInterface dialog) {requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                        }
                                    });
                                    builder.show();
                                } else {
                                    ActivityCompat.requestPermissions(act, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }
                            }else{
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
                                dialog.dismiss();
                            }
                        }else {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, ACTIVITY_SELECT_IMAGE);
                            dialog.dismiss();
                        }
                    }
                });
                TextView dialogButton = (TextView) dialog.findViewById(bigcityapps.com.parkingalert.R.id.anuler);
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
    }

    public void postUser(final String id){
        String url = Constants.URL+"users/addCar/"+id;
        if(nume.getText().length()==0 || nr.getText().length()==0 || producator.getText().length()==0 || model.getText().length()==0 || an.getText().length()==0)
            Toast.makeText(ctx,"Completati toate campurile",Toast.LENGTH_LONG).show();
        else{
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            String json = response;
                            Log.w("meniuu", "response:post user" + response);
                            finish();
                        }
                    }, ErrorListener) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("plates", nr.getText().toString());
                    params.put("given_name", nume.getText().toString());
                    params.put("make", producator.getText().toString());
                    params.put("model", model.getText().toString());
                    params.put("year", an.getText().toString());
                    return params;
                }

//            public Map<String, String> getHeaders() throws AuthFailureError {
////                String auth_token_string = prefs.getString("token1", "");
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", auth_token_string);
//                return params;
//            }
            };
            queue.add(stringRequest);
        }
    }
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
            Toast.makeText(ctx,"Something went wrong",Toast.LENGTH_LONG ).show();
        }
    };
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    String[] projection = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
                    int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToLast();
                    realPath = cursor.getString(column_index_data);
                    Bitmap img = rotateBitmap(realPath);
                    poza_masina.setImageBitmap(img);
                    persistImage(img, "eparti");
                    ///dupa ce este ceva facut
//                    uplloadImageFile(persistImage(img, "eparti"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode==ACTIVITY_SELECT_IMAGE){
            if(resultCode == RESULT_OK){
                if(data.getData()!=null) {
                    try {
                        String[] projection = {MediaStore.Images.Media.DATA};
                        Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
                        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToLast();
                        imagePath = cursor.getString(column_index_data);
                        realPath = GetFilePathFromDevice.getPath(this, data.getData());
                        poza_masina.setImageBitmap(BitmapFactory.decodeFile(imagePath));
                        //dupa ce este ceva facut
//                        uplloadImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        }
    }
    private File persistImage(Bitmap bitmap, String name) {
        File filesDir = ctx.getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");
        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return imageFile;
        } catch (Exception e) {
            Log.e("meniuu", "Error writing bitmap", e);
        }
        return imageFile;
    }
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
}
