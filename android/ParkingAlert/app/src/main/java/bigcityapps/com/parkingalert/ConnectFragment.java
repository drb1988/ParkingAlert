package bigcityapps.com.parkingalert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * Created by anupamchugh on 10/12/15.
 */
public class ConnectFragment extends Fragment {
RelativeLayout notificaProprietar;
    public ConnectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(bigcityapps.com.parkingalert.R.layout.notifica_proprietar, container, false);
        notificaProprietar=(RelativeLayout) rootView.findViewById(bigcityapps.com.parkingalert.R.id.relative_notifica_proprietarul);
        notificaProprietar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                    Intent intent = new Intent(getActivity(),Scan.class);
                    startActivity(intent);
            }
        });
        return rootView;
    }
}
