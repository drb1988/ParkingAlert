package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;

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
import java.util.Map;

import Model.MasiniModel;
import Util.Constants;

/**
 * Created by fasu on 19/09/2016.
 */
public class Masini extends Activity implements View.OnClickListener{
    RelativeLayout inapoi, adauga;
    Context ctx;
    NotificareAdapter adapter;
    TextView title, mesaj;
    private RecyclerView recyclerView;
    RequestQueue queue;
    private Paint p = new Paint();
    FloatingActionButton fab;
    ArrayList<MasiniModel>masiniModelArrayList= new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.masini);
        initcomponents();
        ctx=this;
        queue = Volley.newRequestQueue(this);
        getCars("57e11909853b0122ac974e23");
//        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            Intent vizualizare= new Intent(Masini.this,Vizualizare_masina.class);
//                vizualizare.putExtra("an",masiniModelArrayList.get(i).getAn());
//                vizualizare.putExtra("nume",masiniModelArrayList.get(i).getNume_masina());
//                vizualizare.putExtra("nr",masiniModelArrayList.get(i).getNr());
//                vizualizare.putExtra("producator",masiniModelArrayList.get(i).getProducator());
//                vizualizare.putExtra("model",masiniModelArrayList.get(i).getModel());
//                vizualizare.putExtra("an",masiniModelArrayList.get(i).getAn());
//                vizualizare.putExtra("poza",masiniModelArrayList.get(i).getPoza());
//                Log.w("meniuu","toate"+masiniModelArrayList.get(i).getAn());
//                startActivity(vizualizare);
//            }
//        });
    }

    @Override
    protected void onResume() {
        getCars("57e11909853b0122ac974e23");
        super.onResume();
    }

    public void initcomponents(){
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        title=(TextView)findViewById(bigcityapps.com.parkingalert.R.id.title);
        mesaj=(TextView)findViewById(bigcityapps.com.parkingalert.R.id.mesaj);
//        listViewMasini=(ListView)findViewById(bigcityapps.com.parkingalert.R.id.listview_masini);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_masini);
        inapoi=(RelativeLayout)findViewById(bigcityapps.com.parkingalert.R.id.inapoi_lista_masini);
        adauga=(RelativeLayout)findViewById(bigcityapps.com.parkingalert.R.id.adauga_masini);
        inapoi.setOnClickListener(this);
        adauga.setOnClickListener(this);
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
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
            public void onClick(View view) {
switch (view.getId()){
    case R.id.inapoi_lista_masini:
        Intent inapoi= new Intent(Masini.this,MainActivity.class);
        inapoi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(inapoi);
        break;

//    case R.id.adauga_masini:
//        Intent adauga_masina=new Intent(Masini.this, Adauga_masina.class);
//        startActivity(adauga_masina);
//        break;
    case R.id.fab:
        Intent adauga_masina=new Intent(Masini.this, Adauga_masina.class);
        startActivity(adauga_masina);
        break;
}
    }
//    class NotificareAdapter extends ArrayAdapter<MasiniModel> {
//        private ArrayList<MasiniModel> itemList;
//        private Context context;
//
//        public NotificareAdapter(ArrayList<MasiniModel> itemList, Context ctx) {
//            super(ctx, android.R.layout.simple_list_item_1, itemList);
//            this.itemList=itemList;
//            this.context = ctx;
//        }
//        public int getCount() {
//            return itemList.size();
//        }
//        public int getSize(){
//            return itemList.size();
//        }
//        public View getView(final int position, View convertView, ViewGroup parent) {
//            View v = convertView;
//            ViewHolder holder;
//            if (v == null) {
//                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                v = inflater.inflate(bigcityapps.com.parkingalert.R.layout.listview_lista_masini, null);
//                holder = new ViewHolder();
//                holder.proprietar=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.proprietar);
//                holder.nr=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.nr);
//                holder.poza=(ImageView) v.findViewById(bigcityapps.com.parkingalert.R.id.poza_lista_masini);
//                v.setTag(holder);
//            }else
//            {
//                holder =(ViewHolder)convertView.getTag();
//            }
//            final MasiniModel item = itemList.get(position);
//
//            holder.proprietar.setText(item.getNume_masina());
//            holder.nr.setText(item.getNr());
//            Picasso.with(ctx).load(item.getPoza()).into(holder.poza);
//            return v;
//
//        }
//    }
//    static class ViewHolder {
//        TextView proprietar, nr;
//        ImageView poza;
//    }
    public void getCars(String id){
        String url = Constants.URL+"users/getCars/"+id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                Log.w("meniuu", "response: getcar" + response);
                masiniModelArrayList.clear();
                try {
                    JSONArray obj = new JSONArray(json);
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject c = obj.getJSONObject(i);
                        MasiniModel masiniModel= new MasiniModel();
                        masiniModel.setNr(c.getString("plates"));
                        masiniModel.setNume_masina(c.getString("given_name"));
                        masiniModel.setModel(c.getString("model"));
                        masiniModel.setAn(c.getString("year"));
                        masiniModel.setProducator(c.getString("make"));
                        masiniModelArrayList.add(masiniModel);
                    }
                    if(masiniModelArrayList.size()>0) {
                        adapter = new NotificareAdapter(masiniModelArrayList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);
                        title.setVisibility(View.INVISIBLE);
                        mesaj.setVisibility(View.INVISIBLE);
                        initSwipe();
                        Log.w("meniuu","se afisaza recycler");
                    }else {
                        Log.w("meniuu","se pune pe invisible");
                        recyclerView.setVisibility(View.INVISIBLE);
                        title.setVisibility(View.VISIBLE);
                        mesaj.setVisibility(View.VISIBLE);
                    }
                } catch (Throwable t) {
                    Log.w("meniuu", "cacth get questions");
                    t.printStackTrace();
                }
            }
        }, ErrorListener) {
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                String auth_token_string = prefs.getString("token1", "");
//                Log.w("meniuu", "token:" + auth_token_string);
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", auth_token_string);
//                return params;
//            }
        };
        queue.add(stringRequest);
    }
    Response.ErrorListener ErrorListener = new Response.ErrorListener() {
        public void onErrorResponse(VolleyError error) {
            Log.w("meniuu", "error: errorlistener:" + error);
        }
    };


    public class NotificareAdapter extends RecyclerView.Adapter<NotificareAdapter.MyViewHolder>{

        private List<MasiniModel> moviesList;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView proprietar, nr;
            ImageView poza;

            public MyViewHolder(View view) {
                super(view);
                proprietar=(TextView) view.findViewById(bigcityapps.com.parkingalert.R.id.proprietar);
                nr=(TextView) view.findViewById(bigcityapps.com.parkingalert.R.id.nr);
                poza=(ImageView) view.findViewById(bigcityapps.com.parkingalert.R.id.poza_lista_masini);
            }
        }


        public NotificareAdapter(List<MasiniModel> moviesList) {
            this.moviesList = moviesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_lista_masini, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent vizualizare= new Intent(Masini.this,Vizualizare_masina.class);
                    vizualizare.putExtra("an",masiniModelArrayList.get(position).getAn());
                    vizualizare.putExtra("nume",masiniModelArrayList.get(position).getNume_masina());
                    vizualizare.putExtra("nr",masiniModelArrayList.get(position).getNr());
                    vizualizare.putExtra("producator",masiniModelArrayList.get(position).getProducator());
                    vizualizare.putExtra("model",masiniModelArrayList.get(position).getModel());
                    vizualizare.putExtra("an",masiniModelArrayList.get(position).getAn());
                    vizualizare.putExtra("poza",masiniModelArrayList.get(position).getPoza());
                    Log.w("meniuu","toate"+masiniModelArrayList.get(position).getAn());
                    startActivity(vizualizare);
                }
            });
            MasiniModel item = moviesList.get(position);
            holder.proprietar.setText(item.getNume_masina());
            holder.nr.setText(item.getNr());
            Picasso.with(ctx).load(item.getPoza()).into(holder.poza);

        }
        public void removeItem(int position) {
            deleteCar("57e11909853b0122ac974e23",moviesList.get(position).getNr());
            moviesList.remove(position);
            if(moviesList.size()==0){
                recyclerView.setVisibility(View.INVISIBLE);
                title.setVisibility(View.VISIBLE);
                mesaj.setVisibility(View.VISIBLE);
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

    public void deleteCar(final String id, final String plates){
        String url = Constants.URL+"users/removeCar/"+id;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            String json = response;
                            Log.w("meniuu", "response:post user" + response);
                        }
                    }, ErrorListener) {
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("plates", plates);
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
