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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 14/09/2016.
 */
public class Notificari extends AppCompatActivity implements View.OnClickListener{
    static boolean active = false;
    ListView listView;
    RecyclerView notifRecyclerView;
    RequestQueue queue;
    Context ctx;
    NotificareAdapter adapter;
    private Paint p = new Paint();
    RelativeLayout inapoi, istoric;
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
//                if(modelNotificationArrayList.get(i).getType()==3) {
//                    receiverRead(modelNotificationArrayList.get(i).getId());
//                    Log.w(TAG,"intra in view, type:"+modelNotificationArrayList.get(i).getType());
//                    Intent view_notification = new Intent(Notificari.this, ViewNotification.class);
//                    view_notification.putExtra("details", modelNotificationArrayList.get(i).getDetails());
//                    view_notification.putExtra("notification_id", modelNotificationArrayList.get(i).getId());
//                    view_notification.putExtra("nr_car", modelNotificationArrayList.get(i).getNr_car());
//                    startActivity(view_notification);
//                } else
//                if(modelNotificationArrayList.get(i).getType()==2) {
//                    Intent timer = new Intent(Notificari.this, Timer.class);
//                    timer.putExtra("time", modelNotificationArrayList.get(i).getEstimeted_time());
//                    timer.putExtra("hour", modelNotificationArrayList.get(i).getHour());
//                    timer.putExtra("nr_car", modelNotificationArrayList.get(i).getNr_car());
//                    startActivity(timer);
//                }else
//                    if(modelNotificationArrayList.get(i).getType()==1){
//                        Intent harta = new Intent(Notificari.this, Harta.class);
//                        harta.putExtra("hour", modelNotificationArrayList.get(i).getHour());
//                        harta.putExtra("nr_car", modelNotificationArrayList.get(i).getNr_car());
//                        harta.putExtra("time", modelNotificationArrayList.get(i).getEstimeted_time());
//                        startActivity(harta);
//                    }else
//                    if(modelNotificationArrayList.get(i).getType()==4) {
//                        Intent timer = new Intent(Notificari.this, TimerSender.class);
//                        timer.putExtra("time", modelNotificationArrayList.get(i).getEstimeted_time());
//                        timer.putExtra("hour", modelNotificationArrayList.get(i).getHour());
//                        timer.putExtra("nr_car", modelNotificationArrayList.get(i).getNr_car());
//                        startActivity(timer);
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
        notifRecyclerView=(RecyclerView)findViewById(R.id.listview_notificari);
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

