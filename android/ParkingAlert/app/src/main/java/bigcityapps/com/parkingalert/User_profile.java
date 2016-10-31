package bigcityapps.com.parkingalert;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import Util.Constants;
import Util.GetFilePathFromDevice;
import Util.SecurePreferences;
import Util.SingleUploadBroadcastReceiver;

/**
 * Created by fasu on 20/09/2016.
 */
public class User_profile extends Activity implements View.OnClickListener , SingleUploadBroadcastReceiver.Delegate{
    Context ctx;
    SharedPreferences prefs;
    ImageView poza_patrata_user_profile,poza_rotunda_user_profile;
    EditText nume, mobile, email,prenom;
    RequestQueue queue;
    RelativeLayout inapoi, salvare;
    TextInputLayout textInputLayout;
    Activity act;
    String realPath;
    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    Uri imageUri;
    final int ACTIVITY_SELECT_IMAGE = 1234;
    private final SingleUploadBroadcastReceiver uploadReceiver = new SingleUploadBroadcastReceiver();
    ProgressDialog progresss;
    String url_upload;
    String imagePath=null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.user_profile);
        ctx = this;
        prefs = new SecurePreferences(ctx);
        url_upload = Constants.URL + "users/setPicture/"+prefs.getString("user_id","");
        queue = Volley.newRequestQueue(this);
        initComponents();
        getUser(prefs.getString("user_id",""));
        FluiEdittext();
        progresss = new ProgressDialog(this);
    }
    public void initComponents(){
        prenom=(EditText)findViewById(R.id.prenume_user_profile);
        textInputLayout=(TextInputLayout)findViewById(R.id.inputEmail);
        salvare=(RelativeLayout)findViewById(R.id.salvare_user_profile);
        salvare.setOnClickListener(this);
        inapoi=(RelativeLayout)findViewById(R.id.inapoi_user_profile);
        inapoi.setOnClickListener(this);
        poza_patrata_user_profile=(ImageView)findViewById(bigcityapps.com.parkingalert.R.id.poza_patrata_user_profile);
        poza_rotunda_user_profile=(ImageView)findViewById(bigcityapps.com.parkingalert.R.id.poza_rotunda_user_profile);
        poza_rotunda_user_profile.setOnClickListener(this);
        nume=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.numele_user_profile);
//        nickname=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.nick_name);
        mobile=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.mobile_user_profile);
        email=(EditText)findViewById(bigcityapps.com.parkingalert.R.id.email);
        email.addTextChangedListener(new MyTextWatcher(email));
    }
  public void  FluiEdittext(){
        mobile.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                } else
                    showKeyboard(mobile);
            }
        });
      email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          public void onFocusChange(View v, boolean hasFocus) {
              if (!hasFocus) {
                  hideKeyboard(v);
              } else
                  showKeyboard(email);
          }
      });
      nume.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          public void onFocusChange(View v, boolean hasFocus) {
              if (!hasFocus) {
                  hideKeyboard(v);
              } else
                  showKeyboard(nume);
          }
      });
      prenom.setOnFocusChangeListener(new View.OnFocusChangeListener() {
          public void onFocusChange(View v, boolean hasFocus) {
              if (!hasFocus) {
                  hideKeyboard(v);
              } else
                  showKeyboard(prenom);
          }
      });
    }
    public void UpdateUser(final String id){
       String url = Constants.URL+"users/updateUser/"+id;
        if(nume.getText().length()==0 || validateEmail()==false )
            Toast.makeText(ctx,"Completati toate campurile",Toast.LENGTH_LONG).show();
            else{
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            String json = response;
                            Log.w("meniuu", "response:update_user" + response);
                            finish();
                        }
                    }, ErrorListener) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    String name=nume.getText().toString();
                    if (name.contains(" ")) {
                        String[] splitNume = name.split(" ", 2);
                        params.put("first_name", splitNume[0]);
                        params.put("last_name", splitNume[1]);
                        Log.w("meniuu", "fname:" + splitNume[0] + " lname: " + splitNume[1]);
                    } else
                        params.put("first_name", name);



//                    params.put("first_name", nume.getText().toString());
//                    params.put("last_name", nume.getText().toString());
//                    params.put("edNickname", nickname.getText().toString());
                    params.put("email", email.getText().toString());
