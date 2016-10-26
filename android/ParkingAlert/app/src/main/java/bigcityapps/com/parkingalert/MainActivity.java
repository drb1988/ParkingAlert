package bigcityapps.com.parkingalert;


import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import Model.DataModel;
import Util.Constants;
import Util.SecurePreferences;

public class MainActivity extends AppCompatActivity {
    protected OnBackPressedListener onBackPressedListener;
    static boolean active = false;
    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    TextView badge_count;
    String notification_id=null,mPlates=null,notification_type,estimated_time,answered_at;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    String latitude = null, longitude = null;
    Context ctx;
    RequestQueue queue;
    SharedPreferences prefs;
    Long estimetedTime, time , actualDate;
    boolean firtComm=true;
    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }
    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.doBack();
        else
            super.onBackPressed();
    }
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
//            if(firtComm) {
//                firtComm=false;
                Bundle b = intent.getExtras();
                try {
                    notification_type = b.getString("notification_type");
                    notification_id = b.getString("notification_id");
                    mPlates = b.getString("mPlates");
                    estimated_time = b.getString("estimated_time");
                    answered_at = b.getString("answered_at");
                    latitude = b.getString("lat");
                    longitude = b.getString("lng");
                    Log.w("meniuu", "notificaion:" + notification_id);
                    Log.w("meniuu", "answered::" + answered_at);
                    updateUi();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.w("meniuu", "catch la luarea de la push");
                }
//            }else
//                firtComm=true;
        }
    };
    public void updateUi(){
        Log.w("meniuu","sa apelat updateui");
//        badge_count.setVisibility(View.VISIBLE);
//        badge_count.setText("1");
        if(!notification_type.equals("review")) {
            getNotification(prefs.getString("user_id", null));
            Log.w("meniuu","nu e review");
        }
        else
        {Log.w("meniuu","e review");
            Fragment  fragment = new SumarFragment();
            Bundle harta = new Bundle();
            fragment.setArguments(harta);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
        }
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.notification_sound);
        mp.start();
        Log.w("meniuu","ai primit un sms in main");
    }

    /**
     *
     * @param savedInstanceState
     */

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ///firebase receiver
        registerReceiver(myReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER));
        setContentView(R.layout.activity_main);
        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        ctx=this;
        prefs = new SecurePreferences(this);
        queue = Volley.newRequestQueue(this);
        setupToolbar();
        postToken(prefs.getString("user_id",null));
        getNotification(prefs.getString("user_id",null));


        DataModel[] drawerItem = new DataModel[10];
        drawerItem[0] = new DataModel(1, "NOTIFICARI");
        drawerItem[1] = new DataModel(R.drawable.notif, "Notificari");
        drawerItem[2] = new DataModel(R.drawable.send_notif, "Trimite notificari");

        drawerItem[3] = new DataModel(1, "SETARI");
        drawerItem[4] = new DataModel(R.drawable.profil, "Profil personal");
        drawerItem[5] = new DataModel(R.drawable.masini_inregistrate, "Masini inregistrate");
        drawerItem[6] = new DataModel(R.drawable.opt_configurare, "Optiuni configurare");
        drawerItem[7] = new DataModel(1, "AJUTOR");
        drawerItem[8] = new DataModel(R.drawable.instructiuni_folosire, "Instructiuni de folosire");
        drawerItem[9] = new DataModel(R.drawable.report_bug, "Raporteaza un bug");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerItemCustomAdapter adapter = new DrawerItemCustomAdapter(this, bigcityapps.com.parkingalert.R.layout.list_view_item_row, drawerItem);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(bigcityapps.com.parkingalert.R.id.drawer_layout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setupDrawerToggle();


        mDrawerList.setItemChecked(0, true);
        mDrawerList.setSelection(0);
        setTitle("Notifica");
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /**
     * onitemclick drawer
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position!=0 && position!=3 && position!=7)
            selectItem(position);
        }
    }

    @Override
    protected void onPause() {
        Log.w("meniuu","onpauza mainactivity implicit si actife e false");
        active = false;
        super.onPause();
    }

    /**
     * onstop method
     */

    @Override
    protected void onStop() {
        active = false;
        Log.w("meniuu","onstop mainactivity implicit si actife e false");
        super.onStop();
    }
    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }

    /**
     * select item from drawer
     * @param position
     */
    private void selectItem(int position) {
        Log.w("meniuu","a intrat in selectitem pos:"+position);
        Fragment fragment = null;
        switch (position) {
            case 0:
                Log.w("meniuu","bydefault");
//                fragment = new ConnectFragment();
                getNotification(prefs.getString("user_id",null));
                break;
            case 1:
                Intent question = new Intent(MainActivity.this, Notifications.class);
                startActivity(question);
                break;
            case 2:
//                fragment = new ViewNotificationFragment();
                getNotification(prefs.getString("user_id",null));
                break;
            case 4:
                Intent user_profile= new Intent(MainActivity.this, User_profile.class);
                startActivity(user_profile);
                break;
            case 5:
                Intent masini= new Intent(MainActivity.this, Cars.class);
                startActivity(masini);
                break;
            default:
                getNotification(prefs.getString("user_id",null));
                break;
        }
        mDrawerLayout.closeDrawer(mDrawerList);
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(mNavigationDrawerItemTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    /**
     * set tvTitle
     * @param title
     */
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        final View notificaitons = menu.findItem(R.id.clopot).getActionView();
        badge_count = (TextView) notificaitons.findViewById(R.id.hotlist_hot);
        badge_count.setVisibility(View.INVISIBLE);
        RelativeLayout clopot_layout= (RelativeLayout)notificaitons.findViewById(R.id.clopot_layout);
        clopot_layout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(notification_id!=null) {
                    badge_count.setVisibility(View.INVISIBLE);
                    if(notification_type.equals("sender")) {
                        Intent viewNotification = new Intent(MainActivity.this, ViewNotification.class);
                        viewNotification.putExtra("notification_id", notification_id);
                        viewNotification.putExtra("mPlates", mPlates);
                        startActivity(viewNotification);
                    }else   if(notification_type.equals("receiver")) {
                        Intent timer = new Intent(MainActivity.this, Timer.class);
                        timer.putExtra("time", estimated_time);
                        timer.putExtra("mHour", answered_at);
                        timer.putExtra("mPlates", mPlates);
                        timer.putExtra("notification_id", notification_id);
                        timer.putExtra("lat", latitude);
                        timer.putExtra("lng", longitude);
                        startActivity(timer);

                    }else if(notification_type.equals("review")) {
                        Toast.makeText(ctx,"Review", Toast.LENGTH_LONG).show();
                    }
                }
                else
                    Log.w("meniuu","e null");
            }
        });