    @Override
    protected void onPostResume() {
        Log.w("meniuu","onresume");
        getNotifications(prefs.getString("user_id",""));
        super.onPostResume();
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
//                v = inflater.inflate(bigcityapps.com.parkingalert.R.layout.custom_listview, null);
//                holder = new ViewHolder();
//                holder.titlu=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.title_listview);
//                holder.detalii=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.detalii_listview);
//                holder.mesaj=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.mesaj_listview);
//                holder.ora=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.ora_listview);
//                holder.poza=(ImageView) v.findViewById(bigcityapps.com.parkingalert.R.id.poza_listview);
//                holder.bagde=(ImageView) v.findViewById(bigcityapps.com.parkingalert.R.id.badge_listview);
//                v.setTag(holder);
//            }else
//            {
//                holder =(ViewHolder)convertView.getTag();
//            }
//            final ModelNotification item = _data.get(position);
//            holder.titlu.setText(item.getTitle());
//            holder.detalii.setText(item.getDetails());
//            holder.mesaj.setText(item.getMessage());
//
//            if(item.getType()==2) {
//                holder.bagde.setImageResource(R.drawable.cerculet_notif);
//            }
//            if(item.getType()==3) {
//                if(item.isRead()==false) {
//                    holder.bagde.setImageResource(R.drawable.cerculet_notif);
//                    Log.w("meniuu","cerc");
//                }
//                else {
//                    holder.bagde.setImageResource(android.R.color.transparent);
//                    Log.w("meniuu","transarent");
//                }
//            }
//            if(item.getType()==1 )
//                holder.bagde.setImageResource(android.R.color.transparent);
//            if(item.getType()==4)
//                holder.bagde.setImageResource(R.drawable.ic_back);
//            Log.w("meniu","item.getora:"+item.getHour());
//
////            String [] split= item.getHour().split("T");
////            Log.w("meniuu","split:"+split.length);
//            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
////            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
////            Date date = null;
//            try {
////                date = format1.parse(split[1]);
//                holder.ora.setText(item.getHour());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
////            if(item.type==1)
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
//                    if (wp.getMessage().toLowerCase(Locale.getDefault()).contains(charText)) {
//                        _data.add(wp);
//                    }
//                }
//            }
//            notifyDataSetChanged();
//        }
//    }
//    static class ViewHolder {
//        TextView titlu, detalii, mesaj,ora;
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

                        if(c.getString("sender_id").equals(id))
                        {
                            if(answer.getString("estimated").equals("null"))
                            {   Log.w("meniuu","este null notificare_id:"+c.getString("_id"));
                                modelNotification.setTitle("Ai trimis notificare");
                                modelNotification.setMessage("M-ai blocat");
                                modelNotification.setType(1);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                                Date myDate = simpleDateFormat.parse(c.getString("create_date"));
                                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                                String data=format1.format(myDate);
                                modelNotification.setHour(data);
                            }else
                            {   Log.w("meniuu","este null notificare_id:"+c.getString("_id"));
                                modelNotification.setTitle("Ai primit raspuns");
                                modelNotification.setMessage("Vin in aprox "+answer.getString("estimated")+" minute");
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                                Date myDate = simpleDateFormat.parse(answer.getString("answered_at"));
                                modelNotification.setEstimeted_time(answer.getString("estimated"));
                                modelNotification.setType(2);
                                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                                String data=format1.format(myDate);
                                modelNotification.setHour(data);
                            }
                        }else
                        if(c.getString("receiver_id").equals(id))
                        {
                            if(answer.getString("estimated").equals("null")) {
                                Log.w("meniuu","este null notificare_id:"+c.getString("_id"));
                                modelNotification.setType(3);
                                if(c.getBoolean("receiver_read")) {
                                    modelNotification.setRead(true);
                                }
                                else {
                                    modelNotification.setRead(false);
                                }
                                modelNotification.setTitle("Ai primit notificare");
                                modelNotification.setMessage("Vino la masina pt ca m-ai blocat");
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                                Date myDate = simpleDateFormat.parse(c.getString("create_date"));
                                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                                String data=format1.format(myDate);
                                modelNotification.setHour(data);
                        }else
                        {   Log.w("meniuu","este null notificare_id:"+c.getString("_id"));
                            modelNotification.setType(4);
                            modelNotification.setTitle("Ai trimis raspuns");
                            modelNotification.setMessage("Vin in aprox "+answer.getString("estimated")+" minute");
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("EEST"));
                            try {
                                Log.w(TAG,"asnwertime:"+answer.getString("answered_at"));
                                Date myDate = simpleDateFormat.parse(answer.getString("answered_at"));
                                SimpleDateFormat format1 = new SimpleDateFormat("HH:mm:ss");
                                String data=format1.format(myDate);
                                modelNotification.setHour(data);
                            }catch (Exception e){
                                e.printStackTrace();
                                Log.w("meniuu","catch la date");
                            }
                            modelNotification.setEstimeted_time(answer.getString("estimated"));

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
    public void receiverRead(String notification_id){
        Log.w("meniuu","notification id:"+notification_id);
            String url = Constants.URL+"notifications/receiverRead/"+notification_id;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            String json = response;
                            Log.w("meniuu", "response: receiveranswer" + response);
                        }
                    }, ErrorListener) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("latitude", "24");
                    params.put("longitude", "24");
                    return params;
                }

            public Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Map<String, String> params = new HashMap<String, String>();
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
            ImageView poza, bagde;

            public MyViewHolder(View view) {
                super(view);

                titlu=(TextView) view.findViewById(R.id.title_listview);
                detalii=(TextView) view.findViewById(R.id.detalii_listview);
                mesaj=(TextView) view.findViewById(R.id.mesaj_listview);
                ora=(TextView) view.findViewById(R.id.ora_listview);
                poza=(ImageView) view.findViewById(R.id.poza_listview);
                bagde=(ImageView) view.findViewById(R.id.badge_listview);

                poza=(ImageView) view.findViewById(R.id.poza_lista_masini);
            }
        }


        public NotificareAdapter(List<ModelNotification> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_listview, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if(modelNotificationArrayList.get(position).getType()==3) {
                        receiverRead(modelNotificationArrayList.get(position).getId());
                        Log.w(TAG,"intra in view, type:"+modelNotificationArrayList.get(position).getType());
                        Intent view_notification = new Intent(Notificari.this, ViewNotification.class);
                        view_notification.putExtra("details", modelNotificationArrayList.get(position).getDetails());
                        view_notification.putExtra("notification_id", modelNotificationArrayList.get(position).getId());
                        view_notification.putExtra("nr_car", modelNotificationArrayList.get(position).getNr_car());
                        startActivity(view_notification);
                    } else
                    if(modelNotificationArrayList.get(position).getType()==2) {
                        Intent timer = new Intent(Notificari.this, Timer.class);
                        timer.putExtra("time", modelNotificationArrayList.get(position).getEstimeted_time());
                        timer.putExtra("hour", modelNotificationArrayList.get(position).getHour());
                        timer.putExtra("nr_car", modelNotificationArrayList.get(position).getNr_car());
                        startActivity(timer);
                    }else
                    if(modelNotificationArrayList.get(position).getType()==1){
                        Intent harta = new Intent(Notificari.this, Harta.class);
                        harta.putExtra("hour", modelNotificationArrayList.get(position).getHour());
                        harta.putExtra("nr_car", modelNotificationArrayList.get(position).getNr_car());
                        harta.putExtra("time", modelNotificationArrayList.get(position).getEstimeted_time());
                        startActivity(harta);
                    }else
                    if(modelNotificationArrayList.get(position).getType()==4) {
                        Intent timer = new Intent(Notificari.this, TimerSender.class);
                        timer.putExtra("time", modelNotificationArrayList.get(position).getEstimeted_time());
                        timer.putExtra("hour", modelNotificationArrayList.get(position).getHour());
                        timer.putExtra("nr_car", modelNotificationArrayList.get(position).getNr_car());
                        startActivity(timer);
                    }
                }
            });
            final ModelNotification item = moviesList.get(position);
            holder.titlu.setText(item.getTitle());
            holder.detalii.setText(item.getDetails());
            holder.mesaj.setText(item.getMessage());

            if(item.getType()==2) {
                holder.bagde.setImageResource(R.drawable.cerculet_notif);
            }
            if(item.getType()==3) {
                if(item.isRead()==false) {
                    holder.bagde.setImageResource(R.drawable.cerculet_notif);
                    Log.w("meniuu","cerc");
                }
                else {
                    holder.bagde.setImageResource(android.R.color.transparent);
                    Log.w("meniuu","transarent");
                }
            }
            if(item.getType()==1 )
                holder.bagde.setImageResource(android.R.color.transparent);
            if(item.getType()==4)
                holder.bagde.setImageResource(R.drawable.ic_back);
            Log.w("meniu","item.getora:"+item.getHour());

