package bigcityapps.com.parkingalert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Model.CarModel;
import Util.Constants;
import Util.SecurePreferences;

/**
 * Created by fasu on 19/09/2016.
 */
public class Cars extends AppCompatActivity implements View.OnClickListener {
    RelativeLayout rlBack, rlAdd, rlLayoutDialog;
    SharedPreferences prefs;
    Context ctx;
    NotificareAdapter adapter;
    private CoordinatorLayout coordinatorLayout;
    TextView tvTitle, tvMessage;
    private RecyclerView recyclerView;
    RequestQueue queue;
    private Paint p = new Paint();
    FloatingActionButton fab;
    ArrayList<CarModel> carModelArrayList = new ArrayList<>();
    TextView tvExistQr, tvNoExistQr, tvCancel;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.masini);
        initcomponents();
        ctx = this;
        prefs = new SecurePreferences(ctx);
        queue = Volley.newRequestQueue(this);
        getCars(prefs.getString("user_id", ""));
//        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//            Intent vizualizare= new Intent(Cars.this,ViewCar.class);
//                vizualizare.putExtra("edYear",carModelArrayList.get(i).getAn());
//                vizualizare.putExtra("edname",carModelArrayList.get(i).getmCarName());
//                vizualizare.putExtra("edNr",carModelArrayList.get(i).getNr());
//                vizualizare.putExtra("edMaker",carModelArrayList.get(i).getProducator());
//                vizualizare.putExtra("edModel",carModelArrayList.get(i).getModel());
//                vizualizare.putExtra("edYear",carModelArrayList.get(i).getAn());
//                vizualizare.putExtra("mImage",carModelArrayList.get(i).getmImage());
//                Log.w("meniuu","toate"+carModelArrayList.get(i).getAn());
//                startActivity(vizualizare);
//            }
//        });
    }

    @Override
    protected void onResume() {
        getCars(prefs.getString("user_id", ""));
        super.onResume();
    }

    public void initcomponents() {
        tvExistQr=(TextView)findViewById(R.id.exist_qr);
        tvExistQr.setOnClickListener(this);
        tvNoExistQr=(TextView)findViewById(R.id.no_exist_qr);
        tvNoExistQr.setOnClickListener(this);
        tvCancel=(TextView)findViewById(R.id.cancel);
        tvCancel.setOnClickListener(this);
        rlLayoutDialog = (RelativeLayout) findViewById(R.id.layout_dialog);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        tvTitle = (TextView) findViewById(bigcityapps.com.parkingalert.R.id.title);
        tvMessage = (TextView) findViewById(R.id.mMessage);
//        listViewMasini=(ListView)findViewById(bigcityapps.com.parkingalert.R.id.listview_masini);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_masini);
        rlBack = (RelativeLayout) findViewById(bigcityapps.com.parkingalert.R.id.inapoi_lista_masini);
        rlAdd = (RelativeLayout) findViewById(bigcityapps.com.parkingalert.R.id.adauga_masini);
        rlBack.setOnClickListener(this);
        rlAdd.setOnClickListener(this);
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
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
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
//                        p.setColor(Color.parseColor("#388E3C"));
//                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
//                        c.drawRect(background,p);
//                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
//                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
//                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.inapoi_lista_masini:
                Intent inapoi = new Intent(Cars.this, MainActivity.class);
                inapoi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(inapoi);
                break;

            case R.id.fab:
                rlLayoutDialog.setVisibility(View.VISIBLE);
                break;
            case R.id.exist_qr:
            Intent addQr= new Intent(Cars.this, AddQR.class);
                startActivity(addQr);
                break;
            case R.id.no_exist_qr:
                Intent adauga_masina = new Intent(Cars.this, AddCar.class);
                startActivity(adauga_masina);
                break;

            case R.id.cancel:
                rlLayoutDialog.setVisibility(View.INVISIBLE);
                break;
        }

    }

    //    class NotificareAdapter extends ArrayAdapter<CarModel> {