//                    params.put("photo", "photo");
                    params.put("platform", "Android");
                    params.put("phone_number", mobile.getText().toString());
                    return params;
                }

            public Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization","Bearer "+ auth_token_string);
                return params;
            }
            };
            queue.add(stringRequest);
        }
    }
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
        }
    };
    public void onClick(View view) {
    switch (view.getId()){
        case R.id.salvare_user_profile:
            UpdateUser(prefs.getString("user_id",null));
            break;
        case R.id.inapoi_user_profile:
            finish();
            break;
        case R.id.poza_rotunda_user_profile:
            final Dialog dialog = new Dialog(ctx);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(bigcityapps.com.parkingalert.R.layout.dialog_user_profile);

            TextView take_photo = (TextView) dialog.findViewById(bigcityapps.com.parkingalert.R.id.take_photo);
            TextView biblioteca = (TextView) dialog.findViewById(bigcityapps.com.parkingalert.R.id.biblioteca);
            take_photo.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(act, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                builder.setTitle("WRITE_EXTERNAL_STORAGE");
                                builder.setPositiveButton(android.R.string.ok, null);
                                builder.setMessage("please confirm WRITE_EXTERNAL_STORAGE");//TODO put real question
                                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @TargetApi(Build.VERSION_CODES.M)
                                    public void onDismiss(DialogInterface dialog) {
                                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }
                                });
                                builder.show();
                            } else {
                                ActivityCompat.requestPermissions(act, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                        if (ContextCompat.checkSelfPermission(ctx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(act, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                                builder.setTitle("WRITE_EXTERNAL_STORAGE");
                                builder.setPositiveButton(android.R.string.ok, null);
                                builder.setMessage("please confirm WRITE_EXTERNAL_STORAGE");//TODO put real question
                                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @TargetApi(Build.VERSION_CODES.M)
                                    public void onDismiss(DialogInterface dialog) {requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                    }
                                });
                                builder.show();
                            } else {
                                ActivityCompat.requestPermissions(act, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
    public void getUser(String id){
        String url = Constants.URL+"users/getUser/"+id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                try {
                    Log.w("meniuu","response getuser:"+response);
                    JSONObject user = new JSONObject(json);
                    if(!user.getString("first_name").equals("null"))
                        nume.setText(user.getString("first_name"));
                    if(!user.getString("last_name").equals("null"))
                        prenom.setText(user.getString("last_name"));
                    email.setText(user.getString("email"));

                    Glide.with(ctx).load(user.getString("profile_picture")).asBitmap().centerCrop().into(new BitmapImageViewTarget(poza_rotunda_user_profile) {
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            poza_rotunda_user_profile.setImageDrawable(circularBitmapDrawable);
                        }
                    });

                    Glide.with(ctx).load(user.getString("profile_picture")).into(poza_patrata_user_profile);
                    if(!user.getString("phone_number").equals("null"))
                        mobile.setText(user.getString("phone_number"));
                }catch (Exception e)
                {Log.w("meniuu","este catch");
                    e.printStackTrace();
                }
            }
        }, ErrorListener) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Log.w("meniuu","authtoken:"+auth_token_string);
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    private boolean validateEmail() {
        String memail = email.getText().toString().trim();
        if (memail.isEmpty() || !isValidEmail(memail)) {
            textInputLayout.setError(getString(R.string.err_msg_email));
            requestFocus(email);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
        }
        return true;
    }
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void onProgress(int progress) {

    }

    @Override
    public void onProgress(long uploadedBytes, long totalBytes) {

    }

    @Override
    public void onError(Exception exception) {
        Log.w("meniuu","error in upload:"+exception);
        progresss.dismiss();
    }

    @Override
    public void onCompleted(int serverResponseCode, byte[] serverResponseBody) throws UnsupportedEncodingException, JSONException {

        progresss.dismiss();
        JSONObject response = new JSONObject(new String(serverResponseBody, "UTF-8"));
        Log.w("meniuu","oncompleted:"+response);
        Glide.with(ctx).load(response.getString("profile_picture")).asBitmap().centerCrop().into(new BitmapImageViewTarget(poza_rotunda_user_profile) {
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                poza_rotunda_user_profile.setImageDrawable(circularBitmapDrawable);
            }
        });
        Glide.with(ctx).load(response.getString("profile_picture")).into(poza_patrata_user_profile);
    }

    @Override
    public void onCancelled() {
        Log.w("meniuu","cancel");
        progresss.dismiss();
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
                case R.id.email:
                    validateEmail();
                    break;
            }
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard(EditText ed) {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(ed, 0);
    }
    protected void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    protected void onDestroy() {
        uploadReceiver.unregister(this);
        super.onDestroy();
    }
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
                    persistImage(img, "parkingalert");
                    uplloadImageFile(persistImage(img, "parkingalert"));
