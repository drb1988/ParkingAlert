package bigcityapps.com.parkingalert;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import Model.DataModel;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class DrawerItemCustomAdapter extends ArrayAdapter<DataModel> {

    Context mContext;
    int layoutResourceId;
    DataModel data[] = null;

    public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, DataModel[] data) {

        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);

        ImageView imageViewIcon = (ImageView) listItem.findViewById(bigcityapps.com.parkingalert.R.id.imageViewIcon);
        TextView textViewName = (TextView) listItem.findViewById(bigcityapps.com.parkingalert.R.id.textViewName);
        RelativeLayout relative_listview=(RelativeLayout)listItem.findViewById(bigcityapps.com.parkingalert.R.id.relative_listview);
        DataModel folder = data[position];

        if(folder.icon==1)
            relative_listview.setBackgroundColor(Color.parseColor("#3358B1"));
        else
            relative_listview.setBackgroundColor(Color.parseColor("#3570BD"));
        if(folder.icon!=1)
        imageViewIcon.setImageResource(folder.icon);
        textViewName.setText(folder.name);

        return listItem;
    }
}

