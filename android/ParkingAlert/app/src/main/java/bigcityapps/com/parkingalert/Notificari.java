package bigcityapps.com.parkingalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private Handler mHandler = new Handler();
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
        getNotifications(prefs.getString("user_id",""));
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
                if(modelNotificationArrayList.get(i).getTip()==3) {
                    Intent view_notification = new Intent(Notificari.this, ViewNotification.class);
                    view_notification.putExtra("detalii", modelNotificationArrayList.get(i).getDetalii());
                    view_notification.putExtra("notification_id", modelNotificationArrayList.get(i).getId());
                    startActivity(view_notification);
                } else
                if(modelNotificationArrayList.get(i).getTip()==2) {
                    Intent timer = new Intent(Notificari.this, Timer.class);
                    timer.putExtra("time", modelNotificationArrayList.get(i).getEstimeted_time());
                    timer.putExtra("ora", modelNotificationArrayList.get(i).getOra());
                    Log.w("meniuu","ora:"+modelNotificationArrayList.get(i).getOra());
                    startActivity(timer);
                }else
                    if(modelNotificationArrayList.get(i).getTip()==1){
                        Intent harta = new Intent(Notificari.this, Harta.class);
                        harta.putExtra("ora", modelNotificationArrayList.get(i).getOra());
                        harta.putExtra("nr_car", modelNotificationArrayList.get(i).getNr_car());
                        harta.putExtra("time", modelNotificationArrayList.get(i).getEstimeted_time());
                        startActivity(harta);
                    }
            }
        });
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
                Intent mainactivity= new Intent(Notificari.this, MainActivity.class);
                mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainactivity);
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

            if(item.getTip()==2) {
                holder.bagde.setImageResource(R.drawable.cerculet_notif);
//                new Thread(new Runnable() {
//                    public void run() {
//                        while (mProgressStatus < min * 60) {
//                            mProgressStatus += 1;
//                            // Update the progress bar
//                            mHandler.post(new Runnable() {
//                                public void run() {
//                                    progBar.setProgress(mProgressStatus);
//                                    int minutes = (mProgressStatus % 3600) / 60;
//                                    int sec = mProgressStatus % 60;
//                                    if (minutes < 10) {
//                                        if (sec < 10)
//                                            text.setText("0" + minutes + ":0" + sec);
//                                        else
//                                            text.setText("0" + minutes + ":" + sec);
//                                    } else {
//                                        if (sec < 10)
//                                            text.setText(minutes + ":0" + sec);
//                                        else
//                                            text.setText("0" + minutes + ":" + sec);
//                                    }
//                                }
//                            });
//                            try {
//                                Thread.sleep(1000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).start();
            }
            if(item.getTip()==3)
                holder.bagde.setImageResource(R.drawable.cerculet_notif);
            if(item.getTip()==1)
                holder.bagde.setImageResource(android.R.color.transparent);
            if(item.getTip()==4)
                holder.bagde.setImageResource(R.drawable.ic_back);
            Log.w("meniu","item.getora:"+item.getOra());
            String [] split= item.getOra().split("T");
            Log.w("meniuu","split:"+split.length);
            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
            Date date = null;
            try {
                date = format1.parse(split[1]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.ora.setText(format2.format(date));

//            if(item.tip==1)
//                Picasso.with(ctx).load(bigcityapps.com.parkingalert.R.drawable.ic_back).into(holder.bagde);
//            else
//                Picasso.with(ctx).load(bigcityapps.com.parkingalert.R.drawable.ic_back).into(holder.bagde);
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
        String url = Constants.URL+"users/getNotifications/"+id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                Log.w("meniuu", "response: getnotifications" + response);
                modelNotificationArrayList.clear();
                try {
                    JSONArray obj = new JSONArray(json);
                    for (int i = 0; i < obj.length(); i++)
                    {
                        ModelNotification modelNotification= new ModelNotification();
                        JSONObject c = obj.getJSONObject(i);
                        modelNotification.setId(c.getString("_id"));
                        JSONObject answer= new JSONObject(c.getString("answer"));
                        modelNotification.setNr_car(c.getString("vehicle"));

                        if(c.getString("sender_id").equals(id))
                        {

                            if(answer.getString("read_at").equals("null"))
                            {    Log.w("meniuu","este null notificare_id:"+c.getString("_id"));
                                modelNotification.setTitlu("Ai trimis notificare");
                                modelNotification.setMesaj("M-ai blocat");
                                modelNotification.setTip(1);
                                modelNotification.setOra(c.getString("create_date"));
                            }else
                            {   Log.w("meniuu","este diferit de null");
                                modelNotification.setTitlu("Ai primit raspuns");
                                modelNotification.setMesaj("Vin in aprox "+answer.getString("estimated")+" minute");
                                Log.w("meniuu","");
                                modelNotification.setOra(answer.getString("answered_at"));
                                modelNotification.setEstimeted_time(answer.getString("estimated"));
                                modelNotification.setTip(2);
                            }
                        }else
                        if(c.getString("receiver_id").equals(id))
                        {   if(answer.getString("read_at")==null) {
                            modelNotification.setTip(3);
                            modelNotification.setTitlu("Ai primit notificare");
                            modelNotification.setMesaj("Vino la masina pt ca m-ai blocat");
                            modelNotification.setOra(c.getString("create_date"));
                        }else
                        {
                            modelNotification.setTip(4);
                            modelNotification.setTitlu("Ai trimis raspuns");
                            modelNotification.setMesaj("Vin in aprox "+answer.getString("estimated")+" minute");
                            modelNotification.setOra(answer.getString("answered_at"));
                        }
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