//                    Bitmap bm = getThumbnailBitmap(realPath,200);
//                    Log.w("meniuu","bm:"+bm.getHeight()+" :"+bm.getWidth());
//                    poza_user_profile.setImageBitmap(bm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode==1234){
            if(resultCode == RESULT_OK){
                if(data.getData()!=null) {
                    try {
                        Uri uri = data.getData();
                        String[] projection = {MediaStore.Images.Media.DATA};
                        Cursor cursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);
                        int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToLast();
                        imagePath = cursor.getString(column_index_data);
//                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                        poza_user_profile.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 200, 200, false));
                        realPath = GetFilePathFromDevice.getPath(this, data.getData());
//                    if (Build.VERSION.SDK_INT < 11)
//                        realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
//                    else if (Build.VERSION.SDK_INT < 19)
//                        realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
//                    else
//                        realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                        Bitmap img = rotateBitmap(realPath);
                        persistImage(img, "parkingalert");
                        uplloadImageFile(persistImage(img, "parkingalert"));
//                        uplloadImage();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    public void uplloadImage(){
        if(realPath!=null) {
            try { ////storage/sdcard0/DCIM/Camera/1470811678304.jpg
                //////storage/sdcard1/DCIM/Camera/lampa.jpg
                Log.w("meniuu","imagepath in upload:"+realPath);
                String uploadId = UUID.randomUUID().toString();
                uploadReceiver.setDelegate(this);
                uploadReceiver.setUploadID(uploadId);
                String auth_token_string = prefs.getString("token", "");
                new MultipartUploadRequest(this, uploadId, url_upload)
                        .addHeader("Authorization", "Bearer"+auth_token_string)
                        .addFileToUpload(realPath, "file") //Adding file
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload();
                progresss.setMessage("Upload file");
                progresss.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progresss.setIndeterminate(true);
                progresss.setProgress(0);
                progresss.setCanceledOnTouchOutside(false);
                progresss.show();
            } catch (Exception exc) {
                progresss.dismiss();
                Log.w("meniuu", "eroare la catpure picture" + exc.getMessage());
                exc.printStackTrace();
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.w("meneiuu","nu se face upload la imagine");
//            patchUser(null);
        }
    }
    private  File persistImage(Bitmap bitmap, String name) {
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
    public void uplloadImageFile(File b){
        if(realPath!=null) {
            try { ////storage/sdcard0/DCIM/Camera/1470811678304.jpg
                //////storage/sdcard1/DCIM/Camera/lampa.jpg
                Log.w("meniuu","imagepath in upload:"+realPath);
                String uploadId = UUID.randomUUID().toString();
                uploadReceiver.setDelegate(this);
                uploadReceiver.setUploadID(uploadId);
                String auth_token_string = prefs.getString("token", "");
                new MultipartUploadRequest(this, uploadId, url_upload)
                        .addHeader("Authorization", "Bearer "+auth_token_string)
                        .addFileToUpload(b+"", "file") //Adding file
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload();
                progresss.setMessage("Upload file");
                progresss.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progresss.setIndeterminate(true);
                progresss.setProgress(0);
                progresss.setCanceledOnTouchOutside(false);
                progresss.show();
            } catch (Exception exc) {
                Log.w("meniuu", "eroare la catpure picture" + exc.getMessage());
                exc.printStackTrace();
                progresss.dismiss();
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }else{
            Log.w("meneiuu","nu se face upload la imagine");
//            patchUser(null);
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String fileName = "Camera_Example.jpg";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
                    imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    // permission was granted, yay! do the
                    // calendar task you need to do.

                } else {
                    final android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ctx);
                    builder.setTitle("Permisiune");
                    builder.setMessage("Ca sa poti pune poza trebuie sa oferi permisiunea. Multumesc");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
                    android.support.v7.app.AlertDialog alert1 = builder.create();
                    alert1.show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }
    }
}
