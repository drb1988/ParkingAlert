package bigcityapps.com.parkingalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 14/09/2016.
 */
public class Notificari extends AppCompatActivity implements View.OnClickListener{
    static boolean active = false;
    ListView listView;
    RequestQueue queue;
    Context ctx;
    NotificareAdapter adapter;
    RelativeLayout inapoi, istoric;
    SharedPreferences prefs;
    ArrayList<ModelNotification> modelNotificationArrayList= new ArrayList<>();
    SearchView search;
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Bundle x = getIntent().getExtras();
            if(x!=null){
                Log.w("meniuu",":"+x.getString("meniuu"));
            }
            else
                Log.w("meniuu","x este null");
            updateUi();
        }
    };
    public void updateUi(){
        Log.w("meniuu","ai primit un sms");

    }
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

    /**
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.notificari);
        ctx=this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        initComponents();
//
//        ModelNotification modelNotification= new ModelNotification();
//        modelNotification.setTitlu("titlu1");
//        modelNotification.setMesaj("mesaj1 care ar trebui sa incapa pe un singur rand, in caz contrar sa puna punctte puncte");
//        modelNotification.setDetalii("detalii 1");
//        modelNotification.setTip(1);
//        modelNotification.setOra("10:30");
//        modelNotificationArrayList.add(modelNotification);
//        modelNotification.setTitlu("titlu1");
//        modelNotification.setMesaj("mesaj1 care ar trebui sa incapa pe un singur rand, in caz contrar sa puna punctte puncte");
//        modelNotification.setDetalii("detalii 1");
//        modelNotification.setTip(2);
//        modelNotification.setOra("10:30");
//        modelNotificationArrayList.add(modelNotification);

//        adapter= new NotificareAdapter(modelNotificationArrayList,ctx);
//        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent view_notification= new Intent(Notificari.this, ViewNotification.class);
                view_notification.putExtra("detalii", modelNotificationArrayList.get(i).getDetalii());
                startActivity(view_notification);
            }
        });
///  user_id=57ea497a1a195829e4444279 ///
Log.w("meniuu","user_id:"+prefs.getString("user_id",""));
        getNotifications(prefs.getString("user_id",""));
//        getNotifications(" ");
         search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            public boolean onQueryTextChange(String newText) {
                try {
                    adapter.filter(newText);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
        });
    }
    public void initComponents(){
        listView=(ListView)findViewById(R.id.listview_notificari);
        search=(SearchView)findViewById(R.id.searchView);
        inapoi=(RelativeLayout)findViewById(R.id.inapoi);
        inapoi.setOnClickListener(this);
        istoric=(RelativeLayout)findViewById(R.id.istoric);
        istoric.setOnClickListener(this);
    }

    /**
     * onclick method
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.inapoi:
//                Intent mainactivity= new Intent(Notificari.this, MainActivity.class);
//                mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(mainactivity);
                finish();
                break;

            case R.id.istoric:
                Intent istoric= new Intent(Notificari.this, Istoric.class);
                istoric.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(istoric);
//                finish();
                break;
        }
    }

    /**
     * notificareAdapter class for listview notificari
     */
    class NotificareAdapter extends ArrayAdapter<ModelNotification> {
        private ArrayList<ModelNotification> itemList;
        public List<ModelNotification> _data;
        private Context context;

        public NotificareAdapter(ArrayList<ModelNotification> itemList, Context ctx) {
            super(ctx, android.R.layout.simple_list_item_1, itemList);
            this._data=itemList;
            this.itemList= new ArrayList<>();
            this.itemList.addAll(_data);
            this.context = ctx;
        }
        public int getCount() {
            return _data.size();
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(bigcityapps.com.parkingalert.R.layout.custom_listview, null);
                holder = new ViewHolder();
                holder.titlu=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.title_listview);
                holder.detalii=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.detalii_listview);
                holder.mesaj=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.mesaj_listview);
                holder.ora=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.ora_listview);
                holder.poza=(ImageView) v.findViewById(bigcityapps.com.parkingalert.R.id.poza_listview);
                holder.bagde=(ImageView) v.findViewById(bigcityapps.com.parkingalert.R.id.badge_listview);
                v.setTag(holder);
            }else
            {
                holder =(ViewHolder)convertView.getTag();
            }
            final ModelNotification item = _data.get(position);
            holder.titlu.setText(item.getTitlu());
            holder.detalii.setText(item.getDetalii());
            holder.mesaj.setText(item.getMesaj());
            holder.ora.setText(item.getOra());
            if(item.tip==1)
                Picasso.with(ctx).load(bigcityapps.com.parkingalert.R.drawable.ic_back).into(holder.bagde);
            else
                Picasso.with(ctx).load(bigcityapps.com.parkingalert.R.drawable.ic_back).into(holder.bagde);
            return v;

        }
        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            _data.clear();
            if (charText.length() == 0) {
                _data.addAll(itemList);
            } else {
                for (ModelNotification wp : itemList) {
                    if (wp.getMesaj().toLowerCase(Locale.getDefault()).contains(charText)) {
                        _data.add(wp);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }
    static class ViewHolder {
        TextView titlu, detalii, mesaj,ora;
        ImageView poza,bagde;
    }

    public void getNotifications(final String id){
        String url = Constants.URL+"users/getCars/"+id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                Log.w("meniuu", "response: getcar" + response);
                modelNotificationArrayList.clear();
                try {
                    JSONArray obj = new JSONArray(json);
                    for (int i = 0; i < obj.length(); i++) {
                        ModelNotification modelNotification= new ModelNotification();
                        JSONObject c = obj.getJSONObject(i);
                        if(c.getString("sender_id").equals(id))
                        {
                            modelNotification.setTitlu("Ai trimis notificare");
                            modelNotification.setMesaj("M-ai blocat");
                        }else
                        if(c.getString("receiver_id").equals(id))
                        {
                            modelNotification.setTitlu("Ai primit notificare");
                            modelNotification.setMesaj("Vino la masina pt ca m-ai blocat");
                        }
                        modelNotificationArrayList.add(modelNotification);
                    }
                    if(modelNotificationArrayList.size()>0) {
                        adapter= new NotificareAdapter(modelNotificationArrayList,ctx);
                        listView.setAdapter(adapter);
                    }else {
                        Log.w("meniuu","se pune pe invisible");
                        listView.setVisibility(View.INVISIBLE);
                    }
                } catch (Throwable t) {
                    Log.w("meniuu", "cacth get questions");
                    t.printStackTrace();
                }
            }
        }, ErrorListener) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Log.w("meniuu", "token:" + auth_token_string);
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization","Bearer "+ auth_token_string);
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
