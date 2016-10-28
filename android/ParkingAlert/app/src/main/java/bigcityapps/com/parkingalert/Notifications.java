package bigcityapps.com.parkingalert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 14/09/2016.
 */
public class Notifications extends AppCompatActivity implements View.OnClickListener{
    static boolean active = false;
//    ListView listView;
    private CoordinatorLayout coordinatorLayout;
    RecyclerView notifRecyclerView;
    RequestQueue queue;
    Context ctx;
    NotificareAdapter adapter;
    private Paint p = new Paint();
    RelativeLayout rlBack, rlHistory;
    SharedPreferences prefs;
    ArrayList<ModelNotification> modelNotificationArrayList= new ArrayList<>();
    SearchView search;
    String TAG="meniuu";

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Bundle x = getIntent().getExtras();
            if(x!=null){
                Log.w("meniuu",":"+x.getString("meniuu"));
            }
            else
            updateUi();
        }
    };
    public void updateUi(){
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.notification_sound);
        mp.start();
        Log.w("meniuu","user_id in updateUi:"+prefs.getString("user_id",""));
        getNotifications(prefs.getString("user_id",""));
        Log.w("meniuu","ai primit un sms in notificari");
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
        registerReceiver(myReceiver, new IntentFilter(MyFirebaseMessagingService.INTENT_FILTER_Notificari));
        ctx=this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        initComponents();

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                if(modelNotificationArrayList.get(i).getmType()==3) {
//                    receiverRead(modelNotificationArrayList.get(i).getId());
//                    Log.w(TAG,"intra in view, mType:"+modelNotificationArrayList.get(i).getmType());
//                    Intent view_notification = new Intent(Notifications.this, ViewNotification.class);
//                    view_notification.putExtra("mDetails", modelNotificationArrayList.get(i).getmDetails());
//                    view_notification.putExtra("notification_id", modelNotificationArrayList.get(i).getId());
//                    view_notification.putExtra("mPlates", modelNotificationArrayList.get(i).getNr_car());
//                    startActivity(view_notification);
//                } else
//                if(modelNotificationArrayList.get(i).getmType()==2) {
//                    Intent timer_sender = new Intent(Notifications.this, Timer.class);
//                    timer_sender.putExtra("time", modelNotificationArrayList.get(i).getEstimeted_time());
//                    timer_sender.putExtra("mHour", modelNotificationArrayList.get(i).getmHour());
//                    timer_sender.putExtra("mPlates", modelNotificationArrayList.get(i).getNr_car());
//                    startActivity(timer_sender);
//                }else
//                    if(modelNotificationArrayList.get(i).getmType()==1){
//                        Intent harta = new Intent(Notifications.this, Map.class);
//                        harta.putExtra("mHour", modelNotificationArrayList.get(i).getmHour());
//                        harta.putExtra("mPlates", modelNotificationArrayList.get(i).getNr_car());
//                        harta.putExtra("time", modelNotificationArrayList.get(i).getEstimeted_time());
//                        startActivity(harta);
//                    }else
//                    if(modelNotificationArrayList.get(i).getmType()==4) {
//                        Intent timer_sender = new Intent(Notifications.this, TimerSender.class);
//                        timer_sender.putExtra("time", modelNotificationArrayList.get(i).getEstimeted_time());
//                        timer_sender.putExtra("mHour", modelNotificationArrayList.get(i).getmHour());
//                        timer_sender.putExtra("mPlates", modelNotificationArrayList.get(i).getNr_car());
//                        startActivity(timer_sender);
//                    }
//            }
//        });
        Log.w("meniuu","user_id:"+prefs.getString("user_id",""));
        getNotifications(prefs.getString("user_id",""));
//         search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//            public boolean onQueryTextChange(String newText) {
//                try {
//                    adapter.filter(newText);
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//                return false;
//            }
//        });
    }
    public void initComponents(){
//        listView=(ListView)findViewById(R.id.listview_notificari);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        notifRecyclerView=(RecyclerView)findViewById(R.id.listview_notificari);
        search=(SearchView)findViewById(R.id.searchView);
        rlBack =(RelativeLayout)findViewById(R.id.inapoi);
        rlBack.setOnClickListener(this);
        rlHistory =(RelativeLayout)findViewById(R.id.istoric);
        rlHistory.setOnClickListener(this);
    }

    /**
     * onclick method
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.inapoi:
                Intent mainactivity= new Intent(Notifications.this, MainActivity.class);
                mainactivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainactivity);
                finish();
                break;

            case R.id.istoric:
                Intent istoric= new Intent(Notifications.this, History.class);
                istoric.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(istoric);
//                finish();
                break;
        }
    }

    @Override
    protected void onPostResume() {
        Log.w("meniuu","onresume");
        getNotifications(prefs.getString("user_id",""));
        super.onPostResume();
    }
    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        super.onDestroy();
    }
    /**
     * notificareAdapter class for listview notificari
     */
