package bigcityapps.com.parkingalert;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by fasu on 14/09/2016.
 */
public class Notificari extends AppCompatActivity implements View.OnClickListener{
    ListView listView;
    Context ctx;
    NotificareAdapter adapter;
    RelativeLayout inapoi, istoric;
    ArrayList<ModelNotification> modelNotificationArrayList= new ArrayList<>();
    SearchView search;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bigcityapps.com.parkingalert.R.layout.notificari);
        ctx=this;
        ModelNotification modelNotification= new ModelNotification();
        modelNotification.setTitlu("titlu1");
        modelNotification.setMesaj("mesaj1 care ar trebui sa incapa pe un singur rand, in caz contrar sa puna punctte puncte");
        modelNotification.setDetalii("detalii 1");
        modelNotification.setTip(1);
        modelNotification.setOra("10:30");
        modelNotificationArrayList.add(modelNotification);
        modelNotification.setTitlu("titlu1");
        modelNotification.setMesaj("mesaj1 care ar trebui sa incapa pe un singur rand, in caz contrar sa puna punctte puncte");
        modelNotification.setDetalii("detalii 1");
        modelNotification.setTip(2);
        modelNotification.setOra("10:30");
        modelNotificationArrayList.add(modelNotification);
        listView=(ListView)findViewById(bigcityapps.com.parkingalert.R.id.listview_notificari);
        adapter= new NotificareAdapter(modelNotificationArrayList,ctx);
        listView.setAdapter(adapter);
        initComponents();
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent vizualizare= new Intent(Notificari.this,Vizualizare_masina.class);
                vizualizare.putExtra("detalii",modelNotificationArrayList.get(i).getMesaj());
                startActivity(vizualizare);
            }
        });
    }
    public void initComponents(){
        search=(SearchView)findViewById(R.id.searchView);
        inapoi=(RelativeLayout)findViewById(R.id.inapoi);
        inapoi.setOnClickListener(this);
        istoric=(RelativeLayout)findViewById(R.id.istoric);
        istoric.setOnClickListener(this);
    }

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
        public int getSize(){
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
}
