package bigcityapps.com.parkingalert;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.text.Editable;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import Util.Constants;
import Util.GetFilePathFromDevice;
import Util.SecurePreferences;

/**
 * Created by fasu on 19/09/2016.
 */
public class ModifyCar extends Activity implements View.OnClickListener{
    Context ctx;
    SharedPreferences prefs;
    RelativeLayout rlBack, rlOk;
    EditText edname, edNr, edMaker, edModel, edYear;
    ImageView ivImageCar;
    Uri imageUri;
    String imagePath;
    Activity act;
    String realPath;
    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    final int ACTIVITY_SELECT_IMAGE = 1234;
    RequestQueue queue;
    String mPLatesOriginal;
    TextInputLayout inputCar;

    /**
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_modify);
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        act=this;
        initComponents();
        FluiEdittext();
        Intent iin = getIntent();
        Bundle b = iin.getExtras();
        if (b != null) {

            if(!b.getString("edNr").equals("null")) {
                edNr.setText(b.getString("edNr"));
                mPLatesOriginal=b.getString("edNr");
            }

            if(!b.getString("edMaker").equals("null"))
                edMaker.setText(b.getString("edMaker"));
            if(!b.getString("edModel").equals("null"))
                edModel.setText(b.getString("edModel"));
            if(!b.getString("edYear").equals("null"))
                edYear.setText(b.getString("edYear"));
            if(!b.getString("edname").equals("null"))
                edname.setText(b.getString("edname"));
        }
    }
    private boolean validateNr() {
        String email = edNr.getText().toString().trim();
        if (email.isEmpty() || email.length()!=7) {
            inputCar.setError(getString(R.string.err_msg_car_nr));
            requestFocus(edNr);
            return false;
        } else {
            inputCar.setErrorEnabled(false);
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
     * initializing components
     */
    public void initComponents(){
        inputCar=(TextInputLayout)findViewById(R.id.input_nr);
        rlBack =(RelativeLayout)findViewById(R.id.inapoi_adauga_masina);
        rlOk =(RelativeLayout)findViewById(R.id.gata_adauga_masina);
        rlOk.setOnClickListener(this);
        rlBack.setOnClickListener(this);
        edname =(EditText) findViewById(R.id.et_numele_masina);
        edNr =(EditText) findViewById(R.id.et_nr);
        edNr.addTextChangedListener(new MyTextWatcher(edNr));
        edMaker =(EditText) findViewById(R.id.et_producator);
        edModel =(EditText) findViewById(R.id.et_model);
        edYear =(EditText) findViewById(R.id.et_an_productie);
//        ivImageCar =(ImageView)findViewById(bigcityapps.com.parkingalert.R.id.poza_masina);
//        ivImageCar.setOnClickListener(this);
    }

    public void  FluiEdittext() {
        edname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                } else
                    showKeyboard(edname);
            }
        });
        edNr.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                } else
                    showKeyboard(edNr);
            }
        });
        edMaker.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                } else
                    showKeyboard(edMaker);
            }
        });
        edModel.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                } else
                    showKeyboard(edModel);
            }
        });
        edYear.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                } else
                    showKeyboard(edYear);
            }
        });
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard(EditText ed) {
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(ed, 0);
    }

    /**
     *
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.inapoi_adauga_masina:
                finish();
                break;
            case R.id.gata_adauga_masina:
                UpdateCar(prefs.getString("user_id",""));
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

    /**
     *  add car method
     * @param id
     */
    public void UpdateCar(final String id){
        String url = Constants.URL + "users/editCar/" + id + "&" + mPLatesOriginal;
        Log.w("meniuu","url:"+url);
        if( edNr.getText().length()<7 )
            Toast.makeText(ctx,"Trebuie sa completezi numarul de inmatriculare",Toast.LENGTH_LONG).show();
        else{
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            String json = response;
                            Log.w("meniuu", "response:post user" + response);
                            Intent cars=new Intent(ModifyCar.this,Cars.class);
                            cars.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(cars);
                            finish();
                        }
                    }, ErrorListener) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("plates", edNr.getText().toString().trim());
                    params.put("given_name", edname.getText().toString().length()>0?edname.getText().toString():"Masina lui");
                    params.put("make", edMaker.getText().toString().length()>0?edMaker.getText().toString():"");
                    params.put("model", edModel.getText().toString().length()>0?edModel.getText().toString():"");
                    params.put("year", edYear.getText().toString().length()>0?edYear.getText().toString():"");
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

    /**
     * error listener at volley library
     */
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
            Toast.makeText(ctx,"Something went wrong",Toast.LENGTH_LONG ).show();
        }
    };

    /**
     * on activity result, when we want to upload a picture
     * @param requestCode
     * @param resultCode
     * @param data
     */
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
//                    saveToInternalStorage(img);
//                    Glide.with(ctx).load(img).crossFade().override(100,100).into(ivImageCar);
//                    Glide.with(ctx).load(img).asBitmap().centerCrop().into(new BitmapImageViewTarget(ivImageCar) {
//                        protected void setResource(Bitmap resource) {
//                            RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
//                            circularBitmapDrawable.setCircular(true);
//                            ivImageCar.setImageDrawable(circularBitmapDrawable);
//                        }
//                    });
                    ivImageCar.setImageBitmap(img);
//                    persistImage(img, "parkingalert");
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
                        ivImageCar.setImageBitmap(BitmapFactory.decodeFile(imagePath));
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

    /**
     *  rotate mImage if si necessary
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

}