//    class NotificareAdapter extends ArrayAdapter<ModelNotification> {
//        private ArrayList<ModelNotification> itemList;
//        public List<ModelNotification> _data;
//        private Context context;
//
//        public NotificareAdapter(ArrayList<ModelNotification> itemList, Context ctx) {
//            super(ctx, android.R.layout.simple_list_item_1, itemList);
//            this._data=itemList;
//            this.itemList= new ArrayList<>();
//            this.itemList.addAll(_data);
//            this.context = ctx;
//        }
//        public int getCount() {
//            return _data.size();
//        }
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            View v = convertView;
//            ViewHolder holder;
//            if (v == null) {
//                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                v = inflater.inflate(bigcityapps.com.parkingalert.R.layout.custom_listview_notification, null);
//                holder = new ViewHolder();
//                holder.titlu=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.title_listview);
//                holder.detalii=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.detalii_listview);
//                holder.tvMessage=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.mesaj_listview);
//                holder.mHour=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.ora_listview);
//                holder.poza=(ImageView) v.findViewById(bigcityapps.com.parkingalert.R.id.poza_listview);
//                holder.bagde=(ImageView) v.findViewById(bigcityapps.com.parkingalert.R.id.badge_listview);
//                v.setTag(holder);
//            }else
//            {
//                holder =(ViewHolder)convertView.getTag();
//            }
//            final ModelNotification item = _data.get(position);
//            holder.titlu.setText(item.getTitle());
//            holder.detalii.setText(item.getmDetails());
//            holder.tvMessage.setText(item.getmMessage());
//
//            if(item.getmType()==2) {
//                holder.bagde.setImageResource(R.drawable.cerculet_notif);
//            }
//            if(item.getmType()==3) {
//                if(item.isReceiverRead()==false) {
//                    holder.bagde.setImageResource(R.drawable.cerculet_notif);
//                    Log.w("meniuu","cerc");
//                }
//                else {
//                    holder.bagde.setImageResource(android.R.color.transparent);
//                    Log.w("meniuu","transarent");
//                }
//            }
//            if(item.getmType()==1 )
//                holder.bagde.setImageResource(android.R.color.transparent);
//            if(item.getmType()==4)
//                holder.bagde.setImageResource(R.drawable.ic_back);
//            Log.w("meniu","item.getora:"+item.getmHour());
//
////            String [] split= item.getmHour().split("T");
////            Log.w("meniuu","split:"+split.length);
//            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
////            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
////            Date date = null;
//            try {
////                date = format1.parse(split[1]);
//                holder.mHour.setText(item.getmHour());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
////            if(item.mType==1)
////                Picasso.with(ctx).load(bigcityapps.com.parkingalert.R.drawable.ic_back).into(holder.bagde);
////            else
////                Picasso.with(ctx).load(bigcityapps.com.parkingalert.R.drawable.ic_back).into(holder.bagde);
//            return v;
//
//        }
//        public void filter(String charText) {
//            charText = charText.toLowerCase(Locale.getDefault());
//            _data.clear();
//            if (charText.length() == 0) {
//                _data.addAll(itemList);
//            } else {
//                for (ModelNotification wp : itemList) {
//                    if (wp.getmMessage().toLowerCase(Locale.getDefault()).contains(charText)) {
//                        _data.add(wp);
//                    }
//                }
//            }
//            notifyDataSetChanged();
//        }
//    }
//    static class ViewHolder {
//        TextView titlu, detalii, tvMessage,mHour;
//        ImageView poza,bagde;
//    }

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
                        JSONObject location= new JSONObject(c.getString("location"));
                        try {
                            JSONObject review = new JSONObject(c.getString("review"));
                            modelNotification.setFeedback(review.getBoolean("feedback"));
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        JSONArray coordinates=new JSONArray(location.getString("coordinates"));
                        modelNotification.setLat(coordinates.get(0).toString());
                        modelNotification.setLng(coordinates.get(1).toString());
                        Log.w("meniuu","lat:"+coordinates.get(0).toString());

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
//                                Log.w("meniuu","datetime in milisec:"+myDate.getTime());
//                                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
//                                String data=format1.format(myDate);
                                modelNotification.setmHour(c.getString("create_date"));
                            }else
                            {
                                modelNotification.setTitle("Ai primit raspuns");
                                modelNotification.setmMessage("Vin in aprox "+answer.getString("estimated")+" minute");
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                                modelNotification.setEstimeted_time(answer.getString("estimated"));
                                try{
                                    JSONArray extesions=new JSONArray(answer.getString("extesions"));
                                    JSONObject extesions1 = obj.getJSONObject(0);
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
                                Date myDate = simpleDateFormat.parse(c.getString("create_date"));
                                Log.w("meniuu","data in get:"+myDate);
//                                Log.w("meniuu","datetime in milisec:"+myDate.getTime());
//                                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
//                                String data=format1.format(myDate);
                                modelNotification.setmHour(c.getString("create_date"));
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
                                JSONObject extesions1 = obj.getJSONObject(0);
                                modelNotification.setExtended(true);
                                modelNotification.setEstimeted_time(extesions1.getString("extension_time"));
                                modelNotification.setExtension_time(extesions1.getString("extended_at"));
                            }catch (Exception e){
                                modelNotification.setExtended(false);
                                e.printStackTrace();
                            }
                        }
                        }
                        modelNotificationArrayList.add(modelNotification);
                    }
                    if(modelNotificationArrayList.size()>0) {
                        adapter = new NotificareAdapter(modelNotificationArrayList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        notifRecyclerView.setLayoutManager(mLayoutManager);
                        notifRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        notifRecyclerView.setAdapter(adapter);
                        initSwipe();
//                        adapter= new NotificareAdapter(modelNotificationArrayList,ctx);
//                        listView.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();
                    }else {
                        notifRecyclerView.setVisibility(View.INVISIBLE);
                    }
                } catch (Throwable t) {
                    Log.w("meniuu", "cacth get questions");
                    t.printStackTrace();
                }
            }
        }, ErrorListener) {
            public java.util.Map getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Log.w("meniuu", "token:" + auth_token_string);
                java.util.Map params = new HashMap<String, String>();
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
    public void receiverRead(String notification_id){
        Log.w("meniuu","notification id:"+notification_id);
            String url = Constants.URL+"notifications/receiverRead/"+notification_id;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            String json = response;
                            Log.w("meniuu", "response: receiveranswer" + response);
                        }
                    }, ErrorListener) {
//                protected java.util.Map getParams() {
//                    java.util.Map params = new HashMap<String, String>();
//                    params.put("mLatitude", "24");
//                    params.put("mLongitude", "24");
//                    return params;
//                }

            public java.util.Map getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+auth_token_string);
                return params;
            }
            };
            queue.add(stringRequest);
    }
    public void senderRead(String notification_id){
        Log.w("meniuu","notification id:"+notification_id);
        String url = Constants.URL+"notifications/senderRead/"+notification_id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response: receiveranswer" + response);
                    }
                }, ErrorListener) {
//                protected java.util.Map getParams() {
//                    java.util.Map params = new HashMap<String, String>();
//                    params.put("mLatitude", "24");
//                    params.put("mLongitude", "24");
//                    return params;
//                }

            public java.util.Map getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    /////recycler view adpater
    public class NotificareAdapter extends RecyclerView.Adapter<NotificareAdapter.MyViewHolder>{
        private List<ModelNotification> moviesList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView titlu, detalii, mesaj, ora;
            ImageView poza;

            public MyViewHolder(View view) {
                super(view);

                titlu=(TextView) view.findViewById(R.id.title_listview);
//                detalii=(TextView) view.findViewById(R.id.detalii_listview);
                mesaj=(TextView) view.findViewById(R.id.mesaj_listview);
                ora=(TextView) view.findViewById(R.id.ora_listview);
                poza=(ImageView) view.findViewById(R.id.poza_listview);
            }
        }


        public NotificareAdapter(List<ModelNotification> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_listview_notification, parent, false);
            return new MyViewHolder(itemView);
        }

        public void onBindViewHolder(final MyViewHolder holder, final int position) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View view) {
//                    if(modelNotificationArrayList.get(position).getmType()==3) {
//                        Log.w("meniuu","view notification");
//                        receiverRead(modelNotificationArrayList.get(position).getId());
//                        Intent view_notification = new Intent(Notifications.this, ViewNotification.class);
//                        view_notification.putExtra("mDetails", modelNotificationArrayList.get(position).getmDetails());
//                        view_notification.putExtra("notification_id", modelNotificationArrayList.get(position).getId());
//                        view_notification.putExtra("mPlates", modelNotificationArrayList.get(position).getNr_car());
//                        startActivity(view_notification);
//                        NotificationManager notifManager= (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
//                        notifManager.cancelAll();
//                    } else
//                    if(modelNotificationArrayList.get(position).getmType()==2) {
//                        senderRead(modelNotificationArrayList.get(position).getId());
//                        Log.w("meniuu","timer_sender");
//                        Intent timer_sender = new Intent(Notifications.this, Timer.class);
//                        timer_sender.putExtra("time", modelNotificationArrayList.get(position).getEstimeted_time());
//                        timer_sender.putExtra("mHour", modelNotificationArrayList.get(position).getmHour());
//                        timer_sender.putExtra("mPlates", modelNotificationArrayList.get(position).getNr_car());
//                        timer_sender.putExtra("notification_id", modelNotificationArrayList.get(position).getId());
//                        timer_sender.putExtra("lat", modelNotificationArrayList.get(position).getLat());
//                        timer_sender.putExtra("lng", modelNotificationArrayList.get(position).getLng());
//                        timer_sender.putExtra("image", modelNotificationArrayList.get(position).getPicture());
//                        startActivity(timer_sender);
//                    }else
//                    if(modelNotificationArrayList.get(position).getmType()==1){
//                        Log.w("meniuu","map");
//                        Intent harta = new Intent(Notifications.this, Map.class);
//                        harta.putExtra("mHour", modelNotificationArrayList.get(position).getmHour());
//                        harta.putExtra("mPlates", modelNotificationArrayList.get(position).getNr_car());
//                        harta.putExtra("time", modelNotificationArrayList.get(position).getEstimeted_time());
//                        harta.putExtra("lat", modelNotificationArrayList.get(position).getLat());
//                        harta.putExtra("lng", modelNotificationArrayList.get(position).getLng());
//                        harta.putExtra("image", modelNotificationArrayList.get(position).getPicture());
//
//                        startActivity(harta);
//                    }else
//                    if(modelNotificationArrayList.get(position).getmType()==4) {
//                        Log.w("meniuu","timersend");
//                        Intent timer_sender = new Intent(Notifications.this, TimerSender.class);
//                        timer_sender.putExtra("time", modelNotificationArrayList.get(position).getEstimeted_time());
//                        timer_sender.putExtra("mHour", modelNotificationArrayList.get(position).getmHour());
//                        timer_sender.putExtra("mPlates", modelNotificationArrayList.get(position).getNr_car());
//                        timer_sender.putExtra("notification_id", modelNotificationArrayList.get(position).getId());
//                        timer_sender.putExtra("lat", modelNotificationArrayList.get(position).getLat());
//                        timer_sender.putExtra("lng", modelNotificationArrayList.get(position).getLng());
//                        timer_sender.putExtra("image", modelNotificationArrayList.get(position).getPicture());
//                        Log.w("meniuu","image:"+ modelNotificationArrayList.get(position).getPicture());
//                        startActivity(timer_sender);
//                    }
//                }
//            });
            final ModelNotification item = moviesList.get(position);
            holder.titlu.setText(item.getTitle());
//            holder.detalii.setText(item.getmDetails());
            if(item.isFeedback())
                holder.mesaj.setText("Notificare rezolvata");
            else
                holder.mesaj.setText("Notificare nerezolvata");
            if(!item.getPicture().equals("null")) {
                Log.w("meniuu","picture:"+item.getPicture());
                Glide.with(ctx).load(item.getPicture()).asBitmap().centerCrop().into(new BitmapImageViewTarget(holder.poza) {
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        holder.poza.setImageDrawable(circularBitmapDrawable);
                    }
                });
            }
            else
                holder.poza.setImageResource(R.drawable.default_image_profile_round);
