package bigcityapps.com.parkingalert;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
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
import android.widget.TextView;
import android.widget.Toast;

import Model.DataModel;

public class MainActivity extends AppCompatActivity {

    private String[] mNavigationDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    Toolbar toolbar;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    TextView badge_count;
    android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        mTitle = mDrawerTitle = getTitle();
        mNavigationDrawerItemTitles= getResources().getStringArray(bigcityapps.com.parkingalert.R.array.navigation_drawer_items_array);
        mDrawerLayout = (DrawerLayout) findViewById(bigcityapps.com.parkingalert.R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(bigcityapps.com.parkingalert.R.id.left_drawer);

        setupToolbar();

//        PackageInfo info;
//        try {
          String  PACKAGE_NAME = getApplicationContext().getPackageName();
            Log.w("meniuu","package name"+PACKAGE_NAME);
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
//            Log.e("meniuu","no such an algorithm"+ e.toString());
//        } catch (Exception e) {
//            Log.e("exception", e.toString());
//        }


        DataModel[] drawerItem = new DataModel[10];
        drawerItem[0] = new DataModel(1, "NOTIFICARI");
        drawerItem[1] = new DataModel(bigcityapps.com.parkingalert.R.drawable.connect, "Notificari");
        drawerItem[2] = new DataModel(bigcityapps.com.parkingalert.R.drawable.fixtures, "Trimite notificari");
        drawerItem[3] = new DataModel(1, "SETARI");
        drawerItem[4] = new DataModel(bigcityapps.com.parkingalert.R.drawable.fixtures, "Profil personal");
        drawerItem[5] = new DataModel(bigcityapps.com.parkingalert.R.drawable.fixtures, "Masini inregistrate");
        drawerItem[6] = new DataModel(bigcityapps.com.parkingalert.R.drawable.fixtures, "Optiuni configurare");
        drawerItem[7] = new DataModel(1, "AJUTOR");
        drawerItem[8] = new DataModel(bigcityapps.com.parkingalert.R.drawable.fixtures, "Instructiuni de folosire");
        drawerItem[9] = new DataModel(bigcityapps.com.parkingalert.R.drawable.fixtures, "Raporteaza un bug");
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

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position!=0 && position!=3 && position!=7)
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Log.w("meniuu","a intrat in selectitem");
        Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new ConnectFragment();
                break;
            case 1:
                Intent question = new Intent(MainActivity.this, Notificari.class);
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
                Intent masini= new Intent(MainActivity.this, Masini.class);
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

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        final View notificaitons = menu.findItem(R.id.clopot).getActionView();
        badge_count = (TextView) notificaitons.findViewById(R.id.hotlist_hot);
        badge_count.setText("3");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case bigcityapps.com.parkingalert.R.id.clopot:
                Toast.makeText(this,"clopot",Toast.LENGTH_LONG).show();
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
