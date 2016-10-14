package bigcityapps.com.parkingalert;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import Model.DataModel;

public class MainActivity extends AppCompatActivity {
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
    Context ctx;

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            try {
                notification_type = b.getString("notification_type");
                notification_id = b.getString("notification_id");
                mPlates = b.getString("mPlates");
                estimated_time = b.getString("estimated_time");
                answered_at = b.getString("answered_at");
                Log.w("meniuu", "notificaion:" + notification_id);
                updateUi();
            }catch (Exception e){
                e.printStackTrace();
                Log.w("meniuu","catch la luarea de la push");
            }
        }
    };
    public void updateUi(){
        badge_count.setVisibility(View.VISIBLE);
        badge_count.setText("1");
//        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
//        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
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
        mNavigationDrawerItemTitles= getResources().getStringArray(bigcityapps.com.parkingalert.R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(bigcityapps.com.parkingalert.R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(bigcityapps.com.parkingalert.R.id.left_drawer);
        ctx=this;
        setupToolbar();

//        PackageInfo info;
//        try {
//          String  PACKAGE_NAME = getApplicationContext().getPackageName();
//            Log.w("meniuu","package name"+PACKAGE_NAME);
//            info = getPackageManager().getPackageInfo(PACKAGE_NAME, PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md;
//                md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                String something = new String(Base64.encode(md.digest(), 0));
//                //String something = new String(Base64.encodeBytes(md.digest()));
//                Log.e("meniuu","hash key:"+ something);
//            }
//        } catch (PackageManager.NameNotFoundException e1) {
//            Log.e("meniuu","name not found"+ e1.toString());
//        } catch (NoSuchAlgorithmException e) {
//            Log.e("meniuu","no such edYear algorithm"+ e.toString());
//        } catch (Exception e) {
//            Log.e("exception", e.toString());
//        }


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
        Fragment fragment= new ConnectFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(bigcityapps.com.parkingalert.R.id.content_frame, fragment).commit();

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
        active = false;
        super.onPause();
    }

    /**
     * onstop method
     */

    @Override
    protected void onStop() {
        active = false;
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
        Log.w("meniuu","a intrat in selectitem");
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ConnectFragment();
                break;
            case 1:
                Intent question = new Intent(MainActivity.this, Notifications.class);
                startActivity(question);
                break;
            case 2:
                fragment = new TableFragment();
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
                fragment = new ConnectFragment();
                break;
        }
        mDrawerLayout.closeDrawer(mDrawerList);
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(bigcityapps.com.parkingalert.R.id.content_frame, fragment).commit();

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
        toolbar = (Toolbar) findViewById(bigcityapps.com.parkingalert.R.id.toolbar);
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



}