//            if(item.getmType()==2) {
//                if(item.isSenderRead()==false) {
//                    holder.bagde.setImageResource(R.drawable.cerculet_notif);
//                    holder.titlu.setTypeface(null, Typeface.BOLD);
//                    holder.itemView.setBackgroundColor(Color.WHITE);
//                }
//                else {
//                    holder.bagde.setImageResource(android.R.color.transparent);
//                    holder.titlu.setTypeface(null, Typeface.NORMAL);
//                    holder.itemView.setBackgroundColor(Color.parseColor("#eaeaea"));
//                }
//            }
//            if(item.getmType()==3) {
//                if(item.isReceiverRead()==false) {
//                    holder.bagde.setImageResource(R.drawable.cerculet_notif);
//                    holder.titlu.setTypeface(null, Typeface.BOLD);
//                    holder.itemView.setBackgroundColor(Color.WHITE);
//                }
//                else {
//                    holder.bagde.setImageResource(android.R.color.transparent);
//                    holder.titlu.setTypeface(null, Typeface.NORMAL);
//                    holder.itemView.setBackgroundColor(Color.parseColor("#eaeaea"));
//                }
//            }
//            if(item.getmType()==1 ) {
//                holder.bagde.setImageResource(android.R.color.transparent);
//                holder.titlu.setTypeface(null, Typeface.NORMAL);
//                holder.itemView.setBackgroundColor(Color.parseColor("#eaeaea"));
//            }
//            if(item.getmType()==4) {
//                holder.bagde.setImageResource(R.drawable.ic_back);
//                holder.titlu.setTypeface(null, Typeface.NORMAL);
//                holder.itemView.setBackgroundColor(Color.parseColor("#eaeaea"));
//            }