//        badge_count.setText("3");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clopot:
                if(notification_id!=null) {
                    Intent viewNotification = new Intent(MainActivity.this, ViewNotification.class);
                    viewNotification.putExtra("notification_id", notification_id);
                    viewNotification.putExtra("mPlates", mPlates);
                    startActivity(viewNotification);
                    Toast.makeText(this, "clopot", Toast.LENGTH_LONG).show();
                    NotificationManager notifManager= (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
                    notifManager.cancelAll();
                }
                else
                Log.w("meniuu","e null");
            return true;
        }
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    void setupToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    void setupDrawerToggle(){
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this,mDrawerLayout,toolbar, bigcityapps.com.parkingalert.R.string.drawer_open, bigcityapps.com.parkingalert.R.string.drawer_close){
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerToggle.syncState();
    }

    public void getNotification(final String id){
        String url = Constants.URL+"users/getNotification/"+id;
        Log.w("meniuu","url in getnotif:"+url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                try {
                    Log.w("meniuu","response getnotification:"+response);
                    ModelNotification modelNotification= new ModelNotification();
                    JSONObject c =new JSONObject(json);
                    modelNotification.setId(c.getString("_id"));
                    JSONObject answer= new JSONObject(c.getString("answer"));
                    modelNotification.setNr_car(c.getString("vehicle"));
                    JSONObject location= new JSONObject(c.getString("location"));
                    JSONArray coordinates=new JSONArray(location.getString("coordinates"));
                    modelNotification.setLat(coordinates.get(0).toString());
                    modelNotification.setLng(coordinates.get(1).toString());
                    Log.w("meniuu","lat:"+coordinates.get(0).toString());
                    Log.w("meniuu","lng:"+coordinates.get(1).toString());

                    if(c.getString("sender_id").equals(id))
                    {  try {
                        modelNotification.setPicture(c.getString("receiver_picture"));
                    }catch (Exception e){
                        Log.w("meniuu","receiverul nu are poza");
                        e.printStackTrace();
                        modelNotification.setPicture("null");
                    }
                        if(answer.getString("estimated").equals("null"))
                        {
                            modelNotification.setTitle("Ai trimis notificare");
                            modelNotification.setmMessage("M-ai blocat");
                            modelNotification.setmType(1);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                            Date myDate = simpleDateFormat.parse(c.getString("create_date"));
                            Log.w("meniuu","data in get:"+myDate);
                            modelNotification.setmHour(c.getString("create_date"));
                            /// se schimba fragmentul cu mapa
                            time=myDate.getTime();
                            estimetedTime=(long)30*1000;
                            time=time+estimetedTime;
                            Date date2 = new Date();
                            actualDate=date2.getTime();
                            long diff=time-actualDate;
                            diff = diff / 1000;
                            if(diff>0) {
                                Log.w("meniuu", "mapfragment");
                                Fragment fragment = new MapFragment();
                                Bundle harta = new Bundle();
                                harta.putString("mHour", modelNotification.getmHour());
                                harta.putString("mPlates", modelNotification.getNr_car());
                                harta.putString("time", modelNotification.getEstimeted_time() + "");
                                harta.putString("lat", modelNotification.getLat());
                                harta.putString("lng", modelNotification.getLng());
                                harta.putString("image", modelNotification.getPicture());
                                harta.putString("notification_id", modelNotification.getId());
                                fragment.setArguments(harta);
                                FragmentManager fragmentManager = getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                            }else
                                Review(modelNotification.getId(),modelNotification.getmHour(),modelNotification.getNr_car() );
                        }else
                        {
                            modelNotification.setTitle("Ai primit raspuns");
                            modelNotification.setmMessage("Vin in aprox "+answer.getString("estimated")+" minute");
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                            modelNotification.setEstimeted_time(answer.getString("estimated"));
                            try{
                                JSONArray extesions=new JSONArray(answer.getString("extesions"));
                                JSONObject extesions1 = extesions.getJSONObject(0);
                                modelNotification.setExtended(true);
                                modelNotification.setEstimeted_time(extesions1.getString("extension_time"));
                                modelNotification.setExtension_time(extesions1.getString("extended_at"));
                            }catch (Exception e){
                                modelNotification.setExtended(false);
                                e.printStackTrace();
                            }
                            modelNotification.setmType(2);
                            modelNotification.setmHour(answer.getString("answered_at"));
                            if(c.getBoolean("sender_read"))
                                modelNotification.setSenderRead(true);
                            else
                                modelNotification.setSenderRead(false);
                            ///
                            Log.w("meniuu","timerfragment");
                            Fragment  fragment = new TimerSenderFragmnet();
                            Bundle timer = new Bundle();
                            timer.putString("time", modelNotification.getEstimeted_time());
                            timer.putString("mHour", modelNotification.getmHour());
                            timer.putString("mPlates", modelNotification.getNr_car());
                            timer.putString("notification_id", modelNotification.getId());
                            timer.putString("lat", modelNotification.getLng());
                            timer.putString("lng", modelNotification.getLng());
                            fragment.setArguments(timer);
                            Log.w("meniuu","notif_id: in maina:"+modelNotification.getId());
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                        }
                    }else
                    if(c.getString("receiver_id").equals(id))
                    { try {
                        modelNotification.setPicture(c.getString("sender_picture"));
                    }catch (Exception e){
                        e.printStackTrace();
                        Log.w("meniuu","senderul nu are poza");
                        modelNotification.setPicture("null");
                    }
                        if(answer.getString("estimated").equals("null")) {
                            modelNotification.setmType(3);
                            if(c.getBoolean("receiver_read"))
                                modelNotification.setReceiverRead(true);
                            else
                                modelNotification.setReceiverRead(false);
                            modelNotification.setTitle("Ai primit notificare");
                            modelNotification.setmMessage("Vino la masina pt ca m-ai blocat");
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                            modelNotification.setmHour(c.getString("create_date"));
////
                            Log.w("meniuu","viewnotiffragment");
                            Fragment  fragment = new ViewNotificationFragment();
                            Bundle vienotif = new Bundle();
                            vienotif.putString("mDetails", modelNotification.getmDetails());
                            vienotif.putString("notification_id", modelNotification.getId());
                            vienotif.putString("mPlates", modelNotification.getNr_car());
                            vienotif.putString("mHour", modelNotification.getmHour());
                            fragment.setArguments(vienotif);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                            //
                        }else
                        {
                            modelNotification.setmType(4);
                            modelNotification.setTitle("Ai trimis raspuns");
                            modelNotification.setmMessage("Vin in aprox "+answer.getString("estimated")+" minute");
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                            modelNotification.setmHour(answer.getString("answered_at"));
                            modelNotification.setEstimeted_time(answer.getString("estimated"));
                            try{
                                JSONArray extesions=new JSONArray(answer.getString("extesions"));
                                JSONObject extesions1 = extesions.getJSONObject(0);
                                modelNotification.setExtended(true);
                                modelNotification.setEstimeted_time(extesions1.getString("extension_time"));
                                modelNotification.setExtension_time(extesions1.getString("extended_at"));
                            }catch (Exception e){
                                modelNotification.setExtended(false);
                                e.printStackTrace();
                            }
                            ///
                            Log.w("meniuu","TimerReceiverFragment");
                            Fragment  fragment = new TimerReceiverFragment();
                            Bundle timerSender = new Bundle();
                            timerSender.putString("time", modelNotification.getEstimeted_time());
                            timerSender.putString("mHour", modelNotification.getmHour());
                            timerSender.putString("mPlates", modelNotification.getNr_car());
                            timerSender.putString("notification_id", modelNotification.getId());
                            timerSender.putString("lat", modelNotification.getLat());
                            timerSender.putString("lng", modelNotification.getLng());
                            timerSender.putString("image", modelNotification.getPicture());
                            fragment.setArguments(timerSender);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                        }
                    }
                }catch (Exception e)
                {Log.w("meniuu","este catch");
                    e.printStackTrace();
                    Fragment fragment= new ConnectFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                }
            }
        }, ErrorListener) {
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+auth_token_string);
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

    public void Review(final  String id,final String ora, final String nr_carString){
        String url = Constants.URL+"notifications/sendReview/"+id;
        Log.w("meniuu","url review:"+url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:review" + response);
                        if(!response.equals("invalid notificationID"))
                        {   Fragment  fragment = new SumarFragment();
                            Bundle harta = new Bundle();
                            harta.putString("mHour", ora);
                            harta.putString("mPlates", nr_carString);
                            harta.putString("feedback", false+"");
                            fragment.setArguments(harta);
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
                        }
                    }
                }, ErrorListener) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("feedback",false+"");
                return params;
            }
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization","Bearer "+ auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public interface OnBackPressedListener {
        public void doBack();
    }
    public class BaseBackPressedListener implements OnBackPressedListener {
        private final FragmentActivity activity;

        public BaseBackPressedListener(FragmentActivity activity) {
            this.activity = activity;
        }

        @Override
        public void doBack() {
            activity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }
    public void postToken(String user_id) {
        Log.w("meniuu","user_id:"+user_id+" device_token:"+prefs.getString("phone_token", ""));
        prefs = new SecurePreferences(ctx);
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        final String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        String url = Constants.URL + "users/addSecurity/"+user_id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
//                        postUser();
                    }
                }, ErrorListener) {
            protected java.util.Map<String, String> getParams() {
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("device_token", prefs.getString("phone_token", ""));
                params.put("password", "nuamideecepltrebeaici");
                params.put("reg_ip", ip);
                return params;
            }
            public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization","Bearer "+ auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