//        private ArrayList<CarModel> itemList;
//        private Context context;
//
//        public NotificareAdapter(ArrayList<CarModel> itemList, Context ctx) {
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
//                holder.edNr=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.edNr);
//                holder.mImage=(ImageView) v.findViewById(bigcityapps.com.parkingalert.R.id.poza_lista_masini);
//                v.setTag(holder);
//            }else
//            {
//                holder =(ViewHolder)convertView.getTag();
//            }
//            final CarModel item = itemList.get(position);
//
//            holder.proprietar.setText(item.getmCarName());
//            holder.edNr.setText(item.getNr());
//            Picasso.with(ctx).load(item.getmImage()).into(holder.mImage);
//            return v;
//
//        }
//    }
//    static class ViewHolder {
//        TextView proprietar, edNr;
//        ImageView mImage;
//    }
    public void getCars(String id) {
        String url = Constants.URL + "users/getCars/" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                Log.w("meniuu", "response: getcar" + response);
                carModelArrayList.clear();
                try {
                    JSONArray obj = new JSONArray(json);
                    for (int i = 0; i < obj.length(); i++) {
                        JSONObject c = obj.getJSONObject(i);
                        CarModel carModel = new CarModel();
                        carModel.setNr(c.getString("plates"));
                        carModel.setmCarName(c.getString("given_name"));
                        carModel.setModel(c.getString("model"));
                        carModel.setAn(c.getString("year"));
                        carModel.setProducator(c.getString("make"));
                        carModel.setEnable_notifications(c.getBoolean("enable_notifications"));
                        carModel.setEnable_others(c.getBoolean("enable_others"));
                        carModelArrayList.add(carModel);
                    }
                    if (carModelArrayList.size() > 0) {
                        adapter = new NotificareAdapter(carModelArrayList);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        recyclerView.setLayoutManager(mLayoutManager);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        recyclerView.setAdapter(adapter);
                        tvTitle.setVisibility(View.INVISIBLE);
                        tvMessage.setVisibility(View.INVISIBLE);
                        initSwipe();
                        Log.w("meniuu", "se afisaza recycler");
                        rlLayoutDialog.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } else {
                        rlLayoutDialog.setVisibility(View.VISIBLE);
                        Log.w("meniuu", "se pune pe invisible");
                        recyclerView.setVisibility(View.INVISIBLE);
                        tvTitle.setVisibility(View.VISIBLE);
                        tvMessage.setVisibility(View.VISIBLE);
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
                params.put("Authorization", "Bearer " + auth_token_string);
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


    public class NotificareAdapter extends RecyclerView.Adapter<NotificareAdapter.MyViewHolder> {

        private List<CarModel> moviesList;


        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView proprietar, nr;
            ImageView poza;
            TextView tvOnOff;

            public MyViewHolder(View view) {
                super(view);
                proprietar = (TextView) view.findViewById(R.id.mMaker);
                nr = (TextView) view.findViewById(R.id.nr);
                tvOnOff = (TextView) view.findViewById(R.id.text_on_off);
                poza = (ImageView) view.findViewById(R.id.poza_lista_masini);
            }
        }


        public NotificareAdapter(List<CarModel> moviesList) {
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
                    Intent vizualizare = new Intent(Cars.this, ViewCar.class);
                    vizualizare.putExtra("edYear", carModelArrayList.get(position).getAn());
                    vizualizare.putExtra("edname", carModelArrayList.get(position).getmCarName());
                    vizualizare.putExtra("edNr", carModelArrayList.get(position).getNr());
                    vizualizare.putExtra("edMaker", carModelArrayList.get(position).getProducator());
                    vizualizare.putExtra("edModel", carModelArrayList.get(position).getModel());
                    vizualizare.putExtra("edYear", carModelArrayList.get(position).getAn());
                    vizualizare.putExtra("image", carModelArrayList.get(position).getmImage());
                    vizualizare.putExtra("enable_notifications", carModelArrayList.get(position).isEnable_notifications());
                    vizualizare.putExtra("enable_others", carModelArrayList.get(position).isEnable_others());
                    startActivity(vizualizare);
                }
            });
            CarModel item = moviesList.get(position);
            holder.proprietar.setText(item.getmCarName());
            holder.nr.setText(item.getNr());
//            Picasso.with(ctx).load(item.getmImage()).into(holder.poza);
            if(item.isEnable_notifications())
                holder.tvOnOff.setText("Pornit");
            else
                holder.tvOnOff.setText("Oprit");

        }

        public void removeItem(int position) {
            deleteCar(prefs.getString("user_id", ""), moviesList.get(position).getNr());
            moviesList.remove(position);
            if (moviesList.size() == 0) {
                recyclerView.setVisibility(View.INVISIBLE);
                tvTitle.setVisibility(View.VISIBLE);
                tvMessage.setVisibility(View.VISIBLE);
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

    public void deleteCar(final String id, final String plates) {
        String url = Constants.URL + "users/removeCar/" + id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    public void onResponse(String response) {
                        String json = response;
                        Log.w("meniuu", "response:post user" + response);
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Masina a fost stearsa!", Snackbar.LENGTH_LONG);
//                                    .setAction("SETARI", new View.OnClickListener() {
//                                        public void onClick(View view) {
//                                            startActivityForResult(new Intent(android. provider.Settings.ACTION_SETTINGS), 0);
//                                        }
//                                    });
                        snackbar.show();
                    }
                }, ErrorListener) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("plates", plates);
                return params;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                String auth_token_string = prefs.getString("token", "");
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + auth_token_string);
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
