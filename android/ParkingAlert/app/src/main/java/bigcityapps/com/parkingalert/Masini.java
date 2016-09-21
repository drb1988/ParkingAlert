package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

import Model.MasiniModel;
import Util.Constants;

/**
 * Created by fasu on 19/09/2016.
 */
public class Masini extends Activity implements View.OnClickListener{
    RelativeLayout inapoi, adauga;
    Context ctx;
    ListView listViewMasini;
    NotificareAdapter adapter;
    TextView title, mesaj;
    RequestQueue queue;
    ArrayList<MasiniModel>masiniModelArrayList= new ArrayList<>();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.masini);
        initcomponents();
        ctx=this;
        queue = Volley.newRequestQueue(this);
        getCars("57e11909853b0122ac974e23");
        listViewMasini.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent vizualizare= new Intent(Masini.this,Vizualizare_masina.class);
                vizualizare.putExtra("an",masiniModelArrayList.get(i).getAn());
                vizualizare.putExtra("nume",masiniModelArrayList.get(i).getNume_masina());
                vizualizare.putExtra("nr",masiniModelArrayList.get(i).getNr());
                vizualizare.putExtra("producator",masiniModelArrayList.get(i).getProducator());
                vizualizare.putExtra("model",masiniModelArrayList.get(i).getModel());
                vizualizare.putExtra("an",masiniModelArrayList.get(i).getAn());
                vizualizare.putExtra("poza",masiniModelArrayList.get(i).getPoza());
                Log.w("meniuu","toate"+masiniModelArrayList.get(i).getAn());
                startActivity(vizualizare);
            }
        });
    }

    @Override
    protected void onResume() {
        getCars("57e11909853b0122ac974e23");
        super.onResume();
    }

    public void initcomponents(){
        title=(TextView)findViewById(bigcityapps.com.parkingalert.R.id.title);
        mesaj=(TextView)findViewById(bigcityapps.com.parkingalert.R.id.mesaj);
        listViewMasini=(ListView)findViewById(bigcityapps.com.parkingalert.R.id.listview_masini);
        inapoi=(RelativeLayout)findViewById(bigcityapps.com.parkingalert.R.id.inapoi_lista_masini);
        adauga=(RelativeLayout)findViewById(bigcityapps.com.parkingalert.R.id.adauga_masini);
        inapoi.setOnClickListener(this);
        adauga.setOnClickListener(this);
    }

    public void onClick(View view) {
switch (view.getId()){
    case bigcityapps.com.parkingalert.R.id.inapoi_lista_masini:
        Intent inapoi= new Intent(Masini.this,MainActivity.class);
        inapoi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(inapoi);
        break;

    case bigcityapps.com.parkingalert.R.id.adauga_masini:
        Intent adauga_masina=new Intent(Masini.this, Adauga_masina.class);
        startActivity(adauga_masina);
        break;
}
    }
    class NotificareAdapter extends ArrayAdapter<MasiniModel> {
        private ArrayList<MasiniModel> itemList;
        private Context context;

        public NotificareAdapter(ArrayList<MasiniModel> itemList, Context ctx) {
            super(ctx, android.R.layout.simple_list_item_1, itemList);
            this.itemList=itemList;
            this.context = ctx;
        }
        public int getCount() {
            return itemList.size();
        }
        public int getSize(){
            return itemList.size();
        }
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(bigcityapps.com.parkingalert.R.layout.listview_lista_masini, null);
                holder = new ViewHolder();
                holder.proprietar=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.proprietar);
                holder.nr=(TextView) v.findViewById(bigcityapps.com.parkingalert.R.id.nr);
                holder.poza=(ImageView) v.findViewById(bigcityapps.com.parkingalert.R.id.poza_lista_masini);
                v.setTag(holder);
            }else
            {
                holder =(ViewHolder)convertView.getTag();
            }
            final MasiniModel item = itemList.get(position);

            holder.proprietar.setText(item.getNume_masina());
            holder.nr.setText(item.getNr());
            Picasso.with(ctx).load(item.getPoza()).into(holder.poza);
            return v;

        }
    }
    static class ViewHolder {
        TextView proprietar, nr;
        ImageView poza;
    }
    public void getCars(String id){
        String url = Constants.URL+"users/getCars/"+id;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            public void onResponse(String response) {
                String json = response;
                Log.w("meniuu", "response: questions" + response);
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
                        adapter = new NotificareAdapter(masiniModelArrayList, ctx);
                        listViewMasini.setAdapter(adapter);
                        title.setVisibility(View.INVISIBLE);
                        mesaj.setVisibility(View.INVISIBLE);
                    }else
                        listViewMasini.setVisibility(View.INVISIBLE);
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
}