//            Date myDate = simpleDateFormat.parse(answer.getString("answered_at"));
//            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
//             String data=format1.format(myDate);


            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
            try {
            Date myDate = simpleDateFormat.parse(item.getmHour());
                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                String data=format1.format(myDate);

//            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
            Date date = null;

                date = format1.parse(data);
                String ora=format2.format(date);
                holder.ora.setText(ora);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            if(item.mType==1)
//                Picasso.with(ctx).load(bigcityapps.com.parkingalert.R.drawable.ic_back).into(holder.bagde);
//            else
//                Picasso.with(ctx).load(bigcityapps.com.parkingalert.R.drawable.ic_back).into(holder.bagde);

        }
        public void removeItem(int position) {
            deleteNotificationSender(modelNotificationArrayList.get(position).getId());
            moviesList.remove(position);
            if (moviesList.size() == 0) {
                notifRecyclerView.setVisibility(View.INVISIBLE);
            } else {
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, moviesList.size());
            }
        }
        public void removeItemReceiver(int position) {
            deleteNotificationReceiver(modelNotificationArrayList.get(position).getId());
            moviesList.remove(position);
            if (moviesList.size() == 0) {
                notifRecyclerView.setVisibility(View.INVISIBLE);
            } else {
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, moviesList.size());
            }
        }
        @Override
        public int getItemCount() {
            return moviesList.size();
        }
    }

    public void deleteNotificationReceiver(final String id){
        String url = Constants.URL+"notifications/receiverDeleted/"+id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:delete notificationSender" + response);
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Notificarea a fost stearsa!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }, ErrorListener) {
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("plates", plates);
//                return params;
//            }

            public java.util.Map getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+  auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    public void deleteNotificationSender(final String id){
        String url = Constants.URL+"notifications/senderDeleted/"+id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:delete notificationSender" + response);
                        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Notificarea a fost stearsa!", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                }, ErrorListener) {
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("plates", plates);
//                return params;
//            }

            public java.util.Map getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                java.util.Map params = new HashMap<String, String>();
                params.put("Authorization", "Bearer "+  auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
    private void initSwipe(){
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT){
                    if(modelNotificationArrayList.get(position).getmType()==1 || modelNotificationArrayList.get(position).getmType()==2) {
                        adapter.removeItem(position);
                    }else
                        adapter.removeItemReceiver(position);
                } else {
//                    removeView();
//                    edit_position = position;
//                    alertDialog.setTitle("Edit Country");
//                    et_country.setText(countries.get(position));
//                    alertDialog.show();
                }
            }
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
//                        p.setColor(Color.parseColor("#388E3C"));
//                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
//                        c.drawRect(background,p);
//                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
//                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
//                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(notifRecyclerView);
    }
}