//            String [] split= item.getHour().split("T");
//            Log.w("meniuu","split:"+split.length);
            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
            SimpleDateFormat format2 = new SimpleDateFormat("HH:mm");
            Date date = null;
            try {
                date = format1.parse(item.getHour());
                String ora=format2.format(date);
                holder.ora.setText(ora);
            } catch (Exception e) {
                e.printStackTrace();
            }

//            if(item.type==1)
//                Picasso.with(ctx).load(bigcityapps.com.parkingalert.R.drawable.ic_back).into(holder.bagde);
//            else
//                Picasso.with(ctx).load(bigcityapps.com.parkingalert.R.drawable.ic_back).into(holder.bagde);

        }
        public void removeItem(int position) {
//            deleteNotification("57e11909853b0122ac974e23");
            moviesList.remove(position);
            if(moviesList.size()==0){
                notifRecyclerView.setVisibility(View.INVISIBLE);
            }else {
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, moviesList.size());
            }
        }
        @Override
        public int getItemCount() {
            return moviesList.size();
        }
    }

    public void deleteNotification(final String id){
        String url = Constants.URL+"users/removeCar/"+id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:post user" + response);
                    }
                }, ErrorListener) {

//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("plates", plates);
//                return params;
//            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Map<String, String> params = new HashMap<String, String>();
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
                    adapter.removeItem(position);
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
